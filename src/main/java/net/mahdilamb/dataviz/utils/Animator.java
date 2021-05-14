package net.mahdilamb.dataviz.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.dataviz.utils.functions.Runnables.EMPTY_RUNNABLE;

/**
 * Main animation class
 */
//todo
public final class Animator {
    /**
     * An instance of an easing animator that runs at 60FPS
     */
    public static final Animator ANIMATOR_60 = new Animator(60);
    public static final Animator ANIMATOR_120 = new Animator(120);

    private final int FPS;
    /**
     * default duration in seconds
     */
    private float duration;
    private DoubleUnaryOperator easingFunction = Interpolations::linear;
    private ScheduledExecutorService timer;
    private final AtomicInteger frame = new AtomicInteger(0);
    private Runnable onComplete = EMPTY_RUNNABLE;

    /**
     * @param FPS the number of target frames in second for this animator
     */
    public Animator(final int FPS) {
        this(FPS, .1f);
    }

    /**
     * @param FPS      the number of target frames in second for this animator
     * @param duration the default duration for the animation
     */
    public Animator(final int FPS, final float duration) {
        this.FPS = FPS;
        this.duration = duration;
    }

    /**
     * Run and animation
     *
     * @param animation animation to play
     */
    public void animate(final DoubleConsumer animation) {
        //stop any previous animation
        stop();
        final int nFrames = Math.round(getNFrames());
        final long animationRate = Math.round(nFrames * FPS);

        if (nFrames <= 1 || animationRate <= 0) {
            animation.accept(1);
            return;
        }
        frame.set(0);
        timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(() -> {
            animation.accept(easingFunction.applyAsDouble(((float) frame.getAndIncrement()) / (nFrames - 1)));
            if (frame.get() >= nFrames) {
                stop();
            }
        }, 0L, animationRate, TimeUnit.NANOSECONDS);
    }

    public Animator setOnComplete(final Runnable onComplete) {
        this.onComplete = onComplete == null ? EMPTY_RUNNABLE : onComplete;
        return this;
    }

    /**
     * set the easing function
     *
     * @param easingFunction the interpolation to use for the animation
     */
    public Animator setEasingFunction(DoubleUnaryOperator easingFunction) {
        this.easingFunction = easingFunction == null ? Interpolations::linear : easingFunction;
        return this;
    }

    /**
     * stop the animation
     */
    public synchronized void stop() {
        if (!isAnimating()) {
            return;
        }
        timer.shutdown();
        try {
            timer.awaitTermination(1, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            onComplete.run();
            timer = null;
            onComplete = EMPTY_RUNNABLE;
        }

    }

    /**
     * @return if there is an animation running
     */
    public boolean isAnimating() {
        return timer != null;
    }

    /**
     * @param seconds the number of seconds to run the animation for
     */
    public Animator setDuration(float seconds) {
        this.duration = seconds;
        return this;
    }

    /**
     * @return the number of frames to do based on animation settings
     */
    private float getNFrames() {
        return duration * FPS;
    }

}
