package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class BenchmarkResultsCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(CommandManager.literal("benchmark")
            .then(CommandManager.literal("results")
                    .then(CommandManager.argument("function", CommandFunctionArgumentType.commandFunction()))
                    .executes(BenchmarkResultsCommand::getResults)
            )

        );
    }

    private static int getResults(CommandContext<ServerCommandSource> ctx) {


        return 1;
    }

}
