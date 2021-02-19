package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.dataframe.Series;
import org.junit.Test;


import static net.mahdilamb.charts.dataframe.StringRepetition.group;
import static net.mahdilamb.charts.dataframe.StringRepetition.repeat;
import static org.junit.Assert.assertEquals;

public class RepeatsTest {
    @Test
    public void RepTest() {
        final Series<String> s = group("test", repeat("red", 1), repeat("green", 3), repeat("blue", 3));
        assertEquals(s.get(0),"red");
        assertEquals(s.get(1),"green");


    }
}
