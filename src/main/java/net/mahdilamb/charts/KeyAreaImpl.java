package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

abstract class KeyAreaImpl<K extends KeyArea<K>> extends ChartPane implements KeyArea<K> {

    Stroke border;
    Color background;

    Font titleFont = Font.DEFAULT_TITLE_FONT;
    Color titleColor = Color.BLACK;

    Font itemFont = Font.DEFAULT_FONT;
    Color valueColor = Color.BLACK;

    boolean isFloating = false;
    Side side = Side.RIGHT;
    Orientation orientation = Orientation.VERTICAL;
    HAlign hAlign = HAlign.CENTER;
    VAlign vAlign = VAlign.MIDDLE;
    double groupSpacing = 2;
    double maxWidth = Double.POSITIVE_INFINITY;

    private double offsetX = 0, offsetY = 0;

    final double getOffsetX() {
        return (side == Side.LEFT ? -1 : 1) * offsetX;
    }

    final double getOffsetY() {
        return (side == Side.TOP ? -1 : 1) * offsetY;
    }

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
    public K setTitleFont(Font font) {
        this.titleFont = font;
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
        itemFont = font;
        return redraw();
    }

    @Override
    public K setXOffset(double x) {
        this.offsetX = x;
        return redraw();
    }

    @Override
    public K setYOffset(double y) {
        this.offsetY = y;
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

    protected void align(Figure<?, ?> source, double height, double width) {
        posY = parent.posY + offsetY;
        posX = parent.posX + offsetX;
        if (side.isVertical()) {
            if (vAlign != VAlign.TOP) {
                posY += (height - sizeY) * (vAlign == VAlign.MIDDLE ? 0.5 : 1);
            }
        } else {
            if (hAlign != HAlign.LEFT) {
                posX += (width - sizeX) * (hAlign == HAlign.CENTER ? 0.5 : 1);
            }
        }
    }


}
