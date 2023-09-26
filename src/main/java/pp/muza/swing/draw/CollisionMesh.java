package pp.muza.swing.draw;


import pp.muza.universe.extensions.CollisionPair;

import java.awt.*;
import java.util.Collection;

public class CollisionMesh implements DraftsMan {

    private Collection<CollisionPair> pairs;

    public void setPairs(Collection<CollisionPair> pairs) {
        this.pairs = pairs;
    }


    public void draw(Graphics g) {
        // draw small circles
        if (pairs == null) {
            return;
        }
        for (CollisionPair pair : pairs) {
            g.setColor(pair.getDotProduct() > 0 ? Color.LIGHT_GRAY : Color.darkGray);

            g.drawLine(pair.body1.getX(), pair.body1.getY(), pair.body2.getX(), pair.body2.getY());

        }
    }

}
