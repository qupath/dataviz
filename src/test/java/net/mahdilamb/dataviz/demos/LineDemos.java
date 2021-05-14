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
                .setColors("country")
                .show();
    }

    static void lifeExpectancy() {
        new Line(gapminder.query("country=='Canada'"), "year", "lifeExp")
                .setTitle("Life expectancy in Canada")
                .show();
    }

    static void lineGrouping() {
        new Line(gapminder.query("continent != 'Asia'"), "year", "lifeExp")
             //   .setGroups("country")
                .setColors("continent")
                .show();
    }


    public static void main(String[] args) {
        lineGrouping();
    }
}
