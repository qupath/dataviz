package net.mahdilamb.dataviz.utils.functions;

@FunctionalInterface
public interface QuadIntObjConsumer<T> {

    void accept(int a, int b, int c, int d, T e);
}
