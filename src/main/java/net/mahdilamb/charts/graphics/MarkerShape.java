package net.mahdilamb.charts.graphics;

import net.mahdilamb.dataframe.utils.PrimitiveIterators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A marker for use in plots
 */
//todo default order: https://github.com/mwaskom/seaborn/blob/6183f1e3b0fc2b56ef441acfe1f9f63a13ae4beb/seaborn/_core.py#L1657
public enum MarkerShape {
    /**
     * A point marker
     */
    POINT('.'),
    /**
     * A pixel marker ','
     */
    PIXEL(','),
    /**
     * Circle marker 'o'
     */
    CIRCLE('o'),
    /**
     * Square marker 's'
     */
    SQUARE('s'),
    /**
     * Filled plus marker 'P'
     */
    FILLED_PLUS('P'),
    /**
     * Star marker '*'
     */
    STAR('*'),
    /**
     * Pentagon marker 'p'
     */
    PENTAGON('p'),
    /**
     * Hexagonal marker 'h'
     */
    HEXAGON1('h'),
    /**
     * Hexagonal marker 'h'
     */
    HEXAGON2('H'),
    /**
     * Diamond marker. 'D'
     */
    DIAMOND('D'),
    /**
     * Diamond marker with width 1/2 the size of the height. 'd'
     */
    THIN_DIAMOND('d'),
    /**
     * Left triangle marker '<'
     */
    TRIANGLE_LEFT('<'),
    /**
     * Right triangle marker '>'
     */
    TRIANGLE_RIGHT('>'),
    /**
     * Up triangle marker '^'
     */
    TRIANGLE_UP('^'),
    /**
     * Down triangle marker 'v'
     */
    TRIANGLE_DOWN('v'),
    /**
     * Outline left triangle marker '1'
     */
    TRI_LEFT('1'),
    /**
     * Outline right triangle marker '2'
     */
    TRI_RIGHT('2'),
    /**
     * Outline up triangle marker '3'
     */
    TRI_UP('3'),
    /**
     * Outline down triangle marker '4'
     */
    TRI_DOWN('4'),
    /**
     * Octagonal marker '8'
     */
    OCTAGON('8'),
    /**
     * Outline plus marker '+'
     */
    PLUS('+'),
    /**
     * Outline X marker 'x'
     */
    X('x'),
    /**
     * Filled X marker 'X'
     */
    FILLED_X('X'),
    /**
     * Vertical line marker '|'
     */
    VERTICAL_LINE('|'),
    /**
     * Horizontal line marker '_'
     */
    HORIZONTAL_LINE('_');

    private static final Map<Character, MarkerShape> store = new HashMap<>();
    private static final char[] ordered = {'o', 'X', 'D', 'P', 's', '*', '^', '+', 'v', 'p', 'h', '8'};

    static {
        for (final MarkerShape m : MarkerShape.class.getEnumConstants()) {
            store.put(m.shortcut, m);
        }
    }

    private final char shortcut;

    MarkerShape(char shortcut) {
        this.shortcut = shortcut;
    }

    /**
     * Get a character from the marker
     *
     * @param character the character representation of a marker
     * @return the marker
     */
    public static MarkerShape get(final char character) {
        return store.get(character);
    }

    /**
     * Get a marker from a character sequence (the first matching marker)
     *
     * @param string the character sequence
     * @return the marker
     */
    public static MarkerShape get(final CharSequence string) {
        MarkerShape out = null;
        int i = 0;
        while (out == null && i < string.length()) {
            out = get(string.charAt(i++));
        }
        return out;
    }

    /**
     * Get the markers from each character in a string
     *
     * @param out    the output list
     * @param string the string
     * @return the output list
     */
    public static List<MarkerShape> get(final List<MarkerShape> out, final CharSequence string) {
        int i = 0;
        while (i < string.length()) {
            final MarkerShape m = get(string.charAt(i++));
            if (m != null) {
                out.add(m);
            }
        }
        return out;
    }

    /**
     * Get a marker from the ordered list
     *
     * @param order the order
     * @return the marker at the position
     */
    public static MarkerShape get(int order) {
        return get(ordered[order]);
    }

    /**
     * Get an iterable over the first n ordered markers
     *
     * @param numValues the number of values
     * @return an iterable over the default order of markers
     * @throws ArrayIndexOutOfBoundsException if the number is greater than 12
     */
    public static Iterable<Character> orderedValues(int numValues) {
        return () -> new PrimitiveIterators.OfCharacter() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numValues;
            }

            @Override
            public char nextChar() {
                return ordered[i++];
            }
        };
    }

}
