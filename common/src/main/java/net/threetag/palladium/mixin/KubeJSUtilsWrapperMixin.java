package net.threetag.palladium.mixin;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UtilsWrapper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = UtilsWrapper.class, remap = false)
public interface KubeJSUtilsWrapperMixin {
}
