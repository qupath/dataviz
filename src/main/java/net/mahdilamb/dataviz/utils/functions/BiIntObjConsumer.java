package net.mahdilamb.dataviz.utils.functions;

@FunctionalInterface
public interface BiIntObjConsumer<T> {
    void accept(int a, int b, T c);
}
