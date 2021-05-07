import net.mahdilamb.dataviz.figure.FigureViewer;
import net.mahdilamb.dataviz.io.FigureExporter;

module net.mahdilamb.dataviz {
    uses FigureExporter;
    uses FigureViewer;
    requires java.desktop;
    requires net.mahdilamb.colormap;
    requires net.mahdilamb.dataframe;
    exports net.mahdilamb.dataviz;
    exports net.mahdilamb.dataviz.plots;
    exports net.mahdilamb.dataviz.data;
    exports net.mahdilamb.dataviz.layouts;
    exports net.mahdilamb.dataviz.graphics;
    exports net.mahdilamb.dataviz.graphics.shapes;
    exports net.mahdilamb.dataviz.figure;
    exports net.mahdilamb.dataviz.ui;
    exports net.mahdilamb.dataviz.utils;
    exports net.mahdilamb.dataviz.utils.rtree;
    exports net.mahdilamb.dataviz.io;
    exports net.mahdilamb.dataviz.swing;
    exports net.mahdilamb.dataviz.utils.functions;
    provides FigureViewer
            with FigureViewer.SwingViewer;
    provides FigureExporter
            with FigureExporter.SVGExporter,
                    FigureExporter.SVGZExporter,
                    FigureExporter.PNGExporter,
                    FigureExporter.JPEGExporter,
                    FigureExporter.TIFFExporter,
                    FigureExporter.BMPExporter;

}