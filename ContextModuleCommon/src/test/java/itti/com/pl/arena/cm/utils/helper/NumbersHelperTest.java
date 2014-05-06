package itti.com.pl.arena.cm.utils.helper;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumbersHelperTest {

    @Test
    public void testChangePrecisionNull() {
        assertNull(NumbersHelper.changePrecision(null));
    }

    @Test
    public void testChangePrecisionZero() {
        Double value = new Double(0);
        assertEquals(value, NumbersHelper.changePrecision(value));
    }

    @Test
    public void testChangePrecisionCutPrecision() {
        double value = 3.1412;
        assertEquals((Double) 3.141, (Double) NumbersHelper.changePrecision(value));
    }

}
