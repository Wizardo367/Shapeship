package harishbhagat.shapeship.model;

/**
 * Turns the phone's tilt into the ship's steering, from -1 (full speed left) to 1 (full speed
 * right). Readings are from the gravity sensor or accelerometer, in metres per second squared.
 */
public final class Tilt
{
    // --- Member(s)

    static final float GRAVITY = 9.80665f;
    /** Tilts of less than about 3 degrees are ignored, so that a phone held roughly level stays still. */
    static final float DEAD_ZONE = 0.05f;
    /** A tilt of about 25 degrees gives full speed. Readings are the sine of the tilt. */
    static final float FULL_TILT = 0.42f;

    // --- Constructor(s)

    private Tilt() {}

    // --- Function(s)

    /**
     * Returns the reading along the screen's x axis, given the sensor's x and y readings and how
     * many quarter turns clockwise the screen is from the device's natural orientation
     * (Surface.ROTATION_0 to ROTATION_270). Phones are naturally portrait, so this is usually
     * just x; some tablets are naturally landscape.
     */
    public static float sideways(final int quarterTurns, final float x, final float y)
    {
        switch (quarterTurns & 3)
        {
            case 1: return -y;
            case 2: return -x;
            case 3: return y;
            default: return x;
        }
    }

    /**
     * Returns the steering for a reading along the screen's x axis. Tilting the right edge down
     * reads negative, and steers right.
     */
    public static float steering(final float sideways)
    {
        final float sine = Math.max(-1, Math.min(-sideways / GRAVITY, 1));
        final float magnitude = Math.abs(sine);
        if (magnitude <= DEAD_ZONE) return 0;
        final float steering = Math.min((magnitude - DEAD_ZONE) / (FULL_TILT - DEAD_ZONE), 1);
        return Math.copySign(steering, sine);
    }
}
