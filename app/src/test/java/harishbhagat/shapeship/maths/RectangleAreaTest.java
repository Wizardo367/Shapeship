package harishbhagat.shapeship.maths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RectangleAreaTest
{
    private static final float DELTA = 1e-4f;

    private static void assertArea(final float minX, final float maxX, final float minY, final float maxY, final RectangleArea actual)
    {
        assertEquals("minX", minX, actual.minX, DELTA);
        assertEquals("maxX", maxX, actual.maxX, DELTA);
        assertEquals("minY", minY, actual.minY, DELTA);
        assertEquals("maxY", maxY, actual.maxY, DELTA);
    }

    @Test
    public void scalingLeavesTheOriginalUnchanged()
    {
        final RectangleArea screen = new RectangleArea(0, 1000, 0, 2000);
        screen.scale(1.2f);
        assertArea(0, 1000, 0, 2000, screen);
    }

    @Test
    public void scalesAboutTheCentre()
    {
        assertArea(-100, 1100, -200, 2200, new RectangleArea(0, 1000, 0, 2000).scale(1.2f));
    }

    @Test
    public void scalesAboutTheCentreWhenNotAtTheOrigin()
    {
        // Before the fix the change in size was worked out from maxX alone, so the width grew by 150 instead of 100
        assertArea(50, 350, 62.5f, 137.5f, new RectangleArea(100, 300, 75, 125).scale(1.5f));
    }

    @Test
    public void containsPointsInsideAndOnTheEdges()
    {
        final RectangleArea area = new RectangleArea(0, 10, 0, 20);
        assertTrue(area.contains(new Vector2(5, 5)));
        assertTrue(area.contains(new Vector2(0, 20)));
        assertFalse(area.contains(new Vector2(11, 5)));
        assertFalse(area.contains(new Vector2(5, -1)));
    }
}
