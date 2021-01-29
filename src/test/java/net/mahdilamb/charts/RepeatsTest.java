package net.mahdilamb.charts;

import net.mahdilamb.charts.series.StringSeries;
import org.junit.Test;

import static net.mahdilamb.charts.series.StringRepetition.group;
import static net.mahdilamb.charts.series.StringRepetition.repeat;
import static org.junit.Assert.assertEquals;

public class RepeatsTest {
    @Test
    public void RepTest() {
        final StringSeries s = group("test", repeat("red", 1), repeat("green", 3), repeat("blue", 3));
        assertEquals(s.get(0),"red");
        assertEquals(s.get(1),"green");


    }
}
