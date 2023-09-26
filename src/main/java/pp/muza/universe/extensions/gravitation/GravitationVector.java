package pp.muza.universe.extensions.gravitation;

import pp.muza.complex.Complex;
import pp.muza.universe.extensions.ForceExtension;
import pp.muza.universe.extensions.GravitationVectorExtension;
import pp.muza.universe.extensions.BaseSystemExtension;

/*
 * A GravitationVector is an extension that can apply a force that pulls bodies down.
 * The ForceExtension is used to apply the force.
 */
public class GravitationVector extends BaseSystemExtension implements ForceExtension.SetForceExtension, GravitationVectorExtension {

    public static final double DEFAULT_G = 0.1;
    private final Complex gVector;

    private ForceExtension forceField;

    public GravitationVector(double g) {
        gVector = Complex.scale(Complex.UP, g);
    }

    public GravitationVector() {
        this(DEFAULT_G);
    }

    @Override
    public void setG(double g) {
        Complex tmp = Complex.normalize(gVector);
        tmp.scale(g);
        gVector.set(tmp);
    }

    @Override
    public void setG(Complex g) {
        gVector.set(g);
    }

    @Override
    public Complex setG(double g, double angle) {
        return Complex.fromPolar(g, angle);
    }

    @Override
    protected void doApply(double dt) {

        for (int i = 0; i < system.getSize(); i++) {
            Complex g = Complex.scale(gVector, system.getBody(i).m);
            forceField.getForce(i).change(g);
        }
    }

    @Override
    public void sync() {

    }


    @Override
    public void setForceSystem(ForceExtension forceSystem) {
        this.forceField = forceSystem;
    }


}
