package harishbhagat.shapeship.maths;

/**
 * Used to perform angle-related operations.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class Angle
{
    // --- Function(s)

    /** Clamps an angle to the range -90 to 90 degrees. */
    public static float Normalise180(float angle)
    {
        // Make the angle fall within the range of -90 to 90 degrees
        if (angle > 90)
            angle = 90;
        else if (angle < -90)
            angle = -90;

        return angle;
    }

    /** Wraps an angle into the range 0 (inclusive) to 360 (exclusive) degrees. */
    public static float Normalise360(float angle)
    {
        // Java's % keeps the sign of the angle, so a negative remainder needs 360 added.
        angle %= 360;
        if (angle < 0)
            angle += 360;

        return angle;
    }
}
