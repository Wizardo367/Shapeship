package harishbhagat.shapeship.game;

import android.graphics.Bitmap;
import android.util.Log;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.Angle;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Used to define a player sprite.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class Player extends Sprite implements Cannon
{
    // --- Member(s)

    /** The ship's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.13f;
    /** How far above the nose a bullet starts, in bullet heights. */
    static final float BULLET_GAP = 0.75f;

    public BulletSpawner bulletSpawner; /** Bullet spawner for the player */
    private final Cooldown cannonCooldown = new Cooldown(1000); // Measured in milliseconds
    private final float screenWidth;

    // --- Constructor(s)

    /** Default constructor. The textures are drawn at the size they appear (see Textures). */
    public Player(final Bitmap texture, final Bitmap bulletTexture, final RectangleArea boundaries, final float screenWidth)
    {
        super(texture, false, ColliderShape.RECTANGLE, 0);
        final Vector2 margins = new Vector2(0, -BULLET_GAP * bulletTexture.getHeight());
        this.bulletSpawner = new BulletSpawner(this, new Bullet(BulletType.PLAYER, bulletTexture), margins, 2, boundaries, null);
        this.screenWidth = screenWidth;
    }

    // --- Function(s)

    /** Used to fire a bullet. Returns true if a bullet was fired. */
    public boolean fire()
    {
        final long now = System.nanoTime();

        // Check if the cooldown period has ended
        if (!cannonCooldown.isReady(now)) return false;

        try
        {
            // Spawn bullet; this fails when too many of the player's bullets are on screen
            if (!bulletSpawner.spawn()) return false;
        }
        catch (CloneNotSupportedException e)
        {
            Log.e("SPAWNER ERROR", "CloneNotSupportedException");
            return false;
        }

        cannonCooldown.use(now);
        return true;
    }

    /** Returns the nearest x position at which a sprite of the given width is wholly on screen. */
    static float clampToScreen(final float x, final float width, final float screenWidth)
    {
        final float halfWidth = width / 2;
        return Math.max(halfWidth, Math.min(x, screenWidth - halfWidth));
    }

    // --- Method(s)

    /** Used to aim the player at a target. */
    public void aim(Vector2 target)
    {
        // Find the angle between the current position and the target, then rotate the player towards the target.
        this.rotate(this.getPosition().angleBetween(target));
    }

    /** Overridden method for rotating the player. */
    @Override
    public void rotate(float angle)
    {
        // Rotate between -90 and 90 degrees
        final float newAngle = Angle.Normalise180(angle + this.getRotation());
        super.setRotation(newAngle);
    }

    /** Overridden method for setting the position of the player. */
    @Override
    public void setPosition(Vector2 position)
    {
        // Prevent the player from going off-screen
        position.x = clampToScreen(position.x, this.boundingBoxSize.x, screenWidth);

        super.setPosition(position);
    }

    /** Used to update the player. */
    public void update(final float deltaTime, final Vector2 tapPosition)
    {
        // Call super method
        super.update(deltaTime);
        // Rotate the ship in the direction of the tap
        // this.rotate(tapPosition.angleBetween(getPosition()));
    }
}
