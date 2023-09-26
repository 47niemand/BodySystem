package pp.muza.swing.draw;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Trail implements DraftsMan  {

    private final int MAX_SIZE = 1000;
    private final List<Point> trail = new ArrayList<>();

    public void add(Point p) {
        synchronized (trail) {
            trail.add(p);
            if (trail.size() > MAX_SIZE) {
                trail.remove(0);
            }
        }
    }

    public void draw(Graphics g) {
        for (int i = 0; i < trail.size() - 1; i++) {
            Point a = trail.get(i);
            g.setColor(Color.LIGHT_GRAY);
            g.drawOval(a.x, a.y, 1, 1);
        }
    }

    @Override
    public void resize(int x, int y, int width, int height) {
        trail.clear();
    }
}
