package net.mahdilamb.dataviz.utils.functions;

@FunctionalInterface
public interface BiDoubleObjConsumer<T> {
    void accept(double a, double b, T c);
}
