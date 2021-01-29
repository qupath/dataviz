package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.PlotFactory;
import net.mahdilamb.charts.series.Dataset;
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
        System.out.println(iris.get("sepal_length"));

        System.out.println((Dataset.from(new File("D:\\mahdi\\Desktop\\train.csv")).get("MSZoning")));
    }
}
