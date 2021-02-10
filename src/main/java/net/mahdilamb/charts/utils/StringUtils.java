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

    public static final Color mpl_cyan = new Color(0, 191, 191);
    public static final Color mpl_magenta = new Color(191, 0, 191);
    public static final Color mpl_yellow = new Color(191, 191, 0);
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

        if (DataType.DOUBLE.matches(colorName)) {
            double gray = Double.parseDouble(colorName);
            return new Color(gray, gray, gray);
        } else if (DataType.LONG.matches(colorName)) {
            long gray = Long.parseLong(colorName);
            return new Color((int) gray, (int) gray, (int) gray);
        } else if (Colors.isValid(colorName)) {
            return new Color(colorName);
        }
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
        }
        return useQualitative ? new QualitativeColormap(colors) : new SequentialColormap(colors);
    }

}
