package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.shapes.Marker;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.plots.ScatterMode;

import java.util.Map;

/**
 * The legend area of a figure
 */
public final class Legend extends KeyArea<Legend> {


    static abstract class Glyph {
        Color color = Color.DARK_GRAY;
        double sizeX = Scatter.DEFAULT_MARKER_SIZE;
        double sizeY = sizeX;

        abstract void draw(final Renderer<?> source, final ChartCanvas<?> canvas, double x, double y);
    }

    static final class XYDataGlyph extends Glyph {
        MarkerShape shape = MarkerShape.CIRCLE;
        static final Stroke DEFAULT_STROKE = new Stroke(2);
        Stroke stroke = DEFAULT_STROKE;
        ScatterMode mode;
        Stroke edgeStroke;
        Color edgeColor;

        XYDataGlyph(ScatterMode mode) {
            this.mode = mode;
            if (mode != ScatterMode.MARKER_ONLY) {
                sizeX = 30;
            }
        }

        @Override
        void draw(Renderer<?> source, ChartCanvas<?> canvas, double x, double y) {
            double markerSize = sizeX;
            double markerY = 0;
            if (mode != ScatterMode.MARKER_ONLY) {
                canvas.setStroke(stroke, color);
                markerY = stroke.getWidth() * .5;
                double yPos = y + 0.5 * sizeY + markerY;
                canvas.strokeLine(x, yPos, x + sizeX, yPos);
                markerSize = sizeY;
            }
            if (mode != ScatterMode.LINE_ONLY) {
                Marker.MARKER.shape = shape;
                Marker.MARKER.size = markerSize;
                Marker.MARKER.x = x + 0.5 * sizeX;
                Marker.MARKER.y = y + 0.5 * markerSize + markerY;
                canvas.setFill(color);
                Marker.MARKER.fill(canvas);
                if (edgeStroke != null) {
                    if (edgeColor != null) {
                        canvas.setStroke(edgeColor);
                    }
                    Marker.MARKER.stroke(canvas);
                }
            }

        }
    }

    static final class LegendItem extends Component {
        String label;
        Glyph glyph;

        LegendItem(final String label, final Glyph glyph) {
            this.label = label;
            this.glyph = glyph;
        }

        @Override
        protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
            //ignored
        }

        @Override
        protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
            //ignored
        }

        @Override
        public String toString() {
            return String.format("LegendItem {%s %s}", glyph, label);
        }
    }

    HAlign align = HAlign.LEFT;
    double itemSpacing = 2;
    PlotTrace.UncategorizedTrace uncategorized;

    Legend(Figure figure) {
        super(figure);
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (orientation == Orientation.VERTICAL) {
            sizeX = 0;
            sizeY = 0;
            int numUncategorized = 0;
            for (final PlotData<?> data : figure.layout.data) {
                if (data.numAttributes() == 0) {
                    if (data.showInLegend && data.name != null && data.name.length() > 0) {
                        ++numUncategorized;
                    }
                } else {
                    for (final Map.Entry<PlotData.Attribute, PlotTrace> trace : data.attributes()) {
                        trace.getValue().calculateSize(source, this);
                        sizeX = Math.max(trace.getValue().sizeX, sizeX);
                        sizeY += trace.getValue().sizeY;
                    }
                }
            }
            if (numUncategorized > 0) {
                if (uncategorized == null) {
                    final PlotData<?>[] uncategorized = new PlotData[numUncategorized];
                    numUncategorized = 0;
                    for (final PlotData<?> data : figure.layout.data) {
                        if (data.numAttributes() == 0) {
                            if (data.showInLegend && data.name != null && data.name.length() > 0) {
                                uncategorized[numUncategorized++] = data;
                            }
                        }
                    }
                    this.uncategorized = new PlotTrace.UncategorizedTrace(figure, uncategorized);
                }
                uncategorized.calculateSize(source, this);
                sizeX = Math.max(uncategorized.sizeX, sizeX);
                sizeY += uncategorized.sizeY;
            }

        } else {
            //todo
            throw new UnsupportedOperationException();
        }

        switch (side) {
            case RIGHT:
                posX = maxX - sizeX;
                break;
            case LEFT:
                posX = minX;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (side.isVertical()) {
            posY = minY;
            if (vAlign != VAlign.TOP) {
                double height = maxY - minY;
                if (vAlign == VAlign.MIDDLE) {
                    posY += .5 * height - .5 * sizeY;
                } else {
                    posY += height - sizeY;
                }
            }
            double y = posY;
            for (final PlotData<?> data : figure.layout.data) {
                for (final Map.Entry<PlotData.Attribute, PlotTrace> trace : data.attributes()) {
                    trace.getValue().posX = posX;
                    trace.getValue().posY = y;
                    trace.getValue().updateItems(this);
                    y += trace.getValue().sizeY;
                }
            }
            if (uncategorized != null) {
                uncategorized.posX = posX;
                uncategorized.posY = y;
                uncategorized.updateItems(this);
            }
        }
    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        if (background != null) {
            canvas.setFill(background);
            canvas.fillRect(posX, posY, sizeX, sizeY);
        }
        for (final PlotData<?> data : figure.layout.data) {
            for (final Map.Entry<PlotData.Attribute, PlotTrace> trace : data.attributes()) {
                trace.getValue().drawComponent(source, canvas, this);
            }
        }
        if (uncategorized != null) {
            uncategorized.drawComponent(source, canvas, this);
        }
        if (showBorder && border != null) {
            canvas.setStroke(border, borderColor);
            canvas.strokeRect(posX, posY, sizeX, sizeY);
        }
    }

    @Override
    public Legend apply(Theme theme) {
        theme.legend.accept(this);
        return this;
    }
}
