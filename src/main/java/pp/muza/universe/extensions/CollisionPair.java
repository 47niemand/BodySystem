package pp.muza.universe.extensions;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;

/**
 * Simple data structure that represents a collision between two bodies.
 * Contains two bodies and some information about the collision.
 */
public class CollisionPair extends BodyPair {
    public static final double ERROR = 1e-9;
    private static final double SOLVE_ERROR = 0.00001;
    public final Body body1;
    public final Body body2;
    public final double squareRadius;
    private final int dimension;
    private Complex distance;
    private Complex velocity;
    private int inCollision = -1;
    private double dotProduct = Double.NaN;
    private double timeToCollision = Double.NaN;
    private int body2VelocityVersion;
    private int body1VelocityVersion;
    private int body1PositionVersion;
    private int body2PositionVersion;


    public CollisionPair(Body body1, Body body2) {
        super(body1, body2);
        this.dimension = body1.position.getDimension();
        if (dimension != body2.position.getDimension() || dimension != body1.velocity.getDimension() || dimension != body2.velocity.getDimension()) {
            throw new IllegalArgumentException("The bodies must have the same dimension");
        }
        this.body1 = body1;
        this.body2 = body2;
        squareRadius = (body1.r + body2.r) * (body1.r + body2.r);
    }

    private static double calculateExpression(double a, double b, double c, double d, double e, double f, double g, double h, double r1, double r2, double x) {
        /*
           ((a + b * x - (c + d * x))^2 + (e + f * x - (g + h * x))^2 - (r1 + r2)^2
         */
        double term1 = (a + b * x - (c + d * x));
        double term2 = (e + f * x - (g + h * x));
        double term3 = (r1 + r2);

        return Math.pow(term1, 2) + Math.pow(term2, 2) - Math.pow(term3, 2);
    }

    private static double[] solveEquation(double a, double b, double c, double d, double e, double f, double g, double h, double r1, double r2) {


        //(a + b x - (c + d x))^2 + (e + f x - (g + h x))^2 - (r1 + r2)^2 == 0
        //x = ((-2 a (b - d) + 2 c (b - d) - 2 e (f - h) + 2 g (f - h)) ± sqrt((2 a (b - d) - 2 c (b - d) + 2 e (f - h) - 2 g (f - h))^2 - 4 ((b - d)^2 + (f - h)^2) (a^2 - 2 a c + c^2 + e^2 - 2 e g + g^2 - (r1 + r2)^2)))/(2 ((b - d)^2 + (f - h)^2)) ((b - d)^2 + (f - h)^2!=0)

        double numerator1 = -2 * a * (b - d) + 2 * c * (b - d) - 2 * e * (f - h) + 2 * g * (f - h);
        double numerator2 = (b - d) * (b - d) + (f - h) * (f - h);
        double numerator3 = Math.sqrt(Math.pow(2 * a * (b - d) - 2 * c * (b - d) + 2 * e * (f - h) - 2 * g * (f - h), 2) - 4 * numerator2 * (a * a - 2 * a * c + c * c + e * e - 2 * e * g + g * g - (r1 + r2) * (r1 + r2)));
        double denominator = 2 * numerator2;

        double x1 = (numerator1 + numerator3) / denominator;
        double x2 = (numerator1 - numerator3) / denominator;

        assert Double.isNaN(x1) || Math.abs(calculateExpression(a, b, c, d, e, f, g, h, r1, r2, x1)) < SOLVE_ERROR;
        assert Double.isNaN(x2) || Math.abs(calculateExpression(a, b, c, d, e, f, g, h, r1, r2, x2)) < SOLVE_ERROR;

        return new double[]{x1, x2};
    }

