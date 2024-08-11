package dev.neylz.benchmarked;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.neylz.benchmarked.util.ModRegisteries.registerAll;

public class BenchmarkED implements ModInitializer {
    public static final String MOD_ID = "benchmarked";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        registerAll();

        LOGGER.info("BenchmarkED initialized!");
    }
}
