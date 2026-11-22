package harishbhagat.shapeship.graphics;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;

import harishbhagat.shapeship.R;

/**
 * The game's fonts: Noto Sans, under the SIL Open Font License (assets/licenses).
 *
 * Layouts can't set these fonts themselves before Android 8 without AppCompat, so each screen
 * applies them in code. ResourcesCompat caches each font after it first loads.
 */
public final class Fonts
{
    private Fonts() {}

    /** Noto Sans Black, for titles and the score. */
    public static Typeface black(final Context context)
    {
        return load(context, R.font.noto_sans_black, Typeface.DEFAULT_BOLD);
    }

    /** Noto Sans Regular, for body text. */
    public static Typeface regular(final Context context)
    {
        return load(context, R.font.noto_sans_regular, Typeface.DEFAULT);
    }

    /** Sets the typeface of each of the given views. */
    public static void apply(final Typeface typeface, final TextView... views)
    {
        for (TextView view : views)
            view.setTypeface(typeface);
    }

    private static Typeface load(final Context context, @FontRes final int id, final Typeface fallback)
    {
        // getFont returns null only for a restricted context, which the game never uses
        final Typeface typeface = ResourcesCompat.getFont(context, id);
        return typeface != null ? typeface : fallback;
    }
}
