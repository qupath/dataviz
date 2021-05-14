package net.mahdilamb.dataviz.utils.functions;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility methods for working with Java functional interfaces
 */
public final class Runnables {
    private Runnables() {

    }

    /**
     * An empty runnable
     */
    public static final Runnable EMPTY_RUNNABLE = () -> {

    };

    public static final class PausableRunnable implements Runnable {
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final AtomicBoolean paused;
        private final Object pauseLock = new Object();
        final Runnable task;

        public PausableRunnable(Runnable task, boolean startPaused) {
            this.task = task;
            this.paused = new AtomicBoolean(startPaused);
        }

        @Override
        public final void run() {
            while (running.get()) {
                synchronized (pauseLock) {
                    if (!running.get()) {
                        break;
                    }
                    if (paused.get()) {
                        try {
                            synchronized (pauseLock) {
                                pauseLock.wait();
                            }
                        } catch (InterruptedException ex) {
                            break;
                        }
                        if (!running.get()) {
                            break;
                        }
                    }
                }
                task.run();

            }
        }

        public final void stop() {
            running.set(false);
            resume();
        }

        public final void pause() {
            paused.set(true);
        }

        public final void resume() {
            synchronized (pauseLock) {
                paused.set(false);
                pauseLock.notifyAll();
            }
        }
    }

}
