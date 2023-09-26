package pp.muza.complex.impl;

import pp.muza.complex.Complex;

import java.util.Arrays;

/*
 * Base implementation of Complex interface.
 * This class is mutable and not thread safe.
 */
public class BaseComplex implements Complex {

    public static final double EPSILON = 1e-15;

    final transient int dimension;
    final double[] value;
    private int version = 0;

    private double _squareModule;
    private boolean _normalized;
    private boolean _null;
    private boolean _changed = false;

    public BaseComplex(int dimension) {
        this.dimension = dimension;
        this.value = new double[dimension];
    }

    public BaseComplex(Complex complex) {
        this(complex.getDimension());
        set(complex);
        if (complex instanceof BaseComplex) {
            BaseComplex baseComplex = (BaseComplex) complex;
            _squareModule = baseComplex._squareModule;
            _normalized = baseComplex._normalized;
            _null = baseComplex._null;
            _changed = baseComplex._changed;
        }
    }

    public BaseComplex(double... values) {
        this(values.length);
        set(values);
    }

    private static void checkIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be positive");
        }
    }

    protected void onChange() {
        _squareModule = Double.NaN;
        _normalized = false;
        _null = false;
        _changed = true;
        version++;
    }

    @Override
    public void setNull() {
        if (!_changed && _null) {
            return;
        }
        onChange();
        for (int i = 0; i < dimension; i++) {
            this.value[i] = 0.0;
        }
        _squareModule = 0.0;
        _null = true;
        _changed = false;
    }

    @Override
    public void setUnassigned() {
        onChange();
        for (int i = 0; i < dimension; i++) {
            this.value[i] = Double.NaN;
        }
        _squareModule = Double.NaN;
        _changed = false;
    }

    @Override
    public void normalize() {
        if (!_normalized) {
            onChange();
            double m = Math.sqrt(squareModule());
            if (m > 0.0) {
                for (int i = 0; i < dimension; i++) {
                    this.value[i] = value[i] / m;
                }
            }
            _normalized = true;
            _squareModule = 1.0;
            _changed = false;
        }
    }

    @Override
    public boolean isNull() {
        return _null;
    }

    @Override
    public boolean isZero() {
        for (int i = 0; i < dimension; i++) {
            if (value[i] != 0.0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInfinity() {
        for (int i = 0; i < dimension; i++) {
            if (Double.isInfinite(value[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNaN() {
        for (int i = 0; i < dimension; i++) {
            if (Double.isNaN(value[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUnassigned() {
        if (_changed) {
            for (int i = 0; i < dimension; i++) {
                if (Double.isNaN(value[i])) {
                    _squareModule = Double.NaN;
                    _changed = false;
                    break;
                }
            }
        }
        return !_changed && Double.isNaN(_squareModule);
    }

    @Override
    public boolean isNormalized() {
        if (_changed) {
            _normalized = !(Math.abs(squareModule() - 1.0) > EPSILON);
        }
        return _normalized;
    }

    @Override
    public void dec(Complex complex) {
        onChange();
        for (int i = 0; i < dimension; i++) {
            this.value[i] -= complex.getValue(i);
        }
    }

    @Override
    public void dec(double... values) {
        onChange();
        for (int i = 0; i < dimension; i++) {
            this.value[i] -= values[i];
        }
    }

    @Override
    public void change(Complex complex) {
        onChange();
        for (int i = 0; i < dimension; i++) {
            assert Double.isFinite(complex.getValue(i));
            this.value[i] += complex.getValue(i);
            assert Double.isFinite(this.value[i]);
        }
    }

    @Override
    public void change(double... values) {
        onChange();
        for (int i = 0; i < dimension; i++) {
            assert Double.isFinite(values[i]);
            this.value[i] += values[i];
            assert Double.isFinite(this.value[i]);
        }
    }

    @Override
    public void scale(double scale) {
        assert Double.isFinite(scale);
        onChange();
        for (int i = 0; i < dimension; i++) {
            assert Double.isFinite(this.value[i]);
            this.value[i] *= scale;
            assert Double.isFinite(this.value[i]);
        }
    }

    @Override
    public double squareModule() {
        double res = 0.0;
        if (Double.isNaN(_squareModule)) {
            for (int i = 0; i < dimension; i++) {
                res += (this.value[i] * this.value[i]);
            }
            assert Double.isFinite(res);
            _squareModule = res;

        }
        return _squareModule;
    }

    @Override
    public double getValue(int index) {
        checkIndex(index);
        if (index >= dimension) {
            return 0.0;
        }
        return value[index];
    }

    @Override
    public void setValue(int index, double value) {
        checkIndexBounds(index);
        onChange();
        this.value[index] = value;
    }

    private void checkIndexBounds(int index) {
        checkIndex(index);
        if (index >= dimension) {
            throw new IllegalArgumentException("Index must be less than dimension");
        }
    }

    @Override
    public void setIfGreater(int index, double value, double expected) {
        checkIndex(index);
        synchronized (this) {
            if (value > expected) {
                onChange();
                this.value[index] = value;
            }
        }
    }

    @Override
    public void setIfLess(int index, double value, double expected) {
        checkIndex(index);
        synchronized (this) {
            if (value < expected) {
                onChange();
                this.value[index] = value;
            }
        }
    }

    @Override
    public double[] get() {
        double[] copy = new double[dimension];
        System.arraycopy(this.value, 0, copy, 0, dimension);
        return copy;
    }

    @Override
    public void set(double... values) {
        if (values.length > dimension) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        onChange();
        for (int i = 0; i < dimension; i++) {
            if (i < values.length) {
                this.value[i] = values[i];
            } else {
                this.value[i] = 0.0;
            }
        }
    }

    @Override
    public void set(Complex source) {
        if (source.getDimension() > dimension) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        onChange();
        for (int i = 0; i < dimension; i++) {
            this.value[i] = source.getValue(i);
        }
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Complex complex = (Complex) o;
        return Arrays.equals(value, complex.get());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public Complex copy() {
        return new BaseComplex(this);
    }

    @Override
    public boolean equals(Complex complex, double epsilon) {
        if (complex.getDimension() != dimension) {
            throw new IllegalArgumentException("dimensions are not equal");
        }
        for (int i = 0; i < dimension; i++) {
            double d = Math.abs(value[i] - complex.getValue(i));
            if (d > epsilon) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < dimension; i++) {
            sb.append(value[i]);
            if (i < dimension - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
