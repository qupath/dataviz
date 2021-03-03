package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.Figure;
import net.mahdilamb.dataviz.Theme;
import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.statistics.ArrayUtils;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.dataviz.SeriesTests.loadDataFromResource;
import static net.mahdilamb.statistics.ArrayUtils.full;
import static net.mahdilamb.statistics.ArrayUtils.linearlySpaced;

public class SwingTest {
    static final DataFrame iris = loadDataFromResource("iris.csv");
    static final DataFrame gapminder = loadDataFromResource("gapminder.csv");

    /**
     * Create a basic scatter plot from an array of doubles
     */
    static void scatter0() {
        new Scatter(new double[]{0, 1, 2, 3, 4}, new double[]{0, 1, 4, 9, 16})
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show()

        ;
    }

    /**
     * Create scatter plot from a data frame
     */
    static void scatter1() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show()
        ;
    }

    /**
     * Create scatter plot from a data frame, using size and color as dimensions
     */
    static void scatter2() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .setColors("species")
                .setSizes("petal_length")
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show()
        ;
    }

    /**
     * Create line plot from an array and lambda
     */
    static void line1() {
        new Line(linearlySpaced(0, 2 * Math.PI, 100), Math::cos)
                .setXLabel("t")
                .setYLabel("cos(t)")
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show()
        ;
    }

    /**
     * Create line plot from a data frame
     */
    static void line2() {
        new Line(gapminder.query("continent == 'Oceania'"), "year", "lifeExp")
                .setColors("country")
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show()
        ;
    }

    /**
     * Create a scatter with different marker modes
     */
    static void scatterMarkerMode() {

        int N = 100;
        double[] random_x = linearlySpaced(0, 1, N);
        double[] random_y0 = full(() -> ThreadLocalRandom.current().nextGaussian() + 5, N);
        double[] random_y1 = full(ThreadLocalRandom.current()::nextGaussian, N);
        double[] random_y2 = full(() -> ThreadLocalRandom.current().nextGaussian() - 5, N);

        new Figure()
                .addTraces(
                        new Scatter(random_x, random_y0)
                                .setMarkerMode("markers")
                                .setName("markers"),
                        new Scatter(random_x, random_y1)
                                .setMarkerMode("lines+markers")
                                .setName("lines+markers"),
                        new Scatter(random_x, random_y2)
                                .setMarkerMode("lines")
                                .setName("lines")
                )
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show();

    }

    /**
     * Create a scatter chart from raw values
     */
    static void bubble() {
        new Scatter(new double[]{1, 2, 3, 4}, new double[]{10, 11, 12, 13})
                .setColors(0, 1, 2, 3)
                .setSizes(40, 60, 80, 100)
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .show();
    }

    /**
     * Create a styled scatter chart
     */
    static void styledScatter() {
        double[] t = linearlySpaced(0, 10, 100);
        new Figure()
                .addTraces(
                        new Scatter(t, Math::sin)
                                .setName("sin")
                                .setColor("rgba(152, 0, 0, .8)"),
                        new Scatter(t, Math::cos)
                                .setName("cos")
                                .setColor("rgba(255, 182, 193, .9)")
                )
                .updateTraces(Scatter.class, trace -> {
                    trace.setMarkerMode("markers");
                    trace.setEdgeWidth(2);
                    trace.setSize(10);
                })
                .updateLayout(layout -> {
                    layout.setTitle("Styled scatter");
                    layout.getXAxis().showZeroLine(false);
                    layout.getYAxis().showZeroLine(false);
                })
                .show();

    }

    static void colorDimensionScatter() {
        new Scatter(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, 500))
                .updateFigure(fig -> {
                    fig.apply(Theme.Plotly);
                })
                .setColors(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, 500))
                .setColormap("viridis")
                .setSize(16)
                .show();
    }

    static void colorDimensionScatter1() {
        int N = 100000;
        new Scatter(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N), ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N))
                .setColors(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N))
                .setColormap("viridis")
                .setShape(",")
                .show();
    }


    public static void main(String[] args) {
        colorDimensionScatter();
    }
}
