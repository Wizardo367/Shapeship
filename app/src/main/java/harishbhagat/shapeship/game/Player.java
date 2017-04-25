package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.sql.Time;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

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
    private Context context;
    private int cannonCooldown = 1000; // Measured in milliseconds
    private long lastShot = System.nanoTime();

    // --- Constructor(s)

    /** Default constructor. */
    public Player(Context context, final RectangleArea boundaries)
    {
        // Set the super constructor
        super(null, false, ColliderShape.RECTANGLE, 0);
        // Set the variables
        this.bulletSpawner = new BulletSpawner(this, new Bullet(BulletType.PLAYER, context), new Vector2(0, -35), 2, boundaries, context);
        this.context = context;
        // Load the texture
        Bitmap texture = BitmapFactory.decodeResource(context.getResources(), R.drawable.shapeship);
        // Set the texture
        this.setTexture(texture);
    }

    // --- Function(s)

    /** Used to fire a bullet. */
    public boolean fire()
    {
        try
        {
            // Check if the cooldown period has ended
            if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastShot) >= TimeUnit.MILLISECONDS.toSeconds(cannonCooldown))
            {
                // Spawn bullet
                bulletSpawner.spawn();
                return true;
            }
        }
        catch (CloneNotSupportedException e) { Log.e("SPAWNER ERROR", "CloneNotSupportedException"); };

        // Cooldown has not ended
        return false;
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
        DisplayUtilities displayUtilities = new DisplayUtilities(context);

        float halfTextureWidth = this.texture.getWidth() / 2.f;
        int screenWidth = (int) displayUtilities.getScreenSize().maxX;

        if (position.x >= 0 && position.x < halfTextureWidth)
            position.x += halfTextureWidth;
        else if (position.x > screenWidth - halfTextureWidth && position.x <= screenWidth)
            position.x -= halfTextureWidth;

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
