package dev.neylz.benchmarked.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class CommandArgumentsGetter {


    public static int getInteger(CommandContext<ServerCommandSource> context, String name) {
        int r;
        try {
            r = IntegerArgumentType.getInteger(context, name);
        } catch (IllegalArgumentException e) {
            r = 0;
        }
        return r;
    }

    public static int getInteger(CommandContext<ServerCommandSource> context, String name, int defaultValue) {
        int r;
        try {
            r = IntegerArgumentType.getInteger(context, name);
        } catch (IllegalArgumentException e) {
            r = defaultValue;
        }
        return r;
    }

    public static Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getFunction(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        return CommandFunctionArgumentType.getIdentifiedFunctions(ctx, name);
    }
}
