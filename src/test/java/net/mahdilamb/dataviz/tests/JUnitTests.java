package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataviz.graphics.shapes.MarkerShape;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitTests {
    @Test
    public void findMarkerTest() {
        assertEquals(MarkerShape.X, MarkerShape.get(" x"));
    }
}
