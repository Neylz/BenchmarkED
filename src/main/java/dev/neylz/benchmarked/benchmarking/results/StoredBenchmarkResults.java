package dev.neylz.benchmarked.benchmarking.results;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.BenchmarkED;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.util.CommandArgumentsGetter;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
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

    public static ArrayList<String> getResultsFilesNames(String id) {
        ArrayList<BenchmarkFunctionResult> results = storedResults.get(id);
        if (results == null) return new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();
        for (BenchmarkFunctionResult result : results) {
            names.add(String.format("\"%s\"", result.getFileName()));
        }
        return names;
    }

    private static BenchmarkFunctionResult getFunctionFile(String func, String file) {
        ArrayList<BenchmarkFunctionResult> results = storedResults.get(func);
        if (results == null) return null;

        for (BenchmarkFunctionResult result : results) {
            if (result.getFileName().equals(file)) return result;
        }
        return null;
    }


    public static int printFileResults(CommandContext<ServerCommandSource> ctx, String func, String file) {
        ArrayList<BenchmarkFunctionResult> results = storedResults.get(func);

        if (results == null) {
            ctx.getSource().sendError(
                    Text.literal("No benchmark result found for this function")
            );
            return 0;
        }

        BenchmarkFunctionResult result = getFunctionFile(func, file);
        if (result == null) {
            ctx.getSource().sendError(
                    Text.literal("No benchmark result found with this file name")
            );
            return 0;
        }


        result.sendResults(ctx.getSource());



        return 1;
    }
}
