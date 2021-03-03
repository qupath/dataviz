package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataviz.Figure;
import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.dataviz.swing.SwingRenderer;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.DataFrame;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.dataviz.SeriesTests.loadDataFromResource;
import static net.mahdilamb.statistics.ArrayUtils.*;

public final class TraceTests {
    private TraceTests() {

    }

    static final DataFrame tips = loadDataFromResource("tips.csv");
    static final DataFrame iris = loadDataFromResource("iris.csv");
    static final DataFrame gapminder = loadDataFromResource("gapminder.csv");

    /*
    import plotly.express as px
    fig = px.scatter(x=[0, 1, 2, 3, 4], y=[0, 1, 4, 9, 16])
    fig.show()
    */
    static void LineAndScatter0() {
        new Scatter(new double[]{0, 1, 2, 3, 4}, new double[]{0, 1, 4, 9, 16})
                .show();
    }

    /*
    # x and y given as DataFrame columns
    import plotly.express as px
    df = px.data.iris() # iris is a pandas DataFrame
    fig = px.scatter(df, x="sepal_width", y="sepal_length")
    fig.show()
     */
    static void LineAndScatter1() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .show();
    }

    /*
    import plotly.express as px
    df = px.data.iris()
    fig = px.scatter(df, x="sepal_width", y="sepal_length", color="species",
                     size='petal_length', hover_data=['petal_width'])
    fig.show()
     */
    static void LineAndScatter2() {
        new Scatter(iris, "sepal_width", "sepal_length")
                .setColors("species")
                .setSizes("petal_length")
                .addHoverData("petal_width")
                .show();
    }

    /*
    import plotly.express as px
    import numpy as np

    t = np.linspace(0, 2*np.pi, 100)

    fig = px.line(x=t, y=np.cos(t), labels={'x':'t', 'y':'cos(t)'})
    fig.show()
     */
    static void LineAndScatter3() {
        double[] x = linearlySpaced(0, 2 * Math.PI, 100);
        double[] y = map(x, Math::cos);
        new Line(x, y)
                .setXLabel("t")
                .setYLabel("cos(t)")
                .show();
    }

    /*
    df = px.data.gapminder().query("continent == 'Oceania'")
    fig = px.line(df, x='year', y='lifeExp', color='country')
    fig.show()
     */
    static void LineAndScatter4() {
        new Line(gapminder.query("continent == 'Oceania'"), "year", "lifeExp")
                .setColors("country")
                .show();
    }

    /*
    import plotly.graph_objects as go
    import numpy as np

    N = 1000
    t = np.linspace(0, 10, 100)
    y = np.sin(t)

    fig = go.Figure(data=go.Scatter(x=t, y=y, mode='markers'))

    fig.show()
     */
    static void LineAndScatter5() {
        double[] x = linearlySpaced(0, 10, 100);
        double[] y = map(x, Math::sin);
        new Scatter(x, y)
                .show();
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
    static void LineAndScatter6() {
        int N = 100;
        double[] random_x = linearlySpaced(0, 1, N);
        double[] random_y0 = full(() -> ThreadLocalRandom.current().nextGaussian() + 5, N);
        double[] random_y1 = full(ThreadLocalRandom.current()::nextGaussian, N);
        double[] random_y2 = full(() -> ThreadLocalRandom.current().nextGaussian() - 5, N);
        new Figure()
                .addTraces(
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
                .show(SwingRenderer::new);

    }

    /*
    import plotly.graph_objects as go

    fig = go.Figure(data=go.Scatter(
        x=[1, 2, 3, 4],
        y=[10, 11, 12, 13],
        mode='markers',
        marker=dict(size=[40, 60, 80, 100],
                    color=[0, 1, 2, 3])
    ))

    fig.show()
     */
    static void LineAndScatter7() {
        new Scatter(new double[]{1, 2, 3, 4}, new double[]{10, 11, 12, 13})
                .setMarkerMode("markers")
                .setSizes(40, 60, 80, 100)
                .setColors(0, 1, 2, 3)
                .show();

    }

    /*
    import plotly.graph_objects as go
    import numpy as np


    t = np.linspace(0, 10, 100)

    fig = go.Figure()

    fig.add_trace(go.Scatter(
        x=t, y=np.sin(t),
        name='sin',
        mode='markers',
        marker_color='rgba(152, 0, 0, .8)'
    ))

    fig.add_trace(go.Scatter(
        x=t, y=np.cos(t),
        name='cos',
        marker_color='rgba(255, 182, 193, .9)'
    ))

    # Set options common to all traces with fig.update_traces
    fig.update_traces(mode='markers', marker_line_width=2, marker_size=10)
    fig.update_layout(title='Styled Scatter',
                      yaxis_zeroline=False, xaxis_zeroline=False)


    fig.show()
     */
    static void LineAndScatter8() {
        double[] t = linearlySpaced(0, 10, 100);
        new Figure()
                .addTraces(
                        new Scatter(t, map(t, Math::sin))
                                .setColor("rgba(152, 0, 0, .8)")
                                .setName("sin"),
                        new Scatter(t, map(t, Math::cos))
                                .setColor("rgba(255, 182, 193, .9)")
                                .setName("cos")
                )
                .updateTraces(Scatter.class, traces -> {
                    traces.setEdgeWidth(2);
                    traces.setSize(10);
                })
                .updateLayout(plot -> {
                    plot.setTitle("Styled Scatter");
                    plot.getXAxis().showZeroLine(false);
                    plot.getYAxis().showZeroLine(false);
                })
                .show();
    }

    /*
    import plotly.graph_objects as go
    import numpy as np

    fig = go.Figure(data=go.Scatter(
        y = np.random.randn(500),
        mode='markers',
        marker=dict(
            size=16,
            color=np.random.randn(500), #set color equal to a variable
            colorscale='Viridis', # one of plotly colorscales
            showscale=True
        )
    ))

    fig.show()
     */
    static void LineAndScatter10() {
        new Scatter(range(500), full(ThreadLocalRandom.current()::nextGaussian, 500))
                .setColors(full(ThreadLocalRandom.current()::nextGaussian, 500))
                .setSize(16)
                .setColormap("Viridis")
                .showScale(true)
                .show();

    }

    /*
    import plotly.express as px

    df = px.data.gapminder().query("country=='Canada'")
    fig = px.line(df, x="year", y="lifeExp", title='Life expectancy in Canada')
    fig.show()
     */
    static void Line0() {
        new Line(gapminder.query("country=='Canada'"), "year", "lifeExp")
                .setTitle("Life expectancy in Canada")
                .show();

    }

    /*
    import plotly.express as px

    df = px.data.gapminder().query("continent != 'Asia'") # remove Asia for visibility
    fig = px.line(df, x="year", y="lifeExp", color="continent",
                  line_group="country", hover_name="country")
    fig.show()
     */
    static void Line1() {
        new Line(gapminder.query("continent != 'Asia'"), "year", "lifeExp")
                .setColors("continent")
                .setGroups("country")
                .show();

    }

    /*
    import plotly.graph_objects as go
    import numpy as np

    x = np.arange(10)

    fig = go.Figure(data=go.Scatter(x=x, y=x**2))
    fig.show()
     */
    static void Line2() {
        new Scatter(range(10), x -> x * x)
                .show();
    }

    /*
    import plotly.graph_objects as go

    # Add data
    month = ['January', 'February', 'March', 'April', 'May', 'June', 'July',
             'August', 'September', 'October', 'November', 'December']
    high_2000 = [32.5, 37.6, 49.9, 53.0, 69.1, 75.4, 76.5, 76.6, 70.7, 60.6, 45.1, 29.3]
    low_2000 = [13.8, 22.3, 32.5, 37.2, 49.9, 56.1, 57.7, 58.3, 51.2, 42.8, 31.6, 15.9]
    high_2007 = [36.5, 26.6, 43.6, 52.3, 71.5, 81.4, 80.5, 82.2, 76.0, 67.3, 46.1, 35.0]
    low_2007 = [23.6, 14.0, 27.0, 36.8, 47.6, 57.7, 58.9, 61.2, 53.3, 48.5, 31.0, 23.6]
    high_2014 = [28.8, 28.5, 37.0, 56.8, 69.7, 79.7, 78.5, 77.8, 74.1, 62.6, 45.3, 39.9]
    low_2014 = [12.7, 14.3, 18.6, 35.5, 49.9, 58.0, 60.0, 58.6, 51.7, 45.2, 32.2, 29.1]
    
    fig = go.Figure()
    # Create and style traces
    fig.add_trace(go.Scatter(x=month, y=high_2014, name='High 2014',
                             line=dict(color='firebrick', width=4)))
    fig.add_trace(go.Scatter(x=month, y=low_2014, name = 'Low 2014',
                             line=dict(color='royalblue', width=4)))
    fig.add_trace(go.Scatter(x=month, y=high_2007, name='High 2007',
                             line=dict(color='firebrick', width=4,
                                  dash='dash') # dash options include 'dash', 'dot', and 'dashdot'
    ))
    fig.add_trace(go.Scatter(x=month, y=low_2007, name='Low 2007',
                             line = dict(color='royalblue', width=4, dash='dash')))
    fig.add_trace(go.Scatter(x=month, y=high_2000, name='High 2000',
                             line = dict(color='firebrick', width=4, dash='dot')))
    fig.add_trace(go.Scatter(x=month, y=low_2000, name='Low 2000',
                             line=dict(color='royalblue', width=4, dash='dot')))
    
    # Edit the layout
    fig.update_layout(title='Average High and Low Temperatures in New York',
                       xaxis_title='Month',
                       yaxis_title='Temperature (degrees F)')
    
    
    fig.show()
     */
    static void Line4() {
        final String[] month = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        final double[] high_2000 = {32.5, 37.6, 49.9, 53.0, 69.1, 75.4, 76.5, 76.6, 70.7, 60.6, 45.1, 29.3};
        final double[] low_2000 = {13.8, 22.3, 32.5, 37.2, 49.9, 56.1, 57.7, 58.3, 51.2, 42.8, 31.6, 15.9};
        final double[] high_2007 = {36.5, 26.6, 43.6, 52.3, 71.5, 81.4, 80.5, 82.2, 76.0, 67.3, 46.1, 35.0};
        final double[] low_2007 = {23.6, 14.0, 27.0, 36.8, 47.6, 57.7, 58.9, 61.2, 53.3, 48.5, 31.0, 23.6};
        final double[] high_2014 = {28.8, 28.5, 37.0, 56.8, 69.7, 79.7, 78.5, 77.8, 74.1, 62.6, 45.3, 39.9};
        final double[] low_2014 = {12.7, 14.3, 18.6, 35.5, 49.9, 58.0, 60.0, 58.6, 51.7, 45.2, 32.2, 29.1};
        new Figure()
                .addTraces(
                        new Scatter(month, high_2014)
                                .setName("High 2014"),
                        new Scatter(month, low_2014)
                                .setName("Low 2014"),
                        new Scatter(month, high_2007)
                                .setName("High 2007")
                                .setLineStyle("dash"),
                        new Scatter(month, low_2007)
                                .setName("Low 2007")
                                .setLineStyle("dash"),
                        new Scatter(month, high_2000)
                                .setName("High 2000")
                                .setLineStyle("dot"),
                        new Scatter(month, low_2000)
                                .setName("Low 2000")
                                .setLineStyle("dot")
                )
                .updateTraces(Scatter.class, trace -> {
                    trace.setLineWidth(4);
                    trace.setColor(trace.getName().startsWith("Low") ? "royalblue" : "firebrick");
                })
                .updateLayout(plot -> {
                    plot.setTitle("Average High and Low Temperatures in New York");
                    plot.getXAxis().setTitle("Month");
                    plot.getYAxis().setTitle("Temperature (degrees F)");
                })
                .show();
    }

    /*
    import plotly.graph_objects as go
    import numpy as np



    x = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    x_rev = x[::-1]

    # Line 1
    y1 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    y1_upper = [2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
    y1_lower = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    y1_lower = y1_lower[::-1]

    # Line 2
    y2 = [5, 2.5, 5, 7.5, 5, 2.5, 7.5, 4.5, 5.5, 5]
    y2_upper = [5.5, 3, 5.5, 8, 6, 3, 8, 5, 6, 5.5]
    y2_lower = [4.5, 2, 4.4, 7, 4, 2, 7, 4, 5, 4.75]
    y2_lower = y2_lower[::-1]

    # Line 3
    y3 = [10, 8, 6, 4, 2, 0, 2, 4, 2, 0]
    y3_upper = [11, 9, 7, 5, 3, 1, 3, 5, 3, 1]
    y3_lower = [9, 7, 5, 3, 1, -.5, 1, 3, 1, -1]
    y3_lower = y3_lower[::-1]


    fig = go.Figure()

    fig.add_trace(go.Scatter(
        x=x+x_rev,
        y=y1_upper+y1_lower,
        fill='toself',
        fillcolor='rgba(0,100,80,0.2)',
        line_color='rgba(255,255,255,0)',
        showlegend=False,
        name='Fair',
    ))
    fig.add_trace(go.Scatter(
        x=x+x_rev,
        y=y2_upper+y2_lower,
        fill='toself',
        fillcolor='rgba(0,176,246,0.2)',
        line_color='rgba(255,255,255,0)',
        name='Premium',
        showlegend=False,
    ))
    fig.add_trace(go.Scatter(
        x=x+x_rev,
        y=y3_upper+y3_lower,
        fill='toself',
        fillcolor='rgba(231,107,243,0.2)',
        line_color='rgba(255,255,255,0)',
        showlegend=False,
        name='Ideal',
    ))
    fig.add_trace(go.Scatter(
        x=x, y=y1,
        line_color='rgb(0,100,80)',
        name='Fair',
    ))
    fig.add_trace(go.Scatter(
        x=x, y=y2,
        line_color='rgb(0,176,246)',
        name='Premium',
    ))
    fig.add_trace(go.Scatter(
        x=x, y=y3,
        line_color='rgb(231,107,243)',
        name='Ideal',
    ))

    fig.update_traces(mode='lines')
    fig.show()
     */
    static void Line8() {
        final double[] x = range(1, 11);
        final double[] x_rev = flip(x);

        // Line 1
        final double[] y1 = x.clone();
        final double[] y1_upper = range(2, 12);
        final double[] y1_lower = flipInPlace(range(10));

        // Line 2
        final double[] y2 = {5, 2.5, 5, 7.5, 5, 2.5, 7.5, 4.5, 5.5, 5};
        final double[] y2_upper = {5.5, 3, 5.5, 8, 6, 3, 8, 5, 6, 5.5};
        final double[] y2_lower = flip(new double[]{4.5, 2, 4.4, 7, 4, 2, 7, 4, 5, 4.75});

        // Line 3
        final double[] y3 = {10, 8, 6, 4, 2, 0, 2, 4, 2, 0};
        final double[] y3_upper = {11, 9, 7, 5, 3, 1, 3, 5, 3, 1};
        final double[] y3_lower = flip(new double[]{9, 7, 5, 3, 1, -.5, 1, 3, 1, -1});
        new Figure()
                .addTraces(
                        new Scatter(concatenate(x, x_rev), concatenate(y1_upper, y1_lower))
                                .showInLegend(false)
                                .setName("Fair")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(0,100,80,0.2)")
                                .setLineColor(Color.TRANSPARENT),
                        new Scatter(concatenate(x, x_rev), concatenate(y2_upper, y2_lower))
                                .showInLegend(false)
                                .setName("Premium")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(0,176,246,0.2)")
                                .setLineColor(Color.TRANSPARENT),
                        new Scatter(concatenate(x, x_rev), concatenate(y3_upper, y3_lower))
                                .showInLegend(false)
                                .setName("Ideal")
                                .setFill(FillMode.TO_SELF)
                                .setFillColor("rgba(231,107,243,0.2)")
                                .setLineColor(Color.TRANSPARENT),
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
                .updateTraces(Scatter.class, trace -> trace.setMarkerMode(ScatterMode.LINE_ONLY))
                .show();
    }

    /*
    sns.relplot(data=tips, x="total_bill", y="tip", hue="day")
     */
    static void RelPlot0() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .show();
    }

    /*
    sns.relplot(data=tips, x="total_bill", y="tip", hue="day")
     */
    static void RelPlot1() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setColumns("time")
                .show();
    }

    /*
    sns.relplot(data=tips, x="total_bill", y="tip", hue="day", col="time", row="sex")
    */
    static void RelPlot2() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setColumns("time")
                .setRows("sex")
                .show();
    }

    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="time")
     */
    static void ScatterPlot1() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("time")
                .show();
    }

    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="time")
     */
    static void ScatterPlot2() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("time")
                .setShapes("time")
                .show();
    }

    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="day", style="time")
     */
    static void ScatterPlot3() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setShapes("time")
                .show();
    }

    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size")
    */
    static void ScatterPlot4() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .show();
    }
    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size", palette="deep")
    */
    static void ScatterPlot5() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .setColormap("deep")
                .show();
    }
    /*
    sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size", size="size")
     */
    static void ScatterPlot6() {
        new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .setSizes("size")
                .show();
    }
    public static void main(String[] args) {
        ScatterPlot6();

    }
}
