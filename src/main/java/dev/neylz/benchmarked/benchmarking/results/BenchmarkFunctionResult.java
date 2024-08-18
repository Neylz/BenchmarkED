package dev.neylz.benchmarked.benchmarking.results;

import dev.neylz.benchmarked.benchmarking.BenchmarkFunction;
import dev.neylz.benchmarked.util.IntCharEncoder;
import net.minecraft.server.MinecraftServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BenchmarkFunctionResult {

    private final String functionName;
    private final String benchmarkFileName;

    private final Date timeStamp;

    private final int calls;
    private final ArrayList<Float> times;

    private BenchmarkFunctionResult(
            String functionName,
            ArrayList<Float> times
    ) {
        this.functionName = functionName;

        this.calls = times.size();
        this.times = times;

        this.timeStamp = new Date();

        this.benchmarkFileName = String.format(
                "%s - %s - %d.bed",
                functionName,
                new SimpleDateFormat("yyyy-dd-MM_HH-mm-ss").format(timeStamp),
                StoredBenchmarkResults.getFunctionsCount(functionName) + 1
        );

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
