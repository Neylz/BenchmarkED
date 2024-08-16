package dev.neylz.benchmarked.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.neylz.benchmarked.access.IdentifierAccess;
import dev.neylz.benchmarked.benchmarking.FunctionBenchmarkHandler;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(FunctionCommand.class)
public abstract class FunctionCommandMixin {

    @Inject(method = "enqueueFunction", at = @At("HEAD"))
    private static <T extends AbstractServerCommandSource<T>> void startProfiler(
            @Nullable NbtCompound args, ExecutionControl<T> control, CommandDispatcher<T> dispatcher, T source, CommandFunction<T> function, Identifier id, ReturnValueConsumer returnValueConsumer, boolean propagateReturn, CallbackInfo ci
    ) throws CommandSyntaxException {
        control.enqueueAction(
                (context, frame) -> {
                    FunctionBenchmarkHandler.startProfiler(((IdentifierAccess) (Object) id).benchmarked$getNamespacedPath());
                }
        );
    }



    @Inject(method = "enqueueFunction", at = @At("TAIL"))
    private static <T extends AbstractServerCommandSource<T>> void stopProfiler(
            @Nullable NbtCompound args, ExecutionControl<T> control, CommandDispatcher<T> dispatcher, T source, CommandFunction<T> function, Identifier id, ReturnValueConsumer returnValueConsumer, boolean propagateReturn, CallbackInfo ci
    ) throws CommandSyntaxException {
        control.enqueueAction(
                (context, frame) -> {
                    FunctionBenchmarkHandler.stopProfiler(((IdentifierAccess) (Object) id).benchmarked$getNamespacedPath());
                }
        );
    }


}
