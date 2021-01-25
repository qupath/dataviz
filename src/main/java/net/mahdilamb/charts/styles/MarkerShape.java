package net.mahdilamb.charts.styles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A marker for use in plots
 */
public enum MarkerShape {
    /**
     * A pixel marker '.'
     */
    POINT('.'),
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
    HEXAGON('h'),
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

}
