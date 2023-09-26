package pp.muza.complex;

import org.junit.jupiter.api.Test;
import pp.muza.complex.impl.BaseComplex;

import static org.junit.jupiter.api.Assertions.*;

class BaseComplexTest {

    @Test
    void setNull() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.setNull();
        assertTrue(complex.isNull());
        Complex complex1 = new BaseComplex(0, 0, 0, 0, 0, 0);
        assertFalse(complex1.isNull());
        assertTrue(complex1.isZero());
        Complex complex2 = new BaseComplex(0, 0, 0, 0, 0, 0);
        complex2.change(0, 0, 0, 0, 0, 0);
        assertFalse(complex2.isNull());
        assertTrue(complex2.isZero());
        complex2.setNull();
        assertTrue(complex2.isNull());
        assertTrue(complex2.isZero());
        complex2.set(1, 2, 3, 4, 5, 6);
        assertFalse(complex2.isNull());
        assertFalse(complex2.isZero());
    }

    @Test
    void setUnassigned() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.setUnassigned();
        assertTrue(complex.isUnassigned());
        Complex complex1 = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertFalse(complex1.isUnassigned());
        complex1.setUnassigned();
        assertTrue(complex1.isUnassigned());
        complex1.set(1, 2, 3, 4, 5, 6);
        assertFalse(complex1.isUnassigned());
    }

    @Test
    void normalize() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertFalse(complex.isNormalized());
        complex.normalize();
        assertTrue(complex.isNormalized());

        Complex complex1 = new BaseComplex(0, 0, 0, 0, 0, 1);
        assertTrue(complex1.isNormalized());
        Complex complex2 = complex1.copy();
        complex1.normalize();
        assertTrue(complex1.isNormalized());
        assertEquals(complex1, complex2);

    }

    @Test
    void dec() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.dec(1, 2, 3, 4, 5, 6);
        assertTrue(complex.isZero());
    }


    @Test
    void change() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.change(1, 2, 3, 4, 5, 6);
        Complex complex1 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertEquals(complex, complex1);
    }

    @Test
    void scale() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.scale(2);
        Complex complex1 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertEquals(complex, complex1);
    }

    @Test
    void squareModule() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(complex.squareModule(), 91);
    }

    @Test
    void getValue() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(complex.getValue(0), 1);
        assertEquals(complex.getValue(1), 2);
        assertEquals(complex.getValue(2), 3);
        assertEquals(complex.getValue(3), 4);
        assertEquals(complex.getValue(4), 5);
        assertEquals(complex.getValue(5), 6);
    }

    @Test
    void setValue() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.setValue(0, 2);
        complex.setValue(1, 4);
        complex.setValue(2, 6);
        complex.setValue(3, 8);
        complex.setValue(4, 10);
        complex.setValue(5, 12);
        Complex complex1 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertEquals(complex, complex1);
    }

    @Test
    void getValues() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(6, complex.getDimension());
        double[] values = complex.get();
        assertEquals(values[0], 1);
        assertEquals(values[1], 2);
        assertEquals(values[2], 3);
        assertEquals(values[3], 4);
        assertEquals(values[4], 5);
        assertEquals(values[5], 6);
    }

    @Test
    void setValues() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        complex.set(2, 4, 6, 8, 10, 12);
        Complex complex1 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertEquals(complex, complex1);
    }

    @Test
    void getDimension() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(complex.getDimension(), 6);

        Complex complex1 = new BaseComplex(1);
        assertEquals(complex1.getDimension(), 1);

        Complex complex2 = new BaseComplex(3);
        assertEquals(complex2.getDimension(), 3);
    }

    @Test
    void testEquals() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex1 = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(complex, complex1);

        Complex complex2 = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex3 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertNotEquals(complex2, complex3);

        Complex complex4 = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex5 = new BaseComplex(1, 2, 3, 4, 5, 7);
        assertNotEquals(complex4, complex5);

        Complex complex6 = new BaseComplex(1, 2 - 1e-15, 3, 4, 5, 6);
        Complex complex7 = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertNotEquals(complex6, complex7);

    }

    @Test
    void testHashCode() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex1 = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertEquals(complex.hashCode(), complex1.hashCode());

        Complex complex2 = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex3 = new BaseComplex(2, 4, 6, 8, 10, 12);
        assertNotEquals(complex2.hashCode(), complex3.hashCode());

        Complex complex4 = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex5 = new BaseComplex(1, 2, 3, 4, 5, 7);
        assertNotEquals(complex4.hashCode(), complex5.hashCode());

        Complex complex6 = new BaseComplex(1, 2 - 1e-15, 3, 4, 5, 6);
        Complex complex7 = new BaseComplex(1, 2, 3, 4, 5, 6);
        assertNotEquals(complex6.hashCode(), complex7.hashCode());
    }

    @Test
    void copy() {
        Complex complex = new BaseComplex(1, 2, 3, 4, 5, 6);
        Complex complex1 = complex.copy();
        assertEquals(complex, complex1);
    }
}