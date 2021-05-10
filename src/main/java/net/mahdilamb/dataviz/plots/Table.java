package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.data.TabularData;
import net.mahdilamb.dataviz.layouts.TableLayout;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.utils.ExtendedColormap;

import java.awt.*;

@PlotOptions(name = "Table", supportsManualZoom = false, supportsZoom = false, supportsPan = true, supportsPolygonSelection = false, supportsZoomByWheel = false)
public class Table extends TabularData<Table> {
    static final class CellID {
        final int col, row;

        CellID(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    PlotBounds<PlotBounds.XY, XYLayout> bounds;
    int numRows = -1, numColumns = -1;
    int[] rows, columns;
    int sortBy = -1;
    boolean sortAscending = true;

    ExtendedColormap[] colormaps;

    boolean alternateRowColor = true;
    boolean[] columnsFiltered;
    double cellWidth = 120, cellHeight = 32;

    CellID[] idMap;

    public Table(final DataFrame dataFrame) {
        super(dataFrame);
        getFigure().setTitle(dataFrame.getName());
        getFigure().setTitleVisibility(false);
        init();
    }

    @Override
    protected Color getColor(int i) {
        //TODO
        return null;
    }

    @Override
    protected PlotBounds<? extends PlotBounds.Bounds<TableLayout>, TableLayout> getBoundPreferences() {
        //TODO
        return null;
    }

    @Override
    protected final Legend.Glyph getGlyph(PlotDataAttribute.Categorical attribute, int category) {
        return null;
    }

    @Override
    protected final Legend.Glyph getGlyph(PlotDataAttribute.Numeric attribute, double value) {
        return null;
    }


    @Override
    public int size() {
        return numColumns() * numRows();
    }

    void init() {
        @SuppressWarnings("unchecked") final PlotShape<TableLayout>[] shapes = new PlotShape[numRows() * numColumns()];
        int i = 0;
        for (int r = 0; r < numRows(); ++r) {
            for (int c = 0; c < numColumns(); ++c) {
                //todo  shapes[i] = createRectangle(this, i++, c * cellWidth, r * cellHeight, cellWidth, cellHeight);
            }
        }
        addShapes(shapes, false);
    }

}
