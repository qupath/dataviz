package net.mahdilamb.dataviz.utils;

import java.util.function.Consumer;

/**
 * An object that is one of two types, where both types are related.
 * <p>
 * To create a new variant, use one of the two factory methods ({@link #ofA(Object)} or {@link #ofB(Object)}).
 * <p>
 * Alternatively, use the {@link #of} methods, which are recommended for use with the{@code var} keyword in Java10 and
 * up.
 * <p>
 * Setters are not thread-safe so in multi-threaded applications should be synchronized externally. The value can
 * be {@code null}.
 *
 * @param <A> the type of the A hand variant
 * @param <B> the type of the B hand variant
 */
public class SubVariant<A extends C, B extends C, C> {

    /**
     * Create a variant of the A type
     *
     * @param value the value
     * @param <A>   the type of the A value
     * @param <B>   the type of the B value
     * @return a variant of the value
     */
    public static <A extends C, B extends C, C> SubVariant<A, B, C> ofA(A value) {
        return new SubVariant<>(value, true);
    }

    /**
     * Create a variant of the B type
     *
     * @param value the value
     * @param <A>   the type of the A value
     * @param <B>   the type of the B value
     * @return a variant of the value
     */
    public static <A extends C, B extends C, C> SubVariant<A, B, C> ofB(B value) {
        return new SubVariant<>(value, false);
    }

    /**
     * Create a variant initialized to the A type (useful for Java 10{@literal +} and up as it is works
     * with {@code var})
     *
     * @param value  the value
     * @param bClass the class of the B type
     * @param <A>    the type of the A value
     * @param <B>    the type of the B value
     * @return a variant of the value
     */
    public static <A extends C, B extends C, C> SubVariant<A, B, C> of(A value, Class<? extends B> bClass) {
        return new SubVariant<>(value, true);
    }

    /**
     * Create a variant initialized to the B type (useful for Java 10{@literal +} and up as it is works
     * with {@code var})
     *
     * @param aClass the class of the A type
     * @param value  the value
     * @param <A>    the type of the A value
     * @param <B>    the type of the B value
     * @return a variant of the value
     */
    public static <A extends C, B extends C, C> SubVariant<A, B, C> of(Class<? extends A> aClass, B value) {
        return new SubVariant<>(value, false);
    }

    private C value;
    private boolean isA;

    private SubVariant(final C value, boolean isA) {
        this.value = value;
        this.isA = isA;
    }

    /**
     * @return whether the variant is of the A type
     */
    public boolean isA() {
        return isA;
    }

    /**
     * @return whether the variant is of the B type
     */
    public boolean isB() {
        return !isA;
    }

    /**
     * Set the value to the A type
     *
     * @param value the value
     */
    public void setToA(final A value) {
        this.value = value;
        isA = true;
    }

    /**
     * Set the value to the B type
     *
     * @param value the value
     */
    public void setToB(final B value) {
        this.value = value;
        isA = false;
    }

    /**
     * @return the value of the variant
     */
    public C get() {
        return value;
    }

    /**
     * @return the value as the A type
     * @throws ClassCastException if the value is actually the B type
     */
    @SuppressWarnings("unchecked")
    public A asA() throws ClassCastException {
        if (!isA) {
            throw new ClassCastException("The variant \"" + value + "\" is currently of the B type.");
        }
        return (A) value;
    }

    /**
     * @return the value as the B type
     * @throws ClassCastException if the value is actually the A type
     */
    @SuppressWarnings("unchecked")
    public B asB() throws ClassCastException {
        if (isA) {
            throw new ClassCastException("The variant \"" + value + "\" is currently of the A type.");
        }
        return (B) value;
    }

    /**
     * Apply a condition depending on the type of the variant
     *
     * @param ifA the function to do if A
     * @param ifB the function to do if B
     */
    @SuppressWarnings("unchecked")
    public void accept(final Consumer<A> ifA, final Consumer<B> ifB) {
        if (this.isA) {
            ifA.accept((A) value);
        } else {
            ifB.accept((B) value);
        }
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Variant {%s}", value);
    }

}
