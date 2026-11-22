package harishbhagat.shapeship.physics;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.graphics.Sprite;

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
     * Performs an AABB collision test between two sprites.
     * A sprite's position is its centre, as in Sprite.draw.
     * The normal points from sprite to otherSprite, along the axis with the smaller overlap.
     */
    private static CollisionInfo AABBCollisionTest(Sprite sprite, Sprite otherSprite)
    {
        final Vector2 difference = otherSprite.getPosition().subtract(sprite.getPosition());
        final float overlapX = (sprite.boundingBoxSize.x + otherSprite.boundingBoxSize.x) / 2 - Math.abs(difference.x);
        final float overlapY = (sprite.boundingBoxSize.y + otherSprite.boundingBoxSize.y) / 2 - Math.abs(difference.y);

        // Boxes that only touch don't collide
        if (overlapX <= 0 || overlapY <= 0)
            return new CollisionInfo();

        if (overlapX < overlapY)
            return new CollisionInfo(true, overlapX, new Vector2(Sign(difference.x), 0));
        return new CollisionInfo(true, overlapY, new Vector2(0, Sign(difference.y)));
    }

    /**
     * Performs an AABB-circle collision test between two sprites.
     * The normal points from sprite to otherSprite.
     */
    private static CollisionInfo AABBCircleCollisionTest(Sprite sprite, Sprite otherSprite)
    {
        // Check the colliders of both sprites
        final Sprite rect = (sprite.getColliderShape() == ColliderShape.RECTANGLE) ? sprite : otherSprite;
        final Sprite circle = (rect == sprite) ? otherSprite : sprite;

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

        // Make the normal point from sprite to otherSprite
        if (rect != sprite)
            normal = normal.multiply(-1);

        return new CollisionInfo(true, penetration, normal);
    }

    /**
     * Performs a circle collision test between two sprites.
     * The normal points from sprite to otherSprite.
     */
    private static CollisionInfo CircleCollisionTest(Sprite sprite, Sprite otherSprite)
    {
        // Subtract the two positions
        final Vector2 positionSubtraction = otherSprite.getPosition().subtract(sprite.getPosition());
        // Get the sum of the radii
        final float radiiSum = Radius(sprite) + Radius(otherSprite);

        // Check for a collision. Comparing squares avoids a square root when there's none.
        if (positionSubtraction.dot(positionSubtraction) >= radiiSum * radiiSum)
            // No collision
            return new CollisionInfo();

        // Collision
        final float magnitude = positionSubtraction.magnitude();

        if (magnitude != 0)
            return new CollisionInfo(true, radiiSum - magnitude, positionSubtraction.divide(magnitude));
        else
            // Both sprites are in the same place
            return new CollisionInfo(true, radiiSum, new Vector2(1, 0));
    }

    /** Resolves a collision. */
    public static boolean Collide(Sprite sprite, Sprite otherSprite, CollisionInfo collisionInfo, final float deltaTime)
    {
        final Vector2 spriteVelocity = sprite.getVelocity();
        final Vector2 otherSpriteVelocity = otherSprite.getVelocity();

        final Vector2 subtractionVelocity = spriteVelocity.subtract(otherSpriteVelocity);
        final float contactVelocity = subtractionVelocity.dot(collisionInfo.collisionNormal);

        // Check that the objects aren't already moving apart
        if (contactVelocity < 0) return false;

        // Calculate restitution
        final float restitution = Math.min(sprite.restitution, otherSprite.restitution);
        // Calculate j
        float j = -(1 + restitution) * contactVelocity;
        j /= sprite.invMass + otherSprite.invMass;

        // Calculate the new velocities
        Vector2 impluse = collisionInfo.collisionNormal.multiply(j);
        sprite.setVelocity(spriteVelocity.add(impluse.divide(sprite.mass)));
        otherSprite.setVelocity(otherSpriteVelocity.subtract(impluse.divide(otherSprite.mass)));

        return true;
    }

    /** Performs a suitable collision test between two sprites. */
    public static CollisionInfo HasCollidedWith(Sprite sprite, Sprite otherSprite, final float deltaTime)
    {
        if (sprite.getColliderShape() == ColliderShape.CIRCLE && otherSprite.getColliderShape() == ColliderShape.CIRCLE)
            return CircleCollisionTest(sprite, otherSprite);
        else if (sprite.getColliderShape() == ColliderShape.RECTANGLE && otherSprite.getColliderShape() == ColliderShape.RECTANGLE)
            return AABBCollisionTest(sprite, otherSprite);
        else
            return AABBCircleCollisionTest(sprite, otherSprite);
    }

    /** Used to determine if a point intersects a sprite's bounding box, edges included. */
    public static boolean PointAABBIntersectionTest(Sprite sprite, Vector2 point)
    {
        final Vector2 halfSize = sprite.boundingBoxSize.divide(2);
        final Vector2 spritePosition = sprite.getPosition();

        // Get bounds
        final RectangleArea bounds = new RectangleArea(spritePosition.x - halfSize.x, spritePosition.x + halfSize.x, spritePosition.y - halfSize.y, spritePosition.y + halfSize.y);

        // The point must be within the bounds on both axes
        return bounds.contains(point);
    }

    /** Used to determine the radius of a sprites bounding box. */
    private static float Radius(Sprite sprite)
    {
        Vector2 boundingBoxSize = sprite.boundingBoxSize;
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