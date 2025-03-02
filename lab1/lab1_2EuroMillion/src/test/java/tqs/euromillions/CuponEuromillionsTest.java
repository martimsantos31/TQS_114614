package tqs.euromillions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CuponEuromillionsTest {
    private CuponEuromillions cupon;
    private Dip dip1;
    private Dip dip2;

    @BeforeEach
    public void setUp() {
        cupon = new CuponEuromillions();
        dip1 = new Dip();
        dip2 = new Dip();
    }

    @Test
    public void testAppendDip() {
        assertEquals(0, cupon.countDips(), "Initially, the cupon should have no dips.");

        cupon.appendDip(dip1);
        assertEquals(1, cupon.countDips(), "After adding one dip, count should be 1.");
        assertSame(dip1, cupon.getDipByIndex(0), "The first dip should match the one added.");

        cupon.appendDip(dip2);
        assertEquals(2, cupon.countDips(), "After adding two dips, count should be 2.");
        assertSame(dip2, cupon.getDipByIndex(1), "The second dip should match the one added.");
    }

    @Test
    public void testCountDips() {
        assertEquals(0, cupon.countDips(), "New cupon should have 0 dips.");

        cupon.appendDip(dip1);
        assertEquals(1, cupon.countDips(), "Cupon should have 1 dip after adding one.");

        cupon.appendDip(dip2);
        assertEquals(2, cupon.countDips(), "Cupon should have 2 dips after adding another.");
    }

    @Test
    public void testGetDipByIndex() {
        cupon.appendDip(dip1);
        cupon.appendDip(dip2);

        assertEquals(dip1, cupon.getDipByIndex(0), "First dip should match.");
        assertEquals(dip2, cupon.getDipByIndex(1), "Second dip should match.");

        assertThrows(IndexOutOfBoundsException.class, () -> cupon.getDipByIndex(2),
                "Accessing an out-of-bounds dip should throw an exception.");
    }

    @Test
    public void testFormat() {
        cupon.appendDip(dip1);
        cupon.appendDip(dip2);

        String formatted = cupon.format();

        assertTrue(formatted.contains("Dip #1:"), "Formatted output should contain 'Dip #1:'.");
        assertTrue(formatted.contains("Dip #2:"), "Formatted output should contain 'Dip #2:'.");
    }

    @Test
    public void testIterator() {
        cupon.appendDip(dip1);
        cupon.appendDip(dip2);

        int count = 0;
        for (Dip dip : cupon) {
            assertNotNull(dip, "Each dip in iteration should not be null.");
            count++;
        }
        assertEquals(2, count, "Iterator should iterate over exactly 2 dips.");
    }
}
