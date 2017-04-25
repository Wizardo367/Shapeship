package harishbhagat.shapeship.game;

import harishbhagat.shapeship.maths.Vector2;

/**
 * Used to define methods and functions for the implementation of a cannon.
 *
 * Created by Harish Bhagat on 28/03/2016.
 */
public interface Cannon
{
    // --- Function(s)

    /** Used to define how to fire a bullet. */
    public boolean fire();

    // --- Method(s)

    /** Used to define how to aim the object with the cannon. */
    public void aim(Vector2 target);
}
