package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.graphics.ChartCanvas;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

public final class Table extends PlotSeries<Table>implements RectangularPlot {
    static final double DEFAULT_CELL_WIDTH = 75;
    static final double DEFAULT_CELL_HEIGHT = 32;
    final DataFrame frame;
    double[] cellHeights, cellWidths;
    boolean[] visibleRows, visibleColumns;
    int[] rowSort;
    int[] colSort;

    public Table(final DataFrame frame) {
        this.frame = frame;
    }

    public Table setWidths(Iterable<Double> widths) {
        this.cellWidths = fill(new double[frame.size(Axis.INDEX)], widths, DEFAULT_CELL_WIDTH);
        return requestDataUpdate();
    }

    public Table setHeights(Iterable<Double> heights) {
        this.cellHeights = fill(new double[frame.numSeries()], heights, DEFAULT_CELL_HEIGHT);
        return requestDataUpdate();
    }

    @Override
    public String getXLabel() {
        return null;
    }

    @Override
    public String getYLabel() {
        return null;
    }

    @Override
    public double getMinX() {
        return 0;
    }

    @Override
    public double getMaxX() {
        return frame.size(Axis.INDEX);
    }

    @Override
    public double getMinY() {
        return 0;
    }

    @Override
    public double getMaxY() {
        return frame.numSeries();
    }

    @Override
    public int size() {
        return data.size() * data.numSeries();
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Table> plot) {

    }
}
