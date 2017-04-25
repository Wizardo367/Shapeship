package harishbhagat.shapeship.graphics;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Useful functions that provide information about the device's screen.
 * <p/>
 * Created by Harish Bhagat on 31/03/2016.
 */
public class DisplayUtilities
{
    // --- Member(s)

    public Context context;
    /**
     * The context used for retrival of the device's screen functions.
     */
    private final RectangleArea screenDimensions; /** Holds the screen's dimensions in pixels. */

    // --- Constructor(s)

    /**
     * Default constructor.
     */
    public DisplayUtilities(Context context)
    {
        // Set the variables
        this.context = context;
        // Get the screen size
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenDimensions = new RectangleArea(0, displayMetrics.widthPixels, 0, displayMetrics.heightPixels);
    }

    // --- Function(s)

    /**
     * Converts a percentage into pixels.
     */
    public int percentToPixels(final float percent, final Axis2D axis)
    {
        // Check that the percentage value is valid
        if (percent < 0) return 0;
        // Convert the percentage into pixels
        return (int) (((axis == Axis2D.X) ? screenDimensions.maxX : screenDimensions.maxY) * (percent / 100));
    }

    /**
     * Converts pixels into a percentage.
     */
    public float pixelsToPercent(final int pixels, final Axis2D axis)
    {
        // Check that the pixels value is valid
        if (pixels < 0) return 0;
        // Convert the pixels into a percentage
        return (pixels / ((axis == Axis2D.X) ? screenDimensions.maxX : screenDimensions.maxY)) * 100;

    }

    /** Returns the screen size in pixels. */
    public RectangleArea getScreenSize()
    {
        return new RectangleArea(screenDimensions.minX, screenDimensions.maxX, screenDimensions.minY, screenDimensions.maxY);
    }
}
