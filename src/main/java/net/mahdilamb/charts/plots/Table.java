package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

public final class Table extends PlotSeries<Table> {
    static final double DEFAULT_CELL_WIDTH = 75;
    static final double DEFAULT_CELL_HEIGHT = 32;
    final DataFrame frame;
    double[] cellHeights, cellWidths;
    boolean[] visibleRows, visibleColumns;

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


}
