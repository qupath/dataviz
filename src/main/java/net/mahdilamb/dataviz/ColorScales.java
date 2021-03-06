package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataviz.graphics.ChartCanvas;
import net.mahdilamb.dataviz.graphics.Gradient;
import net.mahdilamb.dataviz.graphics.Orientation;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.Numbers;

import static net.mahdilamb.dataviz.Axis.niceNum;

/**
 * The color scales area of a figure
 */
public final class ColorScales extends KeyArea<ColorScales> {
    private static final class ModifiableGradient extends Gradient {

        ModifiableGradient(Colormap colorMap, double startX, double startY, double endX, double endY) {
            super(GradientType.LINEAR, colorMap, startX, startY, endX, endY);
        }

        final void update(ColorScales scales, ColorBar colorBar) {
            endX = scales.padding + colorBar.posX;
            endY = colorBar.posY;
            startX = endX + colorBar.sizeX;
            startY = colorBar.sizeY + endY;

        }
    }

    static final class ColorBar extends Component {
        static final int MAX_TICKS = 8;
        private final PlotTrace.Numeric trace;
        ModifiableGradient gradient;
        double[] values;
        String[] labels;
        double labelWidth, barWidth, barHeight;

        ColorBar(PlotTrace.Numeric trace) {
            this.trace = trace;
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            //ignored
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            //ignored
        }

        void clear() {
            labelWidth = 0;
            labels = null;
            values = null;
        }

        void computeSize(Renderer<?> source, ColorScales scales) {

            if (scales.orientation == Orientation.VERTICAL) {
                barWidth = sizeX = scales.thickness;
                barHeight = sizeY = scales.size * scales.sizeY;
                if (scales.showTicks) {
                    sizeX += scales.tickLength;
                }
            } else {
                barHeight = sizeY = scales.thickness;
                barWidth = sizeX = scales.size;
            }
            if (scales.showLabels) {
                if (labels == null) {
                    double upper = trace.valMax;
                    double lower = trace.valMin;
                    final double range = niceNum(upper - lower, false);
                    double spacing = niceNum(range / MAX_TICKS, true);
                    double first = Math.floor(lower / spacing) * spacing;
                    double last = Math.ceil(upper / spacing) * spacing;
                    labels = new String[MAX_TICKS + 1];
                    values = new double[MAX_TICKS + 1];
                    int i = 0;
                    for (double d = first; d <= last && i < values.length; d += spacing, ++i) {
                        values[i] = d;
                        labels[i] = String.valueOf(Numbers.approximateDouble(values[i]));
                        labelWidth = Math.max(source.getTextWidth(scales.itemFont, labels[i]), labelWidth);
                    }
                }
                sizeX += labelWidth;

            }
        }

        void drawColorBar(Renderer<?> source, ChartCanvas<?> canvas, ColorScales scales) {
            if (scales.showLabels) {
                double upper = trace.valMax;
                double lower = trace.valMin;
                final double range = upper - lower;
                if (scales.orientation == Orientation.VERTICAL) {
                    double baselineOffset = source.getTextBaselineOffset(scales.itemFont);
                    baselineOffset -= source.getTextLineHeight(scales.itemFont) * .5;
                    canvas.setStroke(scales.tickStyle, Color.black);
                    canvas.setFill(Color.BLACK);
                    canvas.setFont(scales.itemFont);
                    double barEnd = scales.padding + posX + barWidth;
                    for (int i = 0; i < values.length; ++i) {
                        final double d = values[i];
                        if (d < trace.valMin || d > trace.valMax || labels[i] == null) {
                            continue;
                        }
                        double y = posY + (((upper - d) / range) * sizeY);
                        if (scales.showTicks) {
                            canvas.strokeLine(barEnd, y, barEnd + scales.tickLength, y);
                        }
                        if (scales.showLabels) {
                            canvas.fillText(labels[i], barEnd + (scales.showTicks ? (scales.tickLength) : 0) + 2, y + baselineOffset);

                        }
                    }
                    canvas.setFill(getGradient());
                    canvas.fillRect(scales.padding + posX, posY, barWidth, barHeight);
                } else {
                    throw new UnsupportedOperationException();//TODO
                }
            }

        }

        private ModifiableGradient getGradient() {
            if (gradient == null) {
                gradient = new ModifiableGradient(trace.data.getColormap(), sizeX + posX, sizeY + posY, posX, posY);
            }
            return gradient;
        }

    }

    double size = .9,
            thickness = 30,
            padding = 10,
            tickLength = 5;
    Stroke tickStyle = Stroke.SOLID;
    boolean showLabels = true,
            showTicks = false;

    ColorScales(Figure figure) {
        super(figure);
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (orientation == Orientation.VERTICAL) {
            sizeX = 0;
            posY = minY;
            sizeY = maxY - minY;
            for (final PlotData<?> trace : figure.layout.data) {
                if (!trace.showsColorBar()) {
                    continue;
                }
                trace.layoutColorBar(source, this);
                sizeX += trace.colors.asB().getColorBar().sizeX;
                trace.colors.asB().getColorBar().posY = posY + (.5 * (1 - size)) * sizeY;
            }
            posX = maxX - sizeX - padding;
            if (sizeX == 0) {
                sizeY = 0;
            } else {
                double x = posX;
                sizeX += padding;
                for (final PlotData<?> trace : figure.layout.data) {
                    if (!trace.showsColorBar()) {
                        continue;
                    }

                    trace.colors.asB().getColorBar().posX = x;
                    trace.colors.asB().getColorBar().getGradient().update(this, trace.colors.asB().getColorBar());
                    x -= trace.colors.asB().getColorBar().sizeX;

                }
            }
        } else {
            throw new UnsupportedOperationException();//TODO
        }
    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        for (final PlotData<?> trace : figure.layout.data) {
            if (!trace.showsColorBar()) {
                continue;
            }
            trace.drawColorBar(source, canvas, this);
        }

    }

    @Override
    public ColorScales apply(Theme theme) {
        theme.colorScales.accept(this);
        return this;
    }


}
