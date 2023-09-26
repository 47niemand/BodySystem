package pp.muza.universe.body;

import pp.muza.complex.Complex;
import pp.muza.complex.impl.BaseComplex;

/**
 * A body in the universe. It is a circle with a mass.
 */
public class Body {

    /**
     * The position of the body.
     */
    public final Complex position;
    /**
     * The velocity vector of the body.
     */
    public final Complex velocity;
    /**
     * The mass of the body.
     */
    public final double m;
    /**
     * The radius of the body.
     */
    public final double r;
    /**
     * The object associated with the body.
     */
    public transient Object tag;
    /**
     * The pin state of the body. If true, the body will not move.
     */
    public boolean isPinned = false;
    /**
     * The delta time for the body. For internal use.
     */
    double deltaT = 0.0;
    /**
     * The system step when the body was updated.
     */
    int step = -1;

    /**
     * Create a new body
     *
     * @param position the position (Complex)
     * @param velocity the velocity vector
     * @param m        the mass
     * @param r        the radius
     * @param tag      the tag
     */
    public Body(Complex position, Complex velocity, double m, double r, Object tag) {
        this.position = new BaseComplex(position);
        this.velocity = new BaseComplex(velocity);
        this.m = m;
        this.r = r;
        this.tag = tag;
    }

    /**
     * Create a new body
     *
     * @param x   the position x
     * @param y   the position y
     * @param vx  the speed x
     * @param vy  the speed y
     * @param m   the mass
     * @param r   the radius
     * @param tag the tag
     */
    public Body(double x, double y, double vx, double vy, double m, double r, Object tag) {
        this.position = new BaseComplex(x, y);
        this.velocity = new BaseComplex(vx, vy);
        this.m = m;
        this.r = r;
        this.tag = tag;
    }

    public void setStep(int step) {
        if (this.step != step) {
            this.step = step;
            deltaT = 0.0;
        }
    }

    public int getX() {
        return (int) position.getValue(Complex.X);
    }

    public int getY() {
        return (int) position.getValue(Complex.Y);
    }

    public double getDeltaT() {
        return deltaT;
    }

    public double getSpeed() {
        return Math.sqrt(velocity.squareModule());
    }

    public void addDeltaT(double dt) {
        deltaT += dt;
    }

    public boolean checkStep(int step) {
        return this.step == step;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
