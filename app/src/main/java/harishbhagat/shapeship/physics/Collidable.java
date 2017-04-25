package harishbhagat.shapeship.physics;

/**
 * Used to define an object which may be collided with.
 *
 * Created by Harish Bhagat on 24/03/2016.
 */
public abstract class Collidable implements Cloneable
{
    // --- Member(s)

    private boolean physicsObject;
    private ColliderShape shape;

    // --- Constructor(s)

    /** Default constructor. */
    public Collidable(boolean physicsObject, ColliderShape shape)
    {
        this.physicsObject = physicsObject;
        this.shape = shape;
    }

    // --- Function(s)

    /** Returns a copy of the object */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /** Used to check if the collidable is affected by physics. */
    public boolean isPhysicsObject()
    {
        return physicsObject;
    }

    /** Used to retrieve the collidable's shape */
    public ColliderShape getColliderShape()
    {
        return this.shape;
    }
}