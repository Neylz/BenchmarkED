package dev.neylz.benchmarked.benchmarking;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.sun.jdi.ArrayReference;
import dev.neylz.benchmarked.access.IdentifierAccess;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;


public class BenchmarkFunction {

    private final String id;
    private int lifeTime;
    private final ArrayList<Float> times = new ArrayList<>();
    private final BenchmarkProfiler profiler = new BenchmarkProfiler();


    public BenchmarkFunction(String id, int lifeTime) {
        this.id = id;
        this.lifeTime = lifeTime;
    }


    public String getNamespacedPath() {
        return id;
    }

    public void startProfiling() {
        profiler.start();
    }

    public void stopProfiling() {
        profiler.stop();
        if (profiler.isProcessFinished()) {
            times.add(profiler.getTime());
        }
    }

    public void decreaseLifetime() {
        if (lifeTime > 0) lifeTime --;
    }

    public boolean isExpired() {
        return (lifeTime == 0); // -1 is reserved by functions without specified lifespan
    }


    public ArrayList<Float> getTimes() {
        return times;
    }



//    private class Timer extends dev.neylz.benchmarked.util.Timer {
//        public Timer() {
//            super();
//        }
//
//        public void start() {
//            control.enqueueAction((context, frame) -> {
//                super.start();
//            });
//        }
//
//        public void stop() {
//            control.enqueueAction((context, frame) -> {
//                super.stop();
//            });
//        }
//
//        public void registerLastTime() {
//            control.enqueueAction((context, frame) -> {
//                times.add(getElapsedTimeMillis());
//            });
//        }
//    }
}
