package pp.muza.complex.impl;


import pp.muza.complex.Complex;

/*
 * Some useful static methods for Complex.
 */
public final class ComplexUtils {


    /**
     * Create a new complex number
     *
     * @param x the first value
     * @param y the second value
     * @return a new complex number
     */
    public static Complex of(double x, double y) {
        return new BaseComplex(x, y);
    }

    /**
     * Create a new complex number
     *
     * @param x the first value
     * @param y the second value
     * @param z the third value
     * @return a new complex number
     */
    public static Complex of(double x, double y, double z) {
        return new BaseComplex(x, y, z);
    }

    public static Complex of(double... values) {
        return new BaseComplex(values);
    }

    public static Complex of(Complex value) {
        return new BaseComplex(value);
    }

    public static Complex immutableOf(double... values) {
        return new BaseComplex(values) {
            private boolean _protected = false;

            @Override
            protected void onChange() {
                if (_protected) {
                    throw new UnsupportedOperationException("Immutable complex");
                }
                _protected = true;
                super.onChange();
            }
        };
    }
}
