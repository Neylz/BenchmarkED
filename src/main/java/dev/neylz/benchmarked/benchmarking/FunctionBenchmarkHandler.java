package dev.neylz.benchmarked.benchmarking;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.neylz.benchmarked.benchmarking.results.BenchmarkFunctionResult;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Iterator;


public class FunctionBenchmarkHandler {
    private final static ArrayList<BenchmarkFunction> trackedFunctions = new ArrayList<>();

    public static final SuggestionProvider<ServerCommandSource> RUNNING_FUNCTIONS_SUGGESTIONS =
        (context, builder) -> CommandSource.suggestMatching(getTrackedFunctionsNames(), builder);


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


    public static int registerFunction(String id, int lifetime) {
        if (!isFunctionTracked(id)) {
            trackedFunctions.add(new BenchmarkFunction(id, lifetime));
            return 1;
        }
        return 0;
    }

    public static int deregisterFunction(String id) {
        BenchmarkFunction fn = getFunction(id);
        if (fn == null) return 0;

        new BenchmarkFunctionResult(fn);

        return trackedFunctions.remove(fn) ? 1 : 0;

    }

    public static int deregisterAllFunctions() {
        int count = trackedFunctions.size();
        while (!trackedFunctions.isEmpty()) {
            deregisterFunction(trackedFunctions.get(0).getNamespacedPath());
        }
        return count;
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
        Iterator<BenchmarkFunction> it = trackedFunctions.iterator();
        while (it.hasNext()) {
            BenchmarkFunction fn = it.next();
            fn.decreaseLifetime();
            if (fn.isExpired()) {
                new BenchmarkFunctionResult(fn);
                it.remove();
            }
        }
    }


    @NotNull
    private static ArrayList<String> getTrackedFunctionsNames() {
        ArrayList<String> identifiers = new ArrayList<>();

        for (BenchmarkFunction fn : trackedFunctions) {
            identifiers.add(fn.getNamespacedPath());
        }

        return identifiers;
    }

}
