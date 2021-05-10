package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.InputMode;
import net.mahdilamb.dataviz.PlotArea;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.utils.functions.BiDoubleConsumer;

public class TableLayout extends PlotLayout<TableLayout> {
    @Override
    public PlotArea<TableLayout> getPlotArea() {
        //TODO
        return null;
    }

    @Override
    public final XYAxis.XAxis getXAxis() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final XYAxis.YAxis getYAxis() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onAdd(PlotData<?, TableLayout> data) {
        //TODO
    }

    @Override
    protected void panPlotArea(double dx, double dy) {
        //TODO
    }

    @Override
    protected final void zoomPlotArea(double x, double y, double zoom) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void increaseZoom() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void decreaseZoom() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValueToPosition(double x, double y, BiDoubleConsumer xy) throws UnsupportedOperationException {
        //TODO
    }

    @Override
    public void transformPositionToValue(double x, double y, BiDoubleConsumer xy) throws UnsupportedOperationException {
        //TODO
    }

    @Override
    protected void inputModeChanged(InputMode.State state) {
        //TODO
    }
}
