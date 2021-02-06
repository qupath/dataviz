package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.plots.Scatter;

import java.io.File;
import java.util.Objects;

import static net.mahdilamb.charts.PlotFactory.scatter;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingTest {
    public static void main(String[] args) {
        final File source = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile());
        final DataFrame iris = DataFrame.from(source);
        final Scatter s = new Scatter(null, iris.getDoubleSeries("sepal_width").toArray(new double[0]), iris.getDoubleSeries("sepal_length").toArray(new double[0]));
        s.setColors("species", iris.getStringSeries("species"));
        show("Sepal length vs width",
                "Sepal width", "Sepal length",
                scatter(iris, "sepal_width", "sepal_length", "petal_length")
                        .setMarker('o')
                        .setName("sepal: length v width")

        );


    }
}
