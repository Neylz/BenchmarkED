package dev.neylz.benchmarked.benchmarking.results;

import dev.neylz.benchmarked.benchmarking.BenchmarkFunction;

import java.util.ArrayList;

public class BenchmarkFunctionResult {

    private final String functionName;
    private final String benchmarkFileName;

    private final int calls;
    private final ArrayList<Float> times;

    private BenchmarkFunctionResult(
            String functionName,
            ArrayList<Float> times
    ) {
        this.functionName = functionName;

        this.calls = times.size();
        this.times = times;

//        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        this.benchmarkFileName = String.format("%s - %d.bed", functionName, StoredBenchmarkResults.getFunctionsCount(functionName) + 1);

        StoredBenchmarkResults.storeResult(this);
    }


    public BenchmarkFunctionResult(
            BenchmarkFunction function
    ) {
        this(function.getNamespacedPath(), function.getTimes());
    }


    public String getNamespacedPath() {
        return functionName;
    }

    public String getFileName() {
        return benchmarkFileName;
    }


}
