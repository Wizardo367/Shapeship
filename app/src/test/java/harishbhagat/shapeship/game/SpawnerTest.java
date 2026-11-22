package harishbhagat.shapeship.game;

import static harishbhagat.shapeship.TestSprites.circle;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

public class SpawnerTest
{
    private static Spawner spawner()
    {
        return new Spawner(circle(0, 0, 10), 10, new RectangleArea(0, 100, 0, 100), null)
        {
            @Override
            public boolean spawn()
            {
                return false;
            }
        };
    }

    @Test
    public void removesNeighbouringSpritesThatLeftTheBoundaries()
    {
        final Spawner spawner = spawner();
        final Sprite inside = circle(50, 50, 10);
        spawner.entities.add(circle(-5, 50, 10));
        // Before the fix this one was skipped, because it moved into the removed sprite's index
        spawner.entities.add(circle(50, 150, 10));
        spawner.entities.add(inside);

        spawner.update(1);

        assertEquals(1, spawner.numberOfSpawned());
        assertEquals(inside, spawner.entities.firstElement());
    }

    @Test
    public void updatesSpritesInsideTheBoundaries()
    {
        final Spawner spawner = spawner();
        final Sprite sprite = circle(50, 50, 10);
        sprite.setVelocity(new Vector2(0, 20));
        spawner.entities.add(sprite);

        spawner.update(0.5f);

        assertEquals(60, sprite.getPosition().y, 1e-4f);
    }
}
