package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.axes.NumericAxis;
import net.mahdilamb.charts.series.Dataset;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static net.mahdilamb.charts.PlotFactory.scatter;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingTest {
    public static void main(String[] args) {
        final File source = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile());
        final Dataset iris = Dataset.from(source);
        show("Sepal length vs width",
                "Sepal width", "Sepal length",
                scatter(iris, "sepal_width", "sepal_length", "petal_length")
                        .setMarker('o')
                        .setName("sepal: length v width"),
                chart -> {
                    chart.getPlot().getXAxis().setMajorTickSpacing(1);
                    ((NumericAxis)chart.getPlot().getXAxis()).setMinorTickSpacing(0.2);
                    chart.getPlot().getYAxis().setMajorTickSpacing(1);
                    ((NumericAxis)chart.getPlot().getYAxis()).setMinorTickSpacing(0.2);
                    try {
                        chart.saveAs(new File("D:\\mahdi\\Desktop\\123.svg"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );


    }
}
