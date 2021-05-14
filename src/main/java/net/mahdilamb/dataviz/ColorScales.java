package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Gradient;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Orientation;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.Numbers;
import net.mahdilamb.dataviz.utils.StringUtils;

import java.awt.*;
import java.util.Map;

import static net.mahdilamb.dataviz.PlotAxis.niceNum;

public class ColorScales extends KeyArea<ColorScales> {

    private static final class ModifiableGradient extends Gradient {

        ModifiableGradient(Colormap colorMap, double startX, double startY, double endX, double endY) {
            super(GradientType.LINEAR, colorMap, startX, startY, endX, endY);
        }

        final void update(ColorScales scales, ColorBar colorBar) {
            endX = scales.padding + colorBar.getX();
            endY = colorBar.getY();
            startX = endX + colorBar.getWidth();
            startY = colorBar.getHeight() + endY;

        }
    }

    static final class ColorBar extends Component {
        static final int MAX_TICKS = 8;
        private final PlotDataAttribute.Numeric dataAttribute;
        private final ColorScales scales;
        ModifiableGradient gradient;
        double[] values;
        String[] labels;
        double labelWidth;

        ColorBar(ColorScales colorScales, PlotDataAttribute.Numeric trace) {
            this.dataAttribute = trace;
            this.scales = colorScales;
        }

        @Override
        protected final void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            double width = scales.barWidth;
            if (scales.showTicks) {
                width += scales.tickLength;
            }
            if (scales.showLabels) {
                if (labels == null) {
                    labelWidth=0;
                    double upper = dataAttribute.valMax;
                    double lower = dataAttribute.valMin;
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
                        labelWidth = Math.max(getTextWidth(renderer, scales.itemFont, labels[i]), labelWidth);
                    }
                }
                width += labelWidth;

            }
            setBoundsFromRect(maxX - width-scales.padding, minY, width, scales.barHeight);
            getGradient().update(scales,this);
        }


        @Override
        protected final void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            if (scales.showLabels) {
                double upper = dataAttribute.valMax;
                double lower = dataAttribute.valMin;
                final double range = upper - lower;
                if (scales.orientation == Orientation.VERTICAL) {
                    double baselineOffset = getTextBaselineOffset(renderer,scales.itemFont);
                    baselineOffset -= getTextLineHeight(renderer,scales.itemFont, StringUtils.EMPTY_STRING) * .5;
                    canvas.setStroke(scales.tickStyle);
                    canvas.setStroke( Color.black);
                    canvas.setFill(Color.BLACK);
                    canvas.setFont(scales.itemFont);
                    double barEnd = scales.padding + getX() + scales.barWidth;
                    for (int i = 0; i < values.length; ++i) {
                        final double d = values[i];
                        if (d < dataAttribute.valMin || d > dataAttribute.valMax || labels[i] == null) {
                            continue;
                        }
                        double y = getY() + (((upper - d) / range) * getHeight());
                        if (scales.showTicks) {
                            canvas.strokeLine(barEnd, y, barEnd + scales.tickLength, y);
                        }
                        if (scales.showLabels) {
                            canvas.fillText(labels[i], barEnd + (scales.showTicks ? (scales.tickLength) : 0) + 2, y + baselineOffset);

                        }
                    }
                    canvas.setFill(getGradient());
                    canvas.fillRect(scales.padding + getX(), getY(), scales.barWidth, scales.barHeight);

                } else {
                    throw new UnsupportedOperationException();//TODO
                }
            }
        }


        private ModifiableGradient getGradient() {
            if (gradient == null) {
                gradient = new ModifiableGradient(dataAttribute.data.getSequentialColormap(), getWidth() + getX(), getHeight() + getY(), getX(), getY());
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

    double barWidth, barHeight;

    ColorScales(Figure figure) {
        super(figure, false, 40);
        orientation = Orientation.VERTICAL;
    }


    @Override
    protected void layoutVertical(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        barWidth = thickness;
        barHeight = size * (maxY - minY);
        final double startX = maxX;
        for (final PlotData<?, ?> trace : figure.layout.data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> attribute : trace.attributes.entrySet()) {
                if (!attribute.getValue().showColorBar) {
                    continue;
                }
                attribute.getValue().getColorBar(this).layoutComponent(renderer, minX, minY, maxX, maxY);
                maxX -= attribute.getValue().getColorBar(this).getWidth()+padding;
            }
        }
        setBoundsFromExtent(maxX, minY, startX, maxY);

    }

    @Override
    protected void layoutHorizontal(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        //TODO
        throw new UnsupportedOperationException();
    }


    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {

        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> styler : data.attributes.entrySet()) {
                if (!styler.getValue().showColorBar) {
                    continue;
                }
                styler.getValue().getColorBar(this).drawComponent(renderer, canvas);

            }
        }
    }

    @Override
    protected void markLayoutAsOldQuietly() {
        final PlotLayout<?> layout;
        if ((layout = figure.getLayout()) == null) {
            return;
        }
        for (final PlotData<?, ?> data : layout.data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> attributeEntry : data.attributes.entrySet()) {
                if (!attributeEntry.getValue().showColorBar) {
                    continue;
                }
                markLayoutAsOld(attributeEntry.getValue().getColorBar(this));
            }
        }
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        for (final PlotData<?, ?> data : figure.getLayout().data) {
            for (final Map.Entry<PlotDataAttribute.Type, PlotDataAttribute> styler : data.attributes.entrySet()) {
                if (!styler.getValue().showColorBar) {
                    continue;
                }
                final ColorBar group;
                if ((group = styler.getValue().getColorBar(this)).containsPoint(x, y)) {
                    return group;
                }
            }
        }
        return super.getComponentAt(x, y);
    }

    @Override
    public ColorScales apply(Theme theme) {
        theme.colorScales.accept(this);
        return this;
    }

}
