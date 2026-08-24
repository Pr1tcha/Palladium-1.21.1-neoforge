package net.threetag.palladium.client.renderer.entity;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.util.SupporterHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class SupporterCloakHandler {

    private SupporterCloakHandler() {
    }

    public static void load(SupporterHandler.PlayerData data, String url) {
        try (InputStream stream = new URL(url).openStream()) {
            NativeImage image = NativeImage.read(stream);
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.execute(() -> {
                ResourceLocation resourceLocation = Palladium.id("cloaks/" + data.getUuid());
                minecraft.getTextureManager().release(resourceLocation);
                minecraft.getTextureManager().register(resourceLocation, new DynamicTexture(image));
                data.setCloakTexture(resourceLocation);
            });
        } catch (IOException e) {
            Palladium.LOGGER.error("Error loading supporter cloak texture: {}", e.getMessage());
        }
    }
}
