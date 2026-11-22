package harishbhagat.shapeship.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

/**
 * Abstract class used for managing copies of a body, which subclasses spawn, and removing them
 * once they leave the boundaries.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public abstract class Spawner
{
    // --- Member(s)

    public final int maxSpawn; /** The most bodies that the spawner manages at once. */
    public final Body entity; /** The body that spawned bodies are copies of. */
    protected final RectangleArea boundaries; /** Bodies outside this area are removed. */
    public final List<Body> entities = new ArrayList<>(); /** The bodies the spawner currently manages, oldest first. */

    // --- Constructor(s)

    /** Default constructor. */
    Spawner(final Body entity, final int maxSpawn, final RectangleArea boundaries)
    {
        this.boundaries = boundaries;
        this.entity = entity;
        this.maxSpawn = maxSpawn;
    }

    // --- Function(s)

    /** Returns the number of bodies the spawner currently manages. */
    public int numberOfSpawned()
    {
        return entities.size();
    }

    /** Returns the most recently spawned body. Call only after a spawn succeeds. */
    public Body newest()
    {
        return entities.get(entities.size() - 1);
    }

    // --- Method(s)

    /** Used to update each body, removing any that have left the boundaries. */
    public void update(final float deltaTime)
    {
        // Removing through the iterator keeps the loop from skipping the body after a removed one
        final Iterator<Body> iterator = entities.iterator();
        while (iterator.hasNext())
        {
            final Body entity = iterator.next();
            final Vector2 entityPosition = entity.getPosition();

            if (entityPosition.x >= boundaries.minX && entityPosition.x < boundaries.maxX && entityPosition.y >= boundaries.minY && entityPosition.y < boundaries.maxY)
                // Update the body
                entity.update(deltaTime);
            else
                // The body is out of bounds and needs to be deleted
                iterator.remove();
        }
    }
}
