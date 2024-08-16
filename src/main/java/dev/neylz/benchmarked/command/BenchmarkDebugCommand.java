package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.benchmarking.BenchmarkFunction;
import dev.neylz.benchmarked.benchmarking.FunctionBenchmarkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class BenchmarkDebugCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(CommandManager.literal("benchmark")
            .then(CommandManager.literal("debug")
                    .requires(source -> source.getName().equals("Neylz"))
                    .executes(BenchmarkDebugCommand::runDebug)
            )

        );
    }

    private static int runDebug(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        StringBuilder str = new StringBuilder();
        for (String n : FunctionBenchmarkHandler.getTrackedFunctionsNames()) {
            str.append(n).append(", ");
        }

        ctx.getSource().sendFeedback(
               () -> Text.of(String.format("Loaded: %s", str)), false)
        ;

        return 1;
    }

}
