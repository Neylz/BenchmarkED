package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.neylz.benchmarked.BenchmarkED;
import dev.neylz.benchmarked.benchmarking.BenchmarkFunction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class BenchmarkCommand {

    static final Logger LOGGER = BenchmarkED.getLOGGER();
    // DebugCommand message
    static final SimpleCommandExceptionType NO_RETURN_RUN_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.function.noReturnRun"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
            CommandManager.literal("benchmark")
                .requires(source -> {return source.hasPermissionLevel(3);})
                .then(
                    CommandManager.argument("function", CommandFunctionArgumentType.commandFunction())
                        .suggests(FunctionCommand.SUGGESTION_PROVIDER)
                        .executes(new Command())
                    .then(
                            CommandManager.argument("iterations", IntegerArgumentType.integer(1))
                                    .executes(new Command())
                    )
                )

        );

    }



    private static class Command extends ControlFlowAware.Helper<ServerCommandSource> implements ControlFlowAware.Command<ServerCommandSource> {

        public void executeInner(
            ServerCommandSource source,
            ContextChain<ServerCommandSource> contextChain,
            ExecutionFlags flags,
            ExecutionControl<ServerCommandSource> control
        ) throws CommandSyntaxException {


            // prevent ``return run benchmark ...``
            if (flags.isInsideReturnRun())
                throw NO_RETURN_RUN_EXCEPTION.create();

            CommandContext<ServerCommandSource> ctx = contextChain.getTopContext();

            // grab the arguments back
            int iterations;
            try {
                iterations = IntegerArgumentType.getInteger(ctx, "iterations");
            } catch (IllegalArgumentException e) { iterations = 1; }

            BenchmarkFunction function = new BenchmarkFunction(
                    source,
                    control,
                    CommandFunctionArgumentType.getIdentifiedFunctions(ctx, "function"),
                    iterations
            );

            // check for empty tags
            if (function.isFunctionEmpty()) throw (
                new DynamicCommandExceptionType(
                    o -> Text.of(String.format("No function found in the function tag #%s", o))
                )
            ).create(
                Text.of(function.getFunctionName())
            );

            // queue the function
            function.queueBenchmarkFunction();

            function.queuePrintResult();





        }

    }




}
