package net.mahdilamb.charts.graphics;

import net.mahdilamb.charts.utils.StringUtils;

import java.util.Objects;

/**
 * The font characteristics of text.
 * <p>
 * The constants are a compromise between the SVG specification
 * and the AWT constants
 */
public final class Font {

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

    private volatile double size;
    private volatile Family family;
    private volatile Style style;
    private volatile Weight weight;

    /**
     * Create a font with the given characteristics
     *
     * @param size   font size
     * @param family font family
     * @param style  font style
     */
    public Font(final double size, final Family family, final Weight weight, final Style style) {
        this.size = size;
        this.family = family;
        this.style = style;
        this.weight = weight;
    }

    /**
     * Create a font with the given characteristics and other attributes as Normal
     *
     * @param size   font size
     * @param family font family
     */
    public Font(final double size, final Family family) {
        this(size, family, Weight.NORMAL, Style.NORMAL);
    }

    /**
     * Copy constructor
     *
     * @param other font to create a copy of
     */
    public Font(final Font other) {
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
     * Modify the font size
     *
     * @param size font size
     * @return a new font with current characteristics, but modified size
     */
    public final Font modify(final double size) {
        this.size = size;
        return this;

    }

    /**
     * Modify the font family
     *
     * @param family font family
     * @return a new font with current characteristics, but modified family
     */
    public final Font modify(final Family family) {
        this.family = family;
        return this;

    }

    /**
     * Modify the font style
     *
     * @param style font style
     * @return a new font with current characteristics, but modified style
     */
    public final Font modify(final Style style) {
        this.style = style;
        return this;

    }

    /**
     * Modify the font weight
     *
     * @param weight font weight
     * @return a new font with current characteristics, but modified weight
     */
    public final Font modify(final Weight weight) {

        this.weight = weight;
        return this;

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