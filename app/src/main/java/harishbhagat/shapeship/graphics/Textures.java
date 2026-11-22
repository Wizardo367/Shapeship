package harishbhagat.shapeship.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

import harishbhagat.shapeship.maths.RectangleArea;

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

    /**
     * Draws an image resource into a new bitmap of the given size, scaled to cover it and cropped
     * evenly from both sides, as an ImageView with scaleType centerCrop shows it. The menus show
     * the background that way, so the game matches them.
     */
    public static Bitmap renderCentreCropped(final Context context, @DrawableRes final int id, final int widthPixels, final int heightPixels)
    {
        final Bitmap image = BitmapFactory.decodeResource(context.getResources(), id);
        if (image == null) throw new IllegalArgumentException("No image with id " + id);

        final RectangleArea crop = centreCrop(image.getWidth(), image.getHeight(), widthPixels, heightPixels);
        final Rect source = new Rect(Math.round(crop.minX), Math.round(crop.minY), Math.round(crop.maxX), Math.round(crop.maxY));

        final Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        new Canvas(bitmap).drawBitmap(image, source, new Rect(0, 0, widthPixels, heightPixels), new Paint(Paint.FILTER_BITMAP_FLAG));
        image.recycle();
        return bitmap;
    }

    /**
     * Returns the part of an image that fills a target of the given size when scaled to cover it
     * with its proportions kept: all of the image along one axis, and the middle along the other.
     */
    static RectangleArea centreCrop(final float imageWidth, final float imageHeight, final float targetWidth, final float targetHeight)
    {
        if (imageWidth <= 0 || imageHeight <= 0 || targetWidth <= 0 || targetHeight <= 0) throw new IllegalArgumentException("Sizes must be positive");

        // Compare the proportions by cross-multiplying, which avoids dividing
        if (imageWidth * targetHeight > imageHeight * targetWidth)
        {
            // The image is wider than the target: keep its full height, crop the sides
            final float width = imageHeight * targetWidth / targetHeight;
            final float left = (imageWidth - width) / 2;
            return new RectangleArea(left, left + width, 0, imageHeight);
        }

        // The image is taller than the target, or the same shape: keep its full width, crop the top and bottom
        final float height = imageWidth * targetHeight / targetWidth;
        final float top = (imageHeight - height) / 2;
        return new RectangleArea(0, imageWidth, top, top + height);
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
