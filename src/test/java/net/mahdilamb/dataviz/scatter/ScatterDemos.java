package net.mahdilamb.dataviz.scatter;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.plots.Scatter;

import java.awt.*;

import static net.mahdilamb.dataviz.tests.SwingTest.loadDataFromResource;

public class ScatterDemos {

    static final DataFrame iris = loadDataFromResource("iris.csv");
    static final DataFrame gapminder = loadDataFromResource("gapminder.csv");
    static final DataFrame tips = loadDataFromResource("tips.csv");

    static void basicScatter() {
        new Scatter(
                new double[]{0, 1, 2, 3, 4},
                new double[]{0, 1, 4, 9, 16}
        )
                .show();
    }

    static void irisSepalScatter() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .show();
    }

    static void irisSepalSizeAndColorScatter() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .setColors("species")
                .setSizes("petal_length")
                .show();
    }

    public static void main(String[] args) {
        irisSepalSizeAndColorScatter();
    }
}
