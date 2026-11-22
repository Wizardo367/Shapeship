package harishbhagat.shapeship.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

/**
 * Turns the game's vector drawables into bitmaps.
 *
 * Each sprite's vector is drawn once, when the game starts, at the size the sprite has on this
 * screen. Drawing bitmaps every frame is cheaper than drawing vectors, and the bitmaps are
 * sharp because they're never scaled up.
 */
public final class Textures
{
    private Textures() {}

    /** Draws a drawable into a new bitmap the given number of pixels wide, keeping its proportions. */
    public static Bitmap render(final Context context, @DrawableRes final int id, final int widthPixels)
    {
        final Drawable drawable = context.getDrawable(id);
        if (drawable == null) throw new IllegalArgumentException("No drawable with id " + id);

        final int heightPixels = heightFor(widthPixels, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        final Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(new Canvas(bitmap));
        return bitmap;
    }

    /** Returns the height that keeps a drawable's proportions at the given width; at least one pixel. */
    static int heightFor(final int width, final int intrinsicWidth, final int intrinsicHeight)
    {
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) throw new IllegalArgumentException("The drawable has no intrinsic size");
        return Math.max(1, Math.round((float) width * intrinsicHeight / intrinsicWidth));
    }

    /** Returns the given fraction of a length in whole pixels; at least one pixel. */
    public static int pixels(final float length, final float fraction)
    {
        return Math.max(1, Math.round(length * fraction));
    }
}
