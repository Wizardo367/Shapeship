package harishbhagat.shapeship.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;

public class GameTest
{
    private static final float DELTA = 1e-3f;
    private static final float WIDTH = 1000;
    private static final float HEIGHT = 2000;
    /** Sizes as the renderer would give them on a 1000-pixel-wide screen. */
    private static final Game.Sizes SIZES = new Game.Sizes(
            new Vector2(130, 127), new Vector2(13, 45), new Vector2(110, 110), new Vector2(110, 110));

    private RecordingListener events;
    private Game game;

    @Before
    public void startAGame()
    {
        events = new RecordingListener();
        game = new Game(WIDTH, HEIGHT, SIZES, new Random(3), events);
    }

    /** Spawns the first asteroid, out of sight, and returns it. */
    private Body spawnAsteroid()
    {
        game.update(0);
        assertEquals(1, game.asteroids().size());
        return game.asteroids().get(0);
    }

    @Test
    public void theShipStartsCentredAboveTheBottomAndThePauseButtonInTheTopRight()
    {
        final float shipY = HEIGHT - 127 / 2f - Game.PLAYER_BOTTOM_MARGIN * HEIGHT;
        assertEquals(new Vector2(WIDTH / 2, shipY), game.player().getPosition());

        final float margin = Game.EDGE_MARGIN * WIDTH;
        assertEquals(new Vector2(WIDTH - 55 - margin, 55 + margin), game.pauseButton().getPosition());
        assertEquals(0, game.score());
        assertFalse(game.isOver());
    }

    @Test
    public void tappingThePauseButtonAsksForAPauseWithoutFiring()
    {
        game.tap(game.pauseButton().getPosition());

        assertEquals(1, events.pauses);
        assertEquals(0, events.shots);
        assertEquals(WIDTH / 2, game.player().getPosition().x, DELTA);
    }

    @Test
    public void tappingElsewhereMovesTheShipUnderTheTapAndFires()
    {
        final float shipY = game.player().getPosition().y;

        game.tap(new Vector2(300, 900));

        assertEquals(new Vector2(300, shipY), game.player().getPosition());
        assertEquals(1, events.shots);
        assertEquals(1, game.bullets().size());
        assertEquals(300, game.bullets().get(0).getPosition().x, DELTA);
        assertTrue(game.bullets().get(0).getPosition().y < shipY);
    }

    @Test
    public void theShipFiresOnEveryTap()
    {
        // The 2016 game allowed two bullets on screen at once, and step 2 made a one-second
        // cooldown work; both were removed
        for (int i = 0; i < 10; ++i)
            game.tap(new Vector2(100 + 50 * i, 900));

        assertEquals(10, events.shots);
        assertEquals(10, game.bullets().size());
        // The ship still moves under each tap
        assertEquals(550, game.player().getPosition().x, DELTA);
    }

    @Test
    public void bulletsMoveUpTheScreen()
    {
        game.tap(new Vector2(300, 900));
        final float startY = game.bullets().get(0).getPosition().y;

        game.update(0.1f);

        assertEquals(startY + BulletSpawner.SPEED * 0.1f, game.bullets().get(0).getPosition().y, DELTA);
    }

    @Test
    public void asteroidsAreAimedAtTheShip()
    {
        final Body asteroid = spawnAsteroid();

        final Vector2 towardsShip = game.player().getPosition().subtract(asteroid.getPosition()).normalise();
        final Vector2 heading = asteroid.getVelocity().normalise();
        assertEquals(towardsShip.x, heading.x, DELTA);
        assertEquals(towardsShip.y, heading.y, DELTA);
    }

    @Test
    public void shootingAnAsteroidScoresAndExplodes()
    {
        final Body asteroid = spawnAsteroid();
        game.tap(new Vector2(300, 900));
        final Vector2 bullet = game.bullets().get(0).getPosition();

        // Just above the bullet, clear of the ship
        asteroid.setPosition(new Vector2(bullet.x, bullet.y - 60));
        asteroid.setVelocity(new Vector2());
        game.update(0);

        assertEquals(Game.POINTS_PER_HIT, game.score());
        assertEquals(1, events.explosions);
        assertEquals(0, game.bullets().size());
        assertFalse(game.asteroids().contains(asteroid));
        assertFalse(game.isOver());
    }

    @Test
    public void anAsteroidHittingTheShipEndsTheGameOnceWithTheScore()
    {
        final Body asteroid = spawnAsteroid();
        asteroid.setPosition(new Vector2(game.player().getPosition()));
        game.update(0);

        assertTrue(game.isOver());
        assertEquals(1, events.explosions);
        assertEquals(1, events.gameOvers);
        assertEquals(0, events.lastScore);

        // Nothing more happens once the game is over
        final Vector2 shipPosition = new Vector2(game.player().getPosition());
        game.update(1);
        game.tap(new Vector2(100, 900));
        assertEquals(1, events.gameOvers);
        assertEquals(0, events.shots);
        assertEquals(shipPosition, game.player().getPosition());
    }

    /** Counts what the game reports. */
    private static final class RecordingListener implements Game.Listener
    {
        int shots, explosions, pauses, gameOvers, lastScore = -1;

        @Override public void onShot() { ++shots; }
        @Override public void onExplosion() { ++explosions; }
        @Override public void onPauseRequested() { ++pauses; }
        @Override public void onGameOver(final int score) { ++gameOvers; lastScore = score; }
    }
}
