package net.threetag.palladium.mixin.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.PreloadedTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.client.dynamictexture.transformer.TransformedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SuspiciousMethodCalls")
@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Shadow
    @Final
    public Map<ResourceLocation, AbstractTexture> byPath;

    @Shadow
    @Final
    private Set<Tickable> tickableTextures;

    @Shadow
    protected abstract void safeClose(ResourceLocation path, AbstractTexture texture);

    @Inject(method = "register(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/texture/AbstractTexture;)V", at = @At("HEAD"))
    private void register(ResourceLocation path, AbstractTexture texture, CallbackInfo ci) {
        if (!(texture instanceof PreloadedTexture)) {
            return;
        }

        List<ResourceLocation> toRemove = this.byPath.entrySet().stream()
                .filter(e -> e.getValue() instanceof TransformedTexture)
                .map(Map.Entry::getKey)
                .toList();

        for (ResourceLocation resourceLocation : toRemove) {
            var abstractTexture = this.byPath.get(resourceLocation);
            if (abstractTexture != null && abstractTexture != MissingTextureAtlasSprite.getTexture()) {
                this.tickableTextures.remove(abstractTexture);
                this.safeClose(resourceLocation, abstractTexture);
                this.byPath.remove(resourceLocation);
            }
        }
    }

}