    public double solveCollision(double maxDt) {

        double first = 0.0;
        double last = 1.0;

        double doubleRadius = (body1.r + body2.r) * (body1.r + body2.r);

        int iteration = 0;

        double lambda = Double.NaN;
        double error = Double.POSITIVE_INFINITY;

        Complex nextBody1Position = null;
        Complex nextBody2Position = null;
        Complex nextDistance = null;

        while ((first < last) && (iteration < 100) && (Math.abs(error) > ERROR)) {
            iteration++;
            lambda = first + (last - first) / 2.0;
            assert lambda >= 0 && lambda <= 1;

            nextBody1Position = Complex.add(body1.position, Complex.scale(body1.velocity, lambda * maxDt));
            nextBody2Position = Complex.add(body2.position, Complex.scale(body2.velocity, lambda * maxDt));
            nextDistance = Complex.sub(nextBody1Position, nextBody2Position);

            error = nextDistance.squareModule() - doubleRadius;

            if (error <= 0) {
                last = lambda;
            } else {
                first = lambda;
            }
        }

        if (Math.abs(error) <= SOLVE_ERROR) {
            return lambda * maxDt;
        } else if (iteration >= 100) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Double.NaN;
        }

    }

    public Complex getVelocity() {
        if (velocity != null && !checkVelocity()) {
            resetVelocity();
        }
        if (velocity == null) {
            body2VelocityVersion = body2.velocity.getVersion();
            body1VelocityVersion = body1.velocity.getVersion();
            velocity = Complex.sub(body2.velocity, body1.velocity);
        }
        assert velocity.equals(Complex.sub(body2.velocity, body1.velocity));
        return velocity;
    }

    public double getDotProduct() {
        if (!Double.isNaN(dotProduct)) {
            if (!checkPosition()) {
                resetPosition();
            }
            if (!checkVelocity()) {
                resetVelocity();
            }
        }
        if (Double.isNaN(dotProduct)) {
            dotProduct = Complex.dot(getDistance(), getVelocity());
        }
        assert dotProduct == Complex.dot(Complex.sub(body1.position, body2.position), Complex.sub(body2.velocity, body1.velocity));
        return dotProduct;
    }

    public double getModuleDistance() {
        return getDistance().squareModule() - squareRadius;
    }

    public Complex getDistance() {
        if (distance != null && !checkPosition()) {
            resetPosition();
        }
        if (distance == null) {
            body1PositionVersion = body1.position.getVersion();
            body2PositionVersion = body2.position.getVersion();
            distance = Complex.sub(body1.position, body2.position);
        }
        assert distance.equals(Complex.sub(body1.position, body2.position));
        return distance;
    }

    public boolean isInCollision() {
        if (inCollision < 0) {
            inCollision = getModuleDistance()  < ERROR ? 1 : 0;
        }
        return inCollision == 1;
    }

    private void resetPosition() {
        distance = null;
        inCollision = -1;
        dotProduct = Double.NaN;
        timeToCollision = Double.NaN;
    }

    private boolean checkPosition() {
        return body1PositionVersion == body1.position.getVersion() && body2PositionVersion == body2.position.getVersion();
    }

    private void resetVelocity() {
        velocity = null;
        dotProduct = Double.NaN;
        timeToCollision = Double.NaN;
    }

    private boolean checkVelocity() {
        return body2VelocityVersion == body2.velocity.getVersion() && body1VelocityVersion == body1.velocity.getVersion();
    }

    public double getTimeToCollision() {
        if (dimension != 2) {
            throw new UnsupportedOperationException("Not implemented for dimension " + dimension);
        }
        if (!Double.isNaN(timeToCollision)) {
            if (!checkVelocity()) {
                resetVelocity();
            } else if (!checkPosition()) {
                resetPosition();
            }
        }
        if (Double.isNaN(timeToCollision)) {
            getDistance();
            getVelocity();
            if (getModuleDistance() + ERROR  < 0) {
                // already in collision
                return 0;
            }
            Complex position1 = body1.position;
            Complex velocity1 = body1.velocity;
            Complex position2 = body2.position;
            Complex velocity2 = body2.velocity;
            double a = position1.getValue(Complex.X);
            double b = velocity1.getValue(Complex.X);
            double c = position2.getValue(Complex.X);
            double d = velocity2.getValue(Complex.X);
            double e = position1.getValue(Complex.Y);
            double f = velocity1.getValue(Complex.Y);
            double g = position2.getValue(Complex.Y);
            double h = velocity2.getValue(Complex.Y);
            double r1 = body1.r;
            double r2 = body2.r;

            assert position1.equals(Complex.of(a, e));
            assert velocity1.equals(Complex.of(b, f));
            assert position2.equals(Complex.of(c, g));
            assert velocity2.equals(Complex.of(d, h));

            double[] x = solveEquation(a, b, c, d, e, f, g, h, r1, r2);

            if (x.length == 1) {
                timeToCollision = x[0];
            } else if (x[0] < 0 || x[1] < 0) {
                timeToCollision = Math.max(x[0], x[1]);
            } else {
                timeToCollision = Math.min(x[0], x[1]);
            }

            if (!(!Double.isFinite(timeToCollision) || timeToCollision < 0 || Math.abs(Complex.squareDistance(Complex.add(position1, Complex.scale(velocity1, timeToCollision)), Complex.add(position2, Complex.scale(velocity2, timeToCollision))) - (r1 + r2) * (r1 + r2)) <= SOLVE_ERROR)) {
                System.out.println("timeToCollision = " + timeToCollision);
            }

            assert !Double.isFinite(timeToCollision) || timeToCollision < 0 || Math.abs(Complex.squareDistance(Complex.add(position1, Complex.scale(velocity1, timeToCollision)), Complex.add(position2, Complex.scale(velocity2, timeToCollision))) - (r1 + r2) * (r1 + r2)) <= SOLVE_ERROR;
        }
        return timeToCollision;
    }
}
