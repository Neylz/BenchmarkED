package dev.neylz.benchmarked.benchmarking;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import dev.neylz.benchmarked.util.TextUtils;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;


public class BenchmarkFunction {
    private final static Style STYLE_MEAN = Style.EMPTY.withColor(0x11fdaa);
    private final static Style STYLE_MIN = Style.EMPTY.withColor(0x32bbff);
    private final static Style STYLE_MAX = Style.EMPTY.withColor(0xe21955);


    private final ServerCommandSource source;
    private final ServerCommandSource executor;
    private final CommandDispatcher<ServerCommandSource> commandDispatcher;
    private final ExecutionControl<ServerCommandSource> control;

    private final Identifier id;
    private final Collection<CommandFunction<ServerCommandSource>> functions;
    private final int iterations;


    private ArrayList<Float> times = new ArrayList<Float>();


    public BenchmarkFunction(
        ServerCommandSource source,
        ExecutionControl<ServerCommandSource> control,
        Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> identifierCollectionPair,
        int iterations
    ) {
        this.source = source;
        this.executor = source.withMaxLevel(2).withSilent();
        this.commandDispatcher = source.getServer().getCommandFunctionManager().getDispatcher();
        this.control = control;

        this.id = identifierCollectionPair.getFirst();
        this.functions = identifierCollectionPair.getSecond();

        this.iterations = iterations;


    }


    public String getFunctionName() {
        return id.getNamespace() + ":" + id.getPath();
    }

    public Collection<CommandFunction<ServerCommandSource>> getFunctions() {
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
        for (int i = 0; i < iterations; i++) {
            timer.start();
            queueFunction();
            timer.stop();
            timer.registerLastTime();
        }
    }



    private void queueFunction() {
        for (final CommandFunction<ServerCommandSource> func : functions) {
            try {
                Procedure<ServerCommandSource> procedure = func.withMacroReplaced(null, commandDispatcher);
                control.enqueueAction(
                        (new CommandFunctionAction<>(procedure, ReturnValueConsumer.EMPTY, false))
                        .bind(executor)
                );
            } catch (MacroException e) {
                source.sendError(e.getMessage());
            }
        }
    }

    public void queuePrintResult() {
        control.enqueueAction((context, frame) -> {
            printResults();
        });
    }

    private void printResults() {

        if (times.isEmpty()) {
            source.sendError(TextUtils.listOf(
                Text.literal("Benchmarked: "),
                Text.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                Text.literal("\nError: No times recorded").setStyle(TextUtils.DefaultStyle.RED)
            ));

        } else if (times.size() == 1) {
            source.sendFeedback(
                () -> TextUtils.listOf(
                    Text.literal("Benchmarked: "),
                    Text.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                    Text.literal("\nran in " + getLastTime() + "ms").setStyle(TextUtils.DefaultStyle.RESET)
                ),
                false
            );
        } else {
            source.sendFeedback(
                () -> TextUtils.listOf(
                    Text.literal("Benchmarked: "),
                    Text.literal(getFunctionName()).setStyle(TextUtils.DefaultStyle.GREEN),
                    Text.literal("\nTime (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Text.literal("mean").setStyle(STYLE_MEAN))
                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal("σ").setStyle(STYLE_MEAN))
                        .append(Text.literal("):\n  ").setStyle(TextUtils.DefaultStyle.RESET))

                        .append(Text.literal(String.format("%.3f ms", getAverageTime())).setStyle(STYLE_MEAN))
                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal(String.format("%.3f ms", getStandardDeviation())).setStyle(STYLE_MEAN)),

                    Text.literal("\nRange (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Text.literal("min").setStyle(STYLE_MIN))
                        .append(Text.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal("max").setStyle(STYLE_MAX))
                        .append(Text.literal("):\n  ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal(String.format("%.3f ms", getMinTime())).setStyle(STYLE_MIN))
                        .append(Text.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal(String.format("%.3f ms", getMaxTime())).setStyle(STYLE_MAX)),

                    Text.literal("\nTotal: ").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Text.literal(String.format("[%.3f ms | %d runs]", getSumTime(), times.size())).setStyle(TextUtils.DefaultStyle.GRAY))


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

    public int getTimesSize() {
        return times.size();
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


    private class Timer extends dev.neylz.benchmarked.util.Timer {
        public Timer() {
            super();
        }

        public void start() {
            control.enqueueAction((context, frame) -> {
                super.start();
            });
        }

        public void stop() {
            control.enqueueAction((context, frame) -> {
                super.stop();
            });
        }

        public void registerLastTime() {
            control.enqueueAction((context, frame) -> {
                times.add(getElapsedTimeMillis());
            });
        }
    }
}
