package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * A bullet fired by the ship.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class Bullet extends Body
{
    // --- Member(s)

    /** A bullet's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.013f;

    // --- Constructor(s)

    /** Default constructor. The size is that of the bullet's texture. */
    public Bullet(final Vector2 size)
    {
        super(size, true, ColliderShape.RECTANGLE, 1);
    }
}
