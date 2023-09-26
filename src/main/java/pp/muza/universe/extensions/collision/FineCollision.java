package pp.muza.universe.extensions.collision;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.CollisionPair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Collision extension. It checks collisions between bodies in the system by
 * using the CollisionDetectorExtension.
 * It adjusts the position of the bodies that are going to collide in the next
 * step.
 * It changes the velocity of the bodies depending on the collision type.
 */
public class FineCollision extends BaseSystemExtension implements CollisionDetectorExtension.SetDetectorExtension {

    private static final double ERROR = CollisionPair.ERROR;
    private CollisionDetectorExtension collisionDetector;

    private int checked = 0;
    private int willCollide = 0;
    private int missed = 0;
    private int adjusted = 0;
    private int error = 0;

    @Override
    protected void doApply(double dt) {
        boolean assertEnabled = false;
        assert assertEnabled = true;
        if (!assertEnabled) {
            error = -1;
        }
        double tS = 0.0;
        double finalTS;

        collisionDetector.getClustersCount();

        List<CollisionPair> collisionPairs = new ArrayList<>(collisionDetector.getCollisionPairs());

        if (collisionPairs.isEmpty()) {
            return;
        }

        if (assertEnabled) {
            List<CollisionPair> l = collisionPairs.stream().filter(
                    collisionPair -> collisionPair.getDotProduct() >= 0 && collisionPair.getModuleDistance() < 0)
                    .collect(Collectors.toList());

            if (!l.isEmpty()) {
                missed += l.size();
                for (CollisionPair collisionPair : l) {
                    collisionPair.body1.tag = Color.WHITE;
                    collisionPair.body2.tag = Color.WHITE;
                }
            }
        }
        // assert l.isEmpty();

        do {
            checked += collisionPairs.size();
            finalTS = tS;

            // todo check if multiple collisions are possible
            collisionPairs.sort(Comparator.comparingDouble(CollisionPair::getTimeToCollision));

            double minTime = collisionPairs.stream().peek(collisionPair -> {
                collisionPair.body1.setStep(system.getStep());
                collisionPair.body2.setStep(system.getStep());
            }).filter(
                    collisionPair -> collisionPair.getDotProduct() >= 0 && collisionPair.getTimeToCollision() > -ERROR)
                    .mapToDouble(CollisionPair::getTimeToCollision).min().orElse(Double.NaN);

            if ((minTime > (dt - tS)) || Double.isNaN(minTime) || (minTime < 0)) {

                if (assertEnabled) {
                    List<CollisionPair> l2 = collisionPairs.stream()
                            .filter(collisionPair -> collisionPair.getDotProduct() >= 0
                                    && collisionPair.getModuleDistance() < -ERROR)
                            .collect(Collectors.toList());
                    if (!l2.isEmpty()) {
                        error += l2.size();
                        for (CollisionPair collisionPair : l2) {
                            collisionPair.body1.tag = Color.WHITE;
                            collisionPair.body2.tag = Color.WHITE;
                        }
                    }
                }

                break;
            }

            tS += minTime;

            for (CollisionPair collisionPair : collisionPairs) {
                adjusted++;
                moveTo(collisionPair.body1, tS);
                moveTo(collisionPair.body2, tS);
            }

            for (CollisionPair pair : collisionPairs) {
                if (pair.getModuleDistance() <= ERROR && pair.getDotProduct() >= 0) {
                    Body body1 = pair.body1;
                    Body body2 = pair.body2;

                    // assert Math.abs(Complex.squareDistance(body1.position, body2.position) -
                    // (body1.r + body2.r) * (body1.r + body2.r)) < ERROR;
                    willCollide++;

                    double collisionScale = pair.getDotProduct() / pair.getDistance().squareModule();
                    Complex collisionResponse = Complex.scale(pair.getDistance(), collisionScale);

                    double totalMass = body1.m + body2.m;
                    double collisionWeightA = 2 * body2.m / totalMass;
                    double collisionWeightB = 2 * body1.m / totalMass;

                    if (body1.isPinned && body2.isPinned) {
                        missed++;
                        // do nothing both are pinned
                    } else if (body1.isPinned) {
                        // mass is infinite
                        body2.velocity.change(Complex.scale(collisionResponse, -2));
                    } else if (body2.isPinned) {
                        // mass is infinite
                        body1.velocity.change(Complex.scale(collisionResponse, 2));
                    } else {
                        body1.velocity.change(Complex.scale(collisionResponse, collisionWeightA));
                        body2.velocity.change(Complex.scale(collisionResponse, -collisionWeightB));
                    }
                }
            }

        } while (finalTS != tS);
    }

    private void moveTo(Body body, double newDt) {
        double dt = newDt - body.getDeltaT();
        assert dt >= 0;
        if (!body.isPinned) {
            body.position.change(Complex.scale(body.velocity, dt));
        } else {
            // assert body.velocity.squareModule() == 0;
        }
        body.addDeltaT(dt);
        assert body.getDeltaT() == newDt;
    }

    @Override
    public void sync() {

    }

    @Override
    public String getState() {
        return this.getClass().getSimpleName() + ": checked: " + checked + ", willCollide: " + willCollide
                + ", skipped: " + missed + ", adjusted: " + adjusted + ", error: " + error;
    }

    @Override
    public void setCollisionDetector(CollisionDetectorExtension collisionDetector) {
        this.collisionDetector = collisionDetector;

    }
}
