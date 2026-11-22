package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * The player's ship. It stays on screen, and fires at most once a second.
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
    /** The most of the player's bullets on screen at once. */
    static final int MAX_BULLETS = 2;
    /** The shortest time between shots. */
    static final long COOLDOWN_MILLIS = 1000;

    public final BulletSpawner bulletSpawner; /** The player's bullets. */
    private final Cooldown cannonCooldown = new Cooldown(COOLDOWN_MILLIS);
    private final float screenWidth;

    // --- Constructor(s)

    /** Default constructor. The sizes are those of the ship's and the bullet's textures. */
    public Player(final Vector2 size, final Vector2 bulletSize, final RectangleArea boundaries, final float screenWidth)
    {
        super(size, false, ColliderShape.RECTANGLE, 0);
        this.screenWidth = screenWidth;
        final Vector2 margins = new Vector2(0, -BULLET_GAP * bulletSize.y);
        this.bulletSpawner = new BulletSpawner(this, new Bullet(bulletSize), margins, MAX_BULLETS, boundaries);
    }

    // --- Function(s)

    /** Used to fire a bullet, given the time from System.nanoTime(). Returns true if a bullet was fired. */
    public boolean fire(final long nowNanos)
    {
        // Wait for the cooldown, and for a bullet to leave if too many are on screen
        if (!cannonCooldown.isReady(nowNanos) || !bulletSpawner.spawn()) return false;

        cannonCooldown.use(nowNanos);
        return true;
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
