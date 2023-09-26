package pp.muza.universe.extensions;

import pp.muza.complex.Complex;

/**
 * A ForceExtension is an extension that can apply forces to bodies in the
 * system.
 */
public interface ForceExtension extends SystemExtension {

    Complex getForce(int i);

    /**
     * A SetForceExtension is an extension that requires a ForceExtension to be set.
     */
    interface SetForceExtension extends SystemExtension {

        void setForceSystem(ForceExtension forceSystem);

    }
}
