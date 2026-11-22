package harishbhagat.shapeship.maths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

public class Vector2Test
{
    private static final float DELTA = 1e-4f;

    private static void assertVector(final float x, final float y, final Vector2 actual)
    {
        assertEquals("x", x, actual.x, DELTA);
        assertEquals("y", y, actual.y, DELTA);
    }

    @Test
    public void rotatesAQuarterTurn()
    {
        final Vector2 vector = new Vector2(1, 0);
        vector.rotate(90);
        // Before the fix this gave (0, 0), because y was computed from the already-rotated x
        assertVector(0, 1, vector);
    }

    @Test
    public void rotatesAHalfTurn()
    {
        final Vector2 vector = new Vector2(3, 4);
        vector.rotate(180);
        assertVector(-3, -4, vector);
    }

    @Test
    public void rotatingKeepsTheLength()
    {
        final Vector2 vector = new Vector2(3, 4);
        vector.rotate(37);
        assertEquals(5, vector.magnitude(), DELTA);
    }

    @Test
    public void rotatesBackwardsForANegativeAngle()
    {
        final Vector2 vector = new Vector2(0, 1);
        vector.rotate(-90);
        assertVector(1, 0, vector);
    }

    @Test
    public void scalesInPlace()
    {
        final Vector2 vector = new Vector2(2, -3);
        vector.scale(2.5f);
        assertVector(5, -7.5f, vector);
    }

    @Test
    public void angleBetweenIsMeasuredFromStraightDown()
    {
        final Vector2 origin = new Vector2(10, 10);
        assertEquals(0, origin.angleBetween(new Vector2(10, 50)), DELTA);
        assertEquals(90, origin.angleBetween(new Vector2(50, 10)), DELTA);
        assertEquals(-45, origin.angleBetween(new Vector2(0, 20)), DELTA);
    }

    @Test
    public void normalisesToLengthOne()
    {
        assertVector(0.6f, 0.8f, new Vector2(3, 4).normalise());
    }

    @Test
    public void equalVectorsAreEqualAsObjects()
    {
        // The 2016 equals(Vector2) was an overload, so Object.equals (identity) was used here
        final Object a = new Vector2(1.5f, -2);
        final Object b = new Vector2(1.5f, -2);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(new HashSet<>(Collections.singletonList(a)).contains(b));
    }

    @Test
    public void differentVectorsAreNotEqual()
    {
        final Vector2 vector = new Vector2(1, 2);
        assertNotEquals(vector, new Vector2(2, 1));
        assertNotEquals(vector, new Vector2(1, 2.001f));
        assertNotEquals(vector, null);
        assertNotEquals(vector, "(1.0, 2.0)");
    }
}
