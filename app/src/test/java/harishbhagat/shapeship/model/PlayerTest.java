package harishbhagat.shapeship.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;

public class PlayerTest
{
    private static final float DELTA = 1e-4f;

    @Test
    public void keepsTheWholeShipOnScreen()
    {
        // Before the fix, x = 10 with a 100-wide ship moved to 60, and x = -10 wasn't moved at all
        assertEquals(50, Player.clampToScreen(10, 100, 1000), DELTA);
        assertEquals(50, Player.clampToScreen(-10, 100, 1000), DELTA);
        assertEquals(950, Player.clampToScreen(990, 100, 1000), DELTA);
        assertEquals(950, Player.clampToScreen(1200, 100, 1000), DELTA);
    }

    @Test
    public void leavesPositionsOnScreenAlone()
    {
        assertEquals(400, Player.clampToScreen(400, 100, 1000), DELTA);
    }

    private static Player player()
    {
        final Player player = new Player(new Vector2(130, 127), new Vector2(13, 45), new RectangleArea(-100, 1100, -200, 2200), 1000);
        player.setPosition(new Vector2(500, 1800));
        return player;
    }

    @Test
    public void firesEveryTime()
    {
        final Player player = player();

        // No cooldown and no limit on bullets
        for (int i = 0; i < 20; ++i)
            player.fire();

        assertEquals(20, player.bulletSpawner.numberOfSpawned());
    }

    @Test
    public void bulletsStartAboveTheNoseAndFlyUp()
    {
        final Player player = player();
        player.fire();

        final Vector2 bullet = player.bulletSpawner.entities.get(0).getPosition();
        assertEquals(500, bullet.x, DELTA);
        assertEquals(1800 - 127 / 2f - Player.BULLET_GAP * 45, bullet.y, DELTA);
        assertEquals(new Vector2(0, Player.BULLET_SPEED), player.bulletSpawner.entities.get(0).getVelocity());
    }

    @Test
    public void steersInProportionToTheTilt()
    {
        final Player player = player();

        // Full tilt right for a tenth of a second, then half tilt left for the same
        player.steer(1, 0.1f);
        assertEquals(500 + Player.MAX_SPEED * 1000 * 0.1f, player.getPosition().x, DELTA);
        player.steer(-0.5f, 0.1f);
        assertEquals(500 + Player.MAX_SPEED * 1000 * 0.05f, player.getPosition().x, DELTA);
        assertEquals(1800, player.getPosition().y, DELTA);
    }

    @Test
    public void steeringStopsAtTheEdgesAndCapsTheSpeed()
    {
        final Player player = player();
        player.steer(-1, 10);
        assertEquals(65, player.getPosition().x, DELTA);

        // A tilt beyond full is treated as full
        final Player other = player();
        other.steer(5, 0.1f);
        assertEquals(500 + Player.MAX_SPEED * 1000 * 0.1f, other.getPosition().x, DELTA);
    }
}
