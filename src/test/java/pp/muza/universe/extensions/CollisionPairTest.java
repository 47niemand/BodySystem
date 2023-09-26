package pp.muza.universe.extensions;

import org.junit.jupiter.api.Test;
import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionPairTest {

    @Test
    void solveCollision() {
        Complex A = Complex.of(399.5, 290.0);
        Complex B = Complex.of(0.0, 0.0);
        Complex C = Complex.of(519.5274148718489, 293.14143315871064);
        Complex D = Complex.of(-5.999086170938348, 0.10471443862370107);
        double r1 = 25.0;
        double r2 = 90.0;
        Body body1 = new Body(A, B, 0.0, r1, null);
        Body body2 = new Body(C, D, 0.0, r2, null);

        CollisionPair collisionPair = new CollisionPair(body1, body2);
        double d = collisionPair.getTimeToCollision();
        assertTrue(d > 0.0);

        body2.velocity.set(Complex.of(0.0, 0.0));
        body1.velocity.set(Complex.of(0.0, 0.0));
        d = collisionPair.getTimeToCollision();
        assertTrue(Double.isNaN(d));
    }

    @Test
    void getTimeToCollision() {

        Complex A = Complex.of(399.5, 290.0);
        Complex B = Complex.of(0.0, 0.0);
        Complex C = Complex.of(519.5274148718489, 293.14143315871064);
        Complex D = Complex.of(-5.999086170938348, 0.10471443862370107);
        double r1 = 25.0;
        double r2 = 90.0;
        Body body1 = new Body(A, B, 0.0, r1, null);
        Body body2 = new Body(C, D, 0.0, r2, null);

        CollisionPair collisionPair = new CollisionPair(body1, body2);
        double d = collisionPair.getTimeToCollision();
        double t = collisionPair.solveCollision(2.0);
        assertEquals(0.0, d - t, CollisionPair.ERROR);
        body1.position.change(Complex.scale(body1.velocity, d));
        body2.position.change(Complex.scale(body2.velocity, d));
        assertTrue(collisionPair.isInCollision());
        assertEquals((body1.r + body2.r) * (body1.r + body2.r), collisionPair.getDistance().squareModule(), CollisionPair.ERROR);
    }

    @Test
    void test() {
        Body body1 = new Body(Complex.of(399.5, 299.5), Complex.of(0.1129217715069567, -0.039849041293895916), 3906.25, 125.0, null);
        Body body2 = new Body(Complex.of(540.9507914042508, 349.41666666666663), Complex.of(-8.0, 9.797174393178826E-16), 31.25, 25.0, null);
        CollisionPair collisionPair = new CollisionPair(body1, body2);
        double d = collisionPair.getTimeToCollision();
        System.out.println(d);

    }
}