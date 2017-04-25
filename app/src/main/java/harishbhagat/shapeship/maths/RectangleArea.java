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

    /** Used to scale the size of the area. */
    public RectangleArea scale(final float scalar)
    {
        // Calculate the adjustments needed
        final float adjustmentX = ((maxX * scalar) - maxX) / 2;
        final float adjustmentY = ((maxY * scalar) - maxY) / 2;
        // Set the new values
        minX -= adjustmentX;
        maxX += adjustmentX;
        minY -= adjustmentY;
        maxY += adjustmentY;
        return new RectangleArea(minX, maxX, minY, maxY);
    }
}
