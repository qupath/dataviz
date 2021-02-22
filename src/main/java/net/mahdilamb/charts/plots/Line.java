package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.geom2d.trees.RectangularNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.charts.Figure.DEFAULT_QUALITATIVE_COLORMAP;
import static net.mahdilamb.charts.utils.ArrayUtils.toArray;

public class Line extends AbstractScatter<Line> implements RectangularPlot {

    boolean fillGaps = false;

    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] errorLowers;
    double[] errorUppers;
    double[] bandLowers;
    double[] bandUppers;

    Stroke bandStroke;
    Color bandFill;

    public Line(double[] x, double[] y) {
        super(x, y);
    }

    public Line(double[] x, DoubleUnaryOperator toYFunction) {
        this(x, map(x, toYFunction));
    }

    public Line(DataFrame data, String x, String y) {
        this(toArray(data.getDoubleSeries(x), data.size(Axis.INDEX)), toArray(data.getDoubleSeries(y), data.size(Axis.INDEX)));
        this.xLabel = x;
        this.yLabel = y;
        this.data = data;
    }

    @Override
    public Line setLineStyle(Stroke style) {
        return super.setLineStyle(style);
    }

    public Line setColors(final String seriesName) throws DataFrameOnlyOperationException {
        return ifSeriesCategorical(
                seriesName,
                (s, series) -> {
                    final QualitativeColorAttribute qca = new QualitativeColorAttribute(new GroupBy<>(series, series.size()));
                    addAttribute(
                            AttributeType.COLOR,
                            TraceGroup.createForQualitativeColor(series.getName(), qca),
                            (canvas, trace, x, y) -> {
                                if (trace.glyphData == null) {
                                    trace.glyphSize *= 2;
                                    trace.glyphData = new Stroke(qca.getI(qca.colormap == null ? DEFAULT_QUALITATIVE_COLORMAP : qca.colormap, trace.get()), 2);
                                }
                                canvas.setStroke((Stroke) trace.glyphData);
                                canvas.strokeLine(x - trace.glyphSize * .5, y, x + trace.glyphSize * .5, y);

                            }
                    );
                },
                (s, series) -> {
                    //TODO
                    SequentialColorAttribute sequentialColormap = new SequentialColorAttribute(series.toArray(new double[series.size()]));
                    addAttribute(AttributeType.COLOR, TraceGroup.createForSequentialColormap(series.getName(), sequentialColormap));
                },
                this::requestDataUpdate
        );
    }

    public Line setBandUpper(Iterable<Double> error) {
        bandUppers = ArrayUtils.fill(new double[size()], error, Double.NaN);
        return redraw();
    }

    public Line setBandLower(Iterable<Double> error) {
        bandLowers = ArrayUtils.fill(new double[size()], error, Double.NaN);
        return redraw();
    }

    public Line setErrorUpper(Iterable<Double> error) {
        errorUppers = ArrayUtils.fill(new double[size()], error, Double.NaN);
        return redraw();
    }

    public Line setErrorLower(Iterable<Double> error) {
        errorLowers = ArrayUtils.fill(new double[size()], error, Double.NaN);
        return redraw();
    }

    public Line setBandFill(final Color color) {
        this.bandFill = color;
        return redraw();
    }

    public Line setBandStroke(final Stroke stroke) {
        this.bandStroke = stroke;
        return redraw();
    }

    public Line fillGaps(boolean fillGaps) {
        this.fillGaps = fillGaps;
        return requestDataUpdate();
    }



    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Line> plot) {
        if (lineStyle == null) {
            lineStyle = new Stroke(DEFAULT_QUALITATIVE_COLORMAP.get(0), 2);
        }

        for (final LineElement l : getLines()) {
            canvas.setStroke(l.stroke);

            canvas.strokeLine(convertXToPosition(plot, l.startX), convertYToPosition(plot, l.startY), convertXToPosition(plot, l.endX), convertYToPosition(plot, l.endY));
        }
    }

    @Override
    protected Line requestDataUpdate() {
        lines = null;
        return super.requestDataUpdate();
    }

    @Override
    public int size() {
        return x.size();
    }
}
