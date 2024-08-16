package dev.neylz.benchmarked.event;

import dev.neylz.benchmarked.benchmarking.FunctionBenchmarkHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class EndServerTickEvent implements ServerTickEvents.EndTick {

    @Override
    public void onEndTick(MinecraftServer server) {
        FunctionBenchmarkHandler.postTick();
    }
}
