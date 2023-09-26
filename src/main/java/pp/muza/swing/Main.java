package pp.muza.swing;

import pp.muza.swing.model.GameImpl;

import javax.swing.JFrame;

public class Main {

    public static MainPanel mainPanel;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Balls");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            mainPanel = new MainPanel(GameImpl.DEFAULT_CANVAS_HEIGHT, GameImpl.DEFAULT_CANVAS_WIDTH, new GameImpl());
            frame.setContentPane(mainPanel);
            frame.pack();
            frame.setVisible(true);

            mainPanel.startDrawing();
        });
    }
}
