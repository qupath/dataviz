package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.Chart;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;

import java.io.File;
import java.util.Objects;

public class SwingTest {
    public static void main(String[] args) {
        final File source = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile());
        final DataFrame iris = DataFrame.from(source);
        final Chart.Scatter s = new Chart.Scatter(((DoubleSeries) iris.getDoubleSeries("sepal_width")).toArray(new double[0]), ((DoubleSeries) iris.getDoubleSeries("sepal_length")).toArray(new double[0]));
        s.setGroupBy("species", iris.getStringSeries("species"));
       /* show("Sepal length vs width",
                "Sepal width", "Sepal length",
                scatter(iris, "sepal_width", "sepal_length", "petal_length")
                        .setMarker('o')
                        .setName("sepal: length v width")

        );*/


    }
}
