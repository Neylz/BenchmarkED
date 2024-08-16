package dev.neylz.benchmarked.benchmarking;

import com.mojang.brigadier.suggestion.SuggestionProvider;
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
        if (isFunctionTracked(id)) {
            trackedFunctions.remove(getFunction(id));
            return 1;
        }
        return 0;
    }

    public static int deregisterAllFunctions() {
        int count = trackedFunctions.size();
        trackedFunctions.clear();
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
        for (Iterator<BenchmarkFunction> iterator = trackedFunctions.iterator(); iterator.hasNext(); ) {
            boolean isExpired = iterator.next().isExpired();
            if ( isExpired ) {
                iterator.remove();
            }
        }
    }


    @NotNull
    public static ArrayList<String> getTrackedFunctionsNames() {
        ArrayList<String> identifiers = new ArrayList<>();

        for (BenchmarkFunction fn : trackedFunctions) {
            identifiers.add(fn.getNamespacedPath());
        }

        return identifiers;
    }

}
