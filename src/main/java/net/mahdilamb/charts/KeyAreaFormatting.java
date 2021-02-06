package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

interface KeyAreaFormatting<K extends KeyAreaFormatting<K>> {
    K setFloating(boolean floating);

    K setOrientation(Orientation orientation);

    K setGroupSpacing(double spacing);

    K setTitle(String title);

    K setTitleFont(Font font);

    K setTitleColor(Color color);

    K setTextColor(Color color);

    K setTextFont(final Font font);

    K setXOffset(double x);

    K setYOffset(double y);

    K setSide(Side side);

    K setAlignment(HAlign alignment);

    K setAlignment(VAlign alignment);

    K setMaxItemWidth(double width);

    K setBorder(final Stroke stroke);

    K setBackground(final Color color);

}
