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

public class TestCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
            CommandManager.literal("test")
                .requires(source -> {return source.hasPermissionLevel(3);})
                .executes(
                        context -> {
                            BenchmarkFunctionsHandler.runTick();
                            return 1;
                        }
                )

        );

    }







}
