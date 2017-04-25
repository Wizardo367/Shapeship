package harishbhagat.shapeship.game;

import android.content.Context;
import android.util.Log;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to spawn bullet sprites.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class BulletSpawner extends Spawner
{
    // --- Member(s)

    private final Sprite shooter;
    private final Vector2 margins;

    // --- Constructor(s)

    /** Default constructor. */
    public BulletSpawner(final Sprite shooter, final Bullet bullet, final Vector2 margins, final int maxSpawn, final RectangleArea boundaries, Context context)
    {
        super(bullet, maxSpawn, boundaries, context);
        this.margins = margins;
        this.shooter = shooter;
    }

    // --- Function(s)

    /** Used to spawn new bullet objects. */
    public boolean spawn() throws CloneNotSupportedException
    {
        // Check the current number of spawned entities
        if (entities.size() < maxSpawn)
        {
            // Set the spawn position
            final Vector2 shooterPosition = shooter.getPosition();
            Vector2 spawnPosition = new Vector2(shooterPosition.x, shooterPosition.y);
            spawnPosition.y += margins.y + shooter.getBitmap().getHeight();
            // Rotate the bitmap to the direction of the player
            spawnPosition.rotate(shooter.getRotation());
            // Offset the vector
            spawnPosition.add(margins);
            // Create the bullet
            Bullet newBullet = (Bullet) this.entity.clone();
            newBullet.setPosition(spawnPosition);
            // Apply an impulse
            newBullet.impulse(new Vector2(0, -500));
            // Add the bullet
            this.entities.addElement(newBullet);
            return true;
        }
        return false;
    }
}
