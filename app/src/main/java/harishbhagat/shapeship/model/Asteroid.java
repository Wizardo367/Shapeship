package harishbhagat.shapeship.model;

import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * An asteroid: a heavy circle that bounces off the others.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class Asteroid extends Body
{
    // --- Member(s)

    /** An asteroid's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.11f;

    // --- Constructor(s)

    /** Default constructor. The size is that of the asteroid's texture. */
    public Asteroid(final Vector2 size)
    {
        super(size, true, ColliderShape.CIRCLE, 50);
    }
}
