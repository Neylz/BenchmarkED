package dev.neylz.benchmarked.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

public class BenchmarkCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("benchmark").requires(serverCommandSource -> {return serverCommandSource.hasPermissionLevel(3);})

            .then(CommandManager.argument(
                    "function",
                    CommandFunctionArgumentType.commandFunction()
                    ).suggests(FunctionCommand.SUGGESTION_PROVIDER)
                    .executes(BenchmarkCommand::datapackInfo)
            )
        );
    }

    private static int datapackInfo(CommandContext<ServerCommandSource> ctx) {



        return 1;
    }

}
