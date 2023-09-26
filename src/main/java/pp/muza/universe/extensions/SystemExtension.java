package pp.muza.universe.extensions;

import pp.muza.universe.body.BodySystem;

/**
 * A SystemExtension. It is a basic extension that can be applied to a
 * BodySystem.
 */
public interface SystemExtension {

    void apply(double dt);

    void sync();

    void setSystem(BodySystem system);

    String getState();

    void enable();

    void disable();

    void toggle();

    boolean isEnabled();

}
