package harishbhagat.shapeship.model;

import java.util.Iterator;
import java.util.List;

import harishbhagat.shapeship.physics.Body;
import harishbhagat.shapeship.physics.Collision;

/**
 * Used to find bullets that have hit targets.
 */
final class BulletHits
{
    // --- Constructor(s)

    private BulletHits() {}

    // --- Function(s)

    /**
     * Removes each bullet that has hit a target, along with the target it hit.
     * A bullet destroys at most one target. Returns the number of hits.
     */
    static int removeHits(final List<Body> bullets, final List<Body> targets)
    {
        int hits = 0;

        // Removing through the iterators keeps the loops from skipping the item after a removed one
        final Iterator<Body> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext())
        {
            final Body bullet = bulletIterator.next();
            final Iterator<Body> targetIterator = targets.iterator();

            while (targetIterator.hasNext())
            {
                if (Collision.HasCollidedWith(bullet, targetIterator.next()).collision)
                {
                    targetIterator.remove();
                    bulletIterator.remove();
                    ++hits;
                    break;
                }
            }
        }

        return hits;
    }
}
