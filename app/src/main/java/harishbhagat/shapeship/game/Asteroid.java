package harishbhagat.shapeship.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import harishbhagat.shapeship.R;
import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * Container class for asteroid sprites.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class Asteroid extends Sprite
{
    // --- Member(s)

    private Bitmap texture;

    // --- Constructor(s)

    /** Default constructor. */
    public Asteroid(Context context)
    {
        super(null, true, ColliderShape.CIRCLE, 50);
        // Load and scale the texture
        texture = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid);
        this.setTexture(texture);
        this.scale(new Vector2(0.5f, 0.5f));
    }

    // --- Method(s)

    /** Overridden method for updating the object. */
    @Override
    public void update(final float deltaTime)
    {
        // Perform physics operations in super method
        super.update(deltaTime);
    }
}
