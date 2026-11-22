package harishbhagat.shapeship.game;

/**
 * Measures the time between frames and how long to sleep to hold a target frame rate.
 *
 * Times are System.nanoTime() values, passed in so that tests can supply their own.
 */
final class FrameTimer
{
    // --- Member(s)

    /** The longest step the game takes in one frame, so that a stall (such as a garbage collection) doesn't move sprites through each other. */
    static final float MAX_DELTA_SECONDS = 0.1f;

    private static final float NANOS_PER_SECOND = 1_000_000_000f;

    private final long targetFrameNanos;
    private long frameStart;
    private boolean started = false;

    // --- Constructor(s)

    FrameTimer(final int targetFps)
    {
        if (targetFps <= 0) throw new IllegalArgumentException("targetFps must be positive");
        this.targetFrameNanos = (long) (NANOS_PER_SECOND / targetFps);
    }

    // --- Function(s)

    /**
     * Starts a frame and returns the seconds since the previous one started: 0 for the first
     * frame after a reset, and never more than MAX_DELTA_SECONDS.
     */
    float tick(final long nowNanos)
    {
        final float delta = started ? (nowNanos - frameStart) / NANOS_PER_SECOND : 0;
        frameStart = nowNanos;
        started = true;
        return Math.max(0, Math.min(delta, MAX_DELTA_SECONDS));
    }

    /** Returns how many nanoseconds to sleep so that the current frame lasts the target time; 0 if it already has. */
    long sleepNanos(final long nowNanos)
    {
        if (!started) return 0;
        return Math.max(0, targetFrameNanos - (nowNanos - frameStart));
    }

    // --- Method(s)

    /** Forgets the previous frame, so that time spent paused or waiting for the surface isn't counted. */
    void reset()
    {
        started = false;
    }
}
