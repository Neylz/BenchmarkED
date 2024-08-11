package dev.neylz.benchmarked.util;

import dev.neylz.benchmarked.command.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModRegisteries {
    public static void registerAll() {
        registerCommands();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(BenchmarkCommand::register);

    }
}
