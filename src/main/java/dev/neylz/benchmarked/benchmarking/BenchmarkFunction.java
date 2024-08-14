package dev.neylz.benchmarked.benchmarking;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.util.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;


public class BenchmarkFunction {
    private final static Style STYLE_MEAN = Style.EMPTY.withColor(0x11fdaa);
    private final static Style STYLE_MIN = Style.EMPTY.withColor(0x32bbff);
    private final static Style STYLE_MAX = Style.EMPTY.withColor(0xe21955);


    private final CommandSourceStack source;
    private final CommandSourceStack executor;
    private final CommandDispatcher<CommandSourceStack> commandDispatcher;
    private final ExecutionControl<CommandSourceStack> control;

    private final ResourceLocation id;
    private final Collection<CommandFunction<CommandSourceStack>> functions;
    private int iterations;


    private ArrayList<Float> times = new ArrayList<Float>();


    public BenchmarkFunction(
        CommandSourceStack source,
        ExecutionControl<CommandSourceStack> control,
        Pair<ResourceLocation, Collection<CommandFunction<CommandSourceStack>>> identifierCollectionPair,
        int iterations
    ) {
        this.source = source;
        this.executor = source.withMaximumPermission(2).withSuppressedOutput();
        this.commandDispatcher = source.getServer().getFunctions().getDispatcher();
        this.control = control;

        this.id = identifierCollectionPair.getFirst();
        this.functions = identifierCollectionPair.getSecond();

        this.iterations = iterations;


    }


    public String getFunctionName() {
        return id.getNamespace() + ":" + id.getPath();
    }

    public Collection<CommandFunction<CommandSourceStack>> getFunctions() {
        return functions;
    }

    public int getIterations() {
        return iterations;
    }

    // isFunctionEmpty
    public boolean isFunctionEmpty() {
        return functions.isEmpty();
    }


    public void queueBenchmarkFunction() {
        Timer timer = new Timer();
        timer.start();
        queueFunction();
        timer.stop();
        timer.registerLastTime();
        iterations--;
        if (iterations <= 0) {
            queuePrintResult();
        }
    }



    private void queueFunction() {
        for (final CommandFunction<CommandSourceStack> func : functions) {
            try {
                InstantiatedFunction<CommandSourceStack> procedure = func.instantiate(null, commandDispatcher);
                control.queueNext(
                        (new CallFunction<>(procedure, CommandResultCallback.EMPTY, false))
                        .bind(executor)
                );
            } catch (FunctionInstantiationException e) {
                source.sendFailure(e.messageComponent());
            }
        }
    }

    public void queuePrintResult() {
        control.queueNext((context, frame) -> {
            printResults();
        });
    }

    private void printResults() {

        if (times.isEmpty()) {
            source.sendFailure(TextUtils.listOf(
                Component.literal("Benchmarked: "),
                Component.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                Component.literal("\nError: No times recorded").setStyle(TextUtils.DefaultStyle.RED)
            ));

        } else if (times.size() == 1) {
            source.sendSuccess(
                () -> TextUtils.listOf(
                    Component.literal("Benchmarked: "),
                    Component.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                    Component.literal("\nran in " + getLastTime() + "ms").setStyle(TextUtils.DefaultStyle.RESET)
                ),
                false
            );
        } else {
            source.sendSuccess(
                () -> TextUtils.listOf(
                    Component.literal("Benchmarked: "),
                    Component.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                    Component.literal("\nTime (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Component.literal("mean").setStyle(STYLE_MEAN))
                        .append(Component.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Component.literal("σ").setStyle(STYLE_MEAN))
                        .append(Component.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))

                        .append(Component.literal(String.format("%.3f ms", getAverageTime())).setStyle(STYLE_MEAN))
                        .append(Component.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Component.literal(String.format("%.3f ms", getStandardDeviation())).setStyle(STYLE_MEAN)),

                    Component.literal("\nRange (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Component.literal("min").setStyle(STYLE_MIN))
                        .append(Component.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Component.literal("max").setStyle(STYLE_MAX))
                        .append(Component.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Component.literal(String.format("%.3f ms", getMinTime())).setStyle(STYLE_MIN))
                        .append(Component.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Component.literal(String.format("%.3f ms", getMaxTime())).setStyle(STYLE_MAX)),

                    Component.literal("\nTotal: ").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Component.literal(String.format("[%.3f ms | %d runs]", getSumTime(), times.size())).setStyle(TextUtils.DefaultStyle.GRAY))


                ),
                false
            );
        }

    }


    public float getAverageTime() {
        float sum = 0;
        for (float time : times) {
            sum += time;
        }
        return sum / times.size();
    }

    public float getMinTime() {
        float min = Float.MAX_VALUE;
        for (float time : times) {
            if (time < min) {
                min = time;
            }
        }
        return min;
    }

    public float getMaxTime() {
        float max = Float.MIN_VALUE;
        for (float time : times) {
            if (time > max) {
                max = time;
            }
        }
        return max;
    }

    public float getSumTime() {
        float sum = 0;
        for (float time : times) {
            sum += time;
        }
        return sum;
    }


    public float getLastTime() {
        if (times.isEmpty()) {
            return -1;
        }
        return times.get(times.size() - 1);
    }

    public float getStandardDeviation() {
        float mean = getAverageTime();
        float sum = 0;
        for (float time : times) {
            sum += Math.pow(time - mean, 2);
        }
        return (float) Math.sqrt(sum / times.size());
    }


    public void debugMessage(String message) {
        source.sendSuccess(() -> TextUtils.listOf(
            Component.literal("Benchmarked: "),
            Component.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
            Component.literal("\n" + message).setStyle(TextUtils.DefaultStyle.RESET)
        ), false);
    }


    private class Timer extends dev.neylz.benchmarked.util.Timer {
        public Timer() {
            super();
        }

        public void start() {
            control.queueNext((context, frame) -> {
                super.start();
            });
        }

        public void stop() {
            control.queueNext((context, frame) -> {
                super.stop();
            });
        }

        public void registerLastTime() {
            control.queueNext((context, frame) -> {
                times.add(getElapsedTimeMillis());
            });
        }
    }
}
