package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public interface ColorScaleFormatting extends KeyAreaFormatting<ColorScaleFormatting> {

    @Override
    ColorScaleFormatting setFloating(boolean floating);

    @Override
    ColorScaleFormatting setOrientation(Orientation orientation);

    @Override
    ColorScaleFormatting setGroupSpacing(double spacing);

    @Override
    ColorScaleFormatting setTitle(String title);

    @Override
    ColorScaleFormatting setTitleFont(Font font);

    @Override
    ColorScaleFormatting setTitleColor(Color color);

    @Override
    ColorScaleFormatting setValueColor(Color color);

    @Override
    ColorScaleFormatting setValueFont(final Font font);

    @Override
    ColorScaleFormatting setXOffset(double x);

    @Override
    ColorScaleFormatting setYOffset(double y);

    @Override
    ColorScaleFormatting setSide(Side side);

    @Override
    ColorScaleFormatting setAlignment(HAlign alignment);

    @Override
    ColorScaleFormatting setAlignment(VAlign alignment);

    @Override
    ColorScaleFormatting setMaxItemWidth(double width);

    @Override
    ColorScaleFormatting setBorder(final Stroke stroke);

    @Override
    ColorScaleFormatting setBackground(final Color color);

    ColorScaleFormatting showLabels(boolean values);

    ColorScaleFormatting setLabelAlignment(HAlign alignment);

    ColorScaleFormatting setLabelAlignment(VAlign alignment);

    ColorScaleFormatting showTickMarks(boolean showTickMarks);

    ColorScaleFormatting setTickPosition(HAlign alignment);

    ColorScaleFormatting setTickPosition(VAlign alignment);

    /**
     * Sets the width if vertical, or height if horizontal
     *
     * @param size the size to set
     * @return this ColorScaleFormatting
     */
    ColorScaleFormatting setSize(double size);

}
