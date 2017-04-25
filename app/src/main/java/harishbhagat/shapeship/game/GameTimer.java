package harishbhagat.shapeship.game;

import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Singleton class used for retrieving and managing the time between frames.
 *
 * Created by Harish Bhagat on 01/04/2016.
 */
public class GameTimer
{
    // --- Member(s)

    private static GameTimer gameTimer = new GameTimer();
    private static Thread thread;

    private static float frameTime = 0;
    private static long timeStart = System.currentTimeMillis();
    private static int targetFPS = 30;

    // --- Constructor(s)

    /** Default constructor. */
    private GameTimer(){}

    // --- Function(s)

    /** Used to retrieve the time difference between the previous and current frames. */
    protected static float getDeltaTime()
    {
        // Check for null
        if (thread == null) return 0;

        // Get deltatime
        final long timeEnd = System.currentTimeMillis();
        frameTime = timeEnd - timeStart;
        timeStart = System.currentTimeMillis();
        // Sleep to achieve target FPS
        sleep((long) ((1000 / targetFPS) - frameTime));
        // Return deltatime as seconds
        return frameTime / 1000;
    }

    /** Used to retrieve the only instance of this class. */
    public static GameTimer getInstance()
    {
        return gameTimer;
    }

     // --- Method(s)

    /** Used to set a target for the amount of frames per second. */
    protected static void setTargetFPS(final int newTarget)
    {
        targetFPS = newTarget;
    }

    /** Used to set the thread which the timer controls. */
    protected static void setThread(Thread newThread)
    {
        thread = newThread;
    }

    /** Used to make the controlled thread sleep for a defined period of time. */
    protected static void sleep(final long milliseconds)
    {
        try
        {
            final long nanoseconds = milliseconds / 1000000;
            if (nanoseconds > 0) thread.sleep(nanoseconds);
        }
        catch (InterruptedException e) {}
    }
}
