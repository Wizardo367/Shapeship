package harishbhagat.shapeship.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Random;

import harishbhagat.shapeship.maths.Vector2;

public class UfoTest
{
    private static final float DELTA = 1e-3f;
    private static final float WIDTH = 1000;
    private static final Vector2 SIZE = new Vector2(200, 104);

    /** Returns a UFO from the given seed, stepped until it's wholly on screen. */
    private static Ufo onScreen(final long seed)
    {
        final Ufo ufo = new Ufo(SIZE, 300, WIDTH, new Random(seed));
        while (!ufo.isOnScreen()) ufo.update(0.05f);
        return ufo;
    }

    @Test
    public void slidesInFromJustOffEitherSide()
    {
        // One generator for all of them, as in the game: the first values from neighbouring seeds are alike
        final Random random = new Random(7);
        boolean fromLeft = false, fromRight = false;
        for (int i = 0; i < 20; ++i)
        {
            final Ufo ufo = new Ufo(SIZE, 300, WIDTH, random);
            assertFalse(ufo.isOnScreen());
            assertEquals(300, ufo.getPosition().y, DELTA);

            if (ufo.getPosition().x < 0)
            {
                fromLeft = true;
                assertEquals(-100, ufo.getPosition().x, DELTA);
                assertEquals(Ufo.SPEED * WIDTH, ufo.getVelocity().x, DELTA);
            }
            else
            {
                fromRight = true;
                assertEquals(WIDTH + 100, ufo.getPosition().x, DELTA);
                assertEquals(-Ufo.SPEED * WIDTH, ufo.getVelocity().x, DELTA);
            }
        }
        assertTrue(fromLeft && fromRight);
    }

    @Test
    public void turnsBackAtEachEdgeAndStaysLevel()
    {
        final Ufo ufo = onScreen(1);
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;

        // Ten seconds is two and a half crossings
        for (int step = 0; step < 200; ++step)
        {
            ufo.update(0.05f);
            minX = Math.min(minX, ufo.getPosition().x);
            maxX = Math.max(maxX, ufo.getPosition().x);
            assertEquals(300, ufo.getPosition().y, DELTA);
        }

        // It reaches each edge, overshooting by at most one step
        final float step = Ufo.SPEED * WIDTH * 0.05f;
        assertTrue(minX <= 100 && minX >= 100 - step);
        assertTrue(maxX >= WIDTH - 100 && maxX <= WIDTH - 100 + step);
    }

    @Test
    public void firesWithGapsBetweenTheShortestAndLongest()
    {
        final Ufo ufo = onScreen(4);
        float sinceShot = 0;
        int shots = 0;

        for (int step = 0; step < 2000; ++step)
        {
            ufo.update(0.01f);
            sinceShot += 0.01f;
            if (ufo.takeShot())
            {
                if (shots > 0) assertTrue("gap " + sinceShot, sinceShot >= Ufo.MIN_SHOT_GAP - 0.011f && sinceShot <= Ufo.MAX_SHOT_GAP + 0.011f);
                sinceShot = 0;
                ++shots;
            }
        }

        // Twenty seconds holds between 20 / MAX_SHOT_GAP and 20 / MIN_SHOT_GAP shots, plus the first
        assertTrue("shots " + shots, shots >= (int) (20 / Ufo.MAX_SHOT_GAP) && shots <= (int) (20 / Ufo.MIN_SHOT_GAP) + 1);
    }

    @Test
    public void holdsFireUntilWhollyOnScreen()
    {
        final Ufo ufo = new Ufo(SIZE, 300, WIDTH, new Random(2));
        // Wait well past the longest gap without moving it
        ufo.setVelocity(new Vector2());
        ufo.update(Ufo.MAX_SHOT_GAP + 1);
        assertFalse(ufo.takeShot());

        // Once on screen, the overdue shot fires straight away
        ufo.setPosition(new Vector2(WIDTH / 2, 300));
        assertTrue(ufo.takeShot());
        assertFalse(ufo.takeShot());
    }
}
