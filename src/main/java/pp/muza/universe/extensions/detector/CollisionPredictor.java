package pp.muza.universe.extensions.detector;

import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.BodyPair;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.CollisionPair;

import java.util.*;

/**
 * A CollisionDetectorExtension is an extension that can detect collisions between bodies in the system.
 * There are two modes of operation:
 * detector: it detects if two bodies are in collision in the current step
 * predictor: it predicts if two bodies are going to collide in the next step
 */
public class CollisionPredictor extends BaseSystemExtension implements CollisionDetectorExtension {

    private final Map<BodyPair, CollisionPair> collisionPairs = new HashMap<>();
    private final Map<Body, Set<CollisionPair>> inCollision = new HashMap<>();
    private final Map<Integer, Set<CollisionPair>> clusters = new HashMap<>();
    // we need to create a graph of the bodies and their connections

    private final BodyGraph bodyGraph = new BodyGraph();

    private int step = -1;
    private int totalInCollision = 0;
    private int predictedPairs = 0;
    private int totalProcessed = 0;

    protected void doApply(double dt) {

        if (step != system.getStep()) {
            sync();
        }

        collisionPairs.clear();
        inCollision.clear();
        bodyGraph.clear();
        for (int i = 0; i < system.getSize(); i++) {
            for (int j = i + 1; j < system.getSize(); j++) {
                Body body1 = system.getBody(i);
                Body body2 = system.getBody(j);

                body1.setStep(system.getStep());
                body2.setStep(system.getStep());

                double doubleRadius = (body1.r + body2.r) * (body1.r + body2.r);
                CollisionPair collisionPair = new CollisionPair(body1, body2);
                if (collisionPair.isInCollision()) {
                    bodyGraph.addBodyPair(body1, body2);
                    collisionPairs.put(collisionPair, collisionPair);
                    inCollision.computeIfAbsent(body1, k -> new HashSet<>()).add(collisionPair);
                    inCollision.computeIfAbsent(body2, k -> new HashSet<>()).add(collisionPair);
                    totalInCollision++;
                } else if (collisionPair.isInCollision() || collisionPair.getDistance().squareModule() < 4 * doubleRadius) {
                    bodyGraph.addBodyPair(body1, body2);
                    collisionPairs.put(collisionPair, collisionPair);
                    predictedPairs++;
                }
                totalProcessed++;
            }
        }
    }

    @Override
    public void sync() {
        if (step != system.getStep()) {
            this.step = system.getStep();
        }
    }

    @Override
    public String getState() {
        return this.getClass().getSimpleName() + ": " + totalProcessed + " processed, " + predictedPairs + " predicted, " + totalInCollision + " in collision";
    }

    @Override
    public Collection<CollisionPair> getCollisionPairs() {
        if (step != system.getStep()) {
            sync();
        }
        return Collections.unmodifiableCollection(collisionPairs.values());
    }

    @Override
    public boolean isInCollision(Body body) {
        if (step != system.getStep()) {
            sync();
        }
        return inCollision.containsKey(body);
    }

    @Override
    public void getClustersCount() {
        bodyGraph.getClustersCount();
    }

    @Override
    public Collection<CollisionPair> getBodyCollisions(Body body) {
        if (step != system.getStep()) {
            sync();
        }
        return Collections.unmodifiableCollection(inCollision.get(body));
    }

    @Override
    public Collection<CollisionPair> getCollisionCluster(int cluster) {
        if (step != system.getStep()) {
            sync();
        }
        Collection<CollisionPair> result = clusters.get(cluster);
        if (result == null) {
            result = new HashSet<>();
            Collection<Body> cc = bodyGraph.getCluster(cluster);

            for (Body body1 : cc) {
                for (Body body2 : cc) {
                    if (body1 != body2) {
                        CollisionPair pair = collisionPairs.get(new BodyPair(body1, body2));
                        result.add(pair);
                    }
                }
            }
        }

        return Collections.unmodifiableCollection(result);
    }

}
