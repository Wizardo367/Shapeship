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

    /** Used to determine if two vectors are equal. */
    public boolean equals(Vector2 comparator)
    {
        return (this.x == comparator.x && this.y == comparator.y);
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
        // Rotate the vector
        this.x = (float) (this.x * cos - this.y * sin);
        this.y = (float) (this.x * sin + this.y * cos);
    }

    /** Used to scale the vector. */
    public void scale(float scalar)
    {
        this.multiply(scalar);
    }
}
