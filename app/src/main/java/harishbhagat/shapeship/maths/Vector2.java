package harishbhagat.shapeship.maths;

/**
 * Used to create and modify a two-dimensional vector.
 * <p/>
 * Created by Harish Bhagat on 24/03/2016.
 */
public class Vector2
{
    // --- Member(s)

    public float x; /** Stores the vector's X coordinate value. */
    public float y; /** Stores the vector's Y coordinate value. */

    // --- Constructor(s)

    /** Default constructor. */
    public Vector2()
    {
        this.x = 0;
        this.y = 0;
    }

    /** Alternate constructor. */
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /** Alternate constructor. */
    public Vector2(Vector2 vector2)
    {
        x = vector2.x;
        y = vector2.y;
    }

    // --- Function(s)

    /** Used to a add a value to a vector. */
    public Vector2 add(float amount)
    {
        return new Vector2(this.x + amount, this.y + amount);
    }

    /** Used to a add a vector to a vector. */
    public Vector2 add(Vector2 amount)
    {
        return new Vector2(this.x + amount.x, this.y + amount.y);
    }

    /** Used to find the angle in degrees between two vectors. */
    public float angleBetween(Vector2 comparator)
    {
        return (float) Math.toDegrees(Math.atan2(comparator.x - this.x, comparator.y - this.y));
    }

    /** Used to find the cross product of two vectors. */
    public float cross(Vector2 comparator)
    {
        return this.x * comparator.y - comparator.x * this.y;
    }

    /** Used to a divide a vector by a value. */
    public Vector2 divide(float amount)
    {
        return new Vector2(this.x / amount, this.y / amount);
    }

    /** Used to a divide a vector by a vector. */
    public Vector2 divide(Vector2 amount)
    {
        return new Vector2(this.x / amount.x, this.y / amount.y);
    }

    /** Used to find the dot product of two vectors. */
    public float dot(Vector2 comparator)
    {
        return this.x * comparator.x + this.y * comparator.y;
    }

    /**
     * Returns true if the other object is a vector with the same coordinates. This overrides
     * Object.equals, so assertEquals and collections use it; the 2016 version took a Vector2
     * and so was never called by either. Coordinates compare as Float.equals does (NaN equals
     * NaN, 0 differs from -0), which keeps equals consistent with hashCode.
     * Vectors are mutable: don't change one while it's in a hash set or used as a map key.
     */
    @Override
    public boolean equals(final Object other)
    {
        if (this == other) return true;
        if (!(other instanceof Vector2)) return false;
        final Vector2 vector = (Vector2) other;
        return Float.compare(x, vector.x) == 0 && Float.compare(y, vector.y) == 0;
    }

    @Override
    public int hashCode()
    {
        return 31 * Float.hashCode(x) + Float.hashCode(y);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    /** Used to find the magnitude of a vector. */
    public float magnitude()
    {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /** Used to a multiply a vector by a value. */
    public Vector2 multiply(float multiplier)
    {
        return new Vector2(this.x * multiplier, this.y * multiplier);
    }

    /** Used to a multiply a vector by a vector. */
    public Vector2 multiply(Vector2 amount)
    {
        return new Vector2(this.x * amount.x, this.y * amount.y);
    }

    /** Used to normalise the vector. */
    public Vector2 normalise()
    {
        return this.divide(this.magnitude());
    }

    /** Used to a subtract a value from a vector. */
    public Vector2 subtract(float amount)
    {
        return new Vector2(this.x - amount, this.y - amount);
    }

    /** Used to a subtract a vector from a vector. */
    public Vector2 subtract(Vector2 amount)
    {
        return new Vector2(this.x - amount.x, this.y - amount.y);
    }

    // --- Method(s)

    /** Used to rotate the vector by a number of degrees. */
    public void rotate(float angle)
    {
        // Convert degrees into radians
        final double rad = Math.toRadians(angle);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);
        // Rotate the vector. Both lines use the original x, so keep it.
        final float oldX = this.x;
        this.x = (float) (oldX * cos - this.y * sin);
        this.y = (float) (oldX * sin + this.y * cos);
    }

    /** Used to scale the vector in place. */
    public void scale(float scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
    }
}
