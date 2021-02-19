package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.HAlign;
import net.mahdilamb.charts.graphics.VAlign;

final class ColorScaleImpl<S extends PlotSeries<S>> extends KeyAreaImpl<ColorScale> implements ColorScale {

    //TODO merge with ColorScale Attributes

    boolean showLabels = true, showTicks = true;

    HAlign hLabelAlign = HAlign.RIGHT;
    VAlign vLabelAlign = VAlign.TOP;

    HAlign hTickPosition = hLabelAlign;
    VAlign vTickPosition = vLabelAlign;

    double size = 16, length = 240;

    public ColorScaleImpl() {
        super();
    }

    public ColorScaleImpl(Figure.PlotImpl<S> layout) {
        super();
    }

    @Override
    public ColorScale showLabels(boolean values) {
        this.showLabels = values;
        return redraw();
    }

    @Override
    public ColorScale setLabelAlignment(HAlign alignment) {
        this.hLabelAlign = alignment;
        return redraw();
    }

    @Override
    public ColorScale setLabelAlignment(VAlign alignment) {
        this.vLabelAlign = alignment;
        return redraw();
    }

    @Override
    public ColorScale showTickMarks(boolean showTickMarks) {
        this.showTicks = showTickMarks;
        return redraw();
    }

    @Override
    public ColorScale setTickPosition(HAlign alignment) {
        this.hTickPosition = alignment;
        return redraw();
    }

    @Override
    public ColorScale setTickPosition(VAlign alignment) {
        this.vTickPosition = alignment;
        return redraw();
    }

    @Override
    public ColorScale setThickness(double size) {
        this.size = size;
        return redraw();
    }

    @Override
    public ColorScale setLength(double size) {
        this.length = size;
        return redraw();
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
        posX = minX;
        posY = minY;
    }

    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        //TODO
    }
}
