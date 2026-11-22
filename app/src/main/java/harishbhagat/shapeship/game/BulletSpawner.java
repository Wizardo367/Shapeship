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
            // Start the bullet beyond the shooter's edge
            final Vector2 spawnPosition = spawnPosition(shooter.getPosition(), shooter.boundingBoxSize, shooter.getRotation(), margins);
            // Create the bullet
            Sprite newBullet = (Sprite) this.entity.clone();
            newBullet.setPosition(spawnPosition);
            // Apply an impulse
            newBullet.impulse(new Vector2(0, -500));
            // Add the bullet
            this.entities.addElement(newBullet);
            return true;
        }
        return false;
    }

    /**
     * Returns where a bullet starts: beyond the edge of the shooter that the margin points to
     * (up for a negative margin, down for a positive one), turned with the shooter.
     */
    static Vector2 spawnPosition(final Vector2 shooterPosition, final Vector2 shooterSize, final float shooterRotation, final Vector2 margins)
    {
        final Vector2 offset = new Vector2(margins.x, margins.y + Math.signum(margins.y) * shooterSize.y / 2);
        offset.rotate(shooterRotation);
        return shooterPosition.add(offset);
    }
}
