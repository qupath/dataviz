package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataviz.graphics.*;

/**
 * Either a colorscale or legend
 */
abstract class KeyArea<K extends KeyArea<K>> extends Component {
    final Figure figure;
    boolean showBorder = false;
    Stroke border = Stroke.SOLID;
    Color borderColor = Color.BLACK;
    Color background;

    Font titleFont = Font.DEFAULT_FONT;
    Color titleColor = Color.BLACK;

    Font itemFont = Font.DEFAULT_FONT;
    Color itemColor = Color.BLACK;

    boolean isFloating = false;
    Side side = Side.RIGHT;
    Orientation orientation = Orientation.VERTICAL;
    HAlign hAlign = HAlign.CENTER;
    VAlign vAlign = VAlign.MIDDLE;

    KeyArea(final Figure figure) {
        this.figure = figure;
    }

    private double offsetX = 0, offsetY = 0;

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

    @Override
    @SuppressWarnings("unchecked")
    protected K refresh() {
        return (K) super.refresh();
    }

}
