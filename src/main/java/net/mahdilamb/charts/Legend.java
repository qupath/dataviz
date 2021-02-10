package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public interface Legend extends KeyArea<Legend> {
    @Override
    Legend setFloating(boolean floating);

    @Override
    Legend setOrientation(Orientation orientation);

    @Override
    Legend setGroupSpacing(double spacing);

    @Override
    Legend setTitle(String title);

    @Override
    Legend setTitleFont(Font font);

    @Override
    Legend setTitleColor(Color color);

    @Override
    Legend setValueColor(Color color);

    @Override
    Legend setValueFont(final Font font);

    @Override
    Legend setXOffset(double x);

    @Override
    Legend setYOffset(double y);

    @Override
    Legend setSide(Side side);

    @Override
    Legend setAlignment(HAlign alignment);

    @Override
    Legend setAlignment(VAlign alignment);

    @Override
    Legend setMaxItemWidth(double width);

    @Override
    Legend setBorder(final Stroke stroke);

    @Override
    Legend setBackground(final Color color);
}
