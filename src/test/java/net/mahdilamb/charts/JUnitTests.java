package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.MarkerShape;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitTests {
    @Test
    public void findMarkerTest() {
        assertEquals(MarkerShape.X, MarkerShape.get(" x"));
    }
}
