package harishbhagat.shapeship.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FrameTimerTest
{
    private static final float DELTA = 1e-6f;
    private static final long MS = 1_000_000L;

    @Test
    public void theFirstFrameHasNoDelta()
    {
        assertEquals(0, new FrameTimer(60).tick(5_000 * MS), DELTA);
    }

    @Test
    public void measuresSecondsBetweenFrames()
    {
        final FrameTimer timer = new FrameTimer(60);
        timer.tick(1_000 * MS);
        assertEquals(0.02f, timer.tick(1_020 * MS), DELTA);
        assertEquals(0.016f, timer.tick(1_036 * MS), DELTA);
    }

    @Test
    public void sleepsForTheRestOfTheFrame()
    {
        // The 2016 timer divided milliseconds by a million here, so it never slept
        final FrameTimer timer = new FrameTimer(50); // 20 ms frames
        timer.tick(1_000 * MS);
        assertEquals(15 * MS, timer.sleepNanos(1_005 * MS));
    }

    @Test
    public void doesNotSleepAfterASlowFrame()
    {
        final FrameTimer timer = new FrameTimer(50);
        timer.tick(1_000 * MS);
        assertEquals(0, timer.sleepNanos(1_030 * MS));
    }

    @Test
    public void capsTheDeltaAfterAStall()
    {
        final FrameTimer timer = new FrameTimer(60);
        timer.tick(0);
        assertEquals(FrameTimer.MAX_DELTA_SECONDS, timer.tick(2_000 * MS), DELTA);
    }

    @Test
    public void resetForgetsTimeSpentPaused()
    {
        // The 2016 timer kept its start time while paused, so the first frame after resuming
        // moved everything by the whole pause
        final FrameTimer timer = new FrameTimer(60);
        timer.tick(1_000 * MS);
        timer.reset();
        assertEquals(0, timer.tick(60_000 * MS), DELTA);
        assertEquals(0.01f, timer.tick(60_010 * MS), DELTA);
    }

    @Test
    public void workAcrossTheWrapOfNanoTime()
    {
        final FrameTimer timer = new FrameTimer(60);
        timer.tick(Long.MAX_VALUE - 5 * MS);
        assertEquals(0.01f, timer.tick(Long.MIN_VALUE + 5 * MS - 1), 1e-5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAZeroFrameRate()
    {
        new FrameTimer(0);
    }
}
