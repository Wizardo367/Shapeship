package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

/**
 * Used to spawn bullets: the ship's, or the UFOs'. There's no limit on how many are in flight;
 * each is removed once it leaves the boundaries.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class BulletSpawner extends Spawner
{
    // --- Member(s)

    private final Vector2 margins;
    private final Vector2 velocity;

    // --- Constructor(s)

    /**
     * Default constructor. Spawned bullets are copies of the given one, start beyond the
     * shooter's edge by the margins, and move at the given velocity in pixels per second.
     */
    public BulletSpawner(final Body bullet, final Vector2 margins, final Vector2 velocity, final RectangleArea boundaries)
    {
        super(bullet, Integer.MAX_VALUE, boundaries);
        this.margins = margins;
        this.velocity = velocity;
    }

    // --- Method(s)

    /** Used to fire a bullet from the given shooter. */
    public void spawn(final Body shooter)
    {
        final Body newBullet = entity.clone();
        newBullet.setPosition(spawnPosition(shooter.getPosition(), shooter.boundingBoxSize, shooter.getRotation(), margins));
        newBullet.setVelocity(new Vector2(velocity));
        entities.add(newBullet);
    }

    // --- Function(s)

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
