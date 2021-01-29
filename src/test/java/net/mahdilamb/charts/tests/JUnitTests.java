package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.graphics.MarkerShape;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitTests {
    @Test
    public void findMarkerTest() {
        assertEquals(MarkerShape.X, MarkerShape.get(" x"));
    }
}
