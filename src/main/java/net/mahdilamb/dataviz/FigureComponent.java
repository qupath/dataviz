package net.mahdilamb.dataviz;

import java.io.File;

interface FigureComponent {
    Figure getFigure();

    default void show() {
        getFigure().show();
    }

    default void saveAs(final File file) {
        getFigure().saveAs(file);
    }
}
