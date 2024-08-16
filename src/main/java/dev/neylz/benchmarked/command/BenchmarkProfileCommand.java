package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
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
            .requires(source -> {return source.hasPermissionLevel(3);})
            .then(CommandManager.literal("profile")
                .then(
                    FUNCTION_ARGUMENT
                        .executes(BenchmarkProfileCommand::toggleProfiler)
                        .then(
                            CommandManager.literal("start")
                                .executes(BenchmarkProfileCommand::startProfiler)
                                .then(TICK_COUNT_ARGUMENT)
                        )
                        .then(
                            CommandManager.literal("stop")
                                .executes(BenchmarkProfileCommand::stopProfiler)
                                .then(TICK_COUNT_ARGUMENT)
                        )
                        .then(
                            CommandManager.literal("toggle")
                                .executes(BenchmarkProfileCommand::toggleProfiler)
                                .then(TICK_COUNT_ARGUMENT)
                        )
                )
            )

        );
    }


    private static int startProfiler(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = getProvidedFunctions(ctx);
        int tickCount = getProvidedTickCount(ctx);



        return 1;
    }

    private static int stopProfiler(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = getProvidedFunctions(ctx);
        int tickCount = getProvidedTickCount(ctx);



        return 1;
    }

    private static int toggleProfiler(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions = getProvidedFunctions(ctx);
        int tickCount = getProvidedTickCount(ctx);



        return 1;
    }

    private static Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getProvidedFunctions(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return CommandFunctionArgumentType.getIdentifiedFunctions(ctx, "function");
    }

    private static int getProvidedTickCount(CommandContext<ServerCommandSource> ctx) {
        int tc = IntegerArgumentType.getInteger(ctx, "ticks");
        return tc <= 0 ? -1 : tc;
    }



}
