package harishbhagat.shapeship.game;

import static harishbhagat.shapeship.TestSprites.circle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Random;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

public class RandomSpawnerTest
{
    private static final RectangleArea SCREEN = new RectangleArea(0, 1000, 0, 2000);
    private static final RectangleArea BOUNDARIES = SCREEN.scale(1.2f);
    private static final float SIZE = 100;

    private static RandomSpawner spawner(final int maxSpawn, final RectangleArea boundaries)
    {
        return new RandomSpawner(circle(0, 0, SIZE), maxSpawn, boundaries, SCREEN, new Random(1), null);
    }

    /** True if any part of a sprite centred here would be on screen. */
    private static boolean visible(final Vector2 centre)
    {
        final float half = SIZE / 2;
        return centre.x > SCREEN.minX - half && centre.x < SCREEN.maxX + half
                && centre.y > SCREEN.minY - half && centre.y < SCREEN.maxY + half;
    }

    @Test
    public void spawnsOutOfSightAndAlongTheEdgesNotJustInTheCorners()
    {
        int besideOrAboveOrBelowTheScreen = 0;

        for (int i = 0; i < 500; ++i)
        {
            final Vector2 position = spawner(1, BOUNDARIES).getRandomPosition();
            assertFalse("visible at " + position.x + ", " + position.y, visible(position));
            assertTrue(BOUNDARIES.contains(position));

            // Before the fix only the corners were allowed, where neither coordinate is on screen
            if ((position.x >= SCREEN.minX && position.x <= SCREEN.maxX) || (position.y >= SCREEN.minY && position.y <= SCREEN.maxY))
                ++besideOrAboveOrBelowTheScreen;
        }

        assertTrue(besideOrAboveOrBelowTheScreen > 0);
    }

    @Test
    public void spawnedSpritesDoNotOverlap() throws CloneNotSupportedException
    {
        final RandomSpawner spawner = spawner(20, BOUNDARIES);
        while (spawner.spawn()) { }

        for (Sprite a : spawner.entities)
            for (Sprite b : spawner.entities)
                if (a != b)
                    assertTrue(Math.abs(a.getPosition().x - b.getPosition().x) >= SIZE || Math.abs(a.getPosition().y - b.getPosition().y) >= SIZE);
    }

    @Test
    public void stopsAtTheMaximum() throws CloneNotSupportedException
    {
        final RandomSpawner spawner = spawner(3, BOUNDARIES);
        assertTrue(spawner.spawn());
        assertTrue(spawner.spawn());
        assertTrue(spawner.spawn());
        assertFalse(spawner.spawn());
        assertEquals(3, spawner.numberOfSpawned());
    }

    @Test
    public void givesUpInsteadOfLoopingForeverWhenThereIsNoRoom() throws CloneNotSupportedException
    {
        // The whole spawn area is on screen, so no position is valid
        final RandomSpawner spawner = spawner(5, SCREEN);
        assertFalse(spawner.spawn());
        assertEquals(0, spawner.numberOfSpawned());
    }

    @Test
    public void asteroidsSpawnAboveTheirLimitAndFallTowardsThePlayer() throws CloneNotSupportedException
    {
        final AsteroidSpawner spawner = new AsteroidSpawner(circle(0, 0, SIZE), 7, BOUNDARIES, SCREEN, new Random(2), null);

        for (int round = 0; round < 50; ++round)
        {
            spawner.entities.clear();
            while (spawner.spawn()) { }

            for (Sprite asteroid : spawner.entities)
            {
                assertTrue(asteroid.getPosition().y <= SCREEN.maxY * AsteroidSpawner.MAX_SPAWN_HEIGHT);
                assertFalse(visible(asteroid.getPosition()));
                assertEquals(0, asteroid.getVelocity().x, 0);
                assertTrue(asteroid.getVelocity().y >= 50 && asteroid.getVelocity().y < 150);
            }
        }
    }
}
