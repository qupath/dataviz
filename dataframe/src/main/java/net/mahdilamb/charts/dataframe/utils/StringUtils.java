package net.mahdilamb.charts.dataframe.utils;


import net.mahdilamb.utils.functions.AndBiIntFunction;
import net.mahdilamb.utils.functions.CharacterPredicate;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Utility class for working with strings
 */
public final class StringUtils {
    private StringUtils() {

    }

    public static final String EMPTY_STRING = "";

    /**
     * Floating point pattern that allows for strings that can be parsed by  {@link Double#parseDouble}, but excludes
     * hexadecimal representation.
     */
    public final static String fpPatternWithoutHex = "[+-]?(?:NaN|Infinity|(?:\\d++(?:\\.\\d*+)?|\\.\\d++)(?:[eE][+-]?\\d++)?[fFdD]?)";
    /**
     * Floating point pattern that represents valid strings that can be parsed by {@link Double#parseDouble}.
     * Adapted from Google Guava
     */
    public final static String fpPattern = fpPatternWithoutHex.substring(0, fpPatternWithoutHex.length() - 1) + "|0[xX](?:[0-9a-fA-F]++(?:\\.[0-9a-fA-F]*+)?|\\.[0-9a-fA-F]++)[pP][+-]?\\d++[fFdD]?)";
    /**
     * Regex pattern that matches integers
     */
    public final static String longPattern = "[+-]?(?:\\d++[Ll]?)";
    /**
     * Regex pattern that matches true or false, case-insensitive
     */
    public final static String boolPattern = "(?:[Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee]|[01])";
    /**
     * Regex pattern for a line with a terminator
     * Copied from JDK
     */
    public static final String LINE_PATTERN = ".*(\r\n|[\n\r\u2028\u2029\u0085])|.+$";
    /**
     * The compiled pattern for {@link #fpPatternWithoutHex}
     */
    public final static Pattern FLOATING_POINT_PATTERN_WITHOUT_HEX = Pattern.compile(fpPatternWithoutHex);
    /**
     * The compiled pattern for {@link #fpPattern}
     */
    public final static Pattern FLOATING_POINT_PATTERN = Pattern.compile(fpPattern);
    /**
     * The compiled pattern for {@link #longPattern}
     */
    public final static Pattern LONG_PATTERN = Pattern.compile(longPattern);
    /**
     * The compiled pattern for {@link #boolPattern}
     */
    public final static Pattern BOOLEAN_PATTERN = Pattern.compile(boolPattern);

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
     * Iterate over a line, separated by the provided separator
     *
     * @param line      the line to iterate over
     * @param offset    the starting position
     * @param sepChar   the cell separator e.g. comma or tab
     * @param quoteChar the quote character
     * @param func      the function to apply to the cell
     * @return the ending position
     */
    public static int iterateLine(String line, int offset, char sepChar, char quoteChar, Consumer<String> func) {
        int s = offset, e = offset;
        char quote = 0;
        while (e < line.length()) {
            final char c = line.charAt(e++);
            if (c == quoteChar) {
                quote = quote != c ? c : 0;
            }
            if (c == sepChar && quote == 0) {
                break;
            }
        }
        int f;
        if (e == line.length()) {
            f = line.length() - 1;

            //one column
            if (s == 0) {
                if (line.charAt(0) == line.charAt(f) && line.charAt(f) == quoteChar) {
                    s = 1;
                } else {
                    f = line.length();
                }
            } else {
                if (line.charAt(s) == line.charAt(f) && line.charAt(f) == quoteChar) {
                    ++s;
                    --f;
                }else {
                    f = line.length();
                }
            }
        } else {

            f = e - 2;
            if (line.charAt(s) == line.charAt(f) && line.charAt(s) == quoteChar) {
                ++s;
            } else {
                ++f;
            }
        }
        func.accept(line.substring(s, f));
        return e;
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
     * Test if a comparator is correct
     *
     * @param eq      whether to test for equality
     * @param lt      whether to test for less than
     * @param gt      whether to test for greater than
     * @param ne      whether to test for not equals
     * @param compare the int from compilation
     * @return whether the comparison fits the tests
     */
    private static boolean toBoolean(boolean eq, boolean lt, boolean gt, boolean ne, int compare) {
        return ne ? compare != 0 : ((eq && compare == 0) || (lt && compare < 0 || gt && compare > 0));
    }

    /**
     * Convert a string into a functional operator
     *
     * @param string       the string to convert
     * @param rhsConverter the function used to convert the right hand operand into the the correct type for comparison
     * @param <T>          the type of the value to use in the comparison
     * @return a predicate representation of the input string
     */
    public static <T extends Comparable<T>> Predicate<T> parseToPredicate(final String string, Function<String, T> rhsConverter) {
        boolean[] legn = new boolean[4];
        int i = 0;
        int j = 0;
        while (i < string.length() && j < 2) {
            char c = string.charAt(i++);
            if (c == '=' || c == '<' || c == '>' || c == '!') {
                legn[1] |= c == '=';
                legn[3] |= c == '!';
                legn[2] |= c == '>';
                legn[0] |= c == '<';

                if ((j == 1 && legn[1] && (c == '<' || c == '>'))) {
                    throw new IllegalArgumentException("Could not parse - incorrect boolean operators");
                }
                ++j;
            }
        }
        if (i == string.length()) {
            throw new IllegalArgumentException("Could not parse - no boolean operators found");
        }
        while (i < string.length()) {
            if (!Character.isWhitespace(string.charAt(i++))) {
                --i;
                break;
            }
        }
        j = i + 1;
        while (j < string.length()) {
            if (Character.isWhitespace(string.charAt(j++))) {
                --j;
                break;
            }
        }
        if (i >= j) {
            throw new IllegalArgumentException("Could not parse - no right hand arguments");
        }
        final T val = rhsConverter.apply(string.substring(i, j));
        return t -> toBoolean(legn[1], legn[0], legn[2], legn[3], val.compareTo(t));
    }


    public static <T> T slice(AndBiIntFunction<T> object, String slice, int defaultEnd) {
        if (slice.length() == 0) {
            throw new IllegalArgumentException("slice must not be empty");
        }
        int start = 0, end = defaultEnd, lastColonPos = slice.length();
        int i = slice.length();
        int e = 1;
        int j = 0;
        int k = 0;
        while (i > 0) {
            final char c = slice.charAt(--i);
            if (c == '-') {
                if (k < 0) {
                    throw new IllegalArgumentException("slice has two negatives");
                }
                k *= -1;
                continue;
            }
            if (Character.isWhitespace(c)) {
                throw new IllegalArgumentException("slice must not contain white space characters");
            }
            if (c == ':') {
                e = 1;
                lastColonPos = i;
                if (j == 0) {
                    end = k;
                    k = 0;
                } else {
                    end = defaultEnd;
                }
                ++j;
                if (j >= 2) {
                    throw new IllegalArgumentException("Only start and end supported");
                }
            }
            if (Character.isDigit(c)) {
                k += e * Character.getNumericValue(c);
                e *= 10;
            }
        }
        if (lastColonPos != 0 && j != 0) {
            start = k;
        }
        if (j == 0) {
            start = k;
            end = k + 1;
        }
        if (start >= end) {
            return object.apply(0, 0);
        }
        return object.apply(start < 0 ? (defaultEnd + start) : start, end < 0 ? (defaultEnd + end) : end);
    }

}