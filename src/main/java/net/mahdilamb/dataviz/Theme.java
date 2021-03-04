package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataviz.graphics.Side;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Theme for figure
 */
public final class Theme {

    /**
     * A theme similar to Plotly's default light theme
     */
    public static final Theme Plotly = new Theme(
            fig -> {
                fig.qualitativeColormap = Colormaps.get("Plotly");
                fig.sequentialColormap = Colormaps.get("plasma");
            },
            layout -> {
                layout.background = new Color(229, 236, 246);
            },
            axis -> {
                axis.axisStroke = Stroke.NONE;
                axis.majorGridStroke = new Stroke(1);
                axis.zeroGridStroke = new Stroke(2);
            },
            legend -> {
            },
            colorScale -> {

            },
            "plotly");


    String[] names;

    final Consumer<Figure> figure;
    final Consumer<PlotLayout> layout;
    final Consumer<Axis> axis;
    final Consumer<Legend> legend;
    final Consumer<ColorScales> colorScales;

    Theme(final Consumer<Figure> figure, final Consumer<PlotLayout> layout,
          final Consumer<Axis> axis,
          final Consumer<Legend> legend,
          final Consumer<ColorScales> colorScales, String... names) {
        this.figure = figure;
        this.legend = legend;
        this.layout = layout;
        this.colorScales = colorScales;
        this.axis = axis;
        this.names = names;
    }


    /**
     * @param name the name of the theme
     * @return a default theme by its name
     */
    public static Theme get(final String name) throws IllegalAccessException {
        for (final Field f : Theme.class.getDeclaredFields()) {
            if (!Theme.class.isAssignableFrom(f.getType()) || ((Theme) f.get(Theme.class)).names == null) {
                continue;
            }
            for (final String n : ((Theme) f.get(Theme.class)).names) {
                if (n.compareToIgnoreCase(name) == 0) {
                    return (Theme) f.get(Theme.class);
                }
            }
        }
        throw new IllegalArgumentException("Could not find the theme by the name " + name);
    }

}
