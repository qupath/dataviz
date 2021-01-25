package net.mahdilamb.charts;

import net.mahdilamb.charts.series.StringSeries;
import org.junit.Test;

import static net.mahdilamb.charts.series.StringRepetition.group;
import static net.mahdilamb.charts.series.StringRepetition.rep;
import static org.junit.Assert.assertEquals;

public class RepeatsTest {
    @Test
    public void RepTest() {
        final StringSeries s = group("test", rep("red", 1), rep("green", 3), rep("blue", 3));
        assertEquals(s.get(0),"red");
        assertEquals(s.get(1),"green");


    }
}
