package dev.neylz.benchmarked.benchmarking.results;

import dev.neylz.benchmarked.benchmarking.BenchmarkFunction;
import dev.neylz.benchmarked.util.TextUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BenchmarkFunctionResult {
    private final static Style STYLE_MEAN = Style.EMPTY.withColor(0x11fdaa);
    private final static Style STYLE_MIN = Style.EMPTY.withColor(0x32bbff);
    private final static Style STYLE_MAX = Style.EMPTY.withColor(0xe21955);



    private final String functionName;
    private final String benchmarkFileName;

    private final Date timeStamp;

    private final int calls;
    private final ArrayList<Float> times;

    private BenchmarkFunctionResult(
            String functionName,
            ArrayList<Float> times
    ) {
        this.functionName = functionName;

        this.calls = times.size();
        this.times = times;

        this.timeStamp = new Date();

        this.benchmarkFileName = String.format(
                "%s - %s - %d.bed",
                functionName,
                new SimpleDateFormat("yyyy-dd-MM_HH-mm-ss").format(timeStamp),
                StoredBenchmarkResults.getFunctionsCount(functionName) + 1
        );

        StoredBenchmarkResults.storeResult(this);
    }


    public BenchmarkFunctionResult(
            BenchmarkFunction function
    ) {
        this(function.getNamespacedPath(), function.getTimes());
    }


    public String getNamespacedPath() {
        return functionName;
    }

    public String getFileName() {
        return benchmarkFileName;
    }


    public void sendResults(ServerCommandSource source) {

        if (times.isEmpty()) {
            source.sendError(TextUtils.listOf(
                Text.literal("BenchmarkED: ").setStyle(TextUtils.DefaultStyle.RESET),
                Text.literal(benchmarkFileName).setStyle(TextUtils.DefaultStyle.LIGHT_PURPLE),
                Text.literal("\nThis function has not been called during this benchmark").setStyle(TextUtils.DefaultStyle.RED)
            ));

        } else if (times.size() == 1) {
            source.sendFeedback(
                () -> TextUtils.listOf(
                    Text.literal("Benchmarked: "),
                    Text.literal(benchmarkFileName).setStyle(TextUtils.DefaultStyle.LIGHT_PURPLE),
                    Text.literal("\nran in " + getLastTime() + "ms").setStyle(TextUtils.DefaultStyle.RESET)
                ),
                false
            );
        } else {
            source.sendFeedback(
                () -> TextUtils.listOf(
                    Text.literal("Benchmarked: "),
                        Text.literal(benchmarkFileName).setStyle(TextUtils.DefaultStyle.LIGHT_PURPLE),
                    Text.literal("\nTime (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Text.literal("mean").setStyle(STYLE_MEAN))
                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal("σ").setStyle(STYLE_MEAN))
                        .append(Text.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))

                        .append(Text.literal(String.format("%.3f ms", getAverageTime())).setStyle(STYLE_MEAN))
                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal(String.format("%.3f ms", getStandardDeviation())).setStyle(STYLE_MEAN)),

                    Text.literal("\nRange (").setStyle(TextUtils.DefaultStyle.RESET)
                        .append(Text.literal("min").setStyle(STYLE_MIN))
                        .append(Text.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
                        .append(Text.literal("max").setStyle(STYLE_MAX))
                        .append(Text.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))
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
            sum += (float) Math.pow(time - mean, 2);
        }
        return (float) Math.sqrt(sum / times.size());
    }
}
