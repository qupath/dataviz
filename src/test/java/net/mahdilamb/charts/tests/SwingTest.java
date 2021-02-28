package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.Scatter;
import net.mahdilamb.dataframe.DataFrame;

import java.io.File;

import static net.mahdilamb.charts.SeriesTests.loadDataFromResource;

public class SwingTest {
    public static void main(String[] args) {
        final DataFrame iris = loadDataFromResource("iris.csv");
        new Scatter(iris,"sepal_width","sepal_length")
                .setColors("species")
                .setSizes("petal_length")
                .show();
    }
}
