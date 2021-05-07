package net.mahdilamb.dataviz.utils.functions;

@FunctionalInterface
public interface QuadIntObjFunction<T> {

    T apply(int a, int b, int c, int d);
}
