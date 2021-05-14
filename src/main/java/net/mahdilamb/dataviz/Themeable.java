package net.mahdilamb.dataviz;

/**
 * A themeable element
 *
 * @param <T> the concrete type of the implementing class
 */
interface Themeable<T> {
    /**
     * Apply the theme to this
     *
     * @param theme the theme to apply
     * @return this object
     */
    T apply(Theme theme);
}
