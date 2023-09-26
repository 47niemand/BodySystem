package pp.muza.universe.extensions.resistance;


import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;

/**
 * A MediumResistance is an extension that can change the speed of bodies in the system by applying a resistance force.
 */
public class MediumResistance extends BaseSystemExtension {

    private static final double DEFAULT_K = 0.0001;
    private final double K;  // kg/m^3 * K

    public MediumResistance(double k) {
        K = k;
    }

    public MediumResistance() {
        this(DEFAULT_K);
    }

    @Override
    protected void doApply(double dt) {

        // apply to all bodies
        for (int i = 0; i < system.getSize(); i++) {
            applyResistance(system.getBody(i), dt);
        }
    }

    @Override
    public void sync() {

    }

    private void applyResistance(Body body, double dt) {

        double resistanceForceModule = K * body.r * body.r * body.velocity.squareModule();

        double a = resistanceForceModule / body.m;

        double speed = Math.sqrt(body.velocity.squareModule());

        double nextSpeed = speed - a * dt;

        if (speed < 0.0) {
            speed = 0.0;
        }

        double k;
        if (speed == 0) {
            k = 1;
        } else {
            k = nextSpeed / speed;
        }
        if (k < 0) {
            k = 0;
        }
        assert k >= 0;

        body.velocity.scale(k);


    }

}
