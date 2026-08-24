package net.threetag.palladium.mixin;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(VanillaPackResourcesBuilder.class)
public abstract class VanillaPackResourcesBuilderMixin {

    @Redirect(
            method = "lambda$static$1",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PackType;values()[Lnet/minecraft/server/packs/PackType;")
    )
    private static PackType[] palladium$excludeAddonPackRoot() {
        return Arrays.stream(PackType.values())
                .filter(type -> !"addon".equals(type.getDirectory()))
                .toArray(PackType[]::new);
    }
}
