package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.dataframe.plots.DataframeScatter;
import net.mahdilamb.charts.plots.Bar;
import net.mahdilamb.charts.plots.Line;
import net.mahdilamb.charts.plots.Scatter;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static net.mahdilamb.charts.statistics.ArrayUtils.linearlySpaced;

public class SeriesTests {
    static final DataFrame iris = DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile()));
    static final DataFrame tips = DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("tips.csv")).getFile()));
    static final DataFrame gapminder = DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("gapminder.csv")).getFile()));


    @Test
    public void basicScatterTest() {
        final double[] x = {0, 1, 2, 3, 4};
        final double[] y = {0, 1, 4, 9, 16};
        final Scatter s = new Scatter(x, y);
        final DataframeScatter t = new DataframeScatter(iris, "sepal_width", "sepal_length")
                .setColors("species")
                .setMarkerSizes("petal_length");
        final Line u = new Line(linearlySpaced(0, 2 * Math.PI, 100), Math::cos)
                .setXLabel("t")
                .setYLabel("cos(t)");
        final Bar v = new Bar(tips.getStringSeries("sex").toArray(new String[tips.size(Axis.INDEX)]), ((DoubleSeries) tips.getDoubleSeries("total_bill")).toArray(new double[tips.size(Axis.INDEX)]))
                .setMode(Bar.Mode.GROUPED)
                .setColors("smoker", tips.getStringSeries("smoker"));
        final Line w = new Line(((DoubleSeries) gapminder.getDoubleSeries("year")).toArray(new double[tips.size(Axis.INDEX)]), ((DoubleSeries) gapminder.getDoubleSeries("lifeExp")).toArray(new double[tips.size(Axis.INDEX)]))
                .setColors("continent", gapminder.getStringSeries("continent"))
                .setGroups("country", gapminder.getStringSeries("country"));
        System.out.println(w);

    }
}
