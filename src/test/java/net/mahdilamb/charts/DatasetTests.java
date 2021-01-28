package net.mahdilamb.charts;

import net.mahdilamb.charts.datasets.Dataset;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class DatasetTests {
    @Test
    public void fromCSVTest() {
        final Dataset iris = Dataset.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile()));
        System.out.println(iris);
        System.out.println(
                Chart.scatter(iris, "sepal_length", "sepal_width")
                        .setMarker('^')
                        .setColor("red")
        );
        System.out.println(Dataset.from(new File("D:\\mahdi\\Desktop\\train.csv")));
    }
}
