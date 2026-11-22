package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.DisplayUtilities;
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

    public BulletSpawner bulletSpawner; /** Bullet spawner for the player */
    private final Cooldown cannonCooldown = new Cooldown(1000); // Measured in milliseconds
    private final float screenWidth;

    // --- Constructor(s)

    /** Default constructor. */
    public Player(Context context, final RectangleArea boundaries)
    {
        // Set the super constructor
        super(null, false, ColliderShape.RECTANGLE, 0);
        // Set the variables
        this.bulletSpawner = new BulletSpawner(this, new Bullet(BulletType.PLAYER, context), new Vector2(0, -35), 2, boundaries, context);
        this.screenWidth = new DisplayUtilities(context).getScreenSize().maxX;
        // Load the texture
        Bitmap texture = BitmapFactory.decodeResource(context.getResources(), R.drawable.shapeship);
        // Set the texture
        this.setTexture(texture);
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
