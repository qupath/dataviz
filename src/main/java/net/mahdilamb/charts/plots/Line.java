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

    protected static class LineElement extends RectangularNode<Runnable> {
        public Stroke stroke;
        double startX, startY, endX, endY;
        Line series;
        int i;
        Color color = null;


        public LineElement(double startX, double startY, double endX, double endY) {
            super(Math.min(startX, endX), Math.min(startY, endY), Math.max(startX, endX), Math.max(startY, endY));
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public Color getColor() {
            if (color == null) {
                color = series.color == null ? DEFAULT_QUALITATIVE_COLORMAP.get(0) : series.color;
                final TraceGroup<?> colors = series.getAttribute(AttributeType.COLOR);
                if (colors != null) {
                    if (colors.source.getClass() == QualitativeColorAttribute.class) {
                        final Colormap colormap = ((QualitativeColorAttribute) colors.source).colormap == null ? DEFAULT_QUALITATIVE_COLORMAP : ((QualitativeColorAttribute) colors.source).colormap;
                        color = ((QualitativeColorAttribute) colors.source).get(colormap, i);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                final TraceGroup<?> opacities = series.getAttribute(AttributeType.OPACITY);
                //TODO apply opacity
                if (series.opacity != 1) {
                    color = new Color(color.red(), color.green(), color.blue(), series.opacity);
                }
            }
            return color;
        }


    }

    boolean fillGaps = false;

    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] errorLowers;
    double[] errorUppers;
    double[] bandLowers;
    double[] bandUppers;

    Stroke bandStroke;
    Color bandFill;
    List<LineElement> lines;

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

    private List<LineElement> getLines() {
        if (lines == null) {
            final TraceGroup<?> colors = getAttribute(AttributeType.COLOR);
            if (colors != null) {
                if (colors.source.getClass() == QualitativeColorAttribute.class) {
                    lines = new ArrayList<>(x.size());

                    for (final GroupBy.Group<String> group : ((QualitativeColorAttribute) colors.source).groups) {
                        int last = -1;
                        final Colormap colormap = ((QualitativeColorAttribute) colors.source).colormap == null ? DEFAULT_QUALITATIVE_COLORMAP : ((QualitativeColorAttribute) colors.source).colormap;
                        color = ((QualitativeColorAttribute) colors.source).getI(colormap, group.getID());
                        Stroke currentStroke = new Stroke(color, lineStyle.getWidth());
                        for (int i : group) {
                            if (last != -1) {
                                final LineElement line = new LineElement(x.get(last), y.get(last), x.get(i), y.get(i));
                                line.i = last;
                                line.series = this;

                                line.stroke = currentStroke;
                                lines.add(line);
                            }
                            last = i;
                        }
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                int j = x.size() - 1;
                lines = new ArrayList<>(j);
                for (int i = 1, k = 0; i < j; k = i++) {
                    final LineElement line = new LineElement(x.get(k), y.get(k), x.get(i), y.get(i));
                    line.i = k;
                    line.series = this;
                    lines.add(line);
                }
            }

        }
        return lines;
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
