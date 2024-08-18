package dev.neylz.benchmarked.util;

import dev.neylz.benchmarked.command.*;
import dev.neylz.benchmarked.event.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ModRegistries {
    public static void registerAll() {
        registerCommands();
        registerEvents();
    }

    private static void registerCommands() {
//        CommandRegistrationCallback.EVENT.register(BenchmarkDebugCommand::register);
        CommandRegistrationCallback.EVENT.register(BenchmarkProfileCommand::register);
        CommandRegistrationCallback.EVENT.register(BenchmarkResultsCommand::register);




    }

    private static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(new EndServerTickEvent());


    }
}
