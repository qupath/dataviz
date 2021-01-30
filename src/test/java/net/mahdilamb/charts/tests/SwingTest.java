package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.series.Dataset;

import java.io.File;
import java.util.Objects;

import static net.mahdilamb.charts.PlotFactory.scatter;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingTest {
    public static void main(String[] args) {
        final File source = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile());
        final Dataset iris = Dataset.from(source);
        show(
                "Sepal length vs width",
                800, 640,
                scatter(iris, "sepal_length", "sepal_width", "petal_length")
                        .setMarker('.')
                        .setName("sepal: length v width")
                        .setGroups(iris.get("species").asString())
        );


    }
}
