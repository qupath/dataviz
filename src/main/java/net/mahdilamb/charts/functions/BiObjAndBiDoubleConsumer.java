package net.mahdilamb.charts.functions;

@FunctionalInterface
public interface BiObjAndBiDoubleConsumer<S,T> {
    void accept(S a,T b, double x, double y);
}
