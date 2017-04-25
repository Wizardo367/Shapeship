package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Random;

import harishbhagat.shapeship.graphics.Axis2D;
import harishbhagat.shapeship.graphics.DisplayUtilities;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to spawn sprites in random locations within a set area.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class RandomSpawner extends Spawner
{
    // --- Member(s)

    private DisplayUtilities displayUtilities;

    // --- Constructor(s)

    /** Default constructor. */
    public RandomSpawner(final Sprite entity, final int maxSpawn, final RectangleArea boundaries, Context context)
    {
        super(entity, maxSpawn, boundaries, context);
        // Set the variables
        displayUtilities = new DisplayUtilities(context);
    }

    // --- Function(s)

    /** Used to spawn a new sprite. */
    public boolean spawn() throws CloneNotSupportedException
    {
        if (entities.size() < maxSpawn)
        {
            // Create a new entity
            Sprite newEntity = (Sprite) this.entity.clone();
            // Position the entity randomly
            newEntity.setPosition(getRandomPosition());
            // Add the entity
            this.entities.addElement(newEntity);
            return true;
        }
        return false;
    }

    /** Used to generate a random position to which a sprite may be spawned. */
    protected Vector2 getRandomPosition()
    {
        Random random = new Random();
        Vector2 position;

        while (true)
        {
            // Generate a random position
            final int x = (int) (random.nextInt((int)(Math.abs(boundaries.minX) + boundaries.maxX)) + boundaries.minX);
            final int y = (int) (random.nextInt((int)(Math.abs(boundaries.minY) + boundaries.maxY)) + boundaries.minY);
            position = new Vector2(x, y);
            // Validate the position
            if (validatePosition(position)) break;
        }

        return position;
    }

    /** Used to validate the random position generated. */
    private boolean validatePosition(final Vector2 position)
    {
        // Check that the new object won't intersect others when spawned

        // Get the size of the entity's bitmap
        Bitmap entityBitmap = this.entity.getBitmap();
        Vector2 entitySize = new Vector2(entityBitmap.getWidth(), entityBitmap.getHeight());

        // Check that the object has spawned outside of the viewing area
        final RectangleArea screenSize = displayUtilities.getScreenSize();
        if ((position.x >= screenSize.minX && position.x <= screenSize.maxX) || (position.y >= screenSize.minY && position.y <= screenSize.maxY))
            return false;

        // Check every existing entity
        for (Sprite entity : this.entities)
        {
            Vector2 entityPosition = entity.getPosition();
            if (position.x < entityPosition.x + entitySize.x &&
                    position.x + entitySize.x > entityPosition.x &&
                    position.y < entityPosition.y + entitySize.y &&
                    entitySize.y + position.y > entityPosition.y)
            {
                // Collision
                return false;
            }
        }

        // No collisions
        return true;
    }
}