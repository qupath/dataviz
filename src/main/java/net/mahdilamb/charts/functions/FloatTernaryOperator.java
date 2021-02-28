package net.mahdilamb.charts.functions;

/**
 * Functional interface for a ternary operator
 */
@FunctionalInterface
public interface FloatTernaryOperator {
    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param mid   the middle operand
     * @param right the right operand
     * @return the operator result
     */
    float applyAsFloat(float left, float mid, float right);
}
