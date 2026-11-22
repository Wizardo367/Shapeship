package harishbhagat.shapeship.activities;

import android.graphics.Color;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Keeps each screen's content clear of the status and navigation bars.
 *
 * Android 15 and later always draw an app behind the system bars, so without this the
 * score and buttons would sit under them. The bars stay transparent over the window's
 * black background, with light icons.
 */
final class SystemBars
{
    private SystemBars() {}

    /** Call after setContentView. */
    static void keepContentClear(ComponentActivity activity)
    {
        final SystemBarStyle darkBars = SystemBarStyle.dark(Color.TRANSPARENT);
        EdgeToEdge.enable(activity, darkBars, darkBars);

        final View content = activity.findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, windowInsets) -> {
            final Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
