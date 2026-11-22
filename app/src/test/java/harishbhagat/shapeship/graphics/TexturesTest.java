package harishbhagat.shapeship.graphics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TexturesTest
{
    @Test
    public void keepsTheDrawablesProportions()
    {
        // The ship's vector is 80 x 78 dp
        assertEquals(137, Textures.heightFor(140, 80, 78));
        // A bullet is much taller than it's wide
        assertEquals(49, Textures.heightFor(14, 23, 80));
    }

    @Test
    public void isAtLeastOnePixelHigh()
    {
        assertEquals(1, Textures.heightFor(1, 100, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsADrawableWithNoSize()
    {
        Textures.heightFor(100, -1, -1);
    }

    @Test
    public void convertsAFractionOfTheScreenToPixels()
    {
        assertEquals(140, Textures.pixels(1080, 0.13f));
        assertEquals(1, Textures.pixels(10, 0.01f));
    }
}
