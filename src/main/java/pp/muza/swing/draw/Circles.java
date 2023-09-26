package pp.muza.swing.draw;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;

import java.awt.*;
import java.util.List;

public class Circles implements DraftsMan {

    List<Body> bodies;

    public Circles() {

    }

    public void setBodies(List<Body> bodies) {
        this.bodies = bodies;
    }

    public void draw(Graphics g) {

        if (bodies == null) {
            return;
        }
        for (Body body : bodies) {
            int x = (int) body.position.getValue(Complex.X);
            int y = (int) body.position.getValue(Complex.Y);
            int radius = (int) Math.round(body.r);
            radius = radius < 0 ? 1 : radius;
            g.setColor((Color) body.tag);
            g.fillOval((x - radius), (y - radius), (2 * radius), (2 * radius));
        }
    }
}
