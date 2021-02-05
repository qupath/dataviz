package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.PlotFactory;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.statistics.BinWidthEstimator;
import net.mahdilamb.charts.statistics.StatUtils;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class DataFrameTests {
    @Test
    public void fromCSVTest() {
        final DataFrame iris = DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile()));
        System.out.println(
                PlotFactory.scatter(iris, "sepal_length", "sepal_width")
                        .setMarker('^')
                        .setColor("red")
        );
        System.out.println(iris.subset(s -> s.startsWith("sepal")).first().subset(i -> i > 130));
        System.out.println(StatUtils.histogram(BinWidthEstimator.NUMPY_AUTO, ((DoubleSeries) iris.getDoubleSeries("sepal_length")).toArray(new double[0])));
        System.out.println(DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("gapminder.csv")).getFile())).getStringSeries("iso_alpha").groups());

    }
}
