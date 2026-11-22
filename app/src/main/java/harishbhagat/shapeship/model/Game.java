package harishbhagat.shapeship.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import harishbhagat.shapeship.maths.RectangleArea;
import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;
import harishbhagat.shapeship.physics.Collision;

/**
 * One game of Shapeship: the ship, the asteroids, the UFO, the bullets, the pause button and
 * the score.
 *
 * Tilting the phone steers the ship and tapping fires. From UFO_SCORE points, a UFO crosses
 * near the top of the screen firing down; its bullets end the game, as asteroids do.
 *
 * Positions and sizes are in pixels on a screen of the given size. Everything is sized as a
 * fraction of the screen width, by the textures that draw it. The game uses no Android classes:
 * the game loop calls setTilt, tap and update on its thread, and the game tells its listener
 * what happened, so that the app can play sounds, save the score and change screens.
 */
public final class Game
{
    // --- Member(s)

    /** Told what happens in the game. Called on the thread that calls tap and update. */
    public interface Listener
    {
        /** The ship fired a bullet. */
        void onShot();

        /** A UFO fired a bullet. */
        void onEnemyShot();

        /** An asteroid or a UFO was destroyed, or the ship was. */
        void onExplosion();

        /** The pause button was tapped. */
        void onPauseRequested();

        /** The ship was hit. Called once, after onExplosion; the game ignores input and updates from now on. */
        void onGameOver(int score);
    }

    /** The sizes of the things in the game, in pixels: those of the textures that draw them. */
    public static final class Sizes
    {
        public final Vector2 ship;
        public final Vector2 bullet;
        public final Vector2 asteroid;
        public final Vector2 pauseButton;
        public final Vector2 ufo;
        public final Vector2 enemyBullet;

