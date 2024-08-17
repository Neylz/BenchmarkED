package dev.neylz.benchmarked.benchmarking;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.sun.jdi.ArrayReference;
import dev.neylz.benchmarked.access.IdentifierAccess;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;


public class BenchmarkFunction {
    private final static Style STYLE_MEAN = Style.EMPTY.withColor(0x11fdaa);
    private final static Style STYLE_MIN = Style.EMPTY.withColor(0x32bbff);
    private final static Style STYLE_MAX = Style.EMPTY.withColor(0xe21955);


    private final String id;
    private int lifeTime;
    private final ArrayList<Float> times = new ArrayList<>();
    private final BenchmarkProfiler profiler = new BenchmarkProfiler();


    public BenchmarkFunction(String id, int lifeTime) {
        this.id = id;
        this.lifeTime = lifeTime;
    }


    public String getNamespacedPath() {
        return id;
    }

    public void startProfiling() {
        profiler.start();
    }

    public void stopProfiling() {
        profiler.stop();
        if (profiler.isProcessFinished()) {
            times.add(profiler.getTime());
        }
    }

    public void decreaseLifetime() {
        if (lifeTime > 0) lifeTime --;
    }

    public boolean isExpired() {
        return (lifeTime == 0); // -1 is reserved by functions without specified lifespan
    }


//    private void printResults() {
//
//        if (times.isEmpty()) {
//            source.sendError(TextUtils.listOf(
//                Text.literal("Benchmarked: "),
//                Text.literal(idNamespace).setStyle(TextUtils.DefaultStyle.GREEN),
//                Text.literal("\nError: No times recorded").setStyle(TextUtils.DefaultStyle.RED)
//            ));
//
//        } else if (times.size() == 1) {
//            source.sendFeedback(
//                () -> TextUtils.listOf(
//                    Text.literal("Benchmarked: "),
//                    Text.literal(idNamespace).setStyle(TextUtils.DefaultStyle.GREEN),
//                    Text.literal("\nran in " + getLastTime() + "ms").setStyle(TextUtils.DefaultStyle.RESET)
//                ),
//                false
//            );
//        } else {
//            source.sendFeedback(
//                () -> TextUtils.listOf(
//                    Text.literal("Benchmarked: "),
//                    Text.literal(idNamespace).setStyle(TextUtils.DefaultStyle.GREEN),
//                    Text.literal("\nTime (").setStyle(TextUtils.DefaultStyle.RESET)
//                        .append(Text.literal("mean").setStyle(STYLE_MEAN))
//                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
//                        .append(Text.literal("σ").setStyle(STYLE_MEAN))
//                        .append(Text.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))
//
//                        .append(Text.literal(String.format("%.3f ms", getAverageTime())).setStyle(STYLE_MEAN))
//                        .append(Text.literal(" ± ").setStyle(TextUtils.DefaultStyle.RESET))
//                        .append(Text.literal(String.format("%.3f ms", getStandardDeviation())).setStyle(STYLE_MEAN)),
//
//                    Text.literal("\nRange (").setStyle(TextUtils.DefaultStyle.RESET)
//                        .append(Text.literal("min").setStyle(STYLE_MIN))
//                        .append(Text.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
//                        .append(Text.literal("max").setStyle(STYLE_MAX))
//                        .append(Text.literal("):\n   ").setStyle(TextUtils.DefaultStyle.RESET))
//                        .append(Text.literal(String.format("%.3f ms", getMinTime())).setStyle(STYLE_MIN))
//                        .append(Text.literal(" … ").setStyle(TextUtils.DefaultStyle.RESET))
//                        .append(Text.literal(String.format("%.3f ms", getMaxTime())).setStyle(STYLE_MAX)),
//
//                    Text.literal("\nTotal: ").setStyle(TextUtils.DefaultStyle.RESET)
//                        .append(Text.literal(String.format("[%.3f ms | %d runs]", getSumTime(), times.size())).setStyle(TextUtils.DefaultStyle.GRAY))
//
//
//                ),
//                false
//            );
//        }
//
//    }


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


    public ArrayList<Float> getTimes() {
        return times;
    }


//
//
//    public void debugMessage(String message) {
//        source.sendFeedback(() -> TextUtils.listOf(
//            Text.literal("Benchmarked: "),
//            Text.literal(idNamespace).setStyle(TextUtils.DefaultStyle.GREEN),
//            Text.literal("\n" + message).setStyle(TextUtils.DefaultStyle.RESET)
//        ), false);
//    }
//
//
//    private class Timer extends dev.neylz.benchmarked.util.Timer {
//        public Timer() {
//            super();
//        }
//
//        public void start() {
//            control.enqueueAction((context, frame) -> {
//                super.start();
//            });
//        }
//
//        public void stop() {
//            control.enqueueAction((context, frame) -> {
//                super.stop();
//            });
//        }
//
//        public void registerLastTime() {
//            control.enqueueAction((context, frame) -> {
//                times.add(getElapsedTimeMillis());
//            });
//        }
//    }
}
