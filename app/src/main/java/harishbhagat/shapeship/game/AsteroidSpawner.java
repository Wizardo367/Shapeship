package harishbhagat.shapeship.game;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import harishbhagat.shapeship.graphics.DisplayUtilities;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to spawn asteroid sprites randomly.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class AsteroidSpawner extends RandomSpawner
{
    // --- Constructor(s)

    /** Default constructor. */
    public AsteroidSpawner(Context context, final int maxSpawn, final RectangleArea boundaries)
    {
        super(new Asteroid(context), maxSpawn, boundaries, context);
    }

    // --- Function(s)

    /** Overridden method for spawning asteroid sprites. */
    @Override
    public boolean spawn() throws CloneNotSupportedException
    {
        DisplayUtilities displayUtilities = new DisplayUtilities(context);

        // Spawn asteroid
        if (entities.size() < maxSpawn)
        {
            // Create a new entity
            Sprite newEntity = (Sprite) this.entity.clone();

            // Position the entity randomly
            newEntity.setPosition(getRandomPosition());
            Vector2 entityPosition = newEntity.getPosition();

            // Ensure nothing spawns below the player
            final float maxSpawnY = displayUtilities.getScreenSize().maxY * 0.6f;
            if (entityPosition.y > maxSpawnY) entityPosition.y = maxSpawnY;

            // Add an impulse to the asteroid
            Random random = new Random();
            // Make the impulse between 50 and 150
            int randomY = random.nextInt(100) + 50;
            // Apply the impulse
            newEntity.setVelocity(new Vector2(0, randomY));

            // Add the entity
            this.entities.addElement(newEntity);

            return true;
        }
        return false;
    }
}