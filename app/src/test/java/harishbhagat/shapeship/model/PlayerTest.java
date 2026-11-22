package harishbhagat.shapeship.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlayerTest
{
    private static final float DELTA = 1e-4f;

    @Test
    public void keepsTheWholeShipOnScreen()
    {
        // Before the fix, x = 10 with a 100-wide ship moved to 60, and x = -10 wasn't moved at all
        assertEquals(50, Player.clampToScreen(10, 100, 1000), DELTA);
        assertEquals(50, Player.clampToScreen(-10, 100, 1000), DELTA);
        assertEquals(950, Player.clampToScreen(990, 100, 1000), DELTA);
        assertEquals(950, Player.clampToScreen(1200, 100, 1000), DELTA);
    }

    @Test
    public void leavesPositionsOnScreenAlone()
    {
        assertEquals(400, Player.clampToScreen(400, 100, 1000), DELTA);
    }
}
