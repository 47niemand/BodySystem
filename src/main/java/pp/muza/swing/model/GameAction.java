package pp.muza.swing.model;

import java.util.List;

public class GameAction {

    final GameAction.Action action;
    final List<Object> params;

    public GameAction(Action action, List<Object> params) {
        this.action = action;
        this.params = List.copyOf(params);
    }


    public enum Action {
        ADD_BODY,
        REMOVE_BODY,
        STEP,
        STOP,
        ENABLE_EXTENSIONS,
        DISABLE_EXTENSIONS,
        TOGGLE_EXTENSIONS,
        PIN_UNPIN,
        CLEAR,
        RESIZE,
        RESTART,
        CUSTOM

    }

}
