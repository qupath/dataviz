package net.mahdilamb.dataviz.utils.functions;

@FunctionalInterface
public interface BiIntFunction<T> {
    T apply(int a, int b);
}
