package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.utils.StringUtils;

import java.util.Arrays;
import java.util.Objects;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

/**
 * A basic stroke made up of a width and a color
 */
public class Stroke {

    private static final double DEFAULT_DASH_OFFSET = 0;
    private static final double DEFAULT_MITER_LIMIT = 10;
    private static final EndCap DEFAULT_END_CAP = EndCap.BUTT;
    private static final LineJoin DEFAULT_LINE_JOIN = LineJoin.MITER;
    /**
     * A default solid stroke 1 pixel width.
     */
    public static final Stroke SOLID = new Stroke(1);
    /**
     * A default dashed stroke
     */
    public static final Stroke DASHED = new Stroke(1, new double[]{8, 2});
    /**
     * A default dotted stroke
     */
    public static final Stroke DOTTED = new Stroke(1, new double[]{2, 2});
    /**
     * A default dot-dash stroke
     */
    public static final Stroke DASH_DOT = new Stroke(1, new double[]{10, 2, 2, 2});
    /**
     * An empty stroke
     */
    public static final Stroke NONE = new Stroke(0);

    /**
     * The type of the end cap
     */
    public enum EndCap {
        /**
         * Butted end cap
         */
        BUTT,
        /**
         * Rounded end cap
         */
        ROUND,
        /**
         * Squared end cap
         */
        SQUARE
    }

    /**
     * The type of the line join
     */
    public enum LineJoin {
        /**
         * Beveled line join
         */
        BEVEL,
        /**
         * Rounded line join
         */
        ROUND,
        /**
         * Mitered line join
         */
        MITER
    }

    private final double width;
    private final double[] dashArray;
    private final double dashOffset;
    private final double miterLimit;
    private final EndCap cap;
    private final LineJoin join;

    /**
     * Create a stroke
     *
     * @param width      the width of the stroke
     * @param join       the join style
     * @param cap        the cap style
     * @param miterLimit the miter limit
     * @param dashOffset the dash offset
     * @param dashArray  the dash array
     */
    public Stroke(double width, LineJoin join, EndCap cap, double miterLimit, double dashOffset, double[] dashArray) {
        this.width = width;
        this.join = join == null ? DEFAULT_LINE_JOIN : join;
        this.cap = cap == null ? DEFAULT_END_CAP : cap;
        this.miterLimit = miterLimit;
        this.dashOffset = dashOffset;
        this.dashArray = dashArray;

    }

    /**
     * Create a stroke
     *
     * @param width      the width of the stroke
     * @param join       the join style
     * @param cap        the cap style
     * @param miterLimit the miter limit
     */
    public Stroke(double width, LineJoin join, EndCap cap, double miterLimit) {
        this(width, join, cap, miterLimit, DEFAULT_DASH_OFFSET, null);
    }

    /**
     * Create a stroke
     *
     * @param width      the width of the stroke
     * @param join       the join style
     * @param cap        the cap style
     * @param dashOffset the dash offset
     * @param dashArray  the dash array
     */
    public Stroke(double width, LineJoin join, EndCap cap, double dashOffset, double[] dashArray) {
        this(width, join, cap, DEFAULT_MITER_LIMIT, dashOffset, dashArray);
    }

    /**
     * Create a stroke
     *
     * @param width     the width of the stroke
     * @param join      the join style
     * @param cap       the cap style
     * @param dashArray the dash array
     */
    public Stroke(double width, LineJoin join, EndCap cap, double[] dashArray) {
        this(width, join, cap, DEFAULT_MITER_LIMIT, DEFAULT_DASH_OFFSET, dashArray);
    }

    /**
     * Create a dashed stroke
     *
     * @param width     the width of the stroke
     * @param dashArray the dash array
     */
    public Stroke(double width, double[] dashArray) {
        this(width, DEFAULT_LINE_JOIN, DEFAULT_END_CAP, DEFAULT_MITER_LIMIT, DEFAULT_DASH_OFFSET, dashArray);
    }

