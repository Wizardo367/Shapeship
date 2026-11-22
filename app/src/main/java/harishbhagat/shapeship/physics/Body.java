package harishbhagat.shapeship.physics;

import harishbhagat.shapeship.maths.Angle;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Something in the game that moves and collides: the ship, a bullet, an asteroid or the pause
 * button. Its position is its centre and its bounding box is its size, both in pixels.
 *
 * A body knows nothing about how it's drawn. The game's renderer draws a texture of the same
 * size at its position, so the game model needs no Android classes and its tests run on the JVM.
 *
 * Created by Harish Bhagat on 24/03/2016, as Sprite.
 */
public class Body implements Cloneable
{
    // --- Member(s)

    private final boolean physicsObject; /** Whether update moves the body by its velocity. */
    private final ColliderShape colliderShape;
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private float rotation = 0; /** Degrees clockwise; the renderer turns the texture about its centre. */

    public Vector2 boundingBoxSize; /** The size of the body's bounding box in pixels. */
    public final float mass;
    public final float invMass;
    public float restitution = 0.8f;

    // --- Constructor(s)

    /** Default constructor. The size is that of the texture that draws the body. */
    public Body(final Vector2 size, final boolean physicsObject, final ColliderShape colliderShape, final float mass)
    {
        this.boundingBoxSize = new Vector2(size);
        this.physicsObject = physicsObject;
        this.colliderShape = colliderShape;
        this.mass = mass;
        this.invMass = 1 / mass;
    }

    // --- Function(s)

    /** Returns a copy that shares none of this body's mutable state. */
    @Override
    public Body clone()
    {
        try
        {
            final Body copy = (Body) super.clone();
            copy.boundingBoxSize = new Vector2(boundingBoxSize);
            copy.position = new Vector2(position);
            copy.velocity = new Vector2(velocity);
            return copy;
        }
        catch (CloneNotSupportedException e)
        {
            // Body implements Cloneable, so this can't happen
            throw new AssertionError(e);
        }
    }

    /** Returns the shape used to test the body for collisions. */
    public ColliderShape getColliderShape()
    {
        return colliderShape;
    }

    /** Returns the body's position: its centre. */
    public Vector2 getPosition()
    {
        return position;
    }

    /** Returns the body's rotation in degrees clockwise. */
    public float getRotation()
    {
        return rotation;
    }

    /** Returns the body's velocity in pixels per second. */
    public Vector2 getVelocity()
    {
        return velocity;
    }

    /** Returns true if update moves the body by its velocity. */
    public boolean isPhysicsObject()
    {
        return physicsObject;
    }

    // --- Method(s)

    /** Used to modify the body's velocity. */
    public void impulse(final Vector2 amount)
    {
        velocity = velocity.add(amount);
    }

    /** Used to rotate the body by the given number of degrees, clockwise. */
    public void rotate(final float angle)
    {
        rotation = Angle.Normalise360(rotation + angle);
    }

    /** Used to set the position of the body: its centre. */
    public void setPosition(final Vector2 position)
    {
        this.position = position;
    }

    /** Used to set the rotation of the body, clamped to ±90 degrees. */
    public void setRotation(final float angle)
    {
        rotation = Angle.Normalise180(angle);
    }

    /** Used to turn the body's velocity towards a target, keeping its speed. */
    public void setTarget(final Vector2 target)
    {
        velocity.rotate(-position.angleBetween(target));
    }

    /** Used to set the body's velocity in pixels per second. */
    public void setVelocity(final Vector2 velocity)
    {
        this.velocity = velocity;
    }

    /** Used to move a physics body by its velocity over the given number of seconds. */
    public void update(final float deltaTime)
    {
        if (physicsObject)
            position = position.add(velocity.multiply(deltaTime));
    }
}
