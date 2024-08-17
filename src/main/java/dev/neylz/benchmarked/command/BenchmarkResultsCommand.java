package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.benchmarking.results.StoredBenchmarkResults;
import dev.neylz.benchmarked.util.CommandArgumentsGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

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

    private static int getFunctionResults(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

    private static int getFileResults(CommandContext<ServerCommandSource> ctx) {
        return 1;
    }

}
