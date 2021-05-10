package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.*;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;

abstract class KeyArea<K extends KeyArea<K>> extends Component {

    double paddingX = 5, paddingY = 2.5;
    double groupSpacing = 2;
    boolean showBorder = false;
    Stroke border = Stroke.SOLID;
    Color borderColor = Color.BLACK;

    Color background = Color.WHITE;

    Font titleFont = Font.DEFAULT_FONT;
    Color titleColor = Color.BLACK;
    double titleSpacing = 4;
    Font itemFont = Font.DEFAULT_FONT;
    Color itemColor = Color.BLACK;
    double itemSize;
    double itemSpacing = 2;

    boolean isFloating = false;
    Side side = Side.RIGHT;
    Orientation orientation = null;
    HAlign hAlign = HAlign.CENTER;
    VAlign vAlign = VAlign.MIDDLE;

    private double offsetX = 0, offsetY = 0;
    final Figure figure;

    KeyArea(final Figure figure, final boolean sizeRelative, double itemSize) {
        super(BufferingStrategy.NO_BUFFERING);
        this.figure = figure;
        this.itemSize = (sizeRelative ? -1 : 1) * Numbers.requireFinitePositive(itemSize);
    }

    protected final boolean isSizeRelative() {
        return itemSize < 0;
    }

    protected final double getItemSize() {
        if (isSizeRelative()) {
            return -itemSize * (
                    orientation == getOrientation() ?
                            getContext().getRenderer().getFigure().getWidth() :
                            getContext().getRenderer().getFigure().getHeight()
            );
        }
        return itemSize;
    }

    protected final Orientation getOrientation() {
        if (orientation == null) {
            return side.isVertical() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        }
        return orientation;
    }

    protected abstract <IMG> void layoutVertical(Renderer<IMG> renderer, double minX, double minY, double maxX, double maxY);

    protected abstract <IMG> void layoutHorizontal(Renderer<IMG> renderer, double minX, double minY, double maxX, double maxY);

    @Override
    protected final <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {

        if (side.isVertical()) {
            layoutVertical(renderer, minX, minY, maxX, maxY);
        } else {
            layoutHorizontal(renderer, minX, minY, maxX, maxY);
        }
    }

    final double getOffsetX() {
        return (side == Side.LEFT ? -1 : 1) * offsetX;
    }

    final double getOffsetY() {
        return (side == Side.TOP ? -1 : 1) * offsetY;
    }

    public K setSide(final Side side) {
        this.side = side;
        return refresh();
    }

    public K setAlignment(final VAlign alignment) {
        this.vAlign = alignment;
        return refresh();
    }

    public K setAlignment(final HAlign alignment) {
        this.hAlign = alignment;
        return refresh();
    }

    public K setFloating(final boolean floating) {
        this.isFloating = floating;
        return refresh();
    }

    @SuppressWarnings("unchecked")
    final K refresh() {
        markLayoutAsOldQuietly();
        redraw();
        return (K) this;
    }


}
