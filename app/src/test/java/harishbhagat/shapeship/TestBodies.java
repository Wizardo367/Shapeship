package harishbhagat.shapeship;

import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/** Builds bodies for tests. */
public final class TestBodies
{
    private TestBodies() {}

    /** Returns a physics body of mass 50 centred on (x, y) with the given bounding box size. */
    public static Body body(final ColliderShape shape, final float x, final float y, final float width, final float height)
    {
        final Body body = new Body(new Vector2(width, height), true, shape, 50);
        body.setPosition(new Vector2(x, y));
        return body;
    }

    /** Returns a circle of the given diameter centred on (x, y). */
    public static Body circle(final float x, final float y, final float diameter)
    {
        return body(ColliderShape.CIRCLE, x, y, diameter, diameter);
    }

    /** Returns a rectangle centred on (x, y). */
    public static Body rectangle(final float x, final float y, final float width, final float height)
    {
        return body(ColliderShape.RECTANGLE, x, y, width, height);
    }
}
