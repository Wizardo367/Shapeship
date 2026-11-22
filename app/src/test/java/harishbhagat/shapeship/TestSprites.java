package harishbhagat.shapeship;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.ColliderShape;

/** Builds sprites without textures, for tests. */
public final class TestSprites
{
    private TestSprites() {}

    /** Returns a physics sprite of mass 50 centred on (x, y) with the given bounding box size. */
    public static Sprite sprite(final ColliderShape shape, final float x, final float y, final float width, final float height)
    {
        final Sprite sprite = new Sprite(null, true, shape, 50);
        sprite.boundingBoxSize = new Vector2(width, height);
        sprite.setPosition(new Vector2(x, y));
        return sprite;
    }

    /** Returns a circle of the given diameter centred on (x, y). */
    public static Sprite circle(final float x, final float y, final float diameter)
    {
        return sprite(ColliderShape.CIRCLE, x, y, diameter, diameter);
    }

    /** Returns a rectangle centred on (x, y). */
    public static Sprite rectangle(final float x, final float y, final float width, final float height)
    {
        return sprite(ColliderShape.RECTANGLE, x, y, width, height);
    }
}
