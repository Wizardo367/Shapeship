package harishbhagat.shapeship.game;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Process;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

/**
 * Runs the game on its own thread. It waits until the surface has a size, then runs one frame
 * at a time at the target frame rate: the scene updates and draws onto a locked canvas.
 */
final class GameLoop implements Runnable, SurfaceHolder.Callback
{
    // --- Member(s)

    /** What the loop runs. Both methods are called on the game thread. */
    interface Scene
    {
        /** Called before each frame with the surface's size in pixels, so that the scene can build itself the first time. */
        void prepare(int width, int height);

        /** Moves the scene on by the given number of seconds and draws it. */
        void frame(Canvas canvas, float deltaTime);
    }

    private static final int TARGET_FPS = 60;
    /** How long to wait before checking again for a surface to draw on. */
    private static final long SURFACE_WAIT_MILLIS = 16;

    private final SurfaceHolder holder;
    private final Scene scene;
    private final FrameTimer frameTimer = new FrameTimer(TARGET_FPS);

    // Written by the UI thread and read by the game thread every frame, so volatile
    private volatile boolean running = false;
    private volatile int surfaceWidth = 0;
    private volatile int surfaceHeight = 0;
    private Thread thread = null;

    // --- Constructor(s)

    GameLoop(final SurfaceHolder holder, final Scene scene)
    {
        this.holder = holder;
        this.scene = scene;
        // A SurfaceView draws in 16-bit colour (RGB 565) unless asked otherwise, which bands the
        // dark background into green and purple patches. The menus draw in 32-bit colour, so
        // match them. The game draws every pixel, so the surface needs no alpha channel.
        holder.setFormat(PixelFormat.RGBX_8888);
        holder.addCallback(this);
    }

    // --- Method(s)

    /** Starts the game thread. Call on the UI thread. */
    void start()
    {
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    /** Stops the game thread and waits for it to finish. Call on the UI thread. */
    void stop()
    {
        running = false;

        if (thread == null) return;
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

    /** Stops the loop after the current frame. Safe to call from the game thread. */
    void finish()
    {
        running = false;
    }

    @Override
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

            scene.prepare(width, height);

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
                scene.frame(canvas, deltaTime);
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
