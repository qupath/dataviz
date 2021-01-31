package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.PlotFactory;
import net.mahdilamb.charts.series.Dataset;
import net.mahdilamb.charts.series.DoubleSeries;
import net.mahdilamb.charts.statistics.BinWidthEstimator;
import net.mahdilamb.charts.statistics.StatUtils;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class DatasetTests {
    @Test
    public void fromCSVTest() {
        final Dataset iris = Dataset.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile()));
        System.out.println(
                PlotFactory.scatter(iris, "sepal_length", "sepal_width")
                        .setMarker('^')
                        .setColor("red")
        );
        System.out.println(StatUtils.histogram(BinWidthEstimator.NUMPY_AUTO, ((DoubleSeries) iris.getDoubleSeries("sepal_length")).toArray(new double[0])));
    }
}
