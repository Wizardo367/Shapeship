package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * The player's ship. It stays on screen, and fires on every tap: there's no cooldown and no
 * limit on bullets, which are removed once they leave the boundaries.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class Player extends Body
{
    // --- Member(s)

    /** The ship's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.13f;
    /** How far above the nose a bullet starts, in bullet heights. */
    static final float BULLET_GAP = 0.75f;

    public final BulletSpawner bulletSpawner; /** The player's bullets. */
    private final float screenWidth;

    // --- Constructor(s)

    /** Default constructor. The sizes are those of the ship's and the bullet's textures. */
    public Player(final Vector2 size, final Vector2 bulletSize, final RectangleArea boundaries, final float screenWidth)
    {
        super(size, false, ColliderShape.RECTANGLE, 0);
        this.screenWidth = screenWidth;
        final Vector2 margins = new Vector2(0, -BULLET_GAP * bulletSize.y);
        this.bulletSpawner = new BulletSpawner(this, new Bullet(bulletSize), margins, Integer.MAX_VALUE, boundaries);
    }

    // --- Function(s)

    /** Used to fire a bullet. */
    public void fire()
    {
        bulletSpawner.spawn();
    }

    /** Returns the nearest x position at which a body of the given width is wholly on screen. */
    static float clampToScreen(final float x, final float width, final float screenWidth)
    {
        final float halfWidth = width / 2;
        return Math.max(halfWidth, Math.min(x, screenWidth - halfWidth));
    }

    // --- Method(s)

    /** Overridden method that keeps the whole ship on screen. */
    @Override
    public void setPosition(final Vector2 position)
    {
        super.setPosition(new Vector2(clampToScreen(position.x, boundingBoxSize.x, screenWidth), position.y));
    }
}
