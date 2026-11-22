package harishbhagat.shapeship.maths;

/**
 * Used to define a rectangular area.
 *
 * Created by Harish Bhagat on 02/04/2016.
 */
public class RectangleArea
{
    // --- Member(s)

    public float minX, maxX, minY, maxY;

    // --- Constructor(s)

    /** Default constructor. */
    public RectangleArea()
    {
        minX = 0;
        maxX = 0;
        minY = 0;
        maxY = 0;
    }

    /** Alternate constructor. */
    public RectangleArea(final float minX, final float maxX, final float minY, final float maxY)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    // --- Functions(s)

    /** Returns a copy of the area scaled about its centre. This area is left unchanged. */
    public RectangleArea scale(final float scalar)
    {
        // Half of the change in width and height goes on each side
        final float adjustmentX = (maxX - minX) * (scalar - 1) / 2;
        final float adjustmentY = (maxY - minY) * (scalar - 1) / 2;
        return new RectangleArea(minX - adjustmentX, maxX + adjustmentX, minY - adjustmentY, maxY + adjustmentY);
    }

    /** Used to check whether a point lies inside the area, edges included. */
    public boolean contains(final Vector2 point)
    {
        return point.x >= minX && point.x <= maxX && point.y >= minY && point.y <= maxY;
    }
}
