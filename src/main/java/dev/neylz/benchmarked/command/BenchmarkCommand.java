package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.BenchmarkED;
import dev.neylz.benchmarked.util.Timer;
import net.minecraft.command.*;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Collection;

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

            MinecraftServer server = source.getServer();
            CommandContext<ServerCommandSource> ctx = contextChain.getTopContext();
            CommandDispatcher<ServerCommandSource> commandDispatcher = server.getCommandFunctionManager().getDispatcher();
            // grab the arguments back
            Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> identifierCollectionPair = CommandFunctionArgumentType.getIdentifiedFunctions(ctx, "function");

            Identifier id = identifierCollectionPair.getFirst();
            Collection<CommandFunction<ServerCommandSource>> collection = identifierCollectionPair.getSecond();

            if (collection.isEmpty()) throw (new DynamicCommandExceptionType(o -> Text.stringifiedTranslatable("commands.function.scheduled.no_functions", o))).create(Text.of(id));

            ServerCommandSource executionSource = source.withMaxLevel(2).withSilent();

            int i = 0;

            Timer timer = new Timer();
            control.enqueueAction((context, frame) -> {
                timer.start();
            });

            // run function(s)
            for (final CommandFunction<ServerCommandSource> func : collection) {
                try {

                    Procedure<ServerCommandSource> procedure = func.withMacroReplaced(null, commandDispatcher);
                    control.enqueueAction(
                        (new CommandFunctionAction<>(procedure, ReturnValueConsumer.EMPTY, false))
                        .bind(executionSource)
                    );

                    i += procedure.entries().size();
                } catch (MacroException e) {
                    source.sendError(e.getMessage());
                }
            }

            int finalI = i;
            control.enqueueAction((context, frame) -> {
                timer.stop();

                float elapsedTime = ((float) timer.getElapsedTime()) / 1000000.0f;
                source.sendFeedback(
                    () -> Text.of(String.format("Ran %d commands from %s:%s in %.3fms", finalI, id.getNamespace(), id.getPath(), elapsedTime)), false
                );
            });



        }

    }


}