        public Sizes(final Vector2 ship, final Vector2 bullet, final Vector2 asteroid, final Vector2 pauseButton, final Vector2 ufo, final Vector2 enemyBullet)
        {
            this.ship = ship;
            this.bullet = bullet;
            this.asteroid = asteroid;
            this.pauseButton = pauseButton;
            this.ufo = ufo;
            this.enemyBullet = enemyBullet;
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

    /** UFOs appear once the score reaches this. */
    static final int UFO_SCORE = 250;
    static final int UFO_POINTS = 50;
    /** How long after a UFO is destroyed the next one arrives, in seconds. */
    static final float UFO_RESPAWN_SECONDS = 5;
    /** The height of a UFO's centre, as a fraction of the screen height: below the score and pause button. */
    static final float UFO_HEIGHT = 0.15f;
    /** A UFO's bullets' speed in screen heights per second, down the screen. */
    static final float ENEMY_BULLET_SPEED = 0.3f;

    private final Listener listener;
    private final float width;
    private final float height;
    private final Sizes sizes;
    private final Random random;
    private final Player player;
    private final AsteroidSpawner asteroidSpawner;
    private final BulletSpawner enemyBullets;
    private final Body pauseButton;
    private final List<Body> asteroids;
    private final List<Body> bullets;
    private final List<Body> enemyBulletsView;
    private Ufo ufo = null;
    private float untilUfo = 0;
    private float tilt = 0;
    private int score = 0;
    private boolean over = false;

    // --- Constructor(s)

    /** Starts a game on a screen of the given size, in pixels. */
    public Game(final float width, final float height, final Sizes sizes, final Random random, final Listener listener)
    {
        this(width, height, sizes, random, listener, MAX_ASTEROIDS);
    }

    /** Starts a game with a given number of asteroids, for tests. */
    Game(final float width, final float height, final Sizes sizes, final Random random, final Listener listener, final int maxAsteroids)
    {
        this.listener = listener;
        this.width = width;
        this.height = height;
        this.sizes = sizes;
        this.random = random;

        final RectangleArea screen = new RectangleArea(0, width, 0, height);
        final RectangleArea boundaries = screen.scale(BOUNDARY_SCALE);

        // Positions are centres: centre the player horizontally, above the bottom of the screen
        player = new Player(sizes.ship, sizes.bullet, boundaries, width);
        player.setPosition(new Vector2(width / 2, height - sizes.ship.y / 2 - PLAYER_BOTTOM_MARGIN * height));

        asteroidSpawner = new AsteroidSpawner(new Asteroid(sizes.asteroid), maxAsteroids, boundaries, screen, random);

        final Vector2 enemyMargins = new Vector2(0, Ufo.BULLET_GAP * sizes.enemyBullet.y);
        enemyBullets = new BulletSpawner(new Bullet(sizes.enemyBullet), enemyMargins, new Vector2(0, ENEMY_BULLET_SPEED * height), boundaries);

        // The pause button sits in the top-right corner
        pauseButton = new Body(sizes.pauseButton, false, ColliderShape.RECTANGLE, 0);
        final float margin = EDGE_MARGIN * width;
        pauseButton.setPosition(new Vector2(width - sizes.pauseButton.x / 2 - margin, sizes.pauseButton.y / 2 + margin));

        asteroids = Collections.unmodifiableList(asteroidSpawner.entities);
        bullets = Collections.unmodifiableList(player.bulletSpawner.entities);
        enemyBulletsView = Collections.unmodifiableList(enemyBullets.entities);
    }

    // --- Function(s)

    public Player player() { return player; }

    /** The asteroids, for drawing. */
    public List<Body> asteroids() { return asteroids; }

    /** The player's bullets, for drawing. */
    public List<Body> bullets() { return bullets; }

    /** The UFOs' bullets, for drawing. They stay in flight after the UFO that fired them is destroyed. */
    public List<Body> enemyBullets() { return enemyBulletsView; }

    /** The UFO, or null if there isn't one at the moment. */
    public Ufo ufo() { return ufo; }

    public Body pauseButton() { return pauseButton; }

    public int score() { return score; }

    /** Returns true once the ship has been hit. */
    public boolean isOver() { return over; }

    // --- Method(s)

    /** Sets how the phone is tilted, from -1 (full speed left) to 1 (full speed right); see Tilt. */
    public void setTilt(final float tilt)
    {
        this.tilt = Math.max(-1, Math.min(tilt, 1));
    }

    /** Handles a tap at the given point. Tapping the pause button asks for a pause; tapping anywhere else fires. */
    public void tap(final Vector2 point)
    {
        if (over) return;

        if (Collision.PointAABBIntersectionTest(pauseButton, point))
        {
            listener.onPauseRequested();
            return;
        }

        player.fire();
        listener.onShot();
    }

    /** Steers the ship, spawns asteroids and UFOs, moves everything on by the given number of seconds and handles collisions. */
    public void update(final float deltaTime)
    {
        if (over) return;

        player.steer(tilt, deltaTime);

        // Spawn asteroids, aimed at the player
        if (asteroidSpawner.spawn())
            asteroidSpawner.newest().setTarget(player.getPosition());

        updateUfo(deltaTime);

        asteroidSpawner.update(deltaTime);
        player.bulletSpawner.update(deltaTime);
        enemyBullets.update(deltaTime);

        // The ship is destroyed by an asteroid or a UFO's bullet
        if (hitsPlayer(asteroidSpawner.entities) || hitsPlayer(enemyBullets.entities))
        {
            over = true;
            listener.onExplosion();
            listener.onGameOver(score);
            return;
        }

        // Bullet & Asteroid
        final int hits = BulletHits.removeHits(player.bulletSpawner.entities, asteroidSpawner.entities);
        if (hits > 0)
        {
            addPoints(POINTS_PER_HIT * hits);
            listener.onExplosion();
        }

        // Bullet & UFO
        if (ufo != null && removeHit(player.bulletSpawner.entities, ufo))
        {
            ufo = null;
            untilUfo = UFO_RESPAWN_SECONDS;
            addPoints(UFO_POINTS);
            listener.onExplosion();
        }

        // Asteroid & Asteroid
        Collision.ResolveAll(asteroidSpawner.entities);
    }

    /** Brings in a UFO once the score allows and the last one has been gone long enough, and lets it fire. */
    private void updateUfo(final float deltaTime)
    {
        if (ufo == null)
        {
            if (score < UFO_SCORE) return;
            untilUfo -= deltaTime;
            if (untilUfo > 0) return;
            ufo = new Ufo(sizes.ufo, UFO_HEIGHT * height, width, random);
            return;
        }

        ufo.update(deltaTime);
        if (ufo.takeShot())
        {
            enemyBullets.spawn(ufo);
            listener.onEnemyShot();
        }
    }

    /** Returns true if any of the bodies has hit the ship. */
    private boolean hitsPlayer(final List<Body> bodies)
    {
        for (Body body : bodies)
            if (Collision.HasCollidedWith(player, body).collision) return true;
        return false;
    }

    /** Removes the first bullet that has hit the target, and returns true if there was one. */
    private static boolean removeHit(final List<Body> bullets, final Body target)
    {
        final Iterator<Body> iterator = bullets.iterator();
        while (iterator.hasNext())
        {
            if (Collision.HasCollidedWith(iterator.next(), target).collision)
            {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /** Adds to the score. Package-private so that tests can reach UFO_SCORE without shooting 25 asteroids. */
    void addPoints(final int points)
    {
        score += points;
    }
}
