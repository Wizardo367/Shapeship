package harishbhagat.shapeship.maths;

/**
 * Used to perform angle-related operations.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public class Angle
{
    // --- Function(s)

    /** Normalises an angle so that it falls within the range of -90 to 90 degrees. */
    public static float Normalise180(float angle)
    {
        // Make the angle fall within the range of -90 to 90 degrees
        if (angle > 90)
            angle = 90;
        else if (angle < -90)
            angle = -90;

        return angle;
    }

    /** Normalises an angle so that it falls within the range of 0 to 359 degrees. */
    public static float Normalise360(float angle)
    {
        // Make the angle fall within the range of 0 to 359 degrees
        if (angle < 0)
            angle *= -1;

        if (angle > 359)
            angle %= 360;

        return angle;
    }
}
