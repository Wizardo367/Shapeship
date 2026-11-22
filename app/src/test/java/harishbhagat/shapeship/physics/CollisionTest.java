package harishbhagat.shapeship.physics;

import static harishbhagat.shapeship.TestBodies.circle;
import static harishbhagat.shapeship.TestBodies.rectangle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

import harishbhagat.shapeship.maths.Vector2;

public class CollisionTest
{
    private static final float DELTA = 1e-4f;

    private static CollisionInfo test(final Body body, final Body otherBody)
    {
        return Collision.HasCollidedWith(body, otherBody);
    }

    private static void assertVector(final float x, final float y, final Vector2 actual)
    {
        assertEquals("x", x, actual.x, DELTA);
        assertEquals("y", y, actual.y, DELTA);
    }

    // --- Rectangles

    @Test
    public void rectanglesCollideUsingTheirCentres()
    {
        // Both 100 wide, centres 90 apart: they overlap by 10 on each side of the gap.
        // Before the fix positions were treated as top-left corners, so this needed the
        // second box to be within 100 to the right but missed boxes to the left.
        assertTrue(test(rectangle(500, 500, 100, 100), rectangle(410, 500, 100, 100)).collision);
        assertTrue(test(rectangle(500, 500, 100, 100), rectangle(590, 500, 100, 100)).collision);
    }

    @Test
    public void rectanglesThatOnlyTouchDoNotCollide()
    {
        assertFalse(test(rectangle(0, 0, 100, 100), rectangle(100, 0, 100, 100)).collision);
        assertFalse(test(rectangle(0, 0, 100, 100), rectangle(0, 150, 100, 200)).collision);
    }

    @Test
    public void rectangleNormalPointsAlongTheSmallerOverlap()
    {
        final CollisionInfo info = test(rectangle(0, 0, 100, 100), rectangle(20, -90, 100, 100));
        assertTrue(info.collision);
        assertEquals(10, info.penetration, DELTA);
        assertVector(0, -1, info.collisionNormal);
    }

    // --- Circles

    @Test
    public void circlesCollideWhenCloserThanTheirRadii()
    {
        final CollisionInfo info = test(circle(0, 0, 100), circle(0, 90, 100));
        assertTrue(info.collision);
        // Before the fix the penetration subtracted a distance from a squared distance
        assertEquals(10, info.penetration, DELTA);
        assertVector(0, 1, info.collisionNormal);
    }

    @Test
    public void circlesThatOnlyTouchDoNotCollide()
    {
        assertFalse(test(circle(0, 0, 100), circle(100, 0, 100)).collision);
    }

    @Test
    public void circlesInTheSamePlaceCollide()
    {
        final CollisionInfo info = test(circle(5, 5, 100), circle(5, 5, 60));
        assertTrue(info.collision);
        assertEquals(80, info.penetration, DELTA);
    }

    // --- Rectangle and circle

    @Test
    public void circleBesideARectangleCollides()
    {
        // Rectangle edge at x = 50, circle reaching to x = 40
        final CollisionInfo info = test(rectangle(0, 0, 100, 100), circle(90, 0, 100));
        assertTrue(info.collision);
        assertEquals(10, info.penetration, DELTA);
        assertVector(1, 0, info.collisionNormal);
    }

    @Test
    public void normalPointsFromTheFirstBodyWhicheverShapeItIs()
    {
        final CollisionInfo info = test(circle(90, 0, 100), rectangle(0, 0, 100, 100));
        assertTrue(info.collision);
        assertVector(-1, 0, info.collisionNormal);
    }

    @Test
    public void circleNearACornerButOutOfReachDoesNotCollide()
    {
        // The bounding boxes overlap, but the corner (50, 50) is about 56.6 from the circle's centre
        assertFalse(test(rectangle(0, 0, 100, 100), circle(90, 90, 100)).collision);
    }

    @Test
    public void circleWithItsCentreInsideARectangleIsPushedOutThroughTheNearestSide()
    {
        final CollisionInfo info = test(rectangle(0, 0, 100, 100), circle(0, 40, 20));
        assertTrue(info.collision);
        assertEquals(20, info.penetration, DELTA);
        assertVector(0, 1, info.collisionNormal);
    }

    // --- Points

    @Test
    public void pointInsideABodyIntersects()
    {
        final Body button = rectangle(900, 100, 80, 80);
        assertTrue(Collision.PointAABBIntersectionTest(button, new Vector2(910, 90)));
        assertTrue(Collision.PointAABBIntersectionTest(button, new Vector2(940, 140)));
    }

    @Test
    public void pointLevelWithABodyButOutsideItDoesNotIntersect()
    {
        // Before the fix, tapping anywhere in the button's row or column counted as a hit
        final Body button = rectangle(900, 100, 80, 80);
        assertFalse(Collision.PointAABBIntersectionTest(button, new Vector2(200, 100)));
        assertFalse(Collision.PointAABBIntersectionTest(button, new Vector2(900, 1500)));
    }

    // --- Resolving

    @Test
    public void headOnCollisionReversesBothBodiesWithRestitution()
    {
        final Body a = circle(0, 0, 100);
        final Body b = circle(90, 0, 100);
        a.setVelocity(new Vector2(10, 0));
        b.setVelocity(new Vector2(-10, 0));

        assertTrue(Collision.Collide(a, b, test(a, b)));

        // Equal masses and restitution 0.8: each bounces back at 0.8 of its speed
        assertVector(-8, 0, a.getVelocity());
        assertVector(8, 0, b.getVelocity());
    }

    @Test
    public void bodiesAlreadyMovingApartAreLeftAlone()
    {
        final Body a = circle(0, 0, 100);
        final Body b = circle(90, 0, 100);
        a.setVelocity(new Vector2(-10, 0));
        b.setVelocity(new Vector2(10, 0));

        assertFalse(Collision.Collide(a, b, test(a, b)));
        assertVector(-10, 0, a.getVelocity());
    }

    @Test
    public void resolveAllResolvesEachCollidingPairOnce()
    {
        // Two asteroids closing on each other, and one well away from both
        final Body left = circle(0, 0, 100);
        final Body right = circle(90, 0, 100);
        final Body far = circle(1000, 1000, 100);
        left.setVelocity(new Vector2(50, 0));
        right.setVelocity(new Vector2(-50, 0));

        assertEquals(1, Collision.ResolveAll(Arrays.asList(left, right, far)));

        // Equal masses with restitution 0.8 swap 80% of their speed: they now move apart
        assertVector(-40, 0, left.getVelocity());
        assertVector(40, 0, right.getVelocity());
        assertVector(0, 0, far.getVelocity());
    }

    @Test
    public void resolveAllLeavesBodiesMovingApartAlone()
    {
        final Body left = circle(0, 0, 100);
        final Body right = circle(90, 0, 100);
        left.setVelocity(new Vector2(-50, 0));
        right.setVelocity(new Vector2(50, 0));

        assertEquals(0, Collision.ResolveAll(Arrays.asList(left, right)));
        assertVector(-50, 0, left.getVelocity());
    }
}
