package harishbhagat.shapeship.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.activities.MenuActivity;
import harishbhagat.shapeship.activities.PauseActivity;
import harishbhagat.shapeship.audio.Audio;
import harishbhagat.shapeship.audio.Sound;
import harishbhagat.shapeship.graphics.Fonts;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.graphics.Textures;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;
import harishbhagat.shapeship.physics.Collision;

/**
 * The main view used by the game.
 *
 * The game runs on its own thread. It builds the level once the surface has a size, since
 * sprites are sized as fractions of the surface, then updates and draws one frame at a time.
 *
 * Created by Harish Bhagat on 26/03/2016.
 */
public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    // --- Member(s)

    private static final int TARGET_FPS = 60;
    /** How long to wait before checking again for a surface to draw on. */
    private static final long SURFACE_WAIT_MILLIS = 16;
    private static final int MAX_ASTEROIDS = 7;
    /** The pause button's width, and its gap from the screen's edges, as fractions of the screen width. */
    private static final float PAUSE_BUTTON_WIDTH = 0.11f;
    private static final float EDGE_MARGIN = 0.05f;
    /** The gap below the ship, as a fraction of the screen height. */
    private static final float PLAYER_BOTTOM_MARGIN = 0.05f;
    /** How far past the screen sprites may go before they're removed, so that asteroids can spawn out of sight. */
    private static final float BOUNDARY_SCALE = 1.2f;

    private final Context context;
    private final SurfaceHolder holder;
    private final SharedPreferences preferences;
    private final Audio audio;
    private final Typeface boldTypeface;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final FrameTimer frameTimer = new FrameTimer(TARGET_FPS);

    // Written by the UI thread and read by the game thread every frame, so volatile
    private volatile boolean running = false;
    private volatile int surfaceWidth = 0;
    private volatile int surfaceHeight = 0;
    private Thread thread = null;

    // The level, built and used only on the game thread
    private Bitmap backgroundImage;
    private Player player;
    private AsteroidSpawner asteroidSpawner;
    private Sprite pauseButton;
    private ScoreDisplay scoreDisplay;
    private int score;

    // The latest tap not yet handled, handed from the UI thread to the game thread
    private final AtomicReference<Vector2> pendingTap = new AtomicReference<>();

    // --- Constructor(s)

    /**
     * Default constructor.
     */
    public GameSurfaceView(Context context)
    {
        super(context);

        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        preferences = context.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);
        boldTypeface = Fonts.black(context);

        audio = Audio.get(context);
        audio.loadVolumes(preferences);

        // Record taps for the game thread. The listener is set once, here on the UI thread.
        setOnTouchListener((view, event) ->
        {
            if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
            pendingTap.set(new Vector2(event.getX(), event.getY()));
            return true;
        });
    }

    // --- Method(s)

    /** Builds the level for a surface of the given size. Runs on the game thread. */
    private void buildLevel(final int width, final int height)
    {
        final RectangleArea screen = new RectangleArea(0, width, 0, height);
        final RectangleArea boundaries = screen.scale(BOUNDARY_SCALE);

        // Scale the background to fill the screen, once
        final Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background_0);
        backgroundImage = Bitmap.createScaledBitmap(background, width, height, true);
        if (backgroundImage != background) background.recycle();

        // Draw each sprite's vector at the size it appears on this screen
        final Bitmap shipTexture = Textures.render(context, R.drawable.shapeship, Textures.pixels(width, Player.WIDTH_FRACTION));
        final Bitmap bulletTexture = Textures.render(context, R.drawable.bullet, Textures.pixels(width, Bullet.WIDTH_FRACTION));
        final Bitmap asteroidTexture = Textures.render(context, R.drawable.asteroid, Textures.pixels(width, Asteroid.WIDTH_FRACTION));
        final Bitmap pauseTexture = Textures.render(context, R.drawable.pause_button, Textures.pixels(width, PAUSE_BUTTON_WIDTH));

        // Positions are centres: centre the player horizontally, above the bottom of the screen
        player = new Player(shipTexture, bulletTexture, boundaries, width);
        player.setPosition(new Vector2(width / 2f, height - shipTexture.getHeight() / 2f - PLAYER_BOTTOM_MARGIN * height));

        asteroidSpawner = new AsteroidSpawner(context, asteroidTexture, MAX_ASTEROIDS, boundaries, screen);

        // The pause button sits in the top-right corner
        pauseButton = new Sprite(pauseTexture, false, ColliderShape.RECTANGLE, 0);
        final float margin = EDGE_MARGIN * width;
        pauseButton.setPosition(new Vector2(width - pauseTexture.getWidth() / 2f - margin, pauseTexture.getHeight() / 2f + margin));

        scoreDisplay = new ScoreDisplay(boldTypeface, width);
        score = 0;
    }

    /** Used for drawing to the canvas. */
    protected void drawCanvas(Canvas canvas)
    {
        // The background is opaque and fills the screen, so it replaces the previous frame
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        for (Sprite asteroid : asteroidSpawner.entities)
            asteroid.draw(canvas, paint);

        player.draw(canvas, paint);

        for (Sprite bullet : player.bulletSpawner.entities)
            bullet.draw(canvas, paint);

        scoreDisplay.draw(canvas, score);
        pauseButton.draw(canvas, paint);
    }

    /** Used to pause the view. */
    public void pause()
    {
        audio.pauseMusic();

        running = false;

        if (thread != null)
        {
            try
            {
                thread.join();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            thread = null;
        }
    }

    /** Used to resume the view, if paused. */
    public void resume()
    {
        audio.startMusic();

        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    /** Frees the music player. Call when the game closes. */
    public void release()
    {
        audio.releaseMusic();
    }

    /** Used to run the game loop. */
    public void run()
    {
        // Render at display priority: the default background priority starves the game of CPU time
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);

        // Time spent paused doesn't count towards the first frame
        frameTimer.reset();

        while (running)
        {
            // Wait for a surface to draw on, without spinning
            final int width = surfaceWidth;
            final int height = surfaceHeight;
            if (!holder.getSurface().isValid() || width == 0 || height == 0)
            {
                if (!sleep(TimeUnit.MILLISECONDS.toNanos(SURFACE_WAIT_MILLIS))) return;
                frameTimer.reset();
                continue;
            }

            if (player == null) buildLevel(width, height);

            final float deltaTime = frameTimer.tick(System.nanoTime());

            // The surface can go between the check above and here
            final Canvas canvas = holder.lockCanvas();
            if (canvas == null)
            {
                if (!sleep(TimeUnit.MILLISECONDS.toNanos(SURFACE_WAIT_MILLIS))) return;
                continue;
            }
            try
            {
                update(deltaTime);
                drawCanvas(canvas);
            }
            finally
            {
                holder.unlockCanvasAndPost(canvas);
            }

            // Hold the target frame rate
            if (!sleep(frameTimer.sleepNanos(System.nanoTime()))) return;
        }
    }

    /** Sleeps the game thread. Returns false if it was interrupted, which stops the loop. */
    private static boolean sleep(final long nanos)
    {
        if (nanos <= 0) return true;
        try
        {
            Thread.sleep(TimeUnit.NANOSECONDS.toMillis(nanos), (int) (nanos % 1_000_000));
            return true;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** Ends the game: stops the loop after this frame, saves the score and returns to the menu. */
    private void gameOver()
    {
        // Stop the loop, so that this runs only once
        running = false;

        audio.play(Sound.EXPLOSION);

        // Save the score if it's higher than the existing score
        HighScore.saveIfHigher(preferences, score);

        // Return to the main menu. Activities are started and finished on the UI thread.
        post(() ->
        {
            final Activity activity = (Activity) context;
            activity.startActivity(new Intent(context, MenuActivity.class));
            // Free up memory
            activity.finish();
        });
    }

    /** Handles the latest tap, then moves the sprites and checks for collisions. */
    private void update(final float deltaTime)
    {
        // Spawn asteroids, aimed at the player
        try
        {
            if (asteroidSpawner.spawn())
                asteroidSpawner.entities.lastElement().setTarget(player.getPosition(), deltaTime);
        }
        catch (CloneNotSupportedException e) { Log.e("SPAWNER ERROR", "CloneNotSupportedException"); }

        // Handle the latest tap, if there is one
        final Vector2 tapLocation = pendingTap.getAndSet(null);
        if (tapLocation != null)
        {
            if (Collision.PointAABBIntersectionTest(pauseButton, tapLocation))
            {
                post(() -> context.startActivity(new Intent(context, PauseActivity.class)));
            }
            else
            {
                // Move the player
                player.setPosition(new Vector2(tapLocation.x, player.getPosition().y));
                // Fire a bullet, and play the shooting sound effect if one was fired
                if (player.fire())
                    audio.play(Sound.SHOOT);
            }
        }

        // Update the game elements
        asteroidSpawner.update(deltaTime);
        player.update(deltaTime);
        player.bulletSpawner.update(deltaTime);

        // Player & Asteroid
        for (Sprite asteroid : asteroidSpawner.entities)
        {
            if (Collision.HasCollidedWith(player, asteroid, deltaTime).collision)
            {
                gameOver();
                return;
            }
        }

        // Bullet & Asteroid
        final int hits = BulletHits.removeHits(player.bulletSpawner.entities, asteroidSpawner.entities, deltaTime);
        if (hits > 0)
        {
            audio.play(Sound.EXPLOSION);
            score += 10 * hits;
        }

        // Asteroid & Asteroid
        Collision.ResolveAll(asteroidSpawner.entities, deltaTime);
    }

    // --- SurfaceHolder.Callback

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}
