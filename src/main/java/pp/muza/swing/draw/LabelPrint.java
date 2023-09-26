package pp.muza.swing.draw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LabelPrint implements DraftsMan {

    private final List<AbstractMap.SimpleEntry<String, Integer>> labels = new ArrayList<>();
    private final Font font = new Font("Arial", Font.BOLD, 18);

    @Override
    public void draw(Graphics g) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<AbstractMap.SimpleEntry<String, Integer>> iterator = labels.iterator(); iterator.hasNext();) {
            AbstractMap.SimpleEntry<String, Integer> label = iterator.next();
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(label.getKey());
            label.setValue(label.getValue() - 1);
            if (label.getValue() < 0) {
                iterator.remove();
            }
        }
        g.setColor(Color.GREEN);
        g.setFont(font);
        g.drawString(sb.toString(), 10, 20);

    }

    @Override
    public void reset() {
        labels.clear();
        DraftsMan.super.reset();
    }

    public void add(String hello, int i) {
        labels.add(new AbstractMap.SimpleEntry<>(hello, i));
    }
}
