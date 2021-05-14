package net.mahdilamb.dataviz.demos;

import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.stats.ArrayUtils;

import static net.mahdilamb.dataviz.demos.ScatterDemos.gapminder;

public class LineDemos {
    static void basicLine() {
        new Line(ArrayUtils.linearlySpaced(0, 2 * Math.PI, 100), Math::cos)
                .show();
    }

    static void colorBy() {
        new Line(gapminder.query("continent == 'Oceania'"), "year", "lifeExp")
                .show();
    }

    public static void main(String[] args) {
        basicLine();
    }
}
