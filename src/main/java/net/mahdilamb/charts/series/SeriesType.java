package net.mahdilamb.charts.series;

import java.util.regex.Pattern;

import static net.mahdilamb.charts.utils.StringUtils.*;

/**
 * The type of a series
 */
public enum SeriesType {
    /**
     * A 64-bit integer. If a value cannot be parsed, it will be 0
     *
     * @apiNote Series that contain longs should also contain a {@code #isNaN(int index)} method to test whether it has
     * been zeroed during parse.
     */
    INTEGER(10, INTEGER_PATTERN),
    /**
     * A 64-bit float. If a value cannot be parsed, it will be {@code NaN}.
     */
    DOUBLE(9, FLOATING_POINT_PATTERN_WITHOUT_HEX),
    STRING(0, Pattern.compile(".*")),
    /**
     * Values are either true or false. If a value cannot be parsed, it will be false.
     */
    BOOLEAN(8, BOOLEAN_PATTERN);
    private final Pattern pattern;
    /**
     * The weight represents the preferred type in auto-selecting if all the values could be either of a type.
     * <p>
     * E.g. if all the values in a column could be Long or Double, then the weight decides which is chosen
     */
    final int weight;

    SeriesType(int score, Pattern pattern) {
        this.pattern = pattern;
        this.weight = score;
    }

    /**
     * Test whether a string can be converted to this type
     *
     * @param string the string
     * @return whether this string can be cast as the given type
     */
    public boolean matches(String string) {
        return string != null && pattern.matcher(string).matches();
    }

    /**
     * Cast a string to a double
     *
     * @param value the string
     * @return a double of the string if parsable, or {@code NaN} if not
     */
    static double toDouble(String value) {
        if (FLOATING_POINT_PATTERN.matcher(value).matches()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {

            }
        }
        return Double.NaN;
    }

    /**
     * Convert a boolean to a double
     *
     * @param value the boolean value
     * @return a double representation
     */
    static double toDouble(boolean value) {
        return value ? 1. : 0.;
    }

    /**
     * Convert a long to a double
     *
     * @param value the long
     * @return a double representation of the long
     */
    static double toDouble(long value) {
        return value;
    }

    /**
     * Convert a double to a long
     *
     * @param value the double
     * @return the long (retains the integer part)
     */
    static long toLong(double value) {
        return (long) value;
    }

    /**
     * Convert a boolean to a long
     *
     * @param value the boolean
     * @return a long of the boolean
     */
    static long toLong(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Convert a string to a long
     *
     * @param value the string
     * @return the value as a long. Or 0 if not parsable.
     */
    static long toLong(String value) {
        if (INTEGER_PATTERN.matcher(value).matches()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {

            }
        }
        return 0;
    }

    static boolean toBoolean(double value) {
        return value != 0;
    }

    static boolean toBoolean(long value) {
        return value != 0;
    }

    /**
     * Only the words that are case-insensitively "true" are true. All others are false
     *
     * @param value the value to convert
     * @return the value converted to a boolean
     */
    static boolean toBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    /**
     * Convert a string to a boolean using a lenient version of truthiness (0 length strings are false, all else are true)
     *
     * @param value the value to parse
     * @return the value as a boolean
     */
    static boolean toBooleanLenient(String value) {
        return value.length() > 0;
    }

    static String toString(boolean value) {
        return Boolean.toString(value);
    }

    static String toString(double value) {
        return Double.toString(value);
    }

    static String toString(long value) {
        return Long.toString(value);
    }

}
