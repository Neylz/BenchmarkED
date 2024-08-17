package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.benchmarking.FunctionBenchmarkHandler;
import dev.neylz.benchmarked.util.CommandArgumentsGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class BenchmarkProfileCommand {
    private final static ArgumentBuilder<ServerCommandSource, ?> FUNCTION_ARGUMENT =
        CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
            .suggests(FunctionCommand.SUGGESTION_PROVIDER);

    private final static ArgumentBuilder<ServerCommandSource, ?> TICK_COUNT_ARGUMENT =
        CommandManager.argument("ticks", IntegerArgumentType.integer(1));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(CommandManager.literal("benchmark")
            .requires(source -> source.hasPermissionLevel(3))
            .then(CommandManager.literal("profile")
                .then(
                    CommandManager.literal("start")
                        .then(
                            FUNCTION_ARGUMENT
                                .executes(BenchmarkProfileCommand::registerProfiling)
                            .then(
                                TICK_COUNT_ARGUMENT
                                .executes(BenchmarkProfileCommand::registerProfiling)
                            )
                        )
                )
                .then(
                    CommandManager.literal("stop")
                        .then(
                            CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
                                .suggests(FunctionBenchmarkHandler.RUNNING_FUNCTIONS_SUGGESTIONS)
                                .executes(BenchmarkProfileCommand::deregisterProfiling)
                        )
                )
                .then(
                    CommandManager.literal("stopall")
                        .executes(BenchmarkProfileCommand::deregisterAllProfiling)
                )
            )

        );
    }




    private static int registerProfiling(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = CommandArgumentsGetter.getFunction(ctx, "function");
        int tickCount = CommandArgumentsGetter.getInteger(ctx, "ticks", -1);

        int i = 0, j = 0;
        String id = "";
        for (CommandFunction<ServerCommandSource> fn : functions.getSecond()) {

            id = ((IdentifierAccess) (Object) fn.id()).benchmarked$getNamespacedPath();
            j += FunctionBenchmarkHandler.registerFunction(id, tickCount);

            i++;
        }

        String finalId = id;
        int finalI = i, finalJ = j;
        if (finalJ == 0) {
            if (finalI == 0) {
                ctx.getSource().sendError(
                    Text.of("No functions provided")
                );
            } else {
                ctx.getSource().sendError(
                    Text.of("Provided function(s) are already being profiled")
                );
            }
            return 0;
        } else if (i == 1) {
            ctx.getSource().sendFeedback(
                () -> Text.of(String.format("%s registered for profiling with success.", finalId)), false
            );
        } else {
            String originPath = ((IdentifierAccess) (Object) functions.getFirst()).benchmarked$getNamespacedPath();
            if (finalJ == finalI) {
                ctx.getSource().sendFeedback(
                        () -> Text.of(String.format("Registered %d functions from #%s.", finalI, originPath)), false
                );
            } else {
                ctx.getSource().sendFeedback(
                    () -> Text.of(String.format("Registered %d new functions for profiling (%d provided functions were already registered).", finalJ, finalI-finalJ)), false
                );
            }
        }


        return finalJ;
    }

    @SuppressWarnings("unchecked")
    private static int deregisterProfiling(
            CommandContext<ServerCommandSource> ctx
    ) throws CommandSyntaxException {

        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = CommandArgumentsGetter.getFunction(ctx, "function");


        int i = 0, j = 0;
        String id = "";

        while (!functions.getSecond().isEmpty()) {
            CommandFunction<ServerCommandSource> fn = (CommandFunction<ServerCommandSource>) functions.getSecond().toArray()[0];
            id = ((IdentifierAccess) (Object) fn.id()).benchmarked$getNamespacedPath();
            j += FunctionBenchmarkHandler.deregisterFunction(id);
            i++;
        }

        int finalI = i, finalJ = j;
        if (finalJ == 0) {
            if (finalI == 0) {
                ctx.getSource().sendError(
                    Text.of("No functions provided")
                );
            } else {
                ctx.getSource().sendError(
                    Text.of("None of the provided function(s) were registered for profiling")
                );
            }
            return 0;
        } else if (i == 1) {
            String finalId = id;
            ctx.getSource().sendFeedback(
                () -> Text.of(String.format("%s deregistered from profiling with success.", finalId)), false
            );
        } else {
            String originPath = ((IdentifierAccess) (Object) functions.getFirst()).benchmarked$getNamespacedPath();
            if (finalJ == finalI) {
                ctx.getSource().sendFeedback(
                        () -> Text.of(String.format("Deregistered %d functions from #%s.", finalI, originPath)), false
                );
            } else {
                ctx.getSource().sendFeedback(
                    () -> Text.of(String.format("Deregistered %d functions from #%s (%d provided functions were not being profiled).", finalJ, originPath, finalI-finalJ)), false
                );
            }
        }


        return finalJ;
    }


    private static int deregisterAllProfiling(CommandContext<ServerCommandSource> ctx) {
        int i = FunctionBenchmarkHandler.deregisterAllFunctions();

        if (i == 0) {
            ctx.getSource().sendError(
                Text.of("No functions are being currently profiled")
            );
            return 0;
        }


        ctx.getSource().sendFeedback(
            () -> Text.of(String.format("Deregistered %d functions from profiling.", i)), false
        );
        return i;
    }



}
