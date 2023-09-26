package pp.muza.swing.model;

import pp.muza.complex.Complex;
import pp.muza.universe.body.Body;
import pp.muza.universe.body.BodySystem;
import pp.muza.universe.extensions.BoundaryExtension;
import pp.muza.universe.extensions.CollisionDetectorExtension;
import pp.muza.universe.extensions.CollisionPair;
import pp.muza.universe.extensions.boundary.Boundary;
import pp.muza.universe.extensions.collision.FineCollision;
import pp.muza.universe.extensions.collision.SimpleCollision;
import pp.muza.universe.extensions.detector.CollisionPredictor;
import pp.muza.universe.extensions.force.Force;
import pp.muza.universe.extensions.gravitation.Gravitation;
import pp.muza.universe.extensions.gravitation.GravitationVector;
import pp.muza.universe.extensions.merge.Merge;
import pp.muza.universe.extensions.move.Movement;
import pp.muza.universe.extensions.resistance.MediumResistance;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class GameImpl implements Game {

    public static final int RANDOM_BODIES_COUNT = 500;
    public static final int DEFAULT_CANVAS_WIDTH = 800;
    public static final int DEFAULT_CANVAS_HEIGHT = 600;
    private static final Object[] empty = new Object[0];
    private final Queue<GameAction> gameActions = new ArrayDeque<>();
    private final BodySystem system;

    public GameImpl() {
        system = new BodySystem();

        initExtensions();
        initObjects();
        //initRandomUniqueObjects(RANDOM_BODIES_COUNT);
    }

    private void initExtensions() {
        Force force = new Force();
        Gravitation gravitation = new Gravitation();
        GravitationVector gravitationVector = new GravitationVector();
        CollisionDetectorExtension collisionPredictor = new CollisionPredictor();
        CollisionDetectorExtension.SetDetectorExtension simpleCollision = new SimpleCollision();
        CollisionDetectorExtension.SetDetectorExtension fineCollision = new FineCollision();
        CollisionDetectorExtension.SetDetectorExtension merge = new Merge();

        // inject dependencies
        gravitation.setForceSystem(force);
        gravitationVector.setForceSystem(force);
        simpleCollision.setCollisionDetector(collisionPredictor);
        fineCollision.setCollisionDetector(collisionPredictor);
        merge.setCollisionDetector(collisionPredictor);

        // add extensions, order is important
        system.addExtension(gravitation);
        system.addExtension(gravitationVector);
        system.addExtension(force);
        system.addExtension(collisionPredictor);
        system.addExtension(simpleCollision);
        system.addExtension(fineCollision);
        system.addExtension(new Movement());
        system.addExtension(new MediumResistance());
        system.addExtension(new Boundary(0, 0, DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT));
        system.addExtension(merge);

        // enable extensions
        system.enableExtension(CollisionPredictor.class.getSimpleName());
        system.enableExtension(Boundary.class.getSimpleName());
        //system.enableExtension(SimpleCollision.class.getSimpleName());
        system.enableExtension(FineCollision.class.getSimpleName());
        //system.enableExtension(Gravitation.class.getSimpleName());
        system.enableExtension(Force.class.getSimpleName());
        //system.enableExtension(GravitationVector.class);
        //system.enableExtension(MediumResistance.class.getSimpleName());
        system.enableExtension(Movement.class.getSimpleName());
        //system.enableExtension(Merge.class.getSimpleName());
    }

    private void initObjects() {
        system.addBody(new Body(Complex.of(100, 410), Complex.fromPolar(3, Math.toRadians(34)), 2.0 * Math.pow(25, 3) / 1000f, 25, Color.YELLOW));
        system.addBody(new Body(Complex.of(80, 350), Complex.fromPolar(2, Math.toRadians(-114)), 2.0 * Math.pow(25, 3) / 1000f, 25, Color.YELLOW));
        system.addBody(new Body(Complex.of(530, 400), Complex.fromPolar(3, Math.toRadians(14)), 2.0 * Math.pow(30, 3) / 1000f, 30, Color.GREEN));

        system.addBody(new Body(Complex.of(400, 400), Complex.fromPolar(3, Math.toRadians(14)), 2.0 * Math.pow(30, 3) / 1000f, 30, Color.GREEN));
        system.addBody(new Body(Complex.of(400, 50), Complex.fromPolar(1, Math.toRadians(-47)), 2.0 * Math.pow(35, 3) / 1000f, 35, Color.PINK));
        system.addBody(new Body(Complex.of(480, 320), Complex.fromPolar(4, Math.toRadians(47)), 2.0 * Math.pow(35, 3) / 1000f, 35, Color.PINK));

        system.addBody(new Body(Complex.of(80, 150), Complex.fromPolar(1, Math.toRadians(-114)), 2.0 * Math.pow(40, 3) / 1000f, 40, Color.GRAY));
        system.addBody(new Body(Complex.of(100, 240), Complex.fromPolar(2, Math.toRadians(60)), 2.0 * Math.pow(40, 3) / 1000f, 40, Color.ORANGE));
        system.addBody(new Body(Complex.of(250, 380), Complex.fromPolar(3, Math.toRadians(-42)), 2.0 * Math.pow(50, 3) / 1000f, 50, Color.BLUE));

        system.addBody(new Body(Complex.of(200, 80), Complex.fromPolar(6, Math.toRadians(-84)), 2.0 * Math.pow(70, 3) / 1000f, 70, Color.CYAN));
        system.addBody(new Body(Complex.of(500, 170), Complex.fromPolar(6, Math.toRadians(-42)), 2.0 * Math.pow(90, 3) / 1000f, 90, Color.BLUE));
    }

    public void initRandomUniqueObjects(int count) {
        Random random = new Random(4);
        for (int i = 0; i < count; i++) {
            double radius = 2 + (int) (random.nextDouble() * 8);
            double mass = Math.max(0.5, (int) (1.1 * Math.pow(radius, 2) / 1000f));
            Color color = new Color((int) (random.nextDouble() * 0x1000000));
            system.addBody(new Body(Complex.of((int) (random.nextDouble() * DEFAULT_CANVAS_WIDTH), (int) (random.nextDouble() * DEFAULT_CANVAS_HEIGHT)), Complex.fromPolar(random.nextDouble() * 1, random.nextDouble() * 360), mass, radius, color));
        }
        CollisionPredictor collisionPredictor = new CollisionPredictor();
        collisionPredictor.setSystem(system);
        collisionPredictor.enable();
        collisionPredictor.apply(0.0);

        collisionPredictor.getCollisionPairs();
        for (CollisionPair pair : collisionPredictor.getCollisionPairs()) {
            if (pair.isInCollision()) {
                if (pair.body1.r > pair.body2.r) {
                    system.removeBody(pair.body2);
                } else {
                    system.removeBody(pair.body1);
                }
            }
        }
        collisionPredictor.apply(0.0);
        for (CollisionPair pair : collisionPredictor.getCollisionPairs()) {
            if (pair.isInCollision()) {
                system.removeBody(pair.body2);
                system.removeBody(pair.body1);
            }
        }
    }

    @Override
    public void sendMessages(GameAction.Action gameAction, List<Object> param) {
        gameActions.add(new GameAction(gameAction, List.copyOf(param)));
    }

    private void processMessage(GameAction.Action gameAction, Object[] param) {
        switch (gameAction) {
            case CUSTOM:
                if (param.length != 1) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                if (param[0].equals("random-speed")) {
                    Random random = new Random(4);
                    for (int i = 0; i < system.getSize(); i++) {
                        Body body = system.getBody(i);
                        body.velocity.set(Complex.fromPolar(random.nextDouble() * 2, random.nextDouble() * Math.PI));
                    }
                } else {
                    throw new IllegalArgumentException("Invalid custom action");
                }
                break;
            case RESTART:
                if (param.length == 0) {
                    Restart();
                } else if (param.length == 1) {
                    system.clear();
                    initRandomUniqueObjects((int) param[0]);
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                break;
            case ADD_BODY:
                if (param.length == 4) {
                    system.addBody(new Body((Complex) param[0], (Complex) param[1], (double) param[2], (double) param[3], null));
                } else if (param.length == 5) {
                    system.addBody(new Body((Complex) param[0], (Complex) param[1], (double) param[2], (double) param[3], param[4]));
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                break;
            case REMOVE_BODY:
                if (param.length == 1) {
                    if (param[0] instanceof Body) {
                        system.removeBody((Body) param[0]);
                    } else {
                        system.removeBody((int) param[0]);
                    }
                } else if (param.length == 2) {
                    Body b = getBodyAt((int) param[0], (int) param[1]);
                    if (b != null) {
                        system.removeBody(b);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                break;
            case STEP:
                system.step();
                break;
            case STOP:
                stop();
                break;
            case ENABLE_EXTENSIONS:
                if (param.length != 1) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                system.getExtension((String) param[0]).enable();
                break;
            case TOGGLE_EXTENSIONS:
                if (param.length != 1) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                system.getExtension((String) param[0]).toggle();
                break;
            case DISABLE_EXTENSIONS:
                if (param.length != 1) {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                system.getExtension((String) param[0]).disable();
                break;
            case PIN_UNPIN:
                if (param.length == 1) {
                    if (param[0] instanceof Body) {
                        int i = system.getBodies().indexOf((Body) param[0]);
                        if (i >= 0) {
                            system.getBody(i).isPinned = !system.getBody(i).isPinned;
                        }
                    } else {
                        int i = (int) param[0];
                        system.getBody(i).isPinned = !system.getBody(i).isPinned;

                    }
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                break;
            case CLEAR:
                system.clear();
                break;
            case RESIZE:
                if (param.length == 4) {
                    ((BoundaryExtension) system.getExtension(Boundary.class.getSimpleName())).resize((int) param[0], (int) param[1], (int) param[2], (int) param[3]);
                } else {
                    throw new IllegalArgumentException("Invalid number of parameters");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gameAction);
        }
    }

    private void stop() {
        for (int i = 0; i < system.getSize(); i++) {
            Body body = system.getBody(i);
            body.velocity.scale(0.0);
        }
    }

    @Override
    public Body getBodyAt(int x, int y) {
        for (int i = 0; i < system.getSize(); i++) {
            Body body = system.getBody(i);
            if (Complex.distance(body.position, Complex.of(x, y)) < body.r) {
                return body;
            }
        }
        return null;
    }

    @Override
    public int getStep() {
        return system.getStep();
    }

    @Override
    public List<Body> getBodies() {
        return system.getBodies();
    }

    @Override
    public void doStep(double dt) {
        system.step();
        while (!gameActions.isEmpty()) {
            GameAction gameAction = gameActions.poll();
            processMessage(gameAction.action, gameAction.params == null ? empty : gameAction.params.toArray());
        }
        system.apply(dt);
    }


    @Override
    public Object getExtension(String name) {
        return system.getExtension(name);
    }

    @Override
    public boolean isExtensionEnabled(String name) {
        return system.isExtensionEnabled(name);
    }

    private void Restart() {
        system.clear();
        initObjects();
        //initRandomUniqueObjects();
    }
}
