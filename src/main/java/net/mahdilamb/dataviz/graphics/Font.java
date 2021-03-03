package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.utils.StringUtils;

import java.util.Objects;

/**
 * The font characteristics of text.
 * <p>
 * The constants are a compromise between the SVG specification
 * and the AWT constants
 */
public class Font {

    /**
     * A default 12-point san-serif font.
     */
    public static final Font DEFAULT_FONT = new Font(Family.SANS_SERIF, 12, Weight.NORMAL, Style.NORMAL);
    /**
     * A default 16-point sans-serif title font.
     */
    public static final Font DEFAULT_TITLE_FONT = new Font(Family.SANS_SERIF, 16, Weight.NORMAL, Style.NORMAL);

    /**
     * The font family
     */
    public enum Family {
        SERIF,
        SANS_SERIF,
        MONOSPACE
    }

    /**
     * The font style.
     */
    public enum Style {
        NORMAL,
        ITALIC
    }

    /**
     * The font weight.
     */
    public enum Weight {
        NORMAL,
        BOLD
    }

    private final double size;
    private final Family family;
    private final Style style;
    private final Weight weight;

    /**
     * Create a font with the given characteristics
     *
     * @param family font family
     * @param size   font size
     * @param style  font style
     */
    public Font(final Family family, final double size, final Weight weight, final Style style) {
        this.size = size;
        this.family = family;
        this.style = style;
        this.weight = weight;
    }

    /**
     * Create a font with the given characteristics and other attributes as Normal
     *
     * @param family font family
     * @param size   font size
     */
    public Font(final Family family, final double size) {
        this(family, size, Weight.NORMAL, Style.NORMAL);
    }

    /**
     * Create a sans serif font
     *
     * @param size the size of the font
     */
    public Font(final double size) {
        this(Family.SANS_SERIF, size, Weight.NORMAL, Style.NORMAL);
    }

    /**
     * Copy constructor
     *
     * @param other font to create a copy of
     */
    private Font(final Font other) {
        this.size = other.size;
        this.family = other.family;
        this.style = other.style;
        this.weight = other.weight;
    }

    /**
     * @return the font size
     */
    public final double getSize() {
        return size;
    }

    /**
     * @return the font family
     */
    public final Family getFamily() {
        return family;
    }

    /**
     * @return the font style
     */
    public final Style getStyle() {
        return style;
    }

    /**
     * @return the font weight
     */
    public final Weight getWeight() {
        return weight;
    }

    /**
     * @return a copy of this font
     */
    public Font copy() {
        return new Font(this);
    }

    /**
     * Modify the font size
     *
     * @param size font size
     * @return a new font with current characteristics, but modified size
     */
    public Font derive(final double size) {
        return new Font(family, size, weight, style);
    }

    /**
     * Modify the font family
     *
     * @param family font family
     * @return a new font with current characteristics, but modified family
     */
    public Font derive(final Family family) {
        return new Font(family, size, weight, style);
    }

    /**
     * Modify the font style
     *
     * @param style font style
     * @return a new font with current characteristics, but modified style
     */
    public Font derive(final Style style) {
        return new Font(family, size, weight, style);
    }

    /**
     * Modify the font weight
     *
     * @param weight font weight
     * @return a new font with current characteristics, but modified weight
     */
    public Font derive(final Weight weight) {
        return new Font(family, size, weight, style);
    }


    @Override
    public final String toString() {

        return String.format("Font {family: %s, size: %s%s%s}", family, size,
                weight == Weight.NORMAL ? StringUtils.EMPTY_STRING : String.format(", weight: %s", weight),
                style == Style.NORMAL ? StringUtils.EMPTY_STRING : String.format(", style: %s", style)
        );
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Font)) return false;
        final Font font = (Font) o;
        return family == font.family && style == font.style && weight == font.weight && Double.compare(font.size, size) == 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(size, family, style, weight);
    }

}
