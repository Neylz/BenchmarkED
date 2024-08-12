package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.BenchmarkED;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Collection;

public class BenchmarkCommand {

    static final Logger LOGGER = BenchmarkED.getLOGGER();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("benchmark").requires(serverCommandSource -> {return serverCommandSource.hasPermissionLevel(3);})

            .then(CommandManager.argument(
                    "function",
                    CommandFunctionArgumentType.commandFunction()
                    ).suggests(FunctionCommand.SUGGESTION_PROVIDER)
                    .executes(context -> functionRunner(context, CommandFunctionArgumentType.getIdentifiedFunctions(context, "function")))
            )
        );

    }

    private static int functionRunner(CommandContext<ServerCommandSource> ctx, Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> functions) throws CommandSyntaxException {
        Collection<CommandFunction<ServerCommandSource>> collection = functions.getSecond();
        Identifier id = functions.getFirst();

        // not found
        if (collection.isEmpty()) throw (new DynamicCommandExceptionType(o -> Text.stringifiedTranslatable("commands.function.scheduled.no_functions", o))).create(Text.of(id));

        MinecraftServer server = ctx.getSource().getServer();

        long startTime = System.currentTimeMillis();

        for (CommandFunction function: collection) {
            server.getProfiler().push(() -> {
                return "Benchmarking: " + id.getNamespace();
            });

            server.getCommandFunctionManager().execute(function, server.getCommandSource().withSilent().withMaxLevel(2));



            server.getProfiler().pop();
        }

        long endTime = System.currentTimeMillis();
        long elapsed = (endTime - startTime);

        ctx.getSource().sendFeedback(() -> {
            return Text.of("Ran command in " + elapsed + "ms");
        }, true);

        return 1;
    }


}
