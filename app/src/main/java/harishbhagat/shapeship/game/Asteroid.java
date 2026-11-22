package harishbhagat.shapeship.game;

import android.graphics.Bitmap;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Container class for asteroid sprites.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class Asteroid extends Sprite
{
    // --- Member(s)

    /** An asteroid's width as a fraction of the screen width. */
    public static final float WIDTH_FRACTION = 0.11f;

    // --- Constructor(s)

    /** Default constructor. The texture is drawn at the size the asteroid appears (see Textures). */
    public Asteroid(Bitmap texture)
    {
        super(texture, true, ColliderShape.CIRCLE, 50);
    }
}
