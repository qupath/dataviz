package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

abstract class KeyAreaFormattingImpl<K extends KeyAreaFormatting<K>> extends ChartComponent implements KeyAreaFormatting<K> {

    Stroke border;
    Color background;

    Title title;
    Color titleColor = Color.BLACK;

    Font valueFont = Font.DEFAULT_FONT;
    Color valueColor = Color.BLACK;

    boolean isFloating = false;
    Side side = Side.LEFT;
    Orientation orientation = Orientation.VERTICAL;
    HAlign hAlign = HAlign.CENTER;
    VAlign vAlign = VAlign.MIDDLE;
    double groupSpacing = 2;
    double xOffset = 0, yOffset = 0, maxWidth = Double.POSITIVE_INFINITY;

    @Override
    @SuppressWarnings("unchecked")
    protected K requestLayout() {
        return (K) super.requestLayout();
    }

    @Override
    public K setFloating(boolean floating) {
        this.isFloating = floating;
        return requestLayout();
    }

    @Override
    public K setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return requestLayout();
    }

    @Override
    public K setGroupSpacing(double spacing) {
        groupSpacing = spacing;
        return requestLayout();
    }

    @Override
    @SuppressWarnings("unchecked")
    public K setTitle(String title) {
        if (this.title == null) {
            this.title = new Title(title, Font.DEFAULT_FONT);
        }
        this.title.setTitle(title);
        return (K) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public K setTitleFont(Font font) {
        if (this.title == null) {
            this.title = new Title(EMPTY_STRING, Font.DEFAULT_FONT);
        }
        title.setFont(font);
        return (K) this;
    }

    @Override
    public K setTitleColor(Color color) {
        this.titleColor = color;
        return requestLayout();
    }

    @Override
    public K setValueColor(Color color) {
        valueColor = color;
        return requestLayout();
    }

    @Override
    public K setValueFont(Font font) {
        valueFont = font;
        return requestLayout();
    }

    @Override
    public K setXOffset(double x) {
        this.xOffset = x;
        return requestLayout();
    }

    @Override
    public K setYOffset(double y) {
        this.yOffset = y;
        return requestLayout();
    }

    @Override
    public K setSide(Side side) {
        this.side = side;
        return requestLayout();
    }

    @Override
    public K setAlignment(HAlign alignment) {
        this.hAlign = alignment;
        return requestLayout();
    }

    @Override
    public K setAlignment(VAlign alignment) {
        this.vAlign = alignment;
        return requestLayout();
    }

    @Override
    public K setMaxItemWidth(double width) {
        this.maxWidth = width;
        return requestLayout();
    }

    @Override
    public K setBorder(Stroke stroke) {
        this.border = stroke;
        return requestLayout();
    }

    @Override
    public K setBackground(Color color) {
        this.background = color;
        return requestLayout();
    }
    static final class ColorScaleFormattingImpl<S extends PlotSeries<S>> extends KeyAreaFormattingImpl<ColorScaleFormatting> implements ColorScaleFormatting {

        boolean showLabels = true, showTicks = true;

        HAlign hLabelAlign = HAlign.RIGHT;
        VAlign vLabelAlign = VAlign.TOP;

        HAlign hTickPosition = hLabelAlign;
        VAlign vTickPosition = vLabelAlign;

        double size = 16;

        public ColorScaleFormattingImpl(S[] series) {
            super();
        }

        @Override
        public ColorScaleFormatting showLabels(boolean values) {
            this.showLabels = values;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting setLabelAlignment(HAlign alignment) {
            this.hLabelAlign = alignment;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting setLabelAlignment(VAlign alignment) {
            this.vLabelAlign = alignment;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting showTickMarks(boolean showTickMarks) {
            this.showTicks = showTickMarks;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting setTickPosition(HAlign alignment) {
            this.hTickPosition = alignment;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting setTickPosition(VAlign alignment) {
            this.vTickPosition = alignment;
            return requestLayout();
        }

        @Override
        public ColorScaleFormatting setSize(double size) {
            this.size = size;
            return requestLayout();
        }

        @Override
        protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
            //TODO
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
            //TODO
        }
    }

    static final class LegendFormattingImpl<S extends PlotSeries<S>> extends KeyAreaFormattingImpl<LegendFormatting> implements LegendFormatting{

        public LegendFormattingImpl(S[] series) {
            super();
        }

        @Override
        protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
            //TODO
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
            //TODO
        }
    }
}
