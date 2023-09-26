package pp.muza.universe.extensions.boundary;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.BaseSystemExtension;
import pp.muza.universe.extensions.BoundaryExtension;

/**
 * A Boundary is an extension that changes the velocity of bodies when they
 * reach the boundary of the system.
 * It reflects the velocity vector of the body when it reaches the boundary.
 * It implements a resize method that can be used to resize the boundary.
 */
public class Boundary extends BaseSystemExtension implements BoundaryExtension {

    double xMin;
    double xMax;
    double yMin;
    double yMax;

    double xMinNew;
    double xMaxNew;
    double yMinNew;
    double yMaxNew;

    public Boundary(double xMin, double yMin, double xMax, double yMax) {
        super();
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        this.xMinNew = xMin;
        this.xMaxNew = xMax;
        this.yMinNew = yMin;
        this.yMaxNew = yMax;
    }

    @Override
    protected void doApply(double dt) {

        double scaleX = Double.NaN;
        double scaleY = Double.NaN;

        if (xMinNew != xMin || xMaxNew != xMax || yMinNew != yMin || yMaxNew != yMax) {
            // resize the system
            scaleX = (xMaxNew - xMinNew) / (xMax - xMin);
            scaleY = (yMaxNew - yMinNew) / (yMax - yMin);
        }

        for (Body body : system.getBodies()) {
            if ((body.position.getValue(Complex.X) - body.r < xMin)
                    || (body.position.getValue(Complex.X) + body.r > xMax)
                    || (body.position.getValue(Complex.Y) - body.r < yMin)
                    || (body.position.getValue(Complex.Y) + body.r > yMax)) {
                adjustSpeed(body);
            }
            if (!Double.isNaN(scaleX) && !Double.isNaN(scaleY)) {
                body.position.setValue(Complex.X, (body.position.getValue(Complex.X) - xMin) * scaleX + xMinNew);
                body.position.setValue(Complex.Y, (body.position.getValue(Complex.Y) - yMin) * scaleY + yMinNew);
            }
        }

        if (!Double.isNaN(scaleX) || !Double.isNaN(scaleY)) {
            xMin = xMinNew;
            xMax = xMaxNew;
            yMin = yMinNew;
            yMax = yMaxNew;
        }

    }

    @Override
    public void sync() {

    }

    private void adjustSpeed(Body body) {
        if (body.position.getValue(Complex.X) - body.r < xMin) {
            if (body.velocity.getValue(Complex.X) < 0) {
                body.velocity.setValue(Complex.X, -body.velocity.getValue(Complex.X));
            }
        }
        if (body.position.getValue(Complex.X) + body.r > xMax) {
            if (body.velocity.getValue(Complex.X) > 0) {
                body.velocity.setValue(Complex.X, -body.velocity.getValue(Complex.X));
            }
        }
        if (body.position.getValue(Complex.Y) - body.r < yMin) {
            if (body.velocity.getValue(Complex.Y) < 0) {
                body.velocity.setValue(Complex.Y, -body.velocity.getValue(Complex.Y));
            }

        }
        if (body.position.getValue(Complex.Y) + body.r > yMax) {
            if (body.velocity.getValue(Complex.Y) > 0) {
                body.velocity.setValue(Complex.Y, -body.velocity.getValue(Complex.Y));
            }
        }
    }

    @Override
    public void resize(int x1, int y1, int width, int height) {

        if (width <= 0) {
            width = 1;
        }

        if (height <= 0) {
            height = 1;
        }

        xMinNew = x1;
        xMaxNew = x1 + width - 1;
        yMinNew = y1;
        yMaxNew = y1 + height - 1;

    }
}
