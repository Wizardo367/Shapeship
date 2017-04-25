package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Vector;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Abstract class used for spawning new sprites.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public abstract class Spawner
{
    // --- Member(s)

    public Context context; /** Stores the context used by the class. */
    public int maxSpawn; /** Stores the maximum number of sprites that the spawner may manage at once. */
    public final Sprite entity; /** Stores the reference sprite used for spawning. */
    protected final RectangleArea boundaries; /** Defines the area in which sprites may be spawned. */
    public Vector<Sprite> entities; /** Stores the sprites that are currently being managed by the spawner. */

    // --- Constructor(s)

    /** Default constructor. */
    Spawner(final Sprite entity, final int maxSpawn, final RectangleArea boundaries, Context context)
    {
        // Set the variables
        this.boundaries = boundaries;
        this.context = context;
        this.entity = entity;
        this.entities = new Vector<Sprite>();
        this.maxSpawn = maxSpawn;
    }

    // --- Function(s)

    /** Used to retrieve the current amount of active sprites generated. */
    public int numberOfSpawned()
    {
        return entities.size();
    }

    /** Used to define how a new sprite should be spawned. */
    public abstract boolean spawn() throws CloneNotSupportedException;

    // --- Method(s)

    /** Used to set the maximum number of sprites the spawner may manage at once. */
    public void setMaxSpawn(final int maxSpawn)
    {
        this.maxSpawn = maxSpawn;
    }

    /** Used to update each entity. */
    public void update(final float deltaTime)
    {
        // Check if each entity is within the boundaries
        for (int i = 0; i < entities.size(); ++i)
        {
            Vector2 entityPosition = entities.elementAt(i).getPosition();

            if (entityPosition.x >= boundaries.minX && entityPosition.x < boundaries.maxX && entityPosition.y >= boundaries.minY && entityPosition.y < boundaries.maxY)
                // Update the entity
                entities.elementAt(i).update(deltaTime);
            else
                // The entity is out of bounds and needs to be deleted
                entities.remove(i);
        }
    }
}
