package harishbhagat.shapeship.physics;

import android.graphics.Bitmap;

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

    /** Performs an AABB collision test between two sprites.  */
    private static CollisionInfo AABBCollisionTest(Sprite sprite, Sprite otherSprite, final float deltaTime)
    {
        // get the positions of the sprites
        Vector2 spritePosition = sprite.getPosition();
        Vector2 otherSpritePosition = otherSprite.getPosition();

        if (spritePosition.x < otherSpritePosition.x + otherSprite.boundingBoxSize.x &&
                spritePosition.x + sprite.boundingBoxSize.x > otherSpritePosition.x &&
                spritePosition.y < otherSpritePosition.y + otherSprite.boundingBoxSize.y &&
                sprite.boundingBoxSize.y + spritePosition.y > otherSpritePosition.y)
        {
            // Collision

            // Get the collision normal
            Vector2 collisionNormal = sprite.getVelocity().add(otherSprite.getVelocity()).multiply(deltaTime);

            return new CollisionInfo(true, collisionNormal.magnitude(), collisionNormal);
        }
        else
            // No Collision
            return new CollisionInfo();
    }

    /** Performs an AABB-circle collision test between two sprites.  */
    private static CollisionInfo AABBCircleCollisionTest(Sprite sprite, Sprite otherSprite, final float deltaTime)
    {
        // Check the colliders of both sprites
        Sprite rect = (sprite.getColliderShape() == ColliderShape.RECTANGLE) ? sprite : otherSprite;
        Sprite circle = (rect == sprite) ? otherSprite : sprite;

        // Circle centre minus rectangle centre
        Vector2 difference = circle.getPosition().subtract(rect.getPosition());

        // Get the extents of rect
        Vector2 extents = rect.boundingBoxSize.divide(2);

        // Clamp diff to rect extents
        Vector2 clamp = new Vector2();
        clamp.x = (difference.x >= 0) ? Math.min(difference.x, extents.x) : Math.max(difference.x, -extents.x);
        clamp.y = (difference.y >= 0) ? Math.min(difference.y, extents.y) : Math.max(difference.y, -extents.y);

        // Calculate the distance
        Vector2 dist = difference.subtract(clamp);
        float distance = dist.magnitude() - Radius(circle);

        // Check for a collision
        if (distance < 0)
            // Collision
            return new CollisionInfo(true, distance, clamp.normalise());

        // No Collision
        return new CollisionInfo();
    }

    /** Performs a circle collision test between two sprites.  */
    private static CollisionInfo CircleCollisionTest(Sprite sprite, Sprite otherSprite, final float deltaTime)
    {
        // Subtract the two positions
        final Vector2 positionSubtraction = otherSprite.getPosition().subtract(sprite.getPosition());
        // Get the sum of the radii squared
        final float radius = Radius(sprite);
        final float radiiSumSquared = (float)Math.pow(radius + Radius(otherSprite), 2);

        // Check for a collision
        if (positionSubtraction.dot(positionSubtraction) > radiiSumSquared)
            // No collision
            return new CollisionInfo();

        // Collision
        final float magnitude = positionSubtraction.magnitude();

        if (magnitude != 0)
            return new CollisionInfo(true, radiiSumSquared - magnitude, positionSubtraction.divide(magnitude));
        else
            // Both sprites are in the same place
            return new CollisionInfo(true, radius, new Vector2(1, 0));
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
            return CircleCollisionTest(sprite, otherSprite, deltaTime);
        else if (sprite.getColliderShape() == ColliderShape.RECTANGLE && otherSprite.getColliderShape() == ColliderShape.RECTANGLE)
            return AABBCollisionTest(sprite, otherSprite, deltaTime);
        else
            return AABBCircleCollisionTest(sprite, otherSprite, deltaTime);
    }

    /** Used to determine if a point intersects an AABB */
    public static boolean PointAABBIntersectionTest(Sprite sprite, Vector2 point)
    {
        Bitmap texture = sprite.getBitmap();
        final Vector2 aabbHalfSize = new Vector2(texture.getWidth() / 2, texture.getHeight() / 2);
        final Vector2 spritePosition = sprite.getPosition();

        // Get bounds
        RectangleArea bounds = new RectangleArea(spritePosition.x - aabbHalfSize.x, spritePosition.x + aabbHalfSize.x, spritePosition.y - aabbHalfSize.y, spritePosition.y + aabbHalfSize.y);

        // Check for intersection
        if (point.x >= bounds.minX && point.x <= bounds.maxX)
            return true;
        else if (point.y >= bounds.minY && point.y <= bounds.maxY)
            return true;

        // No intersection
        return false;
    }

    /** Used to determine the radius of a sprites bounding box. */
    private static float Radius(Sprite sprite)
    {
        Vector2 boundingBoxSize = sprite.boundingBoxSize;
        return (boundingBoxSize.x + boundingBoxSize.y) / 4;
    }
}