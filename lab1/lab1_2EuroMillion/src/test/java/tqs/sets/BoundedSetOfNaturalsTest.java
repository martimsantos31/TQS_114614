/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.sets.BoundedSetOfNaturals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;
    private BoundedSetOfNaturals setD;


    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);
        setB = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
        setD = new BoundedSetOfNaturals(2);
    }

    @AfterEach
    public void tearDown() {
        setA = setB = setC = setD = null;
    }

    //@Disabled("TODO revise test logic")
    @Test
    public void testAddElement() {
        setA.add(99);
        assertTrue(setA.contains(99), "add: added element not found in set.");
        assertEquals(1, setA.size());

        assertThrows(IllegalArgumentException.class, () -> setA.add(0), "add: zero is not a natural number.");
        assertThrows(IllegalArgumentException.class, () -> setA.add(-5), "add: negative numbers are not allowed.");

        assertThrows(IllegalArgumentException.class, () -> setB.add(10), "add: duplicate value should throw exception.");
        assertFalse(setB.contains(11), "add: added element should not be present in set.");
        assertEquals(6, setB.size(), "add: elements count not as expected.");
    }

    @Test
    public void testAddEqualElement(){
        setD.add(10);
        assertThrows(IllegalArgumentException.class, () -> setD.add(10), "add: duplicate value should throw exception.");
        assertEquals(6, setB.size(), "add: elements count not as expected.");
    }

    @Test
    public void testAddBadElement(){
        assertThrows(IllegalArgumentException.class, () -> setA.add(0), "add: zero is not a natural number.");
        assertEquals(0, setA.size(), "add: elements count not as expected.");
    }


    //@Disabled("TODO revise to test the construction from invalid arrays")
    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};

        // must fail with exception
        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @Test
    public void testIntersects() {
        BoundedSetOfNaturals set1 = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40});
        BoundedSetOfNaturals set2 = BoundedSetOfNaturals.fromArray(new int[]{30, 50, 60});
        BoundedSetOfNaturals set3 = BoundedSetOfNaturals.fromArray(new int[]{100, 200, 300});

        assertTrue(set1.intersects(set2), "intersects: sets should intersect");

        assertFalse(set1.intersects(set3), "intersects: sets should not intersect");

        assertTrue(set1.intersects(set1), "intersects: set should intersect with itself");
    }

    @Test
    public void testIterator() {
        BoundedSetOfNaturals set = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});

        Iterator<Integer> iterator = set.iterator();

        assertNotNull(iterator, "iterator: should not be null");

        ArrayList<Integer> elements = new ArrayList<>();
        while (iterator.hasNext()) {
            elements.add(iterator.next());
        }

        assertEquals(3, elements.size(), "iterator: size mismatch");
        assertTrue(elements.containsAll(Arrays.asList(10, 20, 30)), "iterator: missing elements");
    }

    @Test
    public void testHashCode() {
        BoundedSetOfNaturals set1 = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});
        BoundedSetOfNaturals set2 = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});
        BoundedSetOfNaturals set3 = BoundedSetOfNaturals.fromArray(new int[]{40, 50, 60});

        assertEquals(set1.hashCode(), set2.hashCode(), "hashCode: equal sets should have the same hash");
        assertNotEquals(set1.hashCode(), set3.hashCode(), "hashCode: different sets should have different hashes");
    }

    @Test
    public void testEquals() {
        BoundedSetOfNaturals set1 = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});
        BoundedSetOfNaturals set2 = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});
        BoundedSetOfNaturals set3 = BoundedSetOfNaturals.fromArray(new int[]{40, 50, 60});
        BoundedSetOfNaturals set4 = new BoundedSetOfNaturals(5);

        assertEquals(set1, set2, "equals: identical sets should be equal");
        assertNotEquals(set1, set3, "equals: different sets should not be equal");
        assertNotEquals(set1, set4, "equals: empty set should not be equal to a non-empty set");
        assertNotEquals(null, set1, "equals: should return false when comparing to null");
    }

}
