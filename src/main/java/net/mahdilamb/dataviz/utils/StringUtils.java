package net.mahdilamb.dataviz.utils;

import net.mahdilamb.dataframe.functions.CharacterPredicate;

import java.io.File;

/**
 * Utility class for working with strings
 */
public final class StringUtils {
    private StringUtils() {

    }

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

    /**
     * Return which ever string is longer.
     *
     * @param a string a
     * @param b string b
     * @return the longer string
     * @throws NullPointerException if both strings are null
     */
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
     * Return the next index of a character
     *
     * @param text   the text to search
     * @param offset the starting point
     * @param needle the character to search
     * @return the next index of the the character or offset, if no match
     */
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

    /**
     * @param c the character
     * @return whether the character is a format specifier
     */
    public static boolean isFormatSpecifier(final char c) {
        return c == 'x' || c == 'X' || c == 't' || c == 'T' || c == 's' || c == 'S' || c == 'n' || c == 'o' || c == 'f' || c == 'e' || c == 'E' || c == 'h' || c == 'H' || c == 'd' || c == 'c' || c == 'b' || c == 'B' || c == 'a' || c == 'A';
    }

    /**
     * @param c the character
     * @return whether a character is a number
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean hasFileExtension(final String file) {
        return file.lastIndexOf('.') > file.lastIndexOf(File.separator);
    }

}
