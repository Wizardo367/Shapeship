package harishbhagat.shapeship.game;

import static harishbhagat.shapeship.TestSprites.circle;
import static harishbhagat.shapeship.TestSprites.rectangle;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import harishbhagat.shapeship.graphics.Sprite;

public class BulletHitsTest
{
    private static final float DELTA_TIME = 1 / 30f;

    @Test
    public void removesEveryBulletThatHitsAndWhatItHit()
    {
        // Before the fix, removing the first bullet made the loop skip the second
        final Sprite missingAsteroid = circle(900, 100, 100);
        final List<Sprite> bullets = new ArrayList<>(Arrays.asList(rectangle(100, 500, 10, 30), rectangle(500, 500, 10, 30)));
        final List<Sprite> asteroids = new ArrayList<>(Arrays.asList(circle(100, 480, 100), circle(500, 480, 100), missingAsteroid));

        assertEquals(2, BulletHits.removeHits(bullets, asteroids, DELTA_TIME));

        assertEquals(0, bullets.size());
        assertEquals(Arrays.asList(missingAsteroid), asteroids);
    }

    @Test
    public void aBulletDestroysOnlyOneAsteroid()
    {
        final List<Sprite> bullets = new ArrayList<>(Arrays.asList(rectangle(100, 500, 10, 30)));
        final List<Sprite> asteroids = new ArrayList<>(Arrays.asList(circle(100, 480, 100), circle(100, 520, 100)));

        assertEquals(1, BulletHits.removeHits(bullets, asteroids, DELTA_TIME));

        assertEquals(0, bullets.size());
        assertEquals(1, asteroids.size());
    }

    @Test
    public void bulletsThatMissStay()
    {
        final List<Sprite> bullets = new ArrayList<>(Arrays.asList(rectangle(100, 500, 10, 30)));
        final List<Sprite> asteroids = new ArrayList<>(Arrays.asList(circle(400, 100, 100)));

        assertEquals(0, BulletHits.removeHits(bullets, asteroids, DELTA_TIME));

        assertEquals(1, bullets.size());
        assertEquals(1, asteroids.size());
    }
}
