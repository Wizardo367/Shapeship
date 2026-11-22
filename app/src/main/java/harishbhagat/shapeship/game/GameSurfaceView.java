package harishbhagat.shapeship.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.activities.MenuActivity;
import harishbhagat.shapeship.activities.PauseActivity;
import harishbhagat.shapeship.audio.Audio;
import harishbhagat.shapeship.audio.AudioType;
import harishbhagat.shapeship.audio.Sound;
import harishbhagat.shapeship.graphics.Axis2D;
import harishbhagat.shapeship.graphics.DisplayUtilities;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;
import harishbhagat.shapeship.physics.Collision;
import harishbhagat.shapeship.physics.CollisionInfo;

/**
 * The main view used by the game.
 *
 * Created by Harish Bhagat on 26/03/2016.
 */
public class GameSurfaceView extends SurfaceView implements Runnable
{
    // --- Member(s)

    // Written by the UI thread in pause() and read by the game thread every frame, so volatile
    private volatile boolean running = false;
    private Bitmap backgroundImage;
    private Context context;
    private DisplayUtilities displayUtilities;
    private GameTimer gameTimer;
    private float deltaTime;
    private RectangleArea screenDimensions;
    private SurfaceHolder holder;
    private Paint paint = new Paint();
    private Thread thread = null;
    private RectangleArea boundaries;

    // Get preferences
    SharedPreferences preferences;

    // Game elements
    private Audio audio;
    private int score;
    private Player player;
    private AsteroidSpawner asteroidSpawner;
    // The latest tap not yet handled, handed from the UI thread to the game thread
    private final AtomicReference<Vector2> pendingTap = new AtomicReference<>();

    // UI elements
    private Sprite pauseButton;

    // --- Constructor(s)

    /**
     * Default constructor.
     */
    public GameSurfaceView(Context context)
    {
        super(context);

        // Set the variables
        audio = Audio.getInstance();
        this.context = context;
        displayUtilities = new DisplayUtilities(context);
        gameTimer = GameTimer.getInstance();
        gameTimer.setThread(thread);
        holder = getHolder(); // Used for the ScreenView
        this.screenDimensions = displayUtilities.getScreenSize();
        this.boundaries = screenDimensions.scale(1.2f); // Padding allows for elements to spawn off-screen without being deleted
        preferences = context.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);

