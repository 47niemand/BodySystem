package pp.muza.universe.extensions.collision;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.CollisionPair;

import java.util.Collection;
import java.util.Iterator;

/**
 * A Collision extension. It checks collisions between bodies in the system by
 * using the CollisionDetectorExtension.
 * It adjusts the position of the bodies that are going to collide in the next
 * step.
 * It changes the speed of the bodies depending on the collision type.
 */
public class SimpleCollision extends BaseSystemExtension implements CollisionDetectorExtension.SetDetectorExtension {

    private CollisionDetectorExtension collisionDetector;

    private int processed = 0;
    private int skipped = 0;
    private int error = 0;

    @Override
    protected void doApply(double dt) {

        Collection<CollisionPair> collisionPairs = collisionDetector.getCollisionPairs();
        // collisionPairs.sort(Comparator.comparingDouble(o -> -o.getDotProduct()));
        Iterator<CollisionPair> iterator = collisionPairs.iterator();
        while (iterator.hasNext()) {
            CollisionPair collisionPair = iterator.next();
            if (collisionPair.isInCollision() && collisionPair.getDotProduct() > 0) {
                if (processCollision(collisionPair)) {
                    // iterator.remove();
                    processed++;
                } else {
                    error++;
                }
            } else {
                skipped++;
            }
        }
    }

    private boolean processCollision(CollisionPair collisionPair) {
        Body body1 = collisionPair.body1;
        Body body2 = collisionPair.body2;
        Complex distance0 = collisionPair.getDistance();
        double dot = collisionPair.getDotProduct();

        double collisionScale = dot / distance0.squareModule();
        Complex distance = Complex.scale(distance0, collisionScale);
        double totalMass = body1.m + body2.m;
        if (body1.isPinned && body2.isPinned) {
            // do nothing both are pinned
            return false;
        } else if (body1.isPinned) {
            // body1 mass is infinite
            body2.velocity.change(Complex.scale(distance, -2));
        } else if (body2.isPinned) {
            // body2 mass is infinite
            body1.velocity.change(Complex.scale(distance, 2));
        } else if (totalMass != 0) {
            double collisionWeightA = 2 * body2.m / totalMass;
            double collisionWeightB = 2 * body1.m / totalMass;
            body1.velocity.change(Complex.scale(distance, collisionWeightA));
            body2.velocity.change(Complex.scale(distance, -collisionWeightB));
        }
        return true;
    }

    @Override
    public void sync() {

    }

    @Override
    public String getState() {
        return this.getClass().getSimpleName() + ": processed: " + processed + ", skipped: " + skipped + ", error: "
                + error;
    }

    @Override
    public void setCollisionDetector(CollisionDetectorExtension collisionDetector) {
        this.collisionDetector = collisionDetector;

    }
}
