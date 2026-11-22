package harishbhagat.shapeship.model;

import java.util.Random;

import harishbhagat.shapeship.maths.Vector2;
import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.ColliderShape;

/**
 * A UFO: it slides in from one side near the top of the screen, then back and forth between
 * the edges, firing straight down every two thirds of a second to two seconds while it's wholly
 * on screen.
 *
 * Created by Harish Bhagat on 29/03/2016.
 */
public class Ufo extends Body
{
    // --- Member(s)

    /** A UFO's width as a fraction of the screen width; the 2016 art is about twice the ship's. */
    public static final float WIDTH_FRACTION = 0.2f;
    /** How much faster than first tuned UFOs move and fire: half as fast again. */
    static final float PACE = 1.5f;
    /** Its speed in screen widths per second. */
    static final float SPEED = 0.25f * PACE;
    /** The shortest and longest waits between shots, in seconds: the 2016 one to three seconds, sped up. */
    static final float MIN_SHOT_GAP = 1 / PACE;
    static final float MAX_SHOT_GAP = 3 / PACE;
    /** How far below the UFO a bullet starts, in bullet heights. */
    static final float BULLET_GAP = 0.25f;

    private final float screenWidth;
    private final Random random;
    private float untilNextShot;

    // --- Constructor(s)

    /** Starts a UFO just off a side of the screen chosen at random, at the given height, sliding in. */
    public Ufo(final Vector2 size, final float y, final float screenWidth, final Random random)
    {
        super(size, true, ColliderShape.RECTANGLE, 250);
        this.screenWidth = screenWidth;
        this.random = random;

        final boolean fromLeft = random.nextBoolean();
        final float halfWidth = size.x / 2;
        setPosition(new Vector2(fromLeft ? -halfWidth : screenWidth + halfWidth, y));
        setVelocity(new Vector2((fromLeft ? 1 : -1) * SPEED * screenWidth, 0));
        untilNextShot = nextShotGap();
    }

    // --- Function(s)

    /** Returns true if no part of the UFO is off the sides of the screen. */
    public boolean isOnScreen()
    {
        final float halfWidth = boundingBoxSize.x / 2;
        final float x = getPosition().x;
        return x - halfWidth >= 0 && x + halfWidth <= screenWidth;
    }

    /**
     * Returns true if it's time to fire, and starts waiting for the next shot. A UFO that's
     * still sliding onto the screen holds its fire.
     */
    public boolean takeShot()
    {
        if (untilNextShot > 0 || !isOnScreen()) return false;
        untilNextShot = nextShotGap();
        return true;
    }

    private float nextShotGap()
    {
        return MIN_SHOT_GAP + random.nextFloat() * (MAX_SHOT_GAP - MIN_SHOT_GAP);
    }

    // --- Method(s)

    /** Moves the UFO on, turning it back at the screen's edges, and counts down to its next shot. */
    @Override
    public void update(final float deltaTime)
    {
        super.update(deltaTime);

        // Turn only when heading out, so that a UFO sliding in from off screen isn't turned back
        final float halfWidth = boundingBoxSize.x / 2;
        final float x = getPosition().x;
        final Vector2 velocity = getVelocity();
        if ((x - halfWidth <= 0 && velocity.x < 0) || (x + halfWidth >= screenWidth && velocity.x > 0))
            setVelocity(new Vector2(-velocity.x, velocity.y));

        untilNextShot -= deltaTime;
    }
}
