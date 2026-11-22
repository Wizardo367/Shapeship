package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

/**
 * Used to spawn the bullets a body fires.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class BulletSpawner extends Spawner
{
    // --- Member(s)

    /** A bullet's speed when fired, in pixels per second. Negative is up the screen. */
    static final float SPEED = -500;

    private final Body shooter;
    private final Vector2 margins;

    // --- Constructor(s)

    /** Default constructor. Spawned bullets are copies of the given one. */
    public BulletSpawner(final Body shooter, final Body bullet, final Vector2 margins, final int maxSpawn, final RectangleArea boundaries)
    {
        super(bullet, maxSpawn, boundaries);
        this.margins = margins;
        this.shooter = shooter;
    }

    // --- Function(s)

    /** Used to spawn a new bullet. Returns false if too many are already on screen. */
    @Override
    public boolean spawn()
    {
        if (entities.size() >= maxSpawn) return false;

        // Start the bullet beyond the shooter's edge
        final Body newBullet = entity.clone();
        newBullet.setPosition(spawnPosition(shooter.getPosition(), shooter.boundingBoxSize, shooter.getRotation(), margins));
        newBullet.impulse(new Vector2(0, SPEED));
        entities.add(newBullet);
        return true;
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
