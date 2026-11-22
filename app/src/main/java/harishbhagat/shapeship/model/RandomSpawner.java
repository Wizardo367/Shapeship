package harishbhagat.shapeship.model;

import java.util.Random;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

/**
 * Used to spawn bodies in random locations within a set area, out of sight of the player.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class RandomSpawner extends Spawner
{
    // --- Member(s)

    /** How many random positions to try before giving up on a spawn, rather than looping forever. */
    static final int MAX_ATTEMPTS = 50;

    protected final RectangleArea screen; /** The visible area. Bodies spawn wholly outside it. */
    protected final Random random; /** Source of the random positions. */

    // --- Constructor(s)

    /** Default constructor. */
    public RandomSpawner(final Body entity, final int maxSpawn, final RectangleArea boundaries, final RectangleArea screen, final Random random)
    {
        super(entity, maxSpawn, boundaries);
        this.screen = screen;
        this.random = random;
    }

    // --- Function(s)

    /** Used to spawn a new body. Returns false if the spawner is full or no free position was found. */
    @Override
    public boolean spawn()
    {
        if (entities.size() >= maxSpawn) return false;

        final Vector2 position = getRandomPosition();
        if (position == null) return false;

        final Body newEntity = entity.clone();
        newEntity.setPosition(position);
        entities.add(newEntity);
        return true;
    }

    /** Used to define the area in which bodies may be spawned. */
    protected RectangleArea getSpawnArea()
    {
        return boundaries;
    }

    /** Used to generate a random position to which a body may be spawned, or null if none was found. */
    protected Vector2 getRandomPosition()
    {
        final RectangleArea area = getSpawnArea();

        for (int attempt = 0; attempt < MAX_ATTEMPTS; ++attempt)
        {
            // Generate a random position
            final Vector2 position = new Vector2(randomBetween(area.minX, area.maxX), randomBetween(area.minY, area.maxY));
            // Validate the position
            if (validatePosition(position)) return position;
        }

        return null;
    }

    /** Returns a random value from min (inclusive) to max (exclusive). */
    private float randomBetween(final float min, final float max)
    {
        return min + random.nextFloat() * (max - min);
    }

    /** Used to validate the random position generated. Positions are body centres. */
    private boolean validatePosition(final Vector2 position)
    {
        final Vector2 entitySize = entity.boundingBoxSize;

        // Check that no part of the new body would be on screen
        final Vector2 halfSize = entitySize.divide(2);
        final RectangleArea visibleCentres = new RectangleArea(screen.minX - halfSize.x, screen.maxX + halfSize.x, screen.minY - halfSize.y, screen.maxY + halfSize.y);
        if (visibleCentres.contains(position))
            return false;

        // Check that the new body won't overlap an existing one
        for (Body other : entities)
        {
            final Vector2 otherPosition = other.getPosition();
            if (Math.abs(position.x - otherPosition.x) < entitySize.x && Math.abs(position.y - otherPosition.y) < entitySize.y)
                return false;
        }

        // No collisions
        return true;
    }
}
