package harishbhagat.shapeship.game;

import android.content.SharedPreferences;

/**
 * Used to read and save the high score.
 */
public final class HighScore
{
    // --- Member(s)

    /** The preferences key for the high score. */
    static final String KEY = "high_score";
    /** Older versions saved the high score under this key, which any other setting named "0" would clash with. */
    static final String OLD_KEY = "0";

    // --- Constructor(s)

    private HighScore() {}

    // --- Function(s)

    /** Returns the saved high score, or 0 if there isn't one. */
    public static int read(final SharedPreferences preferences)
    {
        return preferences.getInt(KEY, preferences.getInt(OLD_KEY, 0));
    }

    /** Saves the score if it beats the saved high score. Returns true if it did. */
    public static boolean saveIfHigher(final SharedPreferences preferences, final int score)
    {
        if (score <= read(preferences)) return false;

        // apply() writes to disk in the background, so the game thread doesn't wait for it
        preferences.edit().putInt(KEY, score).remove(OLD_KEY).apply();
        return true;
    }
}
