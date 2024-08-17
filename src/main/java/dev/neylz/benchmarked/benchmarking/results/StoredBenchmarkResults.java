package dev.neylz.benchmarked.benchmarking.results;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.util.CommandArgumentsGetter;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class StoredBenchmarkResults {
    private static final HashMap<String, ArrayList<BenchmarkFunctionResult>> storedResults = new HashMap<>();

    public static final SuggestionProvider<ServerCommandSource> RESULTS_FUNCTIONS_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(storedResults.keySet(), builder);

    public static final SuggestionProvider<ServerCommandSource> RESULTS_FILES_SUGGESTIONS =
            (context, builder) -> {
                Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = CommandArgumentsGetter.getFunction(context, "function");
                if (functions == null || functions.getSecond().isEmpty()) return CommandSource.suggestMatching(new String[0], builder);

                return CommandSource.suggestMatching(
                    functions.getSecond()
                        .stream()
                        .flatMap(
                            function -> getResultsFilesNames(((IdentifierAccess) (Object) function.id()).benchmarked$getNamespacedPath()).stream()
                        )
                        .toArray(String[]::new),
                    builder
                );


            };




    protected static void storeResult(BenchmarkFunctionResult function) {
        if (!storedResults.containsKey(function.getNamespacedPath())) {
            storedResults.put(function.getNamespacedPath(), new ArrayList<>());
        }

        storedResults.get(function.getNamespacedPath()).add(function);
    }



    public static int getFunctionsCount(String id) {
        ArrayList<BenchmarkFunctionResult> results = storedResults.get(id);
        return results == null ? 0 : results.size();
    }

    static ArrayList<String> getResultsFilesNames(String id) {
        ArrayList<BenchmarkFunctionResult> results = storedResults.get(id);
        if (results == null) return new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();
        for (BenchmarkFunctionResult result : results) {
            names.add(String.format("\"%s\"", result.getFileName()));
        }
        return names;
    }


}
