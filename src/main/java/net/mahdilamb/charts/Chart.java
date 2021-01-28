package net.mahdilamb.charts;

import net.mahdilamb.charts.datasets.DataSeries;
import net.mahdilamb.charts.datasets.DataType;
import net.mahdilamb.charts.datasets.Dataset;
import net.mahdilamb.charts.datasets.NumericSeries;
import net.mahdilamb.charts.plots.Plot;
import net.mahdilamb.charts.plots.XYPlot;
import net.mahdilamb.charts.series.PlotSeries;
import net.mahdilamb.charts.series.Scatter;
import net.mahdilamb.charts.styles.Text;
import net.mahdilamb.charts.styles.Title;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.util.Iterator;

public abstract class Chart<P extends Plot<S>, S extends PlotSeries<S>> {
    /**
     * Create a scatter series from an array of x and y data
     *
     * @param x the x data
     * @param y the y data
     * @return the scatter series
     */
    public static Scatter scatter(double[] x, double[] y) {
        return new PlotSeriesImpl.AbstractScatter.FromArray(x, y);
    }

    /**
     * Create a scatter series from a dataset
     *
     * @param dataset the dataset
     * @param x       the name of the series containing the x data
     * @param y       the name of the series containing the y data
     * @return the scatter series
     * @throws UnsupportedOperationException of either series is not numeric
     * @throws NullPointerException          if the series cannot be found
     */
    public static Scatter scatter(Dataset dataset, String x, String y) {
        final DataSeries<?> xSeries = dataset.get(x);
        final DataSeries<?> ySeries = dataset.get(y);
        if (xSeries.getType() instanceof DataType.Numeric && ySeries.getType() instanceof DataType.Numeric) {
            return new PlotSeriesImpl.AbstractScatter.FromIterable((NumericSeries<?>) xSeries, (NumericSeries<?>) ySeries);
        }
        throw new UnsupportedOperationException("Both series must be numeric");
    }

    protected static abstract class TextImpl {
        double width, height;

        boolean isSet = false;

        protected final void updateSize(double width, double height) {
            this.width = width;
            this.height = height;
            isSet = true;
        }

        protected abstract void calculateSize();

    }

    protected static abstract class ImageImpl {
        byte[] bytes;

        abstract byte[] calculateBytes();
    }

    Title title;
    P plot;
    Legend legend;
    final Colormap colormap = new Plotly();
    Iterator<Float> colormapIt;


    double width, height;
    double titleWidth, titleHeight;

    protected Chart(String title, double width, double height, P plot) {
        this.title = new Title();
        this.title.setTitle(title);//TODO
        this.plot = plot;
        this.width = width;
        this.height = height;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        layout();
    }

    public Legend getLegend() {
        return legend;
    }

    public P getPlot() {
        return plot;
    }

    protected void layout() {
        titleWidth = 0;
        titleHeight = 0;
        legend.yOffset = 0;
        double legendY = 0, plotX = 0, plotY = 0, legendWidth = 0, legendHeight = 0;
        if (title != null && title.isVisible()) {
            titleWidth = getTextWidth(title);
            titleHeight = getTextHeight(title);
            legendY += titleHeight;
            plotY = legendY;
        }
        if (legend.isVisible() && !legend.isFloating()) {
            legend.layout(legendY);
            switch (legend.getSide()) {
                case TOP:
                    plotY += legend.height;
                    break;
                case LEFT:
                    plotX += legend.width;
                    break;
                case RIGHT:
                    legendWidth = legend.width;
                    break;
                case BOTTOM:
                    legendHeight = legend.height;
                    break;
            }
        }
        ((PlotImpl<?>) plot).layout(plotX, plotY, width - legendWidth - plotX, height - legendHeight - plotY);
        draw();
    }


    protected abstract double getTextWidth(Text text);

    protected abstract double getTextHeight(Text text);

    protected abstract void draw();

    static Color getNextColor(Chart<?, ?> chart) {
        if (chart.colormapIt == null || !chart.colormapIt.hasNext()) {
            chart.colormapIt = chart.colormap.iterator();
        }
        return (Color) chart.colormap.get(chart.colormapIt.next());
    }

}
