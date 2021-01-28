package net.mahdilamb.charts.datasets;

import static net.mahdilamb.charts.utils.StringUtils.*;

/**
 * Datatype to be used by dataseries
 */
public abstract class DataType {
    /**
     * A 64-bit integer. If a value cannot be parsed, it will be 0, though the index will be stored as NaN. This
     * can be retrieved using the {@link LongSeries#isNaN} method
     *
     * @apiNote Series that contain longs should also contain a {@code #isNaN(int index)} method to test whether it has
     * been zeroed during parse.
     */
    public static final Numeric LONG = new Long();
    /**
     * A 64-bit float. If a value cannot be parsed, it will be {@code NaN}.
     */
    public static final Numeric DOUBLE = new Double();
    /**
     * String type
     */
    public static final DataType STRING = new StringImpl();
    /**
     * Values are either true or false. If a value cannot be parsed, it will be false.
     */
    public static final DataType BOOLEAN = new BooleanImpl();

    /**
     * @return an array of the values. Creates a new array each time it is called.
     */
    public static DataType[] values() {
        return new DataType[]{LONG, DOUBLE, STRING, BOOLEAN};
    }

    /**
     * The score represents the preferred type in auto-selecting if all the values could be either of a type.
     * <p>
     * E.g. if all the values in a column could be Long or Double, then the weight decides which is chosen
     */
    protected final int score;

    /**
     * Test whether a string can be converted to this type
     *
     * @param string the string
     * @return whether this string can be cast as the given type
     */
    public abstract boolean matches(String string);

    /**
     * A series that contains number. Used to link long and doubles
     */
    public static abstract class Numeric extends DataType {
        private Numeric(int score) {
            super(score);
        }
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
        if (INTEGER_PATTERN.matcher(value).matches()) {
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
        return java.lang.Double.toString(value);
    }

    /**
     * Get a string representation of a long
     *
     * @param value the long
     * @return the long as a string
     */
    public static String toString(long value) {
        return java.lang.Long.toString(value);
    }

    @Override
    public abstract String toString();

    private DataType(int score) {
        this.score = score;
    }

    private static final class Long extends Numeric {

        private Long() {
            super(10);
        }

        @Override
        public boolean matches(String string) {
            return INTEGER_PATTERN.matcher(string).matches();
        }

        @Override
        public String toString() {
            return "long";
        }
    }

    private static final class Double extends Numeric {

        private Double() {
            super(9);
        }

        @Override
        public boolean matches(String string) {
            return FLOATING_POINT_PATTERN_WITHOUT_HEX.matcher(string).matches();
        }

        @Override
        public String toString() {
            return "double";
        }
    }

    private static final class BooleanImpl extends DataType {
        private BooleanImpl() {
            super(8);
        }

        @Override
        public boolean matches(String string) {
            return BOOLEAN_PATTERN.matcher(string).matches();
        }

        @Override
        public String toString() {
            return "boolean";
        }
    }

    private static final class StringImpl extends DataType {
        private StringImpl() {
            super(0);
        }

        @Override
        public boolean matches(String string) {
            return string != null;
        }

        @Override
        public String toString() {
            return "string";
        }
    }

}
