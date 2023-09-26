package pp.muza.universe.extensions.move;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;


/**
 * A Movement is an extension that can move bodies in the system.
 * It uses the velocities of the bodies to move them.
 */
public class Movement extends BaseSystemExtension {

    private double energy = 0.0;
    private double momentum = 0.0;
    private int totalFullSteps = 0;
    private int totalPartialSteps = 0;
    private int totalOverSteps = 0;
    private int step = -1;

    @Override
    protected void doApply(double dt) {
        if (step != system.getStep()) {
            sync();
        }
        for (Body body : system.getBodies()) {
            if (body.isPinned) {
                body.velocity.setNull();
                continue;
            }
            energy += body.m * body.velocity.squareModule();
            momentum += body.m * Math.sqrt(body.velocity.squareModule());
            if (body.checkStep(system.getStep()) && body.getDeltaT() > 0) {
                if (body.getDeltaT() < dt) {
                    move(body, dt - body.getDeltaT());
                    totalPartialSteps++;
                } else {
                    totalOverSteps++;
                }
            } else {
                body.setStep(system.getStep());
                move(body, dt);
                body.addDeltaT(dt);
                totalFullSteps++;
            }
        }
    }

    @Override
    public void sync() {
        if (step != system.getStep()) {
            step = system.getStep();
            energy = 0.0;
            momentum = 0.0;
        }
    }

    private void move(Body body, double dt) {
        assert !body.velocity.isNaN() || !body.velocity.isInfinity();
        body.position.change(Complex.scale(body.velocity, dt));
        body.addDeltaT(dt);
    }

    @Override
    public String getState() {
        return this.getClass().getSimpleName() + ": " + momentum + " momentum, " + energy + " energy, " + totalFullSteps + " full steps, " + totalPartialSteps + " partial steps, " + totalOverSteps + " over steps";
    }
}
