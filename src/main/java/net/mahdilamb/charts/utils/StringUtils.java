package net.mahdilamb.charts.utils;

import net.mahdilamb.charts.dataframe.DataType;
import net.mahdilamb.colormap.*;
import net.mahdilamb.utils.functions.CharacterPredicate;

import java.util.Arrays;

/**
 * Utility class for working with strings
 */
public final class StringUtils {
    private StringUtils() {

    }

    private static final double[] POWER_OF_TWO_PLUS_ONE = {1.0, 3.0, 5.0, 9.0, 17.0, 33.0, 65.0, 129.0, 257.0, 513.0, 1025.0, 2049.0, 4097.0, 8193.0, 16385.0, 32769.0, 65537.0, 131073.0, 262145.0, 524289.0, 1048577.0, 2097153.0, 4194305.0, 8388609.0, 1.6777217E7, 3.3554433E7, 6.7108865E7, 1.34217729E8, 2.68435457E8, 5.36870913E8, 1.073741825E9, 2.147483649E9, 4.294967297E9, 8.589934593E9, 1.7179869185E10, 3.4359738369E10, 6.8719476737E10, 1.37438953473E11, 2.74877906945E11, 5.49755813889E11, 1.099511627777E12, 2.199023255553E12, 4.398046511105E12, 8.796093022209E12, 1.7592186044417E13, 3.5184372088833E13, 7.0368744177665E13, 1.40737488355329E14, 2.81474976710657E14, 5.62949953421313E14, 1.125899906842625E15, 2.251799813685249E15, 4.503599627370497E15, 9.007199254740992E15};


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

    /**
     * Extended version of finding a color from a string. Includes the one character version from matplotlib and
     * creating grays from a floating point value, or integer type
     *
     * @param colorName the color name
     * @return the associated color or {@code null} if no such color exists
     */
    public static Color convertToColor(final String colorName) {
        if (colorName.length() == 1) {
            switch (colorName) {
                case "b":
                    return Color.blue;
                case "g":
                    return Color.green;
                case "r":
                    return Color.red;
                case "c":
                    return mpl_cyan;
                case "m":
                    return mpl_magenta;
                case "y":
                    return mpl_yellow;
                case "k":
                    return Color.black;
                case "w":
                    return Color.white;
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

    public static Colormap convertToQualitativeColormap(final String... names) {
        return convertToColormap(true, names);
    }

    public static Colormap convertToSequentialColormap(final String... names) {
        return convertToColormap(false, names);
    }

    public static Colormap convertToColormap(boolean useQualitative, final String... names) {
        final Color[] colors = new Color[names.length];
        for (int i = 0; i < names.length; ++i) {
            colors[i] = convertToColor(names[i]);
            if (colors[i] == null) {
                throw new IllegalArgumentException(String.format("Color with the name %s could not be found", names[i]));
            }
        }
        return useQualitative ? new QualitativeColormap(colors) : new SequentialColormap(colors);
    }


}
