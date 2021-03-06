package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.Figure;
import net.mahdilamb.dataviz.Theme;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.plots.Bar;
import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.statistics.ArrayUtils;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.dataviz.SeriesTests.loadDataFromResource;
import static net.mahdilamb.statistics.ArrayUtils.*;

public class SwingTest {
    static final DataFrame iris = loadDataFromResource("iris.csv");
    static final DataFrame gapminder = loadDataFromResource("gapminder.csv");
    static final DataFrame tips = loadDataFromResource("tips.csv");


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
        int N = 100_000;
        new Scatter(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N), ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N))
                .setColors(ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N))
                .setColormap("viridis")
                .setSize(8)
                .showEdges(true)
                .show();
    }

    static void line3() {
        new Line(gapminder.query("continent != 'Asia'"), "year", "lifeExp")
                .setColors("continent")
                .setGroups("country")
                .getFigure()
                .apply(Theme.Plotly)
                .show();
    }

    static void scatterAndLine() {
        new Scatter(range(10), x -> x * x)
                .getFigure()
                .apply(Theme.Plotly)
                .show();
    }

    static void styledLine() {
        String[] month = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        double[] high_2000 = {32.5, 37.6, 49.9, 53.0, 69.1, 75.4, 76.5, 76.6, 70.7, 60.6, 45.1, 29.3};
        double[] low_2000 = {13.8, 22.3, 32.5, 37.2, 49.9, 56.1, 57.7, 58.3, 51.2, 42.8, 31.6, 15.9};
        double[] high_2007 = {36.5, 26.6, 43.6, 52.3, 71.5, 81.4, 80.5, 82.2, 76.0, 67.3, 46.1, 35.0};
        double[] low_2007 = {23.6, 14.0, 27.0, 36.8, 47.6, 57.7, 58.9, 61.2, 53.3, 48.5, 31.0, 23.6};
        double[] high_2014 = {28.8, 28.5, 37.0, 56.8, 69.7, 79.7, 78.5, 77.8, 74.1, 62.6, 45.3, 39.9};
        double[] low_2014 = {12.7, 14.3, 18.6, 35.5, 49.9, 58.0, 60.0, 58.6, 51.7, 45.2, 32.2, 29.1};
        new Figure()
                .addTraces(
                        new Scatter(month, high_2014)
                                .setName("High 2014")
                                .setLineWidth(4)
                                .setColor("firebrick"),
                        new Scatter(month, low_2014)
                                .setName("Low 2014")
                                .setLineWidth(4)
                                .setColor("royalblue"),
                        new Scatter(month, high_2007)
                                .setName("High 2007")
                                .setLineWidth(4)
                                .setColor("firebrick")
                                .setLineStyle("dash"),
                        new Scatter(month, low_2007)
                                .setName("Low 2007")
                                .setLineWidth(4)
                                .setColor("royalblue")
                                .setLineStyle("dash"),
                        new Scatter(month, high_2000)
                                .setName("High 2000")
                                .setLineWidth(4)
                                .setColor("firebrick")
                                .setLineStyle("dot"),
                        new Scatter(month, low_2000)
                                .setName("Low 2000")
                                .setLineWidth(4)
                                .setColor("royalblue")
                                .setLineStyle("dot")
                )
                .updateLayout(layout -> {
                    layout.setTitle("Average High and Low Temperatures in New York");
                    layout.getXAxis().setTitle("Month");
                    layout.getYAxis().setTitle("Temperature (degrees F)");
                })
                .apply(Theme.Plotly)
                .show();
    }

    static void filledLines() {
        double[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] x_rev = ArrayUtils.flip(x);

        double[] y1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y1_upper = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        double[] y1_lower = flipInPlace(new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

        double[] y2 = {5, 2.5, 5, 7.5, 5, 2.5, 7.5, 4.5, 5.5, 5};
        double[] y2_upper = {5.5, 3, 5.5, 8, 6, 3, 8, 5, 6, 5.5};
        double[] y2_lower = flipInPlace(new double[]{4.5, 2, 4.4, 7, 4, 2, 7, 4, 5, 4.75});


        double[] y3 = {10, 8, 6, 4, 2, 0, 2, 4, 2, 0};
        double[] y3_upper = {11, 9, 7, 5, 3, 1, 3, 5, 3, 1};
        double[] y3_lower = flipInPlace(new double[]{9, 7, 5, 3, 1, -.5, 1, 3, 1, -1});

        new Figure()
                .addTraces(
                        new Scatter(concatenate(x, x_rev), concatenate(y1_upper, y1_lower))
                                .setName("Fair")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(0,100,80,0.2)")
                                .setLineColor("rgba(255,255,255,0)")
                                .showInLegend(false),
                        new Scatter(
                                concatenate(x, x_rev),
                                concatenate(y2_upper, y2_lower))
                                .setName("Premium")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(0,176,246,0.2)")
                                .setLineColor("rgba(255,255,255,0)")
                                .showInLegend(false),
                        new Scatter(
                                concatenate(x, x_rev),
                                concatenate(y3_upper, y3_lower))
                                .setName("Ideal")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(231,107,243,0.2)")
                                .setLineColor("rgba(255,255,255,0)")
                                .showInLegend(false),
                        new Scatter(x, y1)
                                .setLineColor("rgb(0,100,80)")
                                .setName("Fair"),
                        new Scatter(x, y2)
                                .setLineColor("rgb(0,176,246)")
                                .setName("Premium"),
                        new Scatter(x, y3)
                                .setLineColor("rgb(231,107,243)")
                                .setName("Ideal")

                )
                .updateTraces(Scatter.class, trace -> trace.setMarkerMode("lines"))
                .apply(Theme.Plotly)
                .show();
    }

    static void simpleBar() {
        new Bar(gapminder.query("country == 'Canada'"), "year", "pop")
                .getFigure()
                .apply(Theme.Plotly)
                .show();

    }

    static void longFormBar() {
        new Bar(loadDataFromResource("medals_long.csv"), "nation", "count")
                .setColors("medal")
                .setTitle("Long-Form Input")
                .apply(Theme.Plotly)
                .show();
    }

    static void wideFormBar() {
        new Bar(loadDataFromResource("medals_wide.csv"), "nation", new String[]{"gold", "silver", "bronze"})
                .setTitle("Wide-Form Input")
        // .show()
        ;
    }

    public static void main(String[] args) {
        longFormBar();
    }
}
