package harishbhagat.shapeship.game;

/** Formats scores the way the game shows them: six digits with leading zeros. */
public final class ScoreFormat
{
    /** The number of digits shown; longer scores show in full. */
    static final int DIGITS = 6;

    private ScoreFormat() {}

    /** Returns the score with leading zeros, in ASCII digits whatever the device's language. */
    public static String format(final int score)
    {
        final String digits = Integer.toString(Math.max(0, score));
        final StringBuilder text = new StringBuilder(DIGITS);
        for (int i = digits.length(); i < DIGITS; ++i) text.append('0');
        return text.append(digits).toString();
    }
}
