package pp.muza.swing;


import pp.muza.swing.draw.Painter;
import pp.muza.swing.draw.*;
import pp.muza.swing.model.Game;
import pp.muza.swing.model.GameAction;
import pp.muza.universe.body.Body;
import pp.muza.universe.extensions.detector.CollisionPredictor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

class MainPanel extends JPanel {

    private static final double DT = 1.0;
    private final int DRAWING_DELAY = (int) (20 * DT);

    private final Game system;
    private final Painter painter;
    private final Circles circles;
    private final LabelPrint labelPrint = new LabelPrint();
    private Trail trail;
    private CollisionMesh collisionMesh;


    public MainPanel(int height, int width, Game system) {
        this.system = system;
        painter = new Painter(0, 0, width, height, Color.BLACK, Color.YELLOW);
        trail = null; //new Trail();
        collisionMesh = null; //new CollisionMesh();
        circles = new Circles();

        DrawCanvas canvas = new DrawCanvas();

        this.labelPrint.add("Use keys `1-6`, `s`, `S`, `r`, `R`, `m`, `c` to switch options", 200);
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
        this.setFocusable(true);
        // Handling window resize. Adjust the container box to fill the screen.
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component) e.getSource();
                Dimension dim = c.getSize();
                painter.resize(0, 0, dim.width, dim.height);
                MainPanel.this.system.sendMessages(GameAction.Action.RESIZE, Arrays.asList(0, 0, dim.width, dim.height));
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyChar()) {
                    case 'r':
                        MainPanel.this.system.sendMessages(GameAction.Action.RESTART, List.of());
                        break;
                    case 'R':
                        MainPanel.this.system.sendMessages(GameAction.Action.RESTART, List.of());
                        MainPanel.this.system.sendMessages(GameAction.Action.STOP, List.of());
                        break;
                    case 's':
                        MainPanel.this.system.sendMessages(GameAction.Action.RESTART, List.of(500));
                        MainPanel.this.system.sendMessages(GameAction.Action.STOP, List.of());
                        break;
                    case 'S':
                        MainPanel.this.system.sendMessages(GameAction.Action.STOP, List.of());
                        break;
                    case 'a':
                        MainPanel.this.system.sendMessages(GameAction.Action.CUSTOM, List.of("random-speed"));
                        break;
                    case 'c':
                        trail = null;
                        collisionMesh = null;
                        break;
                    case 't':
                        if (trail == null) {
                            trail = new Trail();
                            labelPrint.add("Trails on", 100);
                        } else {
                            trail = null;
                            labelPrint.add("Trails off", 100);
                        }
                        break;
                    case 'm':
                        if (collisionMesh == null) {
                            collisionMesh = new CollisionMesh();
                            labelPrint.add("Collision mesh on", 100);
                        } else {
                            collisionMesh = null;
                            labelPrint.add("Collision mesh off", 100);
                        }
                        break;
                    case '1':
                        labelPrint.add("Gravitation" + (MainPanel.this.system.isExtensionEnabled("Gravitation") ? " off" : " on"), 100);
                        MainPanel.this.system.sendMessages(GameAction.Action.TOGGLE_EXTENSIONS, List.of("Gravitation"));
                        break;
                    case '2':
                        labelPrint.add("Free Fall" + (MainPanel.this.system.isExtensionEnabled("GravitationVector") ? " off" : " on"), 100);
                        MainPanel.this.system.sendMessages(GameAction.Action.TOGGLE_EXTENSIONS, List.of("GravitationVector"));

                        break;
                    case '3':
                        labelPrint.add("Merge" + (MainPanel.this.system.isExtensionEnabled("Merge") ? " off" : " on"), 100);
                        MainPanel.this.system.sendMessages(GameAction.Action.TOGGLE_EXTENSIONS, List.of("Merge"));
                        break;
                    case '4':
                        labelPrint.add("MediaResistance " + (MainPanel.this.system.isExtensionEnabled("MediaResistance") ? " off" : " on"), 100);
                        MainPanel.this.system.sendMessages(GameAction.Action.TOGGLE_EXTENSIONS, List.of("MediaResistance"));
                        break;
                    case '5':
                        MainPanel.this.system.sendMessages(GameAction.Action.DISABLE_EXTENSIONS, List.of("FineCollision"));
                        MainPanel.this.system.sendMessages(GameAction.Action.ENABLE_EXTENSIONS, List.of("SimpleCollision"));
                        labelPrint.add("SimpleCollision", 100);
                        break;
                    case '6':
                        MainPanel.this.system.sendMessages(GameAction.Action.DISABLE_EXTENSIONS, List.of("SimpleCollision"));
                        MainPanel.this.system.sendMessages(GameAction.Action.ENABLE_EXTENSIONS, List.of("FineCollision"));
                        labelPrint.add("FineCollision", 100);
                        break;
                }

            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int x = e.getX();
                    int y = e.getY();
                    Body b = MainPanel.this.system.getBodyAt(x, y);
                    if (b != null) {
                        b.isPinned = !b.isPinned;
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX();
                    int y = e.getY();
                    Body b = MainPanel.this.system.getBodyAt(x, y);

                    if (b != null) {
                        MainPanel.this.system.sendMessages(GameAction.Action.REMOVE_BODY, List.of(b));
                    }
                }
            }
        });


    }


    /**
     * Start the ball bouncing.
     */
    public void startDrawing() {
        new Thread(() -> {

            while (true) {
                // measure time of execution next step
                long startTime = System.currentTimeMillis();
                updatePositionAndDirection();
                long endTime = System.currentTimeMillis();
                long calcTime = endTime - startTime;
                startTime = System.currentTimeMillis();
                repaint();
                endTime = System.currentTimeMillis();
                long paintTime = endTime - startTime;
                if (system.getStep() % 50 == 0) {
                    System.out.println("step: " + system.getStep());
                    System.out.println("calcTime: " + calcTime + " paintTime: " + paintTime);
                }

                int delay = DRAWING_DELAY;

                int wait = delay - (int) (calcTime - paintTime);
                if (wait < 0) {
                    wait = 0;
                }

                try {
                    Thread.sleep(wait);
                } catch (InterruptedException ex) {
                }
            }

        }).start();

    }

    /**
     * detects collision, bounces, calculate final velocities
     */
    private void updatePositionAndDirection() {

        double dt = DT + 0.0;

        if (trail != null) {
            for (Body body : system.getBodies()) {
                trail.add(new Point(body.getX(), body.getY()));
            }
        }

        system.doStep(dt);
        if (collisionMesh != null) {
            collisionMesh.reset();
        }

        CollisionPredictor p = (CollisionPredictor) system.getExtension(CollisionPredictor.class.getSimpleName());
        circles.setBodies(system.getBodies());
        if (collisionMesh != null) {
            collisionMesh.setPairs(p.getCollisionPairs());
        }
    }

    /**
     * The custom drawing panel for the bouncing ball (inner class).
     */
    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            // Draw the balls and field
            painter.draw(g);
            if (collisionMesh != null) {
                collisionMesh.draw(g);
            }

            circles.draw(g);
            if (trail != null) {
                trail.draw(g);
            }
            labelPrint.draw(g);


        }

        @Override
        public Dimension getPreferredSize() {
            return (new Dimension(painter.width, painter.height));
        }
    }
}
