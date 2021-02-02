package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.series.DataFrame;

import java.io.File;
import java.util.Objects;

import static net.mahdilamb.charts.PlotFactory.scatter;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingTest {
    public static void main(String[] args) {
        final File source = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile());
        final DataFrame iris = DataFrame.from(source);
        show("Sepal length vs width",
                "Sepal width", "Sepal length",
                scatter(iris, "sepal_width", "sepal_length", "petal_length")
                        .setMarker('o')
                        .setName("sepal: length v width")

        );


    }
}
