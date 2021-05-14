package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataviz.Legend;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
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

        final Scatter data;
        final Color color;

        private ScatterGlyph(final Scatter data, final Color fill) {
            this.data = data;
            this.color = data.markerOpacity == 1 ? fill : ColorUtils.applyAlpha(fill, (float) data.markerOpacity);

        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX, minY, getSize(), getSize());
        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            canvas.setFill(color);
            canvas.fillOval(getX(), getY(), getWidth(), getHeight());
        }

        @Override
        public double getSize() {
            return 10;
        }
    }

    private static final class SizedScatterGlyph extends ScatterGlyph {

        private final double size, maxSize;

        private SizedScatterGlyph(Scatter data, Color fill, double size, double maxSize) {
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
            canvas.setFill(color);
            canvas.fillOval(getX() + (maxSize - size) * .5, getY(), size, size);
        }

    }

    private GlyphFactory() {
    }

    static Glyph createScatterGlyph(final Scatter data, final Color fill) {
        return new ScatterGlyph(data, fill);
    }

    static Glyph createSizedScatterGlyph(Scatter data, Color fill, double size, double maxSize) {
        return new SizedScatterGlyph(data, fill, size, maxSize);
    }
}
