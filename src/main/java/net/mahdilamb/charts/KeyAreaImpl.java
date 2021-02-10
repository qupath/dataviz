package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;

abstract class KeyAreaImpl<K extends KeyArea<K>> extends ChartPane implements KeyArea<K> {

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
    double maxWidth = Double.POSITIVE_INFINITY;

    @Override
    @SuppressWarnings("unchecked")
    protected K redraw() {
        return (K) super.redraw();
    }

    @Override
    public K setFloating(boolean floating) {
        this.isFloating = floating;
        if (figure != null) {
            figure.setKeyArea(this);
        }
        return redraw();
    }

    @Override
    public K setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return redraw();
    }

    @Override
    public K setGroupSpacing(double spacing) {
        groupSpacing = spacing;
        return redraw();
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
        return redraw();
    }

    @Override
    public K setValueColor(Color color) {
        valueColor = color;
        return redraw();
    }

    @Override
    public K setValueFont(Font font) {
        valueFont = font;
        return redraw();
    }

    @Override
    public K setXOffset(double x) {
        this.posX = x;
        return redraw();
    }

    @Override
    public K setYOffset(double y) {
        this.posY = y;
        return redraw();
    }

    @Override
    public K setSide(Side side) {
        this.side = side;
        if (figure != null) {
            figure.setKeyArea(this);
        }
        return redraw();
    }

    @Override
    public K setAlignment(HAlign alignment) {
        this.hAlign = alignment;
        return redraw();
    }

    @Override
    public K setAlignment(VAlign alignment) {
        this.vAlign = alignment;
        return redraw();
    }

    @Override
    public K setMaxItemWidth(double width) {
        this.maxWidth = width;
        return redraw();
    }

    @Override
    public K setBorder(Stroke stroke) {
        this.border = stroke;
        return redraw();
    }

    @Override
    public K setBackground(Color color) {
        this.background = color;
        return redraw();
    }

    static final class ColorScaleImpl<S extends PlotSeries<S>> extends KeyAreaImpl<ColorScale> implements ColorScale {

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
        protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
            //TODO
        }

        @Override
        protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
            //TODO
        }
    }

    static final class LegendImpl<S extends PlotSeries<S>> extends KeyAreaImpl<Legend> implements Legend {

        static final class LegendGroup extends ChartNode<LegendItem> {

            Orientation orientation = Orientation.VERTICAL;
            List<LegendItem> legendItems = new ArrayList<>();

            @Override
            protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

            }

            @Override
            protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

            }

            @Override
            protected boolean remove(LegendItem component) {
                if (legendItems.remove(component)) {
                    layoutNeedsRefresh = true;
                    redraw();
                    return true;
                }
                return false;
            }

            @Override
            protected void add(LegendItem component) {
                legendItems.add(component);
            }
        }

        static final class LegendItem extends ChartComponent {
            final ChartComponent glyph;
            final String text;

            LegendItem(final ChartComponent glyph, String text) {
                this.glyph = glyph;
                this.text = text;
            }

            @Override
            protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

            }

            @Override
            protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

            }
        }

        Map<S, LegendItem> legendItems = new LinkedHashMap<>();

        @Override
        protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
            forEach(System.out::println);
            setBoundsFromRect(0, 0, 70, 70);

        }

        @Override
        protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
            //TODO
        }
    }
}
