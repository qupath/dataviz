package net.mahdilamb.dataviz.tests;

import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.ui.Button;
import net.mahdilamb.dataviz.ui.Label;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AnimationPlayground {

    private static final class Figure extends FigureBase<Figure> {
        private final Label label = new Label("clock", Font.DEFAULT_FONT);
        private final Button button = new Button("Stop");
        String theDate;
        final long INTERVAL = 1_000;
        final Thread runner;
        Runnable action = () -> {
            theDate = new Date().toString();
            label.setText(theDate);
        };
        final List<Runnable> actions = Collections.synchronizedList(new LinkedList<>());

        public Figure() {
            addAll(button, label);

            runner = new Thread(() -> {
                while (true) {
                    synchronized (Figure.this) {
                        if (actions.isEmpty()) {
                            try {
                                Figure.this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            final long start = System.currentTimeMillis();

                            for (final Runnable action : actions) {
                                action.run();
                            }
                            final long duration = System.currentTimeMillis() - start;
                            try {
                                Thread.sleep(INTERVAL - duration);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            runner.start();
            button.setOnMouseClick(() -> {
                if (actions.isEmpty()) {
                    actions.add(action);
                    synchronized (Figure.this) {
                        Figure.this.notify();
                    }
                } else {
                    actions.remove(action);
                    label.setText("paused");
                }
            });
        }

    }

    public static void main(String[] args) {
        new Figure().show();
    }
}
