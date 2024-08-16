package dev.neylz.benchmarked.mixin;


import dev.neylz.benchmarked.access.IdentifierAccess;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements IdentifierAccess {


    @Final
    @Shadow
    private String namespace;
    @Final
    @Shadow
    private String path;

    @Override
    public String benchmarked$getNamespacedPath() {
        return this.namespace + ":" + this.path;
    }
}
