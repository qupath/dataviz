package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.utils.StringParseException;

import static net.mahdilamb.dataframe.utils.StringUtils.compareToIgnoreCase;
import static net.mahdilamb.dataframe.utils.StringUtils.mismatchIgnoreCase;

/**
 * Enum for markers where joining lines are supported
 */
public enum ScatterMode {
    /**
     * Draw only markers
     */
    MARKER_ONLY,
    /**
     * Draw markers and lines
     */
    MARKER_AND_LINE,
    /**
     * Draw only lines
     */
    LINE_ONLY;
    private static final String MARKER = "markers";
    private static final String LINE = "lines";

    /**
     * Get the enum from a string. Whitespace is ignored and this is case-insensitive.
     * Must be a combination of "markers" and "lines", optionally joined with a "+".
     *
     * @param mode the string to parse
     * @return the associated mode
     * @throws StringParseException if the string cannot be parsed
     */
    public static ScatterMode from(final String mode) throws StringParseException {
        //TODO spacing
        boolean useMarker = false;
        boolean useLine = false;
        int i = 0;
        while (i < mode.length()) {
            final char c = mode.charAt(i);
            switch (c) {
                case ' ':
                case '+':
                    break;
                case 'm':
                case 'M':
                    if (!compareToIgnoreCase(MARKER, mode, i)) {
                        throw new StringParseException(mode, mismatchIgnoreCase(MARKER, mode, i));
                    }
                    useMarker = true;
                    i += MARKER.length();
                    continue;
                case 'L':
                case 'l':
                    if (!compareToIgnoreCase(LINE, mode, i)) {
                        throw new StringParseException(mode, mismatchIgnoreCase(LINE, mode, i));
                    }
                    useLine = true;
                    i += LINE.length();
                    continue;
                default:
                    throw new StringParseException(mode, i + 1);
            }
            ++i;
        }
        return useLine && useMarker ? MARKER_AND_LINE : (useLine ? LINE_ONLY : MARKER_ONLY);
    }
}
