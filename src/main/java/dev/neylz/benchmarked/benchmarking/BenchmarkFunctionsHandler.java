package dev.neylz.benchmarked.benchmarking;

import java.util.ArrayList;

public class BenchmarkFunctionsHandler {
    private static ArrayList<BenchmarkFunction> functions = new ArrayList<>();


    public static void registerBenchmark(BenchmarkFunction function) {
        functions.add(function);
    }


    public static void runTick() {
        for (BenchmarkFunction function : functions) {
            function.queueBenchmarkFunction();
            function.debugMessage("aaa");
        }

        removeFinishedFunctions();
    }


    private static void removeFinishedFunctions() {
        functions.removeIf(function -> function.getIterations() == 0);
    }

}
