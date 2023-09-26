package pp.muza.universe.extensions;

import pp.muza.complex.Complex;

public interface GravitationVectorExtension extends SystemExtension {


    void setG(double g);

    void setG(Complex g);

    Complex setG(double g, double angle);
}
