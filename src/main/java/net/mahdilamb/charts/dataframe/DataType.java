package net.mahdilamb.charts.dataframe;

import java.util.regex.Pattern;

import static net.mahdilamb.charts.utils.StringUtils.*;

/**
 * Datatype to be used by dataseries
 */
public enum DataType {
    /**
     * A 64-bit integer. If a value cannot be parsed, it will be 0, though the index will be stored as NaN. This
     * can be retrieved using the {@link NumericSeries#isNaN} method
     *
     * @apiNote Series that contain longs should also contain a {@code #isNaN(int index)} method to test whether it has
     * been zeroed during parse.
     */
    LONG(Long.class, 10, LONG_PATTERN),
    /**
     * A 64-bit float. If a value cannot be parsed, it will be {@code NaN}.
     */
    DOUBLE(Double.class, 9, FLOATING_POINT_PATTERN_WITHOUT_HEX),
    /**
     * Values are either true or false. If a value cannot be parsed, it will be false.
     */
    BOOLEAN(Boolean.class, 8, BOOLEAN_PATTERN),
    /**
     * String type
     */
    STRING(String.class, 0, Pattern.compile("(?:.*)"));

    private final Class<?> referenceType;

    /**
     * Test if a data type is numeric
     *
     * @param type the data type
     * @return whether the type is numeric
     */
    public static boolean isNumeric(DataType type) {
        return type == LONG || type == DOUBLE;
    }

    /**
     * The score represents the preferred type in auto-selecting if all the values could be either of a type.
     * <p>
     * E.g. if all the values in a column could be Long or Double, then the weight decides which is chosen
     */
    protected final int score;
    private final Pattern matcher;

    /**
     * Test whether a string can be converted to this type
     *
     * @param string the string
     * @return whether this string can be cast as the given type
     */
    public boolean matches(String string) {
        return string != null && matcher.matcher(string).matches();
    }

    /**
     * Cast a string to a double
     *
     * @param value the string
     * @return a double of the string if parsable, or {@code NaN} if not
     */
    public static double toDouble(String value) {
        if (FLOATING_POINT_PATTERN.matcher(value).matches()) {
            try {
                return java.lang.Double.parseDouble(value);
            } catch (NumberFormatException ignored) {

            }
        }
        return java.lang.Double.NaN;
    }

    /**
     * Convert a boolean to a double
     *
     * @param value the boolean value
     * @return a double representation
     */
    public static double toDouble(boolean value) {
        return value ? 1. : 0.;
    }

    /**
     * Convert a long to a double
     *
     * @param value the long
     * @return a double representation of the long
     */
    public static double toDouble(long value) {
        return value;
    }

    /**
     * Convert a double to a long
     *
     * @param value the double
     * @return the long (retains the integer part)
     */
    public static long toLong(double value) {
        return (long) value;
    }

    /**
     * Convert a boolean to a long
     *
     * @param value the boolean
     * @return a long of the boolean
     */
    public static long toLong(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Convert a string to a long
     *
     * @param value the string
     * @return the value as a long. Or 0 if not parsable.
     */
    public static long toLong(String value) {
        if (LONG_PATTERN.matcher(value).matches()) {
            try {
                return java.lang.Long.parseLong(value);
            } catch (NumberFormatException ignored) {

            }
        }
        return 0;
    }

    /**
     * Convert a boolean to a double
     *
     * @param value the value to convert
     * @return the double as a boolean
     */
    public static boolean toBoolean(double value) {
        return value != 0;
    }

    /**
     * Convert a long to a boolean
     *
     * @param value the value
     * @return the value as a boolean
     */
    public static boolean toBoolean(long value) {
        return value != 0;
    }

    /**
     * Only the words that are case-insensitively "true" are true. All others are false
     *
     * @param value the value to convert
     * @return the value converted to a boolean
     */
    public static boolean toBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    /**
     * Convert a string to a boolean using a lenient version of truthiness (0 length strings are false, all else are true)
     *
     * @param value the value to parse
     * @return the value as a boolean
     */
    public static boolean toBooleanLenient(String value) {
        return value.length() > 0;
    }

    /**
     * Get a string representation of a boolean
     *
     * @param value the boolean
     * @return the boolean as a string
     */
    public static String toString(boolean value) {
        return Boolean.toString(value);
    }

    /**
     * Get a string representation of a double
     *
     * @param value the double
     * @return the double as a string
     */
    public static String toString(double value) {
        return Double.toString(value);
    }

    /**
     * Get a string representation of a long
     *
     * @param value the long
     * @return the long as a string
     */
    public static String toString(long value) {
        return Long.toString(value);
    }

    DataType(Class<?> referenceType, int score, Pattern matcher) {
        this.referenceType = referenceType;
        this.score = score;
        this.matcher = matcher;
    }


}
