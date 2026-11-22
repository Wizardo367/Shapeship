package harishbhagat.shapeship.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import harishbhagat.shapeship.activities.MenuActivity;
import harishbhagat.shapeship.activities.PauseActivity;
import harishbhagat.shapeship.audio.Audio;
import harishbhagat.shapeship.audio.Sound;
import harishbhagat.shapeship.graphics.Fonts;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.model.Game;

/**
 * The view the game is played in. It connects the game model to Android: taps go to the game,
 * a GameLoop runs it and a GameRenderer draws it, and what happens in it plays sounds, saves
 * the high score and changes screens.
 *
 * Created by Harish Bhagat on 26/03/2016.
 */
public class GameSurfaceView extends SurfaceView
{
    // --- Member(s)

    private final Context context;
    private final SharedPreferences preferences;
    private final Audio audio;
    private final Typeface typeface;
    private final GameLoop loop;

    // The latest tap not yet handled, handed from the UI thread to the game thread
    private final AtomicReference<Vector2> pendingTap = new AtomicReference<>();

    // Built and used only on the game thread, once the surface has a size
    private Game game;
    private GameRenderer renderer;

    // --- Constructor(s)

    /**
     * Default constructor.
     */
    public GameSurfaceView(Context context)
    {
        super(context);

        this.context = context;
        preferences = context.getSharedPreferences("shapeship_preferences", Context.MODE_PRIVATE);
        typeface = Fonts.black(context);

        audio = Audio.get(context);
        audio.loadVolumes(preferences);

        loop = new GameLoop(getHolder(), scene);

        // Record taps for the game thread. The listener is set once, here on the UI thread.
        setOnTouchListener((view, event) ->
        {
            if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
            pendingTap.set(new Vector2(event.getX(), event.getY()));
            return true;
        });
    }

    // --- Method(s)

    /** Used to pause the view. */
    public void pause()
    {
        audio.pauseMusic();
        loop.stop();
    }

    /** Used to resume the view, if paused. */
    public void resume()
    {
        audio.startMusic();
        loop.start();
    }

    /** Frees the music player. Call when the game closes. */
    public void release()
    {
        audio.releaseMusic();
    }

    // --- The game, run by the loop on the game thread

    private final GameLoop.Scene scene = new GameLoop.Scene()
    {
        @Override
        public void prepare(final int width, final int height)
        {
            if (game != null) return;
            renderer = new GameRenderer(context, typeface, width, height);
            game = new Game(width, height, renderer.sizes(), new Random(), events);
        }

        @Override
        public void frame(final Canvas canvas, final float deltaTime)
        {
            final Vector2 tap = pendingTap.getAndSet(null);
            if (tap != null) game.tap(tap);
            game.update(deltaTime);
            renderer.draw(canvas, game);
        }
    };

    // --- What happens in the game, also on the game thread

    private final Game.Listener events = new Game.Listener()
    {
        @Override
        public void onShot()
        {
            audio.play(Sound.SHOOT);
        }

        @Override
        public void onExplosion()
        {
            audio.play(Sound.EXPLOSION);
        }

        @Override
        public void onPauseRequested()
        {
            post(() -> context.startActivity(new Intent(context, PauseActivity.class)));
        }

        @Override
        public void onGameOver(final int score)
        {
            // Stop the loop after this frame, which still draws
            loop.finish();

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
    };
}
