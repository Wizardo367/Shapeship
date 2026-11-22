package harishbhagat.shapeship.game;

import android.content.Context;

import java.util.Random;

import harishbhagat.shapeship.graphics.Sprite;
import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to spawn asteroid sprites randomly.
 *
 * Created by Harish Bhagat on 30/03/2016.
 */
public class AsteroidSpawner extends RandomSpawner
{
    // --- Member(s)

    /** Asteroids spawn no lower than this fraction of the screen height, so none appear beside the player. */
    static final float MAX_SPAWN_HEIGHT = 0.6f;

    // --- Constructor(s)

    /** Default constructor. */
    public AsteroidSpawner(Context context, final int maxSpawn, final RectangleArea boundaries, final RectangleArea screen)
    {
        this(new Asteroid(context), maxSpawn, boundaries, screen, new Random(), context);
    }

    /** Constructor that takes the asteroid and the source of randomness, for tests. */
    AsteroidSpawner(final Sprite asteroid, final int maxSpawn, final RectangleArea boundaries, final RectangleArea screen, final Random random, Context context)
    {
        super(asteroid, maxSpawn, boundaries, screen, random, context);
    }

    // --- Function(s)

    /** Overridden method for spawning asteroid sprites. */
    @Override
    public boolean spawn() throws CloneNotSupportedException
    {
        if (!super.spawn()) return false;

        // Make the asteroid fall at between 50 and 150 pixels per second
        final Sprite asteroid = entities.lastElement();
        asteroid.setVelocity(new Vector2(0, random.nextInt(100) + 50));
        return true;
    }

    /** Overridden method that keeps asteroids from spawning beside or below the player. */
    @Override
    protected RectangleArea getSpawnArea()
    {
        final RectangleArea area = super.getSpawnArea();
        return new RectangleArea(area.minX, area.maxX, area.minY, Math.min(area.maxY, screen.maxY * MAX_SPAWN_HEIGHT));
    }
}
