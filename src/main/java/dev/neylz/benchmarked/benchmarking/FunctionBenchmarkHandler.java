package dev.neylz.benchmarked.benchmarking;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;


public abstract class FunctionBenchmarkHandler {
    private static ArrayList<BenchmarkFunction> trackedFunctions = new ArrayList<>();


    public static void startProfiler(String id) {
        BenchmarkFunction fn = getFunction(id);
        if (fn == null) return;

        fn.startProfiling();
    }

    public static void stopProfiler(String id) {
        BenchmarkFunction fn = getFunction(id);
        if (fn == null) return;

        fn.stopProfiling();
    }


    public static void registerFunction(String id) {
        if (!isFunctionTracked(id)) {
            trackedFunctions.add(new BenchmarkFunction(id));
        }
    }


    private static boolean isFunctionTracked(String id) {
        for (BenchmarkFunction fn : trackedFunctions) {
            if (fn.getNamespacedPath().equals(id)) return true;
        }
        return false;
    }

    @Nullable
    private static BenchmarkFunction getFunction(String id) {
        for (BenchmarkFunction fn : trackedFunctions) {
            if (fn.getNamespacedPath().equals(id)) return fn;
        }
        return null;
    }


    public static void postTick() {
        for (BenchmarkFunction fn : trackedFunctions) {
            fn.decreaseLifetime();
            if (fn.isExpired()) {
                trackedFunctions.remove(fn);

            }
        }
    }

}
