package harishbhagat.shapeship.maths;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AngleTest
{
    private static final float DELTA = 1e-4f;

    @Test
    public void wrapsNegativeAnglesTheRightWayRound()
    {
        // Before the fix, -90 became 90
        assertEquals(270, Angle.Normalise360(-90), DELTA);
        assertEquals(350, Angle.Normalise360(-370), DELTA);
    }

    @Test
    public void wrapsAnglesOfAFullTurnOrMore()
    {
        assertEquals(0, Angle.Normalise360(360), DELTA);
        assertEquals(45, Angle.Normalise360(765), DELTA);
    }

    @Test
    public void leavesAnglesInRangeAlone()
    {
        assertEquals(0, Angle.Normalise360(0), DELTA);
        assertEquals(359.5f, Angle.Normalise360(359.5f), DELTA);
    }

    @Test
    public void clampsToAQuarterTurnEitherWay()
    {
        assertEquals(90, Angle.Normalise180(120), DELTA);
        assertEquals(-90, Angle.Normalise180(-200), DELTA);
        assertEquals(30, Angle.Normalise180(30), DELTA);
    }
}
