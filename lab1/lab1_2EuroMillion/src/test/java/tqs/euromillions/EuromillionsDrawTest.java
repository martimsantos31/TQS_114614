package tqs.euromillions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.euromillions.CuponEuromillions;
import tqs.euromillions.Dip;
import tqs.euromillions.EuromillionsDraw;

public class EuromillionsDrawTest {

    private CuponEuromillions sampleCoupon;
    private Dip winningDip;

    @BeforeEach
    public void setUp()  {
        sampleCoupon = new CuponEuromillions();
        sampleCoupon.appendDip(Dip.generateRandomDip());
        sampleCoupon.appendDip(Dip.generateRandomDip());
        winningDip = new Dip(new int[]{1, 2, 3, 48, 49}, new int[]{1, 9});
        sampleCoupon.appendDip(new Dip(new int[]{1, 2, 3, 48, 49}, new int[]{1, 9}));
    }


    @DisplayName("reports correct matches in a coupon")
    @Test
    public void testCompareBetWithDrawToGetResults() {
        Dip winningDip, matchesFound;

        // test for full match, using the 3rd dip in the coupon as the Draw results
        winningDip = sampleCoupon.getDipByIndex(2);
        EuromillionsDraw testDraw = new EuromillionsDraw(winningDip);
        matchesFound = testDraw.findMatchesFor(sampleCoupon).get(2);

        assertEquals(winningDip, matchesFound, "expected the bet and the matches found to be equal");

        // test for no matches at all
        testDraw = new EuromillionsDraw(new Dip(new int[]{9, 10, 11, 12, 13}, new int[]{2, 3}));
        matchesFound = testDraw.findMatchesFor(sampleCoupon).get(2);
        // compare empty with the matches found
        assertEquals( new Dip(), matchesFound);
    }

    @Test
    @DisplayName("Test getDrawResults returns the correct Dip")
    public void testGetDrawResults() {
        EuromillionsDraw draw = new EuromillionsDraw(winningDip);

        assertEquals(winningDip, draw.getDrawResults(),
                "getDrawResults should return the exact dip used to create the draw.");
    }

    @Test
    @DisplayName("Test generateRandomDraw returns a valid draw")
    public void testGenerateRandomDraw() {
        EuromillionsDraw randomDraw = EuromillionsDraw.generateRandomDraw();

        assertNotNull(randomDraw, "generateRandomDraw should not return null.");
        assertNotNull(randomDraw.getDrawResults(), "Draw results should not be null.");
        assertEquals(5, randomDraw.getDrawResults().getNumbersColl().size(),
                "A valid Euromillions Dip should have exactly 5 numbers.");
        assertEquals(2, randomDraw.getDrawResults().getStarsColl().size(),
                "A valid Euromillions Dip should have exactly 2 stars.");
    }

}
