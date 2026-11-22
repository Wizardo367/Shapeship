package harishbhagat.shapeship.graphics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import harishbhagat.shapeship.maths.RectangleArea;

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

    private static void assertArea(final float minX, final float maxX, final float minY, final float maxY, final RectangleArea actual)
    {
        assertEquals("minX", minX, actual.minX, 1e-3f);
        assertEquals("maxX", maxX, actual.maxX, 1e-3f);
        assertEquals("minY", minY, actual.minY, 1e-3f);
        assertEquals("maxY", maxY, actual.maxY, 1e-3f);
    }

    @Test
    public void cropsTheSidesOfAnImageWiderThanTheScreen()
    {
        // The 600 x 1067 background on a tall phone screen keeps its full height and loses
        // about 41 pixels from each side, instead of being stretched taller
        final float width = 1067 * 1008 / 2076f;
        assertArea((600 - width) / 2, (600 + width) / 2, 0, 1067, Textures.centreCrop(600, 1067, 1008, 2076));
    }

    @Test
    public void cropsTheTopAndBottomOfAnImageTallerThanTheScreen()
    {
        // A tablet's 1600 x 2400 screen is 2:3, wider in proportion than the 9:16 image
        final float height = 600 * 2400 / 1600f;
        assertArea(0, 600, (1067 - height) / 2, (1067 + height) / 2, Textures.centreCrop(600, 1067, 1600, 2400));
    }

    @Test
    public void usesAllOfAnImageTheSameShapeAsTheScreen()
    {
        assertArea(0, 600, 0, 1000, Textures.centreCrop(600, 1000, 1200, 2000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAnEmptyScreen()
    {
        Textures.centreCrop(600, 1067, 0, 0);
    }
}
