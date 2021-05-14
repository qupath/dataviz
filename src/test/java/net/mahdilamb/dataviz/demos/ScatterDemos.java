package net.mahdilamb.dataviz.demos;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.Figure;
import net.mahdilamb.dataviz.Theme;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.stats.ArrayUtils;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.dataviz.tests.SwingTest.loadDataFromResource;
import static net.mahdilamb.stats.ArrayUtils.full;
import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;

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
                // .setTitle("Sepal width v length (iris)")
                .setSizes("petal_length")
                .setColors("species")
                .updateLegend(legend -> {
                    //  legend.setSide(Side.LEFT);
                    // legend.setFloating(true);
                    //legend.setFloating(false);

                })
                .show();
    }


    static void functionalScatter() {
        new Scatter(ArrayUtils.linearlySpaced(0, 10, 100), Math::sin)
                .show();
    }

    static void markerModeScatter() {
        int N = 100;
        double[] random_x = linearlySpaced(0, 1, N);
        double[] random_y0 = full(() -> ThreadLocalRandom.current().nextGaussian() + 5, N);
        double[] random_y1 = full(ThreadLocalRandom.current()::nextGaussian, N);
        double[] random_y2 = full(() -> ThreadLocalRandom.current().nextGaussian() - 5, N);
        new Figure()
                .addData(
                        new Scatter(random_x, random_y0)
                                .setName("markers")
                                .setMarkerMode("markers"),
                        new Scatter(random_x, random_y1)
                                .setName("lines+markers")
                                .setMarkerMode("lines+markers"),
                        new Scatter(random_x, random_y2)
                                .setName("lines")
                                .setMarkerMode("lines")
                )
                .show();
    }


    public static void main(String[] args) {
        markerModeScatter();
    }
}
