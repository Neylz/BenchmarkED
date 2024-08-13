package dev.neylz.benchmarked.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

public class StartWorldTickHandler implements ServerTickEvents.StartWorldTick {

    @Override
    public void onStartTick(ServerWorld world) {

    }
}
