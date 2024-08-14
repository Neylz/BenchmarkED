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
import dev.neylz.benchmarked.benchmarking.BenchmarkFunctionsHandler;
import dev.neylz.benchmarked.util.TextUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.FunctionCommand;
import org.slf4j.Logger;

public class BenchmarkCommand {

    static final Logger LOGGER = BenchmarkED.getLOGGER();
    // DebugCommand message
    static final SimpleCommandExceptionType NO_RETURN_RUN_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.debug.function.noReturnRun"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {

        dispatcher.register(
            Commands.literal("benchmark")
                .requires(source -> {return source.hasPermission(3);})
                .then(
                    Commands.argument("function", FunctionArgument.functions())
                        .suggests(FunctionCommand.SUGGEST_FUNCTION)
                        .executes(new Command())
                    .then(
                            Commands.argument("iterations", IntegerArgumentType.integer(1))
                                    .executes(new Command())
                    )
                )

        );

    }



    private static class Command extends CustomCommandExecutor.WithErrorHandling<CommandSourceStack> implements CustomCommandExecutor.CommandAdapter<CommandSourceStack> {

        public void runGuarded(
            CommandSourceStack source,
            ContextChain<CommandSourceStack> contextChain,
            ChainModifiers flags,
            ExecutionControl<CommandSourceStack> control
        ) throws CommandSyntaxException {


            // prevent ``return run benchmark ...``
            if (flags.isReturn())
                throw NO_RETURN_RUN_EXCEPTION.create();

            CommandContext<CommandSourceStack> ctx = contextChain.getTopContext();

            // grab the arguments back
            int iterations;
            try {
                iterations = IntegerArgumentType.getInteger(ctx, "iterations");
            } catch (IllegalArgumentException e) { iterations = 1; }

            BenchmarkFunction function = new BenchmarkFunction(
                    source,
                    control,
                    FunctionArgument.getFunctionCollection(ctx, "function"),
                    iterations
            );

            // check for empty tags
            if (function.isFunctionEmpty()) throw (
                new DynamicCommandExceptionType(
                    o -> Component.nullToEmpty(String.format("No function found in the function tag #%s", o))
                )
            ).create(
                Component.nullToEmpty(function.getFunctionName())
            );



            if (iterations == 1) {
                function.queueBenchmarkFunction();
            } else {
                int finalIterations = iterations;
                source.sendSuccess(
                    () -> TextUtils.listOf(
                        Component.literal(String.format("Benchmarking %s for %d iterations...", function.getFunctionName(), finalIterations))
                    ), false
                );

                BenchmarkFunctionsHandler.registerBenchmark(function);
//                function.queueBenchmarkFunction();

            }





        }

    }




}
