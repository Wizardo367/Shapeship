package harishbhagat.shapeship.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

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
    private Matrix matrix; /** Reused each frame to place, scale and turn the texture. */
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

    /** Returns a copy of the object. The copy shares the texture but none of the mutable state. */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        final Sprite copy = (Sprite) super.clone();
        copy.acceleration = new Vector2(acceleration);
        copy.matrix = new Matrix(matrix);
        copy.position = new Vector2(position);
        copy.scale = new Vector2(scale);
        copy.velocity = new Vector2(velocity);
        if (boundingBoxSize != null) copy.boundingBoxSize = new Vector2(boundingBoxSize);
        return copy;
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

    /** Returns the matrix used to draw the sprite, for tests. */
    Matrix drawMatrix() { return matrix; }

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

    /**
     * Used to define how the sprite should be drawn to the canvas: centred on its position, then
     * scaled and turned about its centre. The texture itself is never changed.
     */
    public void draw(Canvas canvas, Paint paint)
    {
        final float width = texture.getWidth() * scale.x;
        final float height = texture.getHeight() * scale.y;
        matrix.setScale(scale.x, scale.y);
        matrix.postTranslate(position.x - width / 2, position.y - height / 2);
        if (rotation != 0) matrix.postRotate(rotation, position.x, position.y);
        canvas.drawBitmap(texture, matrix, paint);
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

    /** Used to rotate the sprite by the given number of degrees, clockwise. It turns when drawn. */
    public void rotate(float angle)
    {
        rotation = Angle.Normalise360(rotation + angle);
    }

    /**
     * Used to scale the sprite. It's scaled when drawn, so for sharp edges render the texture at
     * the size it's shown instead (see Textures).
     */
    public void scale(Vector2 scalar)
    {
        scale.x *= scalar.x;
        scale.y *= scalar.y;
        // The bounding box follows the drawn size
        if (boundingBoxSize != null) boundingBoxSize = new Vector2(boundingBoxSize.x * scalar.x, boundingBoxSize.y * scalar.y);
    }

    /** Used to set the position of the sprite. */
    public void setPosition(Vector2 position)
    {
        this.position = position;
    }

    /** Used to set the rotation of the sprite, clamped to ±90 degrees. It turns when drawn. */
    public void setRotation(float angle)
    {
        this.rotation = Angle.Normalise180(angle);
    }

    /** Used to set the scale of the sprite. It's scaled when drawn. */
    public void setScale(Vector2 scalar)
    {
        this.scale = new Vector2(scalar);
        if (texture != null) boundingBoxSize = new Vector2(texture.getWidth() * scalar.x, texture.getHeight() * scalar.y);
    }

    /** Used to modify the velocity of a sprite so that it travels towards a target. */
    public void setTarget(Vector2 target, final float deltaTime)
    {
        this.velocity.rotate(-this.position.angleBetween(target));
    }

    /** Used to set the texture of a sprite. The bounding box matches the texture at the current scale. */
    public void setTexture(Bitmap texture)
    {
        this.boundingBoxSize = new Vector2(texture.getWidth() * scale.x, texture.getHeight() * scale.y);
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