        // Load and scale the background
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background_0);
        final Vector2 bgSize = new Vector2(backgroundImage.getWidth(), backgroundImage.getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(displayUtilities.getScreenSize().maxX / bgSize.x, displayUtilities.getScreenSize().maxY / bgSize.y);
        backgroundImage = Bitmap.createBitmap(backgroundImage, 0, 0, (int) bgSize.x, (int) bgSize.y, matrix, true);

        // Game elements

        // Load audio
        audio.loadSound(context, Sound.BACKGROUND_MUSIC);
        audio.loadSound(context, Sound.EXPLOSION);
        audio.loadSound(context, Sound.SHOOT);

        // Set the player's volume preferences
        audio.setVolume(context, AudioType.MUSIC, audio.getVolume(context, AudioType.MUSIC));
        audio.setVolume(context, AudioType.SFX, audio.getVolume(context, AudioType.SFX));

        // Play background music
        audio.playSound(Sound.BACKGROUND_MUSIC);

        // Initialise, scale and position the player
        player = new Player(context, boundaries);
        player.scale(new Vector2((float) 0.6f, 0.6f));
        score = 0;
        Bitmap playerBitmap = player.getBitmap();
        Vector2 playerSize = new Vector2(playerBitmap.getWidth(), playerBitmap.getHeight());
        // Positions are centres: centre the player horizontally, 5% of the screen height above the bottom
        player.setPosition(new Vector2(screenDimensions.maxX / 2, screenDimensions.maxY - (playerSize.y / 2) - displayUtilities.percentToPixels(5, Axis2D.Y)));

        // Initialise the asteroid spawner
        asteroidSpawner = new AsteroidSpawner(context, 7, boundaries, screenDimensions);

        // UI elements

        // Create and position pause button
        pauseButton = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.pause_button), false, ColliderShape.RECTANGLE, 0);
        pauseButton.scale(new Vector2(0.5f, 0.5f));
        final Bitmap pauseButtonBitmap = pauseButton.getBitmap();
        final Vector2 pauseButtonSize = new Vector2(pauseButtonBitmap.getWidth(), pauseButtonBitmap.getHeight());
        pauseButton.setPosition(new Vector2(screenDimensions.maxX - (pauseButtonSize.x / 2) - displayUtilities.percentToPixels(5, Axis2D.X), (pauseButtonSize.y / 2) + displayUtilities.percentToPixels(5, Axis2D.X)));

        // Record taps for the game thread. The listener is set once, here on the UI thread.
        setOnTouchListener((view, event) ->
        {
            if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
            pendingTap.set(new Vector2(event.getX(), event.getY()));
            return true;
        });
    }

    // --- Method(s)

    /**
     * Used for drawing to the canvas.
     */
    protected void drawCanvas(Canvas canvas)
    {
        // Draw to the canvas

        // Asteroids
        for (Sprite asteroid : asteroidSpawner.entities)
            asteroid.draw(canvas, paint);

        // Player
        player.draw(canvas, paint);

        // Player bullets
        for (Sprite bullet : player.bulletSpawner.entities)
            bullet.draw(canvas, paint);

        // Draw UI

        final String scoreString = new DecimalFormat("000000").format(score);
        final Integer screenWidth = (int) screenDimensions.maxX;

        // Score

        // Get typeface
        Typeface boldTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/seguibl.ttf");

        paint.setTypeface(boldTypeface);
        paint.setColor(Color.parseColor("#bc0202"));
        paint.setTextSize(10);
        final String scoreTitle = "SCORE";
        final float scoreTitleWidth = paint.measureText(scoreTitle);
        canvas.drawText(scoreTitle, (screenWidth - scoreTitleWidth) / 2, 40, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        final float scoreWidth = paint.measureText(scoreString);
        canvas.drawText(scoreString, (screenWidth - scoreWidth) / 2, 65, paint);

        // Pause button
        pauseButton.draw(canvas, paint);
    }

    /** Used to pause the view. */
    public void pause()
    {
        // Pause audio
        audio.pauseSound(Sound.BACKGROUND_MUSIC);

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
        gameTimer.setThread(null);
    }

    /** Used to resume the view, if paused. */
    public void resume()
    {
        // Resume audio
        audio.playSound(Sound.BACKGROUND_MUSIC);

        running = true;
        thread = new Thread(this);
        thread.start();
        gameTimer.setThread(thread);
    }

    /** Used to run the game loop. */
    public void run()
    {
        // Remove the conflict between the UI thread and the game thread (Run the thread in the background)
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Store start time
        long lastTime = 0;
        long currentTime = System.currentTimeMillis();

        // Check that the activity isn't paused
        while(running)
        {
            // Check the validity of the surface
            if (!holder.getSurface().isValid()) continue;

            // Get delta time
            deltaTime = gameTimer.getDeltaTime();

            // Spawn asteroids
            try
            {
                if (asteroidSpawner.spawn())
                    asteroidSpawner.entities.lastElement().setTarget(player.getPosition(), deltaTime);
            }
            catch (CloneNotSupportedException e) { Log.e("SPAWNER ERROR", "CloneNotSupportedException"); };

            // Get the canvas
            Canvas canvas = holder.lockCanvas();
            // Clear the canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(backgroundImage, 0, 0, paint);
            // Handle the latest tap, if there is one
            final Vector2 tapLocation = pendingTap.getAndSet(null);
            if (tapLocation != null)
            {
                // Check if the pause button was tapped
                if (Collision.PointAABBIntersectionTest(pauseButton, tapLocation))
                {
                    // Pause game
                    Intent intent = new Intent(context, PauseActivity.class);
                    context.startActivity(intent);
                }
                else
                {
                    // Move the player
                    final Vector2 playerPosition = player.getPosition();
                    player.setPosition(new Vector2(tapLocation.x, playerPosition.y));
                    // Fire a bullet, and play the shooting sound effect if one was fired
                    if (player.fire())
                        audio.playSound(Sound.SHOOT);
                }
            }
            // Update and draw the canvas
            this.updateCanvas(deltaTime);
            this.drawCanvas(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /** Ends the game: stops the loop after this frame, saves the score and returns to the menu. */
    private void gameOver()
    {
        // Stop the loop, so that this runs only once
        running = false;

        // Play explosion sound effect
        audio.playSound(Sound.EXPLOSION);

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

    /** Used to update the canvas */
    private void updateCanvas(final float deltaTime)
    {
        // Update the game elements
        asteroidSpawner.update(deltaTime);
        player.update(deltaTime);
        player.bulletSpawner.update(deltaTime);

        // Check for collisions

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
            // Play explosion sound effect
            audio.playSound(Sound.EXPLOSION);
            // Add score
            score += 10 * hits;
        }

        // Asteroid & Asteroid
        for (Sprite asteroidA : asteroidSpawner.entities)
        {
            for (Sprite asteroidB : asteroidSpawner.entities)
            {
                // Check that asteroidA and asteroidB aren't the same object
                if (asteroidA == asteroidB) continue;
                // Check for a colllision
                CollisionInfo collisionInfo = Collision.HasCollidedWith(asteroidA, asteroidB, deltaTime);

                if (collisionInfo.collision)
                    // Resolve collision
                    Collision.Collide(asteroidA, asteroidB, collisionInfo, deltaTime);
            }
        }
    }
}
