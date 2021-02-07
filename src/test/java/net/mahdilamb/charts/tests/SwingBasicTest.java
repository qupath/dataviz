package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.plots.Scatter;

import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingBasicTest {
    public static void main(String[] args) {
        final Scatter s = new Scatter(new double[]{0, 1, 2, 3, 4, 5}, new double[]{0, 1, 2, 3, 4, 5});
        final Scatter t = new Scatter(new double[]{0, 1, 2, 3, 4, 5}, new double[]{0, 1, 4, 9, 16, 25})
                .setColors(new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5});
        show("Sepal length vs width", s, t);


    }
}
