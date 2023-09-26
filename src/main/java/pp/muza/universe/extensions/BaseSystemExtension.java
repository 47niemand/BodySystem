package pp.muza.universe.extensions;

import pp.muza.universe.body.BodySystem;

public abstract class BaseSystemExtension implements SystemExtension {

    protected BodySystem system;
    protected boolean enabled = false;

    public BaseSystemExtension() {

    }

    public void setSystem(BodySystem system) {
        this.system = system;
    }

    protected abstract void doApply(double dt);

    public final void apply(double dt) {
        if (enabled) {
            doApply(dt);
        }
     }

    public abstract void sync();

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getState() {
        return this.getClass().getSimpleName() + ": " + system.getBodies().size() + " bodies";
    }
}
