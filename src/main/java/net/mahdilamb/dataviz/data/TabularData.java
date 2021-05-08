package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataframe.Axis;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotAxis;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.layouts.XYLayout;

/**
 * Data in a table format with rows and columns
 */
public abstract class TabularData<PD extends PlotData<PD, XYLayout>> extends PlotData<PD, XYLayout> {

    /**
     * Create a tabular data series
     *
     * @param dataFrame the dataframe that is the source of the series
     */
    protected TabularData(final DataFrame dataFrame) {
        super(dataFrame);
    }

    /**
     * @return the number of columns in the table
     */
    public final int numColumns() {
        return dataFrame.size(Axis.COLUMN);
    }

    /**
     * @return the number of rows in the table
     */
    public final int numRows() {
        return dataFrame.size(Axis.INDEX);
    }

    @Override
    protected final XYLayout createLayout() {
        return new XYLayout(new PlotAxis.ColumnHeadings(), new PlotAxis.RowNames());
    }


}
