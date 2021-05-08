package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataviz.MarkerShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTests {
    @Test
    public void findMarkerTest() {
        assertEquals(MarkerShape.X, MarkerShape.get(" x"));
    }
}
