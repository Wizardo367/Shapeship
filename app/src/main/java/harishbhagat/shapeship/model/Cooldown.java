package harishbhagat.shapeship.model;

/**
 * Used to limit how often an action can happen, such as firing a cannon.
 * Times are in nanoseconds from System.nanoTime(), passed in so that tests can control the clock.
 */
public class Cooldown
{
    // --- Member(s)

    private final long periodNanos;
    private long lastUseNanos;
    private boolean used = false;

    // --- Constructor(s)

    /** Default constructor. The action is ready straight away. */
    public Cooldown(final long periodMillis)
    {
        this.periodNanos = periodMillis * 1000000L;
    }

    // --- Function(s)

    /** Returns true if the cooldown period has passed since the last use. */
    public boolean isReady(final long nowNanos)
    {
        // Subtracting first keeps this correct if nanoTime wraps around
        return !used || nowNanos - lastUseNanos >= periodNanos;
    }

    // --- Method(s)

    /** Used to record that the action happened, which starts a new cooldown period. */
    public void use(final long nowNanos)
    {
        lastUseNanos = nowNanos;
        used = true;
    }
}
