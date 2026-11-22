package harishbhagat.shapeship.physics;

import static harishbhagat.shapeship.TestBodies.circle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import harishbhagat.shapeship.maths.Vector2;

public class BodyTest
{
    private static final float DELTA = 1e-3f;

    @Test
    public void clonesDoNotShareStateWithTheOriginal()
    {
        final Body original = circle(10, 20, 100);
        original.setVelocity(new Vector2(0, 50));

        final Body copy = original.clone();
        copy.getPosition().x = 99;
        copy.getVelocity().rotate(90);
        copy.boundingBoxSize.x = 1;

        assertNotSame(original, copy);
        assertEquals(10, original.getPosition().x, DELTA);
        assertEquals(0, original.getVelocity().x, DELTA);
        assertEquals(50, original.getVelocity().y, DELTA);
        assertEquals(100, original.boundingBoxSize.x, DELTA);
    }

    @Test
    public void clonesKeepTheirSubclass()
    {
        final Body original = new Body(new Vector2(10, 10), true, ColliderShape.CIRCLE, 5) {};
        assertEquals(original.getClass(), original.clone().getClass());
    }

    @Test
    public void takesACopyOfItsSize()
    {
        final Vector2 size = new Vector2(30, 40);
        final Body body = new Body(size, false, ColliderShape.RECTANGLE, 1);
        size.x = 0;
        assertEquals(30, body.boundingBoxSize.x, DELTA);
    }

    @Test
    public void setTargetTurnsTheVelocityTowardsTheTarget()
    {
        final Body asteroid = circle(100, 0, 50);
        asteroid.setVelocity(new Vector2(0, 100));

        asteroid.setTarget(new Vector2(400, 400));

        // The target is 300 across and 400 down, so the velocity points along (0.6, 0.8) at the same speed
        final Vector2 velocity = asteroid.getVelocity();
        assertEquals(60, velocity.x, DELTA);
        assertEquals(80, velocity.y, DELTA);
    }

    @Test
    public void updateMovesAPhysicsBodyByItsVelocity()
    {
        final Body body = circle(0, 0, 10);
        body.setVelocity(new Vector2(30, -60));
        body.update(0.5f);
        assertEquals(new Vector2(15, -30), body.getPosition());
    }

    @Test
    public void updateLeavesOtherBodiesWhereTheyAre()
    {
        final Body body = new Body(new Vector2(10, 10), false, ColliderShape.RECTANGLE, 0);
        body.setVelocity(new Vector2(30, -60));
        body.update(0.5f);
        assertEquals(new Vector2(0, 0), body.getPosition());
    }

    @Test
    public void rotatesEitherWay()
    {
        // The 2016 Sprite class ignored turns of zero degrees or less
        final Body body = circle(0, 0, 10);
        body.rotate(30);
        body.rotate(-50);
        assertEquals(340, body.getRotation(), DELTA);
    }

    @Test
    public void setRotationClampsToAQuarterTurn()
    {
        final Body body = circle(0, 0, 10);
        body.setRotation(120);
        assertEquals(90, body.getRotation(), DELTA);
    }
}
