package pp.muza.swing.draw;

import java.awt.Color;
import java.awt.Graphics;

public class Painter implements DraftsMan {
    private final Color back;
    private final Color border;
    int minX, maxX, minY, maxY;
    public int width;
    public int height;

    public Painter(int x, int y, int width, int height, Color back, Color border) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
        this.width = width;
        this.height = height;
        this.back = back;
        this.border = border;
    }


    @Override
    public void resize(int x, int y, int width, int height) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + width - 1;
        this.maxY = y + height - 1;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(back);
        g.fillRect(minX, minY, maxX - minX - 1, maxY - minY - 1);
        g.setColor(border);
        g.drawRect(minX, minY, maxX - minX - 1, maxY - minY - 1);
    }

}
