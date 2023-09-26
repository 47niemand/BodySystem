package pp.muza.universe.extensions.gravitation;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.ForceExtension;

/**
 * A Gravitation extension. It applies the gravitation force to all bodies in the system.
 * It uses the ForceExtension to apply the force.
 * It uses the CollisionDetectorExtension to avoid applying the force to bodies that are in collision.
 */
public class Gravitation extends BaseSystemExtension implements ForceExtension.SetForceExtension, CollisionDetectorExtension.SetDetectorExtension {

    public static final double DEFAULT_GG = 0.667384;
    private final double G;
    private ForceExtension forceField;
    private CollisionDetectorExtension collisionDetector;


    private int skippedDueToCollision = 0;

    public Gravitation(double g) {
        G = g;
    }

    public Gravitation() {
        this(DEFAULT_GG);
    }


    @Override
    protected void doApply(double dt) {

        for (int i = 0; i < system.getSize(); i++) {
            for (int j = i + 1; j < system.getSize(); j++) {
                if (collisionDetector != null) {
                    if (collisionDetector.isInCollision(system.getBody(i)) || collisionDetector.isInCollision(system.getBody(j))) {
                        skippedDueToCollision++;
                        continue;
                    }
                }
                applyGravitation(i, j);
            }
        }
    }

    @Override
    public void sync() {

    }

    private void applyGravitation(int b1, int b2) {

        /*
          Fg = G * m1*m2 / r^2
          F = m * a
          V = a * t
          V = F / m * t
         */
        Body body1 = system.getBody(b1);
        Body body2 = system.getBody(b2);
        Complex distance = Complex.sub(body2.position, body1.position);
        assert !distance.isNaN();
        if (distance.squareModule() > 1e-16) {
            double forceModule = body1.m * body2.m / distance.squareModule() * G;
            assert !Double.isNaN(forceModule) && !Double.isInfinite(forceModule);
            distance.normalize();
            assert !distance.isNaN();
            forceField.getForce(b1).change(Complex.scale(distance, forceModule));
            forceField.getForce(b2).change(Complex.scale(distance, -forceModule));
        } else {
            assert true;
            //bodies are inside each other
        }

    }

    @Override
    public void setForceSystem(ForceExtension forceSystem) {
        this.forceField = forceSystem;
    }

    @Override
    public void setCollisionDetector(CollisionDetectorExtension collisionDetector) {
        this.collisionDetector = collisionDetector;
    }

    @Override
    public String getState() {
        return this.getClass().getSimpleName() + ": " + skippedDueToCollision + " skipped due to collision";
    }
}
