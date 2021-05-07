package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.plots.Scatter;

import java.io.File;
import java.util.Objects;

public class SwingTest {


    public static DataFrame loadDataFromResource(final String resourcePath) {
        return DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(resourcePath)).getFile()));
    }

    static final DataFrame iris = loadDataFromResource("iris.csv");
    static final DataFrame gapminder = loadDataFromResource("gapminder.csv");
    static final DataFrame tips = loadDataFromResource("tips.csv");

    public static void main(String[] args) {
        final double[] x = new double[]{0, 1, 2, 3, 4};
        final double[] y = new double[]{0, 1, 4, 9, 16};
        new Scatter(iris,"petal_length","petal_width")
                .show();

    }
}
