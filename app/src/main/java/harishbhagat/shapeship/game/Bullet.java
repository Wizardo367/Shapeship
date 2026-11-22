package harishbhagat.shapeship.game;

import android.graphics.Bitmap;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Container class for bullet sprites.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class Bullet extends Sprite
{
    // --- Member(s)

    /** A bullet's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.013f;

    private final BulletType bulletType;

    // --- Constructor(s)

    /** Default constructor. The texture is drawn at the size the bullet appears (see Textures). */
    public Bullet(BulletType bulletType, Bitmap texture)
    {
        super(texture, true, ColliderShape.RECTANGLE, 1);
        this.bulletType = bulletType;
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
