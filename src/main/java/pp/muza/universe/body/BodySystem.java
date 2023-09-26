package pp.muza.universe.body;

import pp.muza.universe.extensions.SystemExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BodySystem {

    static final int MAX_BODIES = 100;
    List<Body> bodies = new ArrayList<>(MAX_BODIES);
    List<Body> unmodifiableBodies = null;
    List<SystemExtension> extensions = new ArrayList<>();

    private int step = 0;

    public void step() {
        step++;
    }

    public int getSize() {
        return bodies.size();
    }

    public List<Body> getBodies() {
        if (unmodifiableBodies == null) {
            unmodifiableBodies = Collections.unmodifiableList(bodies);
        }
        return unmodifiableBodies;
    }

    public int addBody(Body body) {
        if (bodies.contains(body)) {
            throw new IllegalArgumentException("Body already added");
        }
        bodies.add(body);
        unmodifiableBodies = null;
        return bodies.size() - 1;
    }

    public int getStep() {
        return step;
    }

    public void removeBody(int index) {
        bodies.remove(index);
        unmodifiableBodies = null;
    }

    public void removeBody(Body body) {
        bodies.remove(body);
    }

    public Body getBody(int index) {
        return bodies.get(index);
    }

    public void addExtension(SystemExtension extension) {
        if (getExtension(extension.getClass().getSimpleName()) != null) {
            throw new IllegalArgumentException("Extension " + extension.getClass().getSimpleName() + " already added");
        }
        extensions.add(extension);
        extension.setSystem(this);
    }

    public void apply(double dt) {
        for (SystemExtension extension : extensions) {
            if (step % 50 == 0) {
                System.out.println("apply " + extension.getClass().getSimpleName() + ": " + extension.getState());
            }
            extension.apply(dt);
        }
    }

    public SystemExtension getExtension(String name) {
        for (SystemExtension extension : extensions) {
            if (name.equals(extension.getClass().getSimpleName())) {
                return extension;
            }
        }
        return null;
    }

    public void enableExtension(String name) {
        SystemExtension extension = getExtension(name);
        if (extension != null) {
            extension.enable();
        } else {
            throw new IllegalArgumentException("Extension " + name + " not found");
        }
    }

    public void clear() {
        bodies.clear();
        unmodifiableBodies = null;
    }

    public boolean isExtensionEnabled(String name) {
        SystemExtension extension = getExtension(name);
        if (extension != null) {
            return extension.isEnabled();
        } else {
            throw new IllegalArgumentException("Extension " + name + " not found");
        }
    }
}
