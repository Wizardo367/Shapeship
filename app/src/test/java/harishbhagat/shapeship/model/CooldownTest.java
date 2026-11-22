package harishbhagat.shapeship.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CooldownTest
{
    private static final long MS = 1000000L;

    @Test
    public void isReadyBeforeTheFirstUse()
    {
        assertTrue(new Cooldown(1000).isReady(0));
    }

    @Test
    public void waitsTheFullPeriodAfterEachUse()
    {
        // Before the fix the last shot was never recorded, so there was no cooldown at all
        final Cooldown cooldown = new Cooldown(1000);
        cooldown.use(5000 * MS);
        assertFalse(cooldown.isReady(5000 * MS));
        assertFalse(cooldown.isReady(5999 * MS));
        assertTrue(cooldown.isReady(6000 * MS));

        cooldown.use(6500 * MS);
        assertFalse(cooldown.isReady(7000 * MS));
    }

    @Test
    public void countsPartSecondsInsteadOfRoundingThemAway()
    {
        // Before the fix both times were rounded down to whole seconds, so 500 ms became 0
        final Cooldown cooldown = new Cooldown(500);
        cooldown.use(0);
        assertFalse(cooldown.isReady(499 * MS));
        assertTrue(cooldown.isReady(500 * MS));
    }

    @Test
    public void copesWithTheClockWrappingAround()
    {
        final Cooldown cooldown = new Cooldown(1000);
        cooldown.use(Long.MAX_VALUE - 100 * MS);
        assertFalse(cooldown.isReady(Long.MIN_VALUE + 100 * MS));
        assertTrue(cooldown.isReady(Long.MIN_VALUE + 900 * MS));
    }
}
