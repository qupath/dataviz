package net.mahdilamb.charts.tests;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.statistics.ArrayUtils;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.charts.ScatterTests.loadData;
import static net.mahdilamb.charts.statistics.ArrayUtils.linearlySpaced;
import static net.mahdilamb.charts.swing.SwingChart.show;

public class SwingBasicTest {
    /*
    import plotly.graph_objects as go
    import numpy as np

    N = 1000
    t = np.linspace(0, 10, 100)
    y = np.sin(t)

    fig = go.Figure(data=go.Scatter(x=t, y=y, mode='markers'))

    fig.show()
     */
    static void SimpleScatterPlot() {
        show(
                new Scatter(linearlySpaced(0, 10, 100), Math::sin)

        );
    }

    /*
    import plotly.graph_objects as go

    # Create random data with numpy
    import numpy as np
    np.random.seed(1)

    N = 100
    random_x = np.linspace(0, 1, N)
    random_y0 = np.random.randn(N) + 5
    random_y1 = np.random.randn(N)
    random_y2 = np.random.randn(N) - 5

    fig = go.Figure()

    # Add traces
    fig.add_trace(go.Scatter(x=random_x, y=random_y0,
                        mode='markers',
                        name='markers'))
    fig.add_trace(go.Scatter(x=random_x, y=random_y1,
                        mode='lines+markers',
                        name='lines+markers'))
    fig.add_trace(go.Scatter(x=random_x, y=random_y2,
                        mode='lines',
                        name='lines'))

    fig.show()
     */
    static void LineAndScatterPlots() {
        int N = 100;
        double[] t = ArrayUtils.linearlySpaced(0, 1, N);
        double[] random_y0 = ArrayUtils.full(() -> ThreadLocalRandom.current().nextGaussian() + 5, N);
        double[] random_y1 = ArrayUtils.full(ThreadLocalRandom.current()::nextGaussian, N);
        double[] random_y2 = ArrayUtils.full(() -> ThreadLocalRandom.current().nextGaussian() - 5, N);
        show(
                new Scatter(t, random_y0).setMode(Scatter.Mode.MARKER_ONLY).setName("markers"),
                new Scatter(t, random_y1).setMode(Scatter.Mode.MARKER_AND_LINE).setName("lines+markers"),
                new Scatter(t, random_y2).setMode(Scatter.Mode.LINE_ONLY).setName("lines")

        );

    }

    public static void main(String[] args) {
        LineAndScatterPlots();
    }
}
