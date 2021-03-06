package net.mahdilamb.dataviz.utils;

import java.util.function.Consumer;

/**
 * An object that is one of two types.
 * <p>
 * To create a new variant, use one of the two factory methods ({@link #ofLeft(Object)} or {@link #ofRight(Object)})
 *
 * @param <LEFT>  the type of the left hand variant
 * @param <RIGHT> the type of the right hand variant
 */
public final class Variant<LEFT, RIGHT> {
    private Object value;
    private boolean isLeft;

    /**
     * Create a variant of the left type
     *
     * @param value   the value
     * @param <LEFT>  the type of the left value
     * @param <RIGHT> the type of the right value
     * @return a variant of the value
     */
    public static <LEFT, RIGHT> Variant<LEFT, RIGHT> ofLeft(LEFT value) {
        return new Variant<>(value, true);
    }

    /**
     * Create a variant of the right type
     *
     * @param value   the value
     * @param <LEFT>  the type of the left value
     * @param <RIGHT> the type of the right value
     * @return a variant of the value
     */
    public static <LEFT, RIGHT> Variant<LEFT, RIGHT> ofRight(RIGHT value) {
        return new Variant<>(value, false);
    }

    /**
     * @return whether the variant is of the left type
     */
    public boolean isLeft() {
        return isLeft;
    }

    /**
     * @return whether the variant is of the right type
     */
    public boolean isRight() {
        return !isLeft;
    }

    /**
     * Set the value to the left type
     *
     * @param value the value
     */
    public void setToLeft(final LEFT value) {
        this.value = value;
        isLeft = true;
    }

    /**
     * Set the value to the right type
     *
     * @param value the value
     */
    public void setToRight(final RIGHT value) {
        this.value = value;
        isLeft = false;
    }

    /**
     * @return the value as the left type
     * @throws ClassCastException if the value is actually the right type
     */
    @SuppressWarnings("unchecked")
    public LEFT asLeft() throws ClassCastException {
        if (!isLeft) {
            throw new ClassCastException("The variant " + value + " is currently of the right type ");
        }
        return (LEFT) value;
    }

    /**
     * @return the value as the right type
     * @throws ClassCastException if the value is actually the left type
     */
    @SuppressWarnings("unchecked")
    public RIGHT asRight() throws ClassCastException {
        if (isLeft) {
            throw new ClassCastException("The variant " + value + " is currently of the left type ");
        }
        return (RIGHT) value;
    }

    /**
     * Apply a condition depending on the type of the variant
     *
     * @param ifLeft  the function to do if left
     * @param ifRight the function to do if right
     */
    @SuppressWarnings("unchecked")
    public void accept(final Consumer<LEFT> ifLeft, final Consumer<RIGHT> ifRight) {
        if (this.isLeft) {
            ifLeft.accept((LEFT) value);
        } else {
            ifRight.accept((RIGHT) value);
        }
    }

    private Variant(final Object value, boolean isLeft) {
        this.value = value;
        this.isLeft = isLeft;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Variant {%s}", value.toString());
    }

}
