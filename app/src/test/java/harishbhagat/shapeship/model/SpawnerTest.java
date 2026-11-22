package harishbhagat.shapeship.model;

import static harishbhagat.shapeship.TestBodies.circle;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

public class SpawnerTest
{
    private static Spawner spawner()
    {
        return new Spawner(circle(0, 0, 10), 10, new RectangleArea(0, 100, 0, 100)) {};
    }

    @Test
    public void removesNeighbouringBodiesThatLeftTheBoundaries()
    {
        final Spawner spawner = spawner();
        final Body inside = circle(50, 50, 10);
        spawner.entities.add(circle(-5, 50, 10));
        // Before the fix this one was skipped, because it moved into the removed body's index
        spawner.entities.add(circle(50, 150, 10));
        spawner.entities.add(inside);

        spawner.update(1);

        assertEquals(1, spawner.numberOfSpawned());
        assertEquals(inside, spawner.entities.get(0));
    }

    @Test
    public void updatesBodiesInsideTheBoundaries()
    {
        final Spawner spawner = spawner();
        final Body body = circle(50, 50, 10);
        body.setVelocity(new Vector2(0, 20));
        spawner.entities.add(body);

        spawner.update(0.5f);

        assertEquals(60, body.getPosition().y, 1e-4f);
    }
}
