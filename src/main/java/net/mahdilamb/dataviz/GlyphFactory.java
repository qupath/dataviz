package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.Legend;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.dataviz.utils.ColorUtils;

import java.awt.*;

public final class GlyphFactory {
    public static abstract class Glyph extends Component {
        public abstract double getSize();

        public double getMaxSize() {
            return getSize();
        }
    }

    private static class ScatterGlyph extends Glyph {

        final RelationalData<?> data;
        final Color color;

        private ScatterGlyph(final RelationalData<?> data, final Color fill) {
            this.data = data;
            this.color = data.getOpacity(-1) == 1 ? fill : ColorUtils.applyAlpha(fill, (float) data.getOpacity(-1));

        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX, minY, getSize(), getSize());
        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            if (data.getMarkerMode() != ScatterMode.MARKER_ONLY) {
                canvas.setStroke(color);
                canvas.strokeLine(getX(), getY() + (getSize()) * .5, getX() + getSize(), getY() + (getSize()) * .5);
            }
            if (data.getMarkerMode() != ScatterMode.LINE_ONLY) {
                canvas.setFill(color);
                canvas.fillOval(getX(), getY(), getWidth(), getHeight());
            }
        }

        @Override
        public double getSize() {
            return 10;
        }
    }

    private static final class SizedScatterGlyph extends ScatterGlyph {

        private final double size, maxSize;

        private SizedScatterGlyph(RelationalData<?> data, Color fill, double size, double maxSize) {
            super(data, fill);
            this.size = size;
            this.maxSize = maxSize;
        }

        @Override
        public double getSize() {
            return size;
        }

        @Override
        public double getMaxSize() {
            return maxSize;
        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            if (data.getMarkerMode() != ScatterMode.MARKER_ONLY) {
                canvas.setStroke(color);
                canvas.strokeLine(getX(), getY() + (size) * .5, getX() + size, getY() + (size) * .5);
            }
            if (data.getMarkerMode() != ScatterMode.LINE_ONLY) {
                canvas.setFill(color);
                canvas.fillOval(getX() + (maxSize - size) * .5, getY(), size, size);
                if (data.showEdges()) {
                    canvas.setStroke(data.lineColor);
                    canvas.strokeOval(getX() + (maxSize - size) * .5, getY(), size, size);
                }
            }
        }

    }

    private GlyphFactory() {
    }

    public static Glyph createScatterGlyph(final RelationalData<?> data, final Color fill) {
        return new ScatterGlyph(data, fill);
    }

    public static Glyph createSizedScatterGlyph(RelationalData<?> data, Color fill, double size, double maxSize) {
        return new SizedScatterGlyph(data, fill, size, maxSize);
    }
}
