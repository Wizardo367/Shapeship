package harishbhagat.shapeship.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import harishbhagat.shapeship.maths.Angle;
import harishbhagat.shapeship.physics.Collidable;
import harishbhagat.shapeship.physics.ColliderShape;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to create sprites.
 *
 * Created by Harish Bhagat on 24/03/2016.
 */
public class Sprite extends Collidable implements Cloneable
{
    // --- Member(s)

    protected Bitmap texture;
    private Matrix matrix;
    private float rotation;
    private Vector2 acceleration;
    private Vector2 position;
    private Vector2 scale;
    private Vector2 velocity;

    public Vector2 boundingBoxSize; /** Contains the size of the sprite's bounding box in pixels. */
    public final float mass; /** Stores the sprite's mass value. */
    public final float invMass; /** Stores the sprite's inverse mass value. */
    public float restitution = 0.8f;

    // --- Constructor(s)

    /** Default constructor. */
    public Sprite(Bitmap texture, boolean physicsObject, ColliderShape colliderShape, float mass)
    {
        // Set the super constructor
        super(physicsObject, colliderShape);
        // Set the variables
        this.acceleration = new Vector2();
        if (texture != null)
        {
            this.boundingBoxSize = new Vector2(texture.getWidth(), texture.getHeight());
            this.texture = texture;
        }
        this.invMass = 1 / mass;
        this.mass = mass;
        this.matrix = new Matrix();
        this.position = new Vector2();
        this.rotation = 0;
        this.scale = new Vector2(1, 1);
        this.velocity = new Vector2();
    }

    // --- Function(s)

    /** Returns a copy of the object */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return (Sprite)super.clone();
    }

    /** Returns the sprite's current acceleration. */
    public Vector2 getAcceleration()
    {
        return this.acceleration;
    }

    /** Returns the sprite's texture. */
    public Bitmap getBitmap()
    {
        return texture;
    }

    /** Returns the sprite's transformation matrix. */
    public Matrix getMatrix() { return matrix; }

    /** Returns the sprite's position. */
    public Vector2 getPosition()
    {
        return this.position;
    }

    /** Returns the sprite's rotation value. */
    public float getRotation()
    {
        return this.rotation;
    }

    /** Returns the scale of the sprite. */
    public Vector2 getScale()
    {
        return this.scale;
    }

    /** Returns the sprite's velocity. */
    public Vector2 getVelocity()
    {
        return this.velocity;
    }

    // --- Method(s)

    /** Used to accelerate the sprite. */
    public void accelerate(Vector2 amount)
    {
        this.acceleration = this.acceleration.add(amount);
    }

    /** Used to modify the sprite's velocity. */
    public void impulse(Vector2 amount)
    {
        this.velocity = this.velocity.add(amount);
    }

    /** Used to define how the sprite should be drawn to the canvas. */
    public void draw(Canvas canvas, Paint paint)
    {
        // Position the sprite at origin (0.5, 0.5) instead of (0, 0)
        final Vector2 newPosition = new Vector2(position.x - (texture.getWidth() / 2), position.y - (texture.getHeight() / 2));
        // Draw the sprite
        canvas.drawBitmap(texture, newPosition.x, newPosition.y, paint);
    }

    /** Used to move the sprite in relation to it's current position. */
    public void move(Vector2 amount)
    {
        this.position = this.position.add(amount);
    }

    /** Used to the reverse a sprite's velocity along one or both axes. */
    public void reverseVelocity(final boolean x, final boolean y)
    {
        if (x) { this.velocity.x *= -1; }
        if (y) { this.velocity.y *= -1; }
    }

    /**
     * Used to rotate the sprite.
     * Note: Can cause memory errors.
     * */
    public void rotate(float angle)
    {
        // TODO Fix OutOfMemory Error
        // Validate the angle
        if (angle <= 0) return;
        rotation += angle;
        rotation = Angle.Normalise360(rotation);
        // Rotate the bitmap
        matrix.postRotate(angle);
        if (texture != null) texture = Bitmap.createBitmap(texture, 0, 0, texture.getWidth(), texture.getHeight(), matrix, true);
    }

    /** Used to scale the sprite. */
    public void scale(Vector2 scalar)
    {
        // Scale the texture
        matrix.postScale(scalar.x, scalar.y);
        scale.x += (scalar.x > 1) ? scalar.x : -scalar.x;
        scale.y += (scalar.y > 1) ? scalar.y : -scalar.y;
        texture = Bitmap.createBitmap(texture, 0, 0, texture.getWidth(), texture.getHeight(), matrix, true);
        // Scale the bounding box
        boundingBoxSize.scale((scalar.x > scalar.y) ? scalar.x : scalar.y);
        this.boundingBoxSize = new Vector2(texture.getWidth(), texture.getHeight());
    }

    /** Used to set the position of the sprite. */
    public void setPosition(Vector2 position)
    {
        this.position = position;
    }

    /**
     * Used to set the rotation of the sprite.
     * Note: Can cause memory errors.
     * */
    public void setRotation(float angle)
    {
        // Normalise the angle
        this.rotation = Angle.Normalise180(angle);
        // Rotate the bitmap
        matrix.setRotate(this.rotation);
        texture = Bitmap.createBitmap(texture, 0, 0, texture.getWidth(), texture.getHeight(), matrix, true);
    }

    /** Used to set the scale of the sprite. */
    public void setScale(Vector2 scalar)
    {
        // Scale the texture
        matrix.setScale(scalar.x, scalar.y);
        this.scale = scalar;
    }

    /** Used to modify the velocity of a sprite so that it travels towards a target. */
    public void setTarget(Vector2 target, final float deltaTime)
    {
        this.velocity.rotate(-this.position.angleBetween(target));
    }

    /** Used to set the texture of a sprite. */
    public void setTexture(Bitmap texture)
    {
        this.boundingBoxSize = new Vector2(texture.getWidth(), texture.getHeight());
        this.texture = texture;
    }

    /** Used to set the sprite's velocity. */
    public void setVelocity(Vector2 velocity)
    {
        this.velocity = velocity;
    }

    /** Used to update the sprite. */
    public void update(final float deltaTime)
    {
        // Update the position using velocity
        if (this.isPhysicsObject())
            this.position = position.add(velocity.multiply(deltaTime));
    }
}