    /**
     * Create a dashed stroke
     *
     * @param width      the width of the stroke
     * @param dashOffset the dash offset
     * @param dashArray  the dash array
     */
    public Stroke(double width, double dashOffset, double[] dashArray) {
        this(width, DEFAULT_LINE_JOIN, DEFAULT_END_CAP, DEFAULT_MITER_LIMIT, dashOffset, dashArray);
    }

    /**
     * Create a solid stroke
     *
     * @param width the width of the stroke
     * @param join  the join style
     * @param cap   the cap style
     */
    public Stroke(double width, LineJoin join, EndCap cap) {
        this(width, join, cap, DEFAULT_MITER_LIMIT, DEFAULT_DASH_OFFSET, null);
    }

    /**
     * Create a solid stroke
     *
     * @param width the width of the stroke
     */
    public Stroke(double width) {
        this(width, DEFAULT_LINE_JOIN, DEFAULT_END_CAP, DEFAULT_MITER_LIMIT, DEFAULT_DASH_OFFSET, null);
    }

    /**
     * @return the stroke width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the line join style
     */
    public LineJoin getLineJoin() {
        return join;
    }

    /**
     * @return the dash offset
     */
    public double getDashOffset() {
        return dashOffset;
    }

    /**
     * @return the miter limit
     */
    public double getMiterLimit() {
        return miterLimit;
    }

    /**
     * @return the end cap style
     */
    public EndCap getEndCap() {
        return cap;
    }

    /**
     * @return get the dashes. Note that this is the original dash array!
     */
    public double[] getDashes() {
        return dashArray;
    }

    /**
     * @return the number of dashes
     */
    public int numDashes() {
        return dashArray == null ? 0 : dashArray.length;
    }

    /**
     * Get the dash at a specific element
     *
     * @param el the index
     * @return the dash at the index
     * @throws NullPointerException if the underlying dash array is null
     */
    public double getDash(int el) {
        return dashArray[el];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stroke)) return false;
        Stroke stroke = (Stroke) o;
        return Double.compare(stroke.dashOffset, dashOffset) == 0 && Double.compare(stroke.miterLimit, miterLimit) == 0 && Double.compare(stroke.width, width) == 0 && cap == stroke.cap && join == stroke.join && Arrays.equals(dashArray, stroke.dashArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(dashOffset, miterLimit, cap, join, width);
        result = 31 * result + Arrays.hashCode(dashArray);
        return result;
    }

    @Override
    public String toString() {
        if (this == SOLID) {
            return "Stroke {Solid}";
        } else if (width <= 0) {
            return "Stroke {None}";
        }
        final StringBuilder out = new StringBuilder("Stroke {width: ").append(width);
        if (join != DEFAULT_LINE_JOIN) {
            out.append(", join: ").append(StringUtils.snakeToLowerCase(join.name()));
        }
        if (cap != DEFAULT_END_CAP) {
            out.append(", cap: ").append(StringUtils.snakeToLowerCase(cap.name()));
        }
        if (miterLimit != DEFAULT_MITER_LIMIT) {
            out.append(", miterLimit: ").append(miterLimit);
        }
        if (numDashes() > 0) {
            out.append(formatDashes(dashArray));
            if (dashOffset != DEFAULT_DASH_OFFSET) {
                out.append(", dashOffset: ").append(dashOffset);
            }
        }
        return out.append('}').toString();
    }

    private static String formatDashes(double[] arr) {
        if (arr == null || arr.length == 0) {
            return EMPTY_STRING;
        }
        final String str;
        if (Arrays.equals(arr, DASHED.dashArray)) {
            str = "dashed";
        } else if (Arrays.equals(arr, DOTTED.dashArray)) {
            str = "dotted";
        } else if (Arrays.equals(arr, DASH_DOT.dashArray)) {
            str = "dashDotted";
        } else {
            str = Arrays.toString(arr);
        }
        return String.format(", dash: \"%s\"", str);
    }
}
