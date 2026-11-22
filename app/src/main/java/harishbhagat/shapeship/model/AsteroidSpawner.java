package harishbhagat.shapeship.model;

import java.util.Random;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

/**
 * Used to spawn asteroids randomly, above the player and out of sight.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class AsteroidSpawner extends RandomSpawner
{
    // --- Member(s)

    /** Asteroids spawn no lower than this fraction of the screen height, so none appear beside the player. */
    static final float MAX_SPAWN_HEIGHT = 0.6f;

    // --- Constructor(s)

    /** Default constructor. Spawned asteroids are copies of the given one. */
    public AsteroidSpawner(final Body asteroid, final int maxSpawn, final RectangleArea boundaries, final RectangleArea screen, final Random random)
    {
        super(asteroid, maxSpawn, boundaries, screen, random);
    }

    // --- Function(s)

    /** Overridden method for spawning asteroids. */
    @Override
    public boolean spawn()
    {
        if (!super.spawn()) return false;

        // Make the asteroid fall at between 50 and 150 pixels per second
        newest().setVelocity(new Vector2(0, random.nextInt(100) + 50));
        return true;
    }

    /** Overridden method that keeps asteroids from spawning beside or below the player. */
    @Override
    protected RectangleArea getSpawnArea()
    {
        final RectangleArea area = super.getSpawnArea();
        return new RectangleArea(area.minX, area.maxX, area.minY, Math.min(area.maxY, screen.maxY * MAX_SPAWN_HEIGHT));
    }
}
