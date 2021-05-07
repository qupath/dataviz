package net.mahdilamb.dataviz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlotOptions {
    String name();

    boolean supportsManualZoom();

    boolean supportsZoom();

    boolean supportsPan();

    boolean supportsPolygonSelection();

    boolean supportsZoomByWheel();
}
