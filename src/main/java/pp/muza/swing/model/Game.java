package pp.muza.swing.model;

import pp.muza.universe.body.Body;

import java.util.List;

public interface Game {
    void sendMessages(GameAction.Action gameAction, List<Object> param);

    Body getBodyAt(int x, int y);

    int getStep();

    List<Body> getBodies();

    void doStep(double dt);

    Object getExtension(String name);

    boolean isExtensionEnabled(String name);
}
