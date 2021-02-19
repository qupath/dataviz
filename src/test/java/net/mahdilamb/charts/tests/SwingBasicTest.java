package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.plots.Line;
import net.mahdilamb.charts.plots.Scatter;

import java.io.File;

import static net.mahdilamb.charts.ScatterTests.loadData;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingBasicTest {
    public static void main(String[] args) {

        final Scatter c = new Scatter(loadData("tips.csv"), "total_bill", "tip")
                //  .setColors("day")
                //.setShapes("time")
                ;
        show(
                new Line(loadData("gapminder.csv").query("continent == 'Oceania'"), "year", "lifeExp")
                        .setColors("country")

        );

    }
}
