package dev.neylz.benchmarked.mixin;

import com.google.common.annotations.GwtIncompatible;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;


@Mixin(FunctionCommand.Command.class)
public abstract class FunctionCommandMixin {


    @Inject(method = "executeInner", at = @At("HEAD"), cancellable = true)
    private void runGuarded(ServerCommandSource source, ContextChain<ServerCommandSource> contextChain, ExecutionFlags flags, ExecutionControl<ServerCommandSource> control) throws CommandSyntaxException {
        // BenchmarkFunction benchmarkFunction = BenchmarkFunctionsHandler.getFunction(source.getLevel(), source.getEntity(), source.getPosition(), source.getRotation(), source.getRotation());
        // benchmarkFunction.run();
    }


}
