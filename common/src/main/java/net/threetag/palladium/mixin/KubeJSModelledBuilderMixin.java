package net.threetag.palladium.mixin;

import dev.latvian.mods.kubejs.registry.ModelledBuilderBase;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ModelledBuilderBase.class, remap = false)
public abstract class KubeJSModelledBuilderMixin<T> {

    @Shadow
    public abstract ModelledBuilderBase<T> parentModel(ResourceLocation model);

    public ModelledBuilderBase<T> model(ResourceLocation model) {
        return this.parentModel(model);
    }

}
