package dev.neylz.benchmarked.util;

import com.mojang.brigadier.CommandDispatcher;
import dev.neylz.benchmarked.command.*;
import dev.neylz.benchmarked.event.StartWorldTickHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ModRegisteries {
    public static void registerAll() {
        registerCommands();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(BenchmarkCommand::register);
        CommandRegistrationCallback.EVENT.register(TestCommand::register);

    }

    private static void registerEvents() {
        ServerTickEvents.START_WORLD_TICK.register(new StartWorldTickHandler());
    }
}
