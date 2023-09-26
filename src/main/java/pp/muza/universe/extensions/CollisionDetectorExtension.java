package pp.muza.universe.extensions;

import pp.muza.universe.body.Body;

import java.util.Collection;

/**
 * A CollisionDetectorExtension is an extension that can detect collision pairs.
 */
public interface CollisionDetectorExtension extends SystemExtension {

    /**
     * Returns the collision pairs detected in this step (predicted and detected).
     *
     * @return the collision pairs detected in this step
     */
    Collection<CollisionPair> getCollisionPairs();

    /**
     * Returns true if the body is in collision with another body in the last step.
     *
     * @param body the index of the body
     * @return true if the body is in collision in the last step
     */
    boolean isInCollision(Body body);

    void getClustersCount();

    Collection<CollisionPair> getBodyCollisions(Body body);

    Collection<CollisionPair> getCollisionCluster(int cluster);

    interface SetDetectorExtension extends SystemExtension {

        /**
         * The extension that uses the collision detector to update the system.
         *
         * @param collisionDetector the collision detector
         */
        void setCollisionDetector(CollisionDetectorExtension collisionDetector);

    }
}
