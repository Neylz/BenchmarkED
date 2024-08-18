package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.benchmarking.results.StoredBenchmarkResults;
import dev.neylz.benchmarked.util.CommandArgumentsGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class BenchmarkResultsCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(CommandManager.literal("benchmark")
            .then(CommandManager.literal("results")
                    .then(CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
                            .suggests(StoredBenchmarkResults.RESULTS_FUNCTIONS_SUGGESTIONS)
                            .executes(BenchmarkResultsCommand::getFunctionResults)
                            .then(CommandManager.argument("file", StringArgumentType.string())
                                    .suggests(StoredBenchmarkResults.RESULTS_FILES_SUGGESTIONS)
                                    .executes(BenchmarkResultsCommand::getFileResults)

                            )
                    )


            )

        );
    }

    private static int getFunctionResults(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ArrayList<String> functions = CommandArgumentsGetter.getFunctionNames(ctx, "function");

        if (functions.isEmpty()) {
            ctx.getSource().sendError(
                    Text.literal("No function found")
            );
            return 0;
        }

        if (functions.size() > 1) {
            ctx.getSource().sendError(
                    Text.literal("Multiple functions found; only one function can be specified")
            );
            return -1;
        }


        ArrayList<String> files = StoredBenchmarkResults.getResultsFilesNames(functions.get(0));

        if (files.isEmpty()) {
            ctx.getSource().sendError(
                    Text.literal("No benchmark result found for this function")
            );
            return 0;
        }

        ctx.getSource().sendFeedback(
                () -> Text.of(
                        String.format("Found %d benchmark results for function %s", files.size(), functions.get(0))
                ), false
        );


        return files.size();
    }

    private static int getFileResults(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ArrayList<String> functions = CommandArgumentsGetter.getFunctionNames(ctx, "function");
        String fileName = CommandArgumentsGetter.getString(ctx, "file");    // quotes are ALREADY removed by brigadier

        if (functions.isEmpty()) {
            ctx.getSource().sendError(
                    Text.literal("No function found")
            );
            return 0;
        }

        if (functions.size() > 1) {
            ctx.getSource().sendError(
                    Text.literal("Multiple functions found; only one function can be specified")
            );
            return -1;
        }


        return StoredBenchmarkResults.printFileResults(ctx, functions.get(0), fileName);
    }

}
