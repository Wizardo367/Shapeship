package harishbhagat.shapeship.graphics;

import static harishbhagat.shapeship.TestSprites.circle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import harishbhagat.shapeship.maths.Vector2;

public class SpriteTest
{
    private static final float DELTA = 1e-3f;

    @Test
    public void clonesDoNotShareStateWithTheOriginal() throws CloneNotSupportedException
    {
        final Sprite original = circle(10, 20, 100);
        original.setVelocity(new Vector2(0, 50));

        final Sprite copy = (Sprite) original.clone();
        copy.getPosition().x = 99;
        copy.getVelocity().rotate(90);
        copy.boundingBoxSize.x = 1;

        assertNotSame(original.getMatrix(), copy.getMatrix());
        assertEquals(10, original.getPosition().x, DELTA);
        assertEquals(0, original.getVelocity().x, DELTA);
        assertEquals(50, original.getVelocity().y, DELTA);
        assertEquals(100, original.boundingBoxSize.x, DELTA);
    }

    @Test
    public void setTargetTurnsTheVelocityTowardsTheTarget()
    {
        final Sprite asteroid = circle(100, 0, 50);
        asteroid.setVelocity(new Vector2(0, 100));

        asteroid.setTarget(new Vector2(400, 400), 1 / 30f);

        // The target is 300 across and 400 down, so the velocity points along (0.6, 0.8) at the same speed
        final Vector2 velocity = asteroid.getVelocity();
        assertEquals(60, velocity.x, DELTA);
        assertEquals(80, velocity.y, DELTA);
    }

    @Test
    public void updateMovesAPhysicsSpriteByItsVelocity()
    {
        final Sprite sprite = circle(0, 0, 10);
        sprite.setVelocity(new Vector2(30, -60));
        sprite.update(0.5f);
        assertEquals(15, sprite.getPosition().x, DELTA);
        assertEquals(-30, sprite.getPosition().y, DELTA);
    }
}
