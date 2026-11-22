package harishbhagat.shapeship.game;

import android.content.Context;

import java.util.Random;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to spawn sprites in random locations within a set area, out of sight of the player.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class RandomSpawner extends Spawner
{
    // --- Member(s)

    /** How many random positions to try before giving up on a spawn, rather than looping forever. */
    static final int MAX_ATTEMPTS = 50;

    protected final RectangleArea screen; /** The visible area. Sprites spawn wholly outside it. */
    protected final Random random; /** Source of the random positions. */

    // --- Constructor(s)

    /** Default constructor. */
    public RandomSpawner(final Sprite entity, final int maxSpawn, final RectangleArea boundaries, final RectangleArea screen, final Random random, Context context)
    {
        super(entity, maxSpawn, boundaries, context);
        // Set the variables
        this.screen = screen;
        this.random = random;
    }

    // --- Function(s)

    /** Used to spawn a new sprite. Returns false if the spawner is full or no free position was found. */
    public boolean spawn() throws CloneNotSupportedException
    {
        if (entities.size() >= maxSpawn) return false;

        final Vector2 position = getRandomPosition();
        if (position == null) return false;

        // Create a new entity
        Sprite newEntity = (Sprite) this.entity.clone();
        newEntity.setPosition(position);
        // Add the entity
        this.entities.addElement(newEntity);
        return true;
    }

    /** Used to define the area in which sprites may be spawned. */
    protected RectangleArea getSpawnArea()
    {
        return boundaries;
    }

    /** Used to generate a random position to which a sprite may be spawned, or null if none was found. */
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

    /** Used to validate the random position generated. Positions are sprite centres. */
    private boolean validatePosition(final Vector2 position)
    {
        final Vector2 entitySize = this.entity.boundingBoxSize;

        // Check that no part of the new sprite would be on screen
        final Vector2 halfSize = entitySize.divide(2);
        final RectangleArea visibleCentres = new RectangleArea(screen.minX - halfSize.x, screen.maxX + halfSize.x, screen.minY - halfSize.y, screen.maxY + halfSize.y);
        if (visibleCentres.contains(position))
            return false;

        // Check that the new sprite won't overlap an existing one
        for (Sprite entity : this.entities)
        {
            final Vector2 entityPosition = entity.getPosition();
            if (Math.abs(position.x - entityPosition.x) < entitySize.x && Math.abs(position.y - entityPosition.y) < entitySize.y)
                return false;
        }

        // No collisions
        return true;
    }
}
