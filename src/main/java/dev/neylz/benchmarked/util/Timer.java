package dev.neylz.benchmarked.util;

public class Timer {
    private long startTime;
    private long endTime;

    public void start() {
        this.startTime = System.nanoTime();
    }

    public void stop() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return this.endTime - this.startTime;
    }

    
}
