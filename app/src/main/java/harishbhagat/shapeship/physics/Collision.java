package harishbhagat.shapeship.physics;

import java.util.List;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to perform tests for collisions and resolve them.
 *
 * Created by Harish Bhagat on 26/03/2016.
 */
public class Collision
{
    // --- Constructor(s)

    /** Default constructor. */
    public Collision() {}

    // --- Function(s)

    /**
     * Performs an AABB collision test between two bodies.
     * A body's position is its centre.
     * The normal points from body to otherBody, along the axis with the smaller overlap.
     */
    private static CollisionInfo AABBCollisionTest(Body body, Body otherBody)
    {
        final Vector2 difference = otherBody.getPosition().subtract(body.getPosition());
        final float overlapX = (body.boundingBoxSize.x + otherBody.boundingBoxSize.x) / 2 - Math.abs(difference.x);
        final float overlapY = (body.boundingBoxSize.y + otherBody.boundingBoxSize.y) / 2 - Math.abs(difference.y);

        // Boxes that only touch don't collide
        if (overlapX <= 0 || overlapY <= 0)
            return new CollisionInfo();

        if (overlapX < overlapY)
            return new CollisionInfo(true, overlapX, new Vector2(Sign(difference.x), 0));
        return new CollisionInfo(true, overlapY, new Vector2(0, Sign(difference.y)));
    }

    /**
     * Performs an AABB-circle collision test between two bodies.
     * The normal points from body to otherBody.
     */
    private static CollisionInfo AABBCircleCollisionTest(Body body, Body otherBody)
    {
        // Check the colliders of both bodies
        final Body rect = (body.getColliderShape() == ColliderShape.RECTANGLE) ? body : otherBody;
        final Body circle = (rect == body) ? otherBody : body;

        // Circle centre minus rectangle centre
        final Vector2 difference = circle.getPosition().subtract(rect.getPosition());

        // Get the extents of rect
        final Vector2 extents = rect.boundingBoxSize.divide(2);

        // The point of the rectangle closest to the circle's centre, relative to the rectangle's centre
        final Vector2 closest = new Vector2(Clamp(difference.x, extents.x), Clamp(difference.y, extents.y));

        // Compare the distance from that point to the circle's centre with the radius
        final Vector2 offset = difference.subtract(closest);
        final float distance = offset.magnitude();
        final float radius = Radius(circle);

        if (distance >= radius)
            // No Collision
            return new CollisionInfo();

        // Find the normal from the rectangle towards the circle
        Vector2 normal;
        float penetration;
        if (distance > 0)
        {
            normal = offset.divide(distance);
            penetration = radius - distance;
        }
        else
        {
            // The circle's centre is inside the rectangle, so push it out through the nearest side
            final float gapX = extents.x - Math.abs(difference.x);
            final float gapY = extents.y - Math.abs(difference.y);
            normal = (gapX < gapY) ? new Vector2(Sign(difference.x), 0) : new Vector2(0, Sign(difference.y));
            penetration = radius + Math.min(gapX, gapY);
        }

        // Make the normal point from body to otherBody
        if (rect != body)
            normal = normal.multiply(-1);

        return new CollisionInfo(true, penetration, normal);
    }

    /**
     * Performs a circle collision test between two bodies.
     * The normal points from body to otherBody.
     */
    private static CollisionInfo CircleCollisionTest(Body body, Body otherBody)
    {
        // Subtract the two positions
        final Vector2 positionSubtraction = otherBody.getPosition().subtract(body.getPosition());
        // Get the sum of the radii
        final float radiiSum = Radius(body) + Radius(otherBody);

        // Check for a collision. Comparing squares avoids a square root when there's none.
        if (positionSubtraction.dot(positionSubtraction) >= radiiSum * radiiSum)
            // No collision
            return new CollisionInfo();

        // Collision
        final float magnitude = positionSubtraction.magnitude();

        if (magnitude != 0)
            return new CollisionInfo(true, radiiSum - magnitude, positionSubtraction.divide(magnitude));
        else
            // Both bodies are in the same place
            return new CollisionInfo(true, radiiSum, new Vector2(1, 0));
    }

    /** Resolves a collision. */
    public static boolean Collide(Body body, Body otherBody, CollisionInfo collisionInfo)
    {
        final Vector2 velocity = body.getVelocity();
        final Vector2 otherVelocity = otherBody.getVelocity();

        final Vector2 subtractionVelocity = velocity.subtract(otherVelocity);
        final float contactVelocity = subtractionVelocity.dot(collisionInfo.collisionNormal);

        // Check that the objects aren't already moving apart
        if (contactVelocity < 0) return false;

        // Calculate restitution
        final float restitution = Math.min(body.restitution, otherBody.restitution);
        // Calculate j
        float j = -(1 + restitution) * contactVelocity;
        j /= body.invMass + otherBody.invMass;

        // Calculate the new velocities
        Vector2 impluse = collisionInfo.collisionNormal.multiply(j);
        body.setVelocity(velocity.add(impluse.divide(body.mass)));
        otherBody.setVelocity(otherVelocity.subtract(impluse.divide(otherBody.mass)));

        return true;
    }

    /**
     * Tests each pair of bodies once and resolves the pairs that collide. Returns the number
     * resolved. Testing (a, b) and then (b, a) as well would double the work for nothing, since
     * Collide changes both bodies.
     */
    public static int ResolveAll(final List<? extends Body> bodies)
    {
        int resolved = 0;
        for (int i = 0; i < bodies.size(); ++i)
        {
            final Body body = bodies.get(i);
            for (int j = i + 1; j < bodies.size(); ++j)
            {
                final Body otherBody = bodies.get(j);
                final CollisionInfo collisionInfo = HasCollidedWith(body, otherBody);
                if (collisionInfo.collision && Collide(body, otherBody, collisionInfo)) ++resolved;
            }
        }
        return resolved;
    }

    /** Performs a suitable collision test between two bodies. */
    public static CollisionInfo HasCollidedWith(Body body, Body otherBody)
    {
        if (body.getColliderShape() == ColliderShape.CIRCLE && otherBody.getColliderShape() == ColliderShape.CIRCLE)
            return CircleCollisionTest(body, otherBody);
        else if (body.getColliderShape() == ColliderShape.RECTANGLE && otherBody.getColliderShape() == ColliderShape.RECTANGLE)
            return AABBCollisionTest(body, otherBody);
        else
            return AABBCircleCollisionTest(body, otherBody);
    }

    /** Used to determine if a point intersects a body's bounding box, edges included. */
    public static boolean PointAABBIntersectionTest(Body body, Vector2 point)
    {
        final Vector2 halfSize = body.boundingBoxSize.divide(2);
        final Vector2 position = body.getPosition();

        // Get bounds
        final RectangleArea bounds = new RectangleArea(position.x - halfSize.x, position.x + halfSize.x, position.y - halfSize.y, position.y + halfSize.y);

        // The point must be within the bounds on both axes
        return bounds.contains(point);
    }

    /** Used to determine the radius of a body's bounding box. */
    private static float Radius(Body body)
    {
        Vector2 boundingBoxSize = body.boundingBoxSize;
        return (boundingBoxSize.x + boundingBoxSize.y) / 4;
    }

    /** Used to limit a value to the range -extent to extent. */
    private static float Clamp(final float value, final float extent)
    {
        return Math.max(-extent, Math.min(value, extent));
    }

    /** Returns -1 for a negative value, otherwise 1. */
    private static float Sign(final float value)
    {
        return (value < 0) ? -1 : 1;
    }
}