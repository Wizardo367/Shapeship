package harishbhagat.shapeship.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScoreFormatTest
{
    @Test
    public void padsToSixDigits()
    {
        assertEquals("000000", ScoreFormat.format(0));
        assertEquals("000010", ScoreFormat.format(10));
        assertEquals("123450", ScoreFormat.format(123450));
    }

    @Test
    public void showsLongerScoresInFull()
    {
        assertEquals("1000000", ScoreFormat.format(1_000_000));
    }

    @Test
    public void showsNegativeScoresAsZero()
    {
        assertEquals("000000", ScoreFormat.format(-5));
    }
}
