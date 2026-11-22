package harishbhagat.shapeship.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;
import harishbhagat.shapeship.physics.Collision;

/**
 * One game of Shapeship: the ship, its bullets, the asteroids, the pause button and the score.
 *
 * Positions and sizes are in pixels on a screen of the given size. Everything is sized as a
 * fraction of the screen width, by the textures that draw it. The game uses no Android classes:
 * the game loop calls tap and update on its thread, and the game tells its listener what
 * happened, so that the app can play sounds, save the score and change screens.
 */
public final class Game
{
    // --- Member(s)

    /** Told what happens in the game. Called on the thread that calls tap and update. */
    public interface Listener
    {
        /** The ship fired a bullet. */
        void onShot();

        /** An asteroid was destroyed, or the ship was. */
        void onExplosion();

        /** The pause button was tapped. */
        void onPauseRequested();

        /** An asteroid hit the ship. Called once, after onExplosion; the game ignores taps and updates from now on. */
        void onGameOver(int score);
    }

    /** The sizes of the things in the game, in pixels: those of the textures that draw them. */
    public static final class Sizes
    {
        public final Vector2 ship;
        public final Vector2 bullet;
        public final Vector2 asteroid;
        public final Vector2 pauseButton;

        public Sizes(final Vector2 ship, final Vector2 bullet, final Vector2 asteroid, final Vector2 pauseButton)
        {
            this.ship = ship;
            this.bullet = bullet;
            this.asteroid = asteroid;
            this.pauseButton = pauseButton;
        }
    }

    /** The pause button's width as a fraction of the screen width. */
    public static final float PAUSE_BUTTON_WIDTH = 0.11f;
    /** The pause button's gap from the screen's edges, as a fraction of the screen width. */
    static final float EDGE_MARGIN = 0.05f;
    /** The gap below the ship, as a fraction of the screen height. */
    static final float PLAYER_BOTTOM_MARGIN = 0.05f;
    /** How far past the screen bodies may go before they're removed, so that asteroids can spawn out of sight. */
    static final float BOUNDARY_SCALE = 1.2f;
    static final int MAX_ASTEROIDS = 7;
    static final int POINTS_PER_HIT = 10;

    private final Listener listener;
    private final Player player;
    private final AsteroidSpawner asteroidSpawner;
    private final Body pauseButton;
    private final List<Body> asteroids;
    private final List<Body> bullets;
    private int score = 0;
    private boolean over = false;

    // --- Constructor(s)

    /** Starts a game on a screen of the given size, in pixels. */
    public Game(final float width, final float height, final Sizes sizes, final Random random, final Listener listener)
    {
        this.listener = listener;

        final RectangleArea screen = new RectangleArea(0, width, 0, height);
        final RectangleArea boundaries = screen.scale(BOUNDARY_SCALE);

        // Positions are centres: centre the player horizontally, above the bottom of the screen
        player = new Player(sizes.ship, sizes.bullet, boundaries, width);
        player.setPosition(new Vector2(width / 2, height - sizes.ship.y / 2 - PLAYER_BOTTOM_MARGIN * height));

        asteroidSpawner = new AsteroidSpawner(new Asteroid(sizes.asteroid), MAX_ASTEROIDS, boundaries, screen, random);

        // The pause button sits in the top-right corner
        pauseButton = new Body(sizes.pauseButton, false, ColliderShape.RECTANGLE, 0);
        final float margin = EDGE_MARGIN * width;
        pauseButton.setPosition(new Vector2(width - sizes.pauseButton.x / 2 - margin, sizes.pauseButton.y / 2 + margin));

        asteroids = Collections.unmodifiableList(asteroidSpawner.entities);
        bullets = Collections.unmodifiableList(player.bulletSpawner.entities);
    }

    // --- Function(s)

    public Player player() { return player; }

    /** The asteroids, for drawing. */
    public List<Body> asteroids() { return asteroids; }

    /** The player's bullets, for drawing. */
    public List<Body> bullets() { return bullets; }

    public Body pauseButton() { return pauseButton; }

    public int score() { return score; }

    /** Returns true once an asteroid has hit the ship. */
    public boolean isOver() { return over; }

    // --- Method(s)

    /**
     * Handles a tap at the given point, at the given time from System.nanoTime(). Tapping the
     * pause button asks for a pause; tapping anywhere else moves the ship below the tap and fires.
     */
    public void tap(final Vector2 point, final long nowNanos)
    {
        if (over) return;

        if (Collision.PointAABBIntersectionTest(pauseButton, point))
        {
            listener.onPauseRequested();
            return;
        }

        player.setPosition(new Vector2(point.x, player.getPosition().y));
        if (player.fire(nowNanos)) listener.onShot();
    }

    /** Spawns asteroids, moves everything on by the given number of seconds and handles collisions. */
    public void update(final float deltaTime)
    {
        if (over) return;

        // Spawn asteroids, aimed at the player
        if (asteroidSpawner.spawn())
            asteroidSpawner.newest().setTarget(player.getPosition());

        asteroidSpawner.update(deltaTime);
        player.update(deltaTime);
        player.bulletSpawner.update(deltaTime);

        // Player & Asteroid
        for (Body asteroid : asteroidSpawner.entities)
        {
            if (Collision.HasCollidedWith(player, asteroid).collision)
            {
                over = true;
                listener.onExplosion();
                listener.onGameOver(score);
                return;
            }
        }

        // Bullet & Asteroid
        final int hits = BulletHits.removeHits(player.bulletSpawner.entities, asteroidSpawner.entities);
        if (hits > 0)
        {
            score += POINTS_PER_HIT * hits;
            listener.onExplosion();
        }

        // Asteroid & Asteroid
        Collision.ResolveAll(asteroidSpawner.entities);
    }
}
