package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Used to create a UFO sprite.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class UFO extends Sprite implements Cannon
{
    // --- Member(s)

    private Bitmap texture;
    private BulletSpawner bulletSpawner;
    private int currentInterval;
    private long lastShot;
    private final RectangleArea boundaries;

    // --- Constructor(s)

    /** Default constructor. */
    public UFO(Context context, final RectangleArea boundaries)
    {
        super(null, true, ColliderShape.RECTANGLE, 250);
        // Load the texture
        texture = BitmapFactory.decodeResource(context.getResources(), R.drawable.ufo);
        this.setTexture(texture);
        // Set the variables
        this.boundaries = boundaries;
        this.bulletSpawner = new BulletSpawner(this, new Bullet(BulletType.ENEMY, context), new Vector2(0, 35), 1, boundaries, context);
        currentInterval = 3;
        lastShot = System.nanoTime();
    }

    // --- Function(s)

    /** Used to fire a bullet. */
    public boolean fire()
    {
        // Check if a shot can be fired
        if (System.nanoTime() - lastShot >= TimeUnit.NANOSECONDS.toMillis(currentInterval) * 1000)
        {
            // Fire a shot
            try
            {
                bulletSpawner.spawn();
            }
            catch (CloneNotSupportedException e) { Log.e("SPAWNER ERROR", "CloneNotSupportedException"); }

            // Record the time
            lastShot = System.nanoTime();
            // Generate a new interval
            Random random = new Random();
            currentInterval = random.nextInt(3) + 1;
            return true;
        }
        return false;
    }

    // --- Method(s)

    // This method is a required implementation, but serves no purpose.
    public void aim(Vector2 target)
    {
        // UFO's can only aim straight down
    }

    /** Overridden method for updating the object. */
    @Override
    public void update(final float deltaTime)
    {
        // Check the position
        Vector2 position = this.getPosition();
        if (position.x >= boundaries.maxX || position.x <= boundaries.minX) { this.reverseVelocity(true, false); }

        // Update the position
        Vector2 velocity = this.getVelocity();
        Vector2 newPosition = position;
        newPosition.x += velocity.x * deltaTime;

        // Shoot every 1-3 seconds
        fire();
    }
}