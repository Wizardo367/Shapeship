package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * The player's ship. Tilting the phone steers it along the bottom of the screen, and it fires
 * on every tap. It always stays wholly on screen.
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
    /** A bullet's speed in pixels per second. Negative is up the screen. */
    static final float BULLET_SPEED = -500;
    /** The ship's speed at full tilt, in screen widths per second. */
    static final float MAX_SPEED = 1.2f;

    public final BulletSpawner bulletSpawner; /** The player's bullets. */
    private final float screenWidth;

    // --- Constructor(s)

    /** Default constructor. The sizes are those of the ship's and the bullet's textures. */
    public Player(final Vector2 size, final Vector2 bulletSize, final RectangleArea boundaries, final float screenWidth)
    {
        super(size, false, ColliderShape.RECTANGLE, 0);
        this.screenWidth = screenWidth;
        final Vector2 margins = new Vector2(0, -BULLET_GAP * bulletSize.y);
        this.bulletSpawner = new BulletSpawner(new Bullet(bulletSize), margins, new Vector2(0, BULLET_SPEED), boundaries);
    }

    // --- Function(s)

    /** Returns the nearest x position at which a body of the given width is wholly on screen. */
    static float clampToScreen(final float x, final float width, final float screenWidth)
    {
        final float halfWidth = width / 2;
        return Math.max(halfWidth, Math.min(x, screenWidth - halfWidth));
    }

    // --- Method(s)

    /** Used to fire a bullet. */
    public void fire()
    {
        bulletSpawner.spawn(this);
    }

    /**
     * Used to move the ship sideways for the given number of seconds. The tilt runs from -1
     * (full speed left) to 1 (full speed right); see Tilt.
     */
    public void steer(final float tilt, final float deltaTime)
    {
        final float speed = Math.max(-1, Math.min(tilt, 1)) * MAX_SPEED * screenWidth;
        setPosition(new Vector2(getPosition().x + speed * deltaTime, getPosition().y));
    }

    /** Overridden method that keeps the whole ship on screen. */
    @Override
    public void setPosition(final Vector2 position)
    {
        super.setPosition(new Vector2(clampToScreen(position.x, boundingBoxSize.x, screenWidth), position.y));
    }
}
