package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public interface LegendFormatting extends KeyAreaFormatting<LegendFormatting> {
    @Override
    LegendFormatting setFloating(boolean floating);

    @Override
    LegendFormatting setOrientation(Orientation orientation);

    @Override
    LegendFormatting setGroupSpacing(double spacing);

    @Override
    LegendFormatting setTitle(String title);

    @Override
    LegendFormatting setTitleFont(Font font);

    @Override
    LegendFormatting setTitleColor(Color color);

    @Override
    LegendFormatting setTextColor(Color color);

    @Override
    LegendFormatting setTextFont(final Font font);

    @Override
    LegendFormatting setXOffset(double x);

    @Override
    LegendFormatting setYOffset(double y);

    @Override
    LegendFormatting setSide(Side side);

    @Override
    LegendFormatting setAlignment(HAlign alignment);

    @Override
    LegendFormatting setAlignment(VAlign alignment);

    @Override
    LegendFormatting setMaxItemWidth(double width);

    @Override
    LegendFormatting setBorder(final Stroke stroke);

    @Override
    LegendFormatting setBackground(final Color color);
}
