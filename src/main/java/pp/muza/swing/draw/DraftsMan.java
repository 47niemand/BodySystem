package pp.muza.swing.draw;

import java.awt.*;

public interface DraftsMan {

    void draw(Graphics g);

    default void resize(int x, int y, int width, int height) {

    }

    default void reset() {

    }
}
