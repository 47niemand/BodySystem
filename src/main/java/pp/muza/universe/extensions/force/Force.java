package pp.muza.universe.extensions.force;

import pp.muza.complex.impl.BaseComplex;
import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.ForceExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * A Force is an extension that can apply forces to bodies in the system.
 * It updates the velocity of the bodies by using the force applied to them.
 * F = m * a
 * a = F / m
 * v = v0 + a * dt
 * v = v0 + F / m * dt
 */
public class Force extends BaseSystemExtension implements ForceExtension {

    private final List<Complex> forces = new ArrayList<>();

    private int step = -1;

    @Override
    protected void doApply(double dt) {

        if (step != system.getStep()) {
            sync();
        }
        for (int i = 0; i < system.getSize(); i++) {
            double _dt = dt;
            Body body = system.getBody(i);
            if (body.checkStep(system.getStep())) {
                assert body.getDeltaT() <= dt;
                _dt = (1.0 - body.getDeltaT()) * dt;
            }
            if (body.isPinned) {
                continue;
            }
            Complex f = forces.get(i);
            assert !f.isInfinity();
            Complex d = Complex.scale(f, _dt / body.m);

            assert !d.isInfinity();

            body.velocity.change(d);

        }

    }

    @Override
    public Complex getForce(int i) {
        if (step != system.getStep()) {
            sync();
        }
        return forces.get(i);
    }

    @Override
    public void sync() {
        if (step != system.getStep()) {

            if (system.getSize() > forces.size()) {
                for (int i = forces.size(); i < system.getSize(); i++) {
                    forces.add(new BaseComplex(2));
                }
            } else {
                if (forces.size() > system.getSize()) {
                    forces.subList(system.getSize(), forces.size()).clear();
                }
            }

            step = system.getStep();
            for (Complex force : forces) {
                force.setNull();
            }
        }
    }
}
