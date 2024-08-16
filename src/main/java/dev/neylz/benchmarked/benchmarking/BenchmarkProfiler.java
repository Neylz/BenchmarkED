package dev.neylz.benchmarked.benchmarking;

import dev.neylz.benchmarked.util.Timer;

public class BenchmarkProfiler {

    private final Timer timer = new Timer();
    private int stack = 0;

    public void start() {
        if (stack == 0) timer.start();
        stack ++;
    }

    public void stop() {
        if (stack == 1) timer.stop();
        if (stack >= 1) stack--;
    }

    public float getTime() {
        return timer.getElapsedTimeMillis();
    }

    public boolean isProcessFinished() {
        return (stack == 0);
    }


}
