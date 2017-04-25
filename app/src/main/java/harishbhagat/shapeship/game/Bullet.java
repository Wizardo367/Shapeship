package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Container class for asteroid sprites.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class Bullet extends Sprite
{
    // --- Member(s)

    private Bitmap texture;
    private final BulletType bulletType;

    // --- Constructor(s)

    /** Default constructor. */
    public Bullet(BulletType bulletType, Context context)
    {
        super(null, true, ColliderShape.RECTANGLE, 1);
        // Set the variables
        this.bulletType = bulletType;
        // Set the texture
        final int bulletID = (bulletType == BulletType.PLAYER) ? R.drawable.bullet : R.drawable.enemy_bullet;
        texture = BitmapFactory.decodeResource(context.getResources(), bulletID);
        this.setTexture(texture);
        // Scale the bullet
        this.scale(new Vector2(0.2f, 0.2f));
    }

    // --- Function(s)

    /** Returns the type of bullet. */
    public BulletType getBulletType()
    {
        return this.bulletType;
    }

    // --- Method(s)

    /** Overridden method for updating the object. */
    @Override
    public void update(final float deltaTime)
    {
        // Update the bullet's position
        Vector2 velocity = this.getVelocity();
        Vector2 newPosition = this.getPosition();
        newPosition.y += velocity.y * deltaTime;
        this.setPosition(newPosition);
    }
}
