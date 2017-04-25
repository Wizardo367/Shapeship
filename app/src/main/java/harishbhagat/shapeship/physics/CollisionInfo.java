package harishbhagat.shapeship.physics;

import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to store collision information.
 *
 * Created by Harish Bhagat on 24/03/2016.
 */
public class CollisionInfo
{
    // --- Member(s)

    public final boolean collision; /** Used to determine whether or not a collision has occured. */
    public final float penetration; /** Used to store the penetration value of the collision */
    public final Vector2 collisionNormal; /** Used to store the collision normal. */

    // --- Constructor(s)

    /** Default constructor. */
    public CollisionInfo()
    {
        this.collision = false;
        this.penetration = 0;
        this.collisionNormal = new Vector2();
    }

    /** Alternate constructor */
    public CollisionInfo(boolean collision, float penetration, Vector2 collisionNormal)
    {
        this.collision = collision;
        this.penetration = penetration;
        this.collisionNormal = collisionNormal;
    }
}
