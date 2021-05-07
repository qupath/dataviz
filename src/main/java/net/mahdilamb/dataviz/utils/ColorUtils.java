package net.mahdilamb.dataviz.utils;

import net.mahdilamb.colormap.*;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.utils.StringParseException;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.awt.*;

import static net.mahdilamb.dataviz.utils.StringUtils.isDigit;

public final class ColorUtils {
    private ColorUtils() {

    }

    /**
     * Cyan as appears in matplotlib
     */
    public static final Color mpl_cyan = new Color(0, 191, 191);
    /**
     * Magenta as appears in matplotlib
     */
    public static final Color mpl_magenta = new Color(191, 0, 191);
    /**
     * Yellow as appears in matplotlib
     */
    public static final Color mpl_yellow = new Color(191, 191, 0);

    /**
     * Extended version of finding a color from a string.
     * Possible inputs:
     * - following single characters as per matplotlib: r, g, b, c, m, y, k, w
     * - grays from int value (e.g. 128) and floating point (e.g 0.5)
     * - css style colors (e.g. rgba(255, 0, 0, 0.8) or rgb(255, 255, 0))
     * - color by name in css, or tableau. (e.g. "salmon")
     * - hexadecimal color value (e.g. "#FF00000")
     *
     * @param colorName the color name
     * @return the associated color or {@code null} if no such color exists
     */
    public static Color convertToColor(final String colorName) {
        //single color
        if (colorName.length() == 1) {
            switch (colorName.charAt(0)) {
                case 'b':
                    return Color.blue;
                case 'g':
                    return Color.green;
                case 'r':
                    return Color.red;
                case 'c':
                    return mpl_cyan;
                case 'm':
                    return mpl_magenta;
                case 'y':
                    return mpl_yellow;
                case 'k':
                    return Color.black;
                case 'w':
                    return Color.white;
            }
        }
        //web style
        if (colorName.startsWith("rgb")) {
            int i = colorName.indexOf('(');
            if (i != -1) {
                float a = 1;
                float r = Float.NaN, g = Float.NaN, b = Float.NaN;
                boolean isFloat = false;
                boolean done = false;
                while (i < colorName.length()) {
                    switch (colorName.charAt(i)) {
                        case ' ':
                            break;
                        case ',':
                        case '(':
                            ++i;
                            int j = i;
                            while (isDigit(colorName.charAt(j)) || colorName.charAt(j) == '.' || colorName.charAt(j) == ' ') {
                                ++j;
                            }
                            final String k = colorName.substring(i, j);
                            if (!done) {
                                isFloat |= k.indexOf('.') != -1;
                            }
                            if (Float.isNaN(r)) {
                                r = Float.parseFloat(k);
                            } else if (Float.isNaN(g)) {
                                g = Float.parseFloat(k);
                            } else if (Float.isNaN(b)) {
                                b = Float.parseFloat(k);
                                done = true;
                            } else {
                                a = Float.parseFloat(k);
                            }
                            continue;
                    }
                    ++i;
                }
                if (done) {
                    if (isFloat) {
                        return new Color(r, g, b, a);
                    } else {
                        return new Color(((int) r), ((int) g), ((int) b), Math.round(a * 255));
                    }
                } else {
                    throw new StringParseException(colorName, i);
                }
            }
        }
        if (DataType.LONG.matches(colorName)) {
            //integer gray
            int gray = (int) Long.parseLong(colorName);
            return new Color(gray, gray, gray);
        } else if (DataType.DOUBLE.matches(colorName)) {
            //floating point grey
            float gray = Float.parseFloat(colorName);
            return new Color(gray, gray, gray);
        } else if (Colors.isValid(colorName)) {
            //hex color
            return Colors.fromHexadecimal(colorName);
        }
        //color by name
        return Colors.get(colorName);
    }

    /**
     * Get a stroke from a string representation
     *
     * @param stroke the string
     * @return a stroke from the string
     */
    public static Stroke convertToStroke(final String stroke) {
        switch (stroke) {
            case "solid":
            case "-":
                return Stroke.SOLID;
            case "--":
            case "dashed":
            case "dash":
                return Stroke.DASHED;
            case "-.":
            case "dashdot":
                return Stroke.DASH_DOT;
            case ":":
            case "dotted":
            case "dot":
            case ".":
                return Stroke.DOTTED;
            case "None":
            case "":
            case " ":
                return Stroke.NONE;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static Colormap convertToQualitativeColormap(final String... names) {
        return convertToColormap(true, names);
    }

    public static Colormap convertToSequentialColormap(final String... names) {
        return convertToColormap(false, names);
    }

    public static Colormap convertToColormap(boolean useQualitative, final String... names) {
        return VarArgsUtils.process(
                names,
                va -> {
                    throw new IllegalArgumentException("Must be at least one name");
                }, va -> {
                    final Color[] colors = new Color[names.length];
                    for (int i = 0; i < names.length; ++i) {
                        colors[i] = convertToColor(names[i]);
                        if (colors[i] == null) {
                            throw new IllegalArgumentException(String.format("Color with the name %s could not be found", names[i]));
                        }
                    }
                    return useQualitative ? new QualitativeColormap(colors) : new SequentialColormap(colors);
                }
        );
    }

    public static Color getForegroundFromBackground(final Color background) {
        return Colors.calculateLuminance(background) > 0.1791 ? Color.BLACK : Color.WHITE;
    }
}
