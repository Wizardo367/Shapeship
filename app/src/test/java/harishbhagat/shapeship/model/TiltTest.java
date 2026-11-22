package harishbhagat.shapeship.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TiltTest
{
    private static final float DELTA = 1e-4f;

    /** The sideways reading for a tilt of the given number of degrees, right edge down. */
    private static float rightEdgeDown(final double degrees)
    {
        return (float) (-Tilt.GRAVITY * Math.sin(Math.toRadians(degrees)));
    }

    @Test
    public void aLevelPhoneDoesNotSteer()
    {
        assertEquals(0, Tilt.steering(0), DELTA);
        // Small wobbles inside the dead zone are ignored
        assertEquals(0, Tilt.steering(rightEdgeDown(2)), DELTA);
        assertEquals(0, Tilt.steering(rightEdgeDown(-2)), DELTA);
    }

    @Test
    public void tiltingRightSteersRightAndLeftSteersLeft()
    {
        final float right = Tilt.steering(rightEdgeDown(12));
        assertEquals(true, right > 0.3f && right < 0.7f);
        assertEquals(-right, Tilt.steering(rightEdgeDown(-12)), DELTA);
    }

    @Test
    public void steeringGrowsWithTheTiltUpToFullSpeed()
    {
        assertEquals(true, Tilt.steering(rightEdgeDown(8)) < Tilt.steering(rightEdgeDown(16)));
        assertEquals(1, Tilt.steering(rightEdgeDown(26)), DELTA);
        assertEquals(1, Tilt.steering(rightEdgeDown(90)), DELTA);
        assertEquals(-1, Tilt.steering(rightEdgeDown(-60)), DELTA);
        // A jolt reading more than gravity is still full speed
        assertEquals(1, Tilt.steering(-30), DELTA);
    }

    @Test
    public void readsTheScreensSidewaysAxisWhateverTheDevicesNaturalOrientation()
    {
        assertEquals(3, Tilt.sideways(0, 3, 7), DELTA);
        assertEquals(-7, Tilt.sideways(1, 3, 7), DELTA);
        assertEquals(-3, Tilt.sideways(2, 3, 7), DELTA);
        assertEquals(7, Tilt.sideways(3, 3, 7), DELTA);
    }
}
