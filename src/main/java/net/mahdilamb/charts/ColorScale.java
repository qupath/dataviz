package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public interface ColorScale extends KeyArea<ColorScale> {

    @Override
    ColorScale setFloating(boolean floating);

    @Override
    ColorScale setOrientation(Orientation orientation);

    @Override
    ColorScale setGroupSpacing(double spacing);

    @Override
    ColorScale setTitle(String title);

    @Override
    ColorScale setTitleFont(Font font);

    @Override
    ColorScale setTitleColor(Color color);

    @Override
    ColorScale setValueColor(Color color);

    @Override
    ColorScale setValueFont(final Font font);

    @Override
    ColorScale setXOffset(double x);

    @Override
    ColorScale setYOffset(double y);

    @Override
    ColorScale setSide(Side side);

    @Override
    ColorScale setAlignment(HAlign alignment);

    @Override
    ColorScale setAlignment(VAlign alignment);

    @Override
    ColorScale setMaxItemWidth(double width);

    @Override
    ColorScale setBorder(final Stroke stroke);

    @Override
    ColorScale setBackground(final Color color);

    ColorScale showLabels(boolean values);

    ColorScale setLabelAlignment(HAlign alignment);

    ColorScale setLabelAlignment(VAlign alignment);

    ColorScale showTickMarks(boolean showTickMarks);

    ColorScale setTickPosition(HAlign alignment);

    ColorScale setTickPosition(VAlign alignment);

    /**
     * Sets the width if vertical, or height if horizontal
     *
     * @param size the size to set
     * @return this ColorScaleFormatting
     */
    ColorScale setThickness(double size);

    ColorScale setLength(double size);

}
