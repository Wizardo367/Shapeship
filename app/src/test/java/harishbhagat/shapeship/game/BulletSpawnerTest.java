package harishbhagat.shapeship.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import harishbhagat.shapeship.maths.Vector2;

public class BulletSpawnerTest
{
    private static final float DELTA = 1e-3f;
    private static final Vector2 SHIP = new Vector2(500, 1800);
    private static final Vector2 SHIP_SIZE = new Vector2(90, 80);

    @Test
    public void playerBulletsStartAboveTheShip()
    {
        // Before the fix they started at y + 80 - 35, below the ship's centre
        final Vector2 position = BulletSpawner.spawnPosition(SHIP, SHIP_SIZE, 0, new Vector2(0, -35));
        assertEquals(500, position.x, DELTA);
        assertEquals(1800 - 40 - 35, position.y, DELTA);
    }

    @Test
    public void enemyBulletsStartBelowTheShooter()
    {
        final Vector2 position = BulletSpawner.spawnPosition(SHIP, SHIP_SIZE, 0, new Vector2(0, 35));
        assertEquals(1800 + 40 + 35, position.y, DELTA);
    }

    @Test
    public void bulletsTurnAboutTheShooterNotTheScreenCorner()
    {
        // Turned 90 degrees clockwise, the nose points right
        final Vector2 position = BulletSpawner.spawnPosition(SHIP, SHIP_SIZE, 90, new Vector2(0, -35));
        assertEquals(500 + 75, position.x, DELTA);
        assertEquals(1800, position.y, DELTA);
    }
}
