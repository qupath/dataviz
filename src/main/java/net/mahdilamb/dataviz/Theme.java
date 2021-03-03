package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.lang.reflect.Field;

/**
 * Theme for figure
 */
public class Theme {
    /**
     * A theme similar to Plotly's default light theme
     */
    public static final Theme Plotly = new Theme("plotly");


    static {
        // PLOTLY_DEFAULT
        Plotly.layoutBackground = new Color(229, 236, 246);
        Plotly.axisStroke = Stroke.NONE;
        Plotly.majorGridStroke = new Stroke(1);
        Plotly.zeroGridStroke = new Stroke(2);
        Plotly.qualitativeColormap = Colormaps.get("Plotly");
        Plotly.sequentialColormap = Colormaps.get("plasma");
        Plotly.showMajorTicks = false;

    }

    String[] names;

    protected Color layoutBackground,
            majorLineColor,
            minorLineColor,
            zeroLineColor,
            axisColor;


    protected Stroke majorGridStroke,
            zeroGridStroke,
            axisStroke;
    protected Colormap qualitativeColormap,
            sequentialColormap;
    protected double majorTickLength = Double.NaN,
            minorTickLength = Double.NaN;
    protected Boolean showMajorTicks,
            showMinorTicks;

    Theme(String... names) {
        this.names = names;
    }

    Theme() {

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

    void apply(final Figure figure) {
        if (sequentialColormap != null) {
            figure.sequentialColormap = sequentialColormap;
        }
        if (qualitativeColormap != null) {
            figure.qualitativeColormap = qualitativeColormap;
            figure.defaultColor.clear();
        }
        final PlotLayout layout = figure.layout;
        if (layoutBackground != null) {
            layout.background = layoutBackground;
        }
        if (layout instanceof PlotLayout.Rectangular) {
            if (axisStroke != null) {
                ((PlotLayout.Rectangular) layout).x.axisStroke = axisStroke;
                ((PlotLayout.Rectangular) layout).y.axisStroke = axisStroke;
            }
            if (zeroGridStroke != null) {
                ((PlotLayout.Rectangular) layout).x.zeroGridStroke = zeroGridStroke;
                ((PlotLayout.Rectangular) layout).y.zeroGridStroke = zeroGridStroke;
            }
            if (majorGridStroke != null) {
                ((PlotLayout.Rectangular) layout).x.majorGridStroke = majorGridStroke;
                ((PlotLayout.Rectangular) layout).y.majorGridStroke = majorGridStroke;
            }
            if (!Double.isNaN(majorTickLength)) {
                ((PlotLayout.Rectangular) layout).x.majorTickLength = majorTickLength;
                ((PlotLayout.Rectangular) layout).y.majorTickLength = majorTickLength;
            }
            if (!Double.isNaN(minorTickLength)) {
                ((PlotLayout.Rectangular) layout).x.minorTickLength = minorTickLength;
                ((PlotLayout.Rectangular) layout).y.minorTickLength = minorTickLength;
            }
            if (majorLineColor != null) {
                ((PlotLayout.Rectangular) layout).x.majorLineColor = majorLineColor;
                ((PlotLayout.Rectangular) layout).y.majorLineColor = majorLineColor;
            }
            if (minorLineColor != null) {
                ((PlotLayout.Rectangular) layout).x.minorLineColor = minorLineColor;
                ((PlotLayout.Rectangular) layout).y.minorLineColor = minorLineColor;
            }
            if (zeroLineColor != null) {
                ((PlotLayout.Rectangular) layout).x.zeroLineColor = zeroLineColor;
                ((PlotLayout.Rectangular) layout).y.zeroLineColor = zeroLineColor;
            }
            if (axisColor != null) {
                ((PlotLayout.Rectangular) layout).x.axisColor = axisColor;
                ((PlotLayout.Rectangular) layout).y.axisColor = axisColor;
            }
                /*if (showMajorTicks != null) {
                    ((PlotLayout.Rectangular) layout).x.showMajorTicks = showMajorTicks;
                    ((PlotLayout.Rectangular) layout).y.showMajorTicks = showMajorTicks;
                }
                if (showMinorTicks != null) {
                    ((PlotLayout.Rectangular) layout).x.showMinorTicks = showMinorTicks;
                    ((PlotLayout.Rectangular) layout).y.showMinorTicks = showMinorTicks;
                }*/
        }

    }
}
