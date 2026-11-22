package harishbhagat.shapeship.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Draws the score at the top of the game screen, sized to the screen.
 *
 * The text and paints are made once; the score's text is remade only when the score changes.
 */
final class ScoreDisplay
{
    // --- Member(s)

    private static final String TITLE = "SCORE";
    private static final int TITLE_COLOUR = Color.rgb(0xbc, 0x02, 0x02);

    /** Text heights and the gap above the title, as fractions of the screen width. */
    static final float TITLE_SIZE = 0.035f;
    static final float SCORE_SIZE = 0.07f;
    static final float TOP_MARGIN = 0.05f;

    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float centreX;
    private final float titleBaseline;
    private final float scoreBaseline;

    private int shownScore = -1;
    private String scoreText = "";

    // --- Constructor(s)

    ScoreDisplay(final Typeface typeface, final float screenWidth)
    {
        for (Paint paint : new Paint[] { titlePaint, scorePaint })
        {
            paint.setTypeface(typeface);
            paint.setTextAlign(Paint.Align.CENTER);
        }
        titlePaint.setColor(TITLE_COLOUR);
        titlePaint.setTextSize(TITLE_SIZE * screenWidth);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(SCORE_SIZE * screenWidth);

        centreX = screenWidth / 2;
        titleBaseline = (TOP_MARGIN + TITLE_SIZE) * screenWidth;
        scoreBaseline = titleBaseline + 1.1f * SCORE_SIZE * screenWidth;
    }

    // --- Method(s)

    /** Draws the title and the score. */
    void draw(final Canvas canvas, final int score)
    {
        if (score != shownScore)
        {
            scoreText = ScoreFormat.format(score);
            shownScore = score;
        }
        canvas.drawText(TITLE, centreX, titleBaseline, titlePaint);
        canvas.drawText(scoreText, centreX, scoreBaseline, scorePaint);
    }
}
