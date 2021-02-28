package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

abstract class KeyArea extends Component{
    Stroke border;
    Color borderColor = Color.BLACK;
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
    final Figure figure;

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
}
