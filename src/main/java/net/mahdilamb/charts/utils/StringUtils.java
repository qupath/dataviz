package net.mahdilamb.charts.utils;

import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.colormap.*;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.functions.CharacterPredicate;
import net.mahdilamb.dataframe.utils.StringParseException;

import java.util.Arrays;

/**
 * Utility class for working with strings
 */
public final class StringUtils {
    private StringUtils() {

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
     * An empty string
     */
    public static final String EMPTY_STRING = "";

    /**
     * Return the last n characters of a string. The number is defined by the length of the output array
     *
     * @param out    the output array
     * @param string the string
     * @return the last n characters of a string.
     * @throws IndexOutOfBoundsException if the length of the output array is longer than the input string (not fail-fast)
     */
    public static String getLastCharacters(final char[] out, final String string) {
        int i = string.length();
        int j = out.length;
        while (i > 0 && j > 0) {
            out[--j] = string.charAt(--i);
        }
        return new String(out);
    }

    /**
     * Return the last n characters of a string (converted to lower case).
     * The number is defined by the length of the output array
     *
     * @param out    the output array
     * @param string the string
     * @return the last n characters of a string.
     * @throws IndexOutOfBoundsException if the length of the output array is longer than the input string (not fail-fast)
     */
    public static String getLastCharactersToLowerCase(final char[] out, final String string) {
        int i = string.length();
        int j = out.length;
        while (i > 0 && j > 0) {
            out[--j] = Character.toLowerCase(string.charAt(--i));
        }
        return new String(out);
    }

    /**
     * Repeat a character
     *
     * @param c the character to repeat
     * @param n the number of times to repeat
     * @return a string of a repeated character
     */
    public static String repeatCharacter(char c, int n) {
        final char[] d = new char[n];
        Arrays.fill(d, c);
        return new String(d);
    }

    /**
     * Create a string with each word beginning with an upper case letter, the rest are lower case
     *
     * @param source the source string
     * @return the source string to title case
     */
    public static String toTitleCase(final String source) {
        return toTitleCase(source, Character::isWhitespace);
    }

    /**
     * Create a string with each word beginning with an upper case letter, the rest are lower case
     *
     * @param source the source string
     * @return the source string to title case
     */
    public static String snakeToTitleCase(final String source) {
        return toTitleCase(source, c -> c == '_');
    }

    /**
     * Create a formatted string from a snake case string
     *
     * @param source snake cased string
     * @return string in lower case
     */
    public static String snakeToLowerCase(final String source) {
        final char[] out = new char[source.length()];
        int i = 0;
        while (i < source.length()) {
            out[i] = source.charAt(i) == '_' ? ' ' : Character.toLowerCase(source.charAt(i));
            ++i;
        }
        return new String(out);
    }

    /**
     * Create a string with each word beginning with an upper case letter, the rest are lower case
     *
     * @param source            the source string
     * @param wordSeparatorTest the test for if this is a wordSeparator
     * @return the source string to title case
     */
    private static String toTitleCase(final String source, CharacterPredicate wordSeparatorTest) {
        final char[] out = new char[source.length()];
        int i = 0;
        boolean title = true;
        while (i < out.length) {
            char c = source.charAt(i++);
            if (wordSeparatorTest.test(c)) {
                out[i - 1] = ' ';
                title = true;
                continue;
            }
            out[i - 1] = title ? Character.toTitleCase(c) : Character.toLowerCase(c);
            title = false;

        }
        return new String(out);
    }

    public static String longerString(final String a, final String b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.length() >= b.length() ? a : b;
    }

    public static int nextIndexOf(final String text, int offset, char needle) {
        int i = offset;
        while (i < text.length()) {
            if (text.charAt(i) == needle) {
                return i;
            }
            ++i;
        }
        return offset;
    }

    public static int nextIndexOf(final String text, int offset, char needle, char orNeedle) {
        int i = offset;
        while (i < text.length()) {
            if (text.charAt(i) == needle || text.charAt(i) == orNeedle) {
                return i;
            }
            ++i;
        }
        return offset;
    }

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
        if (colorName.startsWith("rgb")) {
            int i = colorName.indexOf('(');
            if (i != -1) {
                double a = 1;
                double r = Double.NaN, g = Double.NaN, b = Double.NaN;
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
                            if (Double.isNaN(r)) {
                                r = Double.parseDouble(k);
                            } else if (Double.isNaN(g)) {
                                g = Double.parseDouble(k);
                            } else if (Double.isNaN(b)) {
                                b = Double.parseDouble(k);
                                done = true;
                            } else {
                                a = Double.parseDouble(k);
                            }
                            continue;
                    }
                    ++i;
                }
                if (done) {
                    if (isFloat) {
                        return new Color(r, g, b, a);
                    } else {
                        return new Color(((int) r), ((int) g), ((int) b), (int) Math.round(a * 255));
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
            double gray = Double.parseDouble(colorName);
            return new Color(gray, gray, gray);
        } else if (Colors.isValid(colorName)) {
            //hex color
            return new Color(colorName);
        }
        //color by name
        return Color.get(colorName);
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

    /**
     * @param c the character
     * @return whether the character is a format specifier
     */
    public static boolean isFormatSpecifier(final char c) {
        return c == 'x' || c == 'X' || c == 't' || c == 'T' || c == 's' || c == 'S' || c == 'n' || c == 'o' || c == 'f' || c == 'e' || c == 'E' || c == 'h' || c == 'H' || c == 'd' || c == 'c' || c == 'b' || c == 'B' || c == 'a' || c == 'A';
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }


}
