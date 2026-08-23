package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OverlayRegistry {

    private static final Map<ResourceLocation, IngameOverlay> OVERLAYS = new LinkedHashMap<>();

    private OverlayRegistry() {
    }

    public static void registerOverlay(String id, IngameOverlay overlay) {
        OVERLAYS.put(normalizeId(id), overlay);
    }

    private static ResourceLocation normalizeId(String id) {
        if (id.indexOf(':') >= 0) {
            return ResourceLocation.parse(id);
        }
        int separator = id.indexOf('/');
        if (separator > 0) {
            return ResourceLocation.fromNamespaceAndPath(id.substring(0, separator), id.substring(separator + 1));
        }
        return ResourceLocation.fromNamespaceAndPath("palladium", id);
    }

    static void registerOverlays(RegisterGuiLayersEvent event) {
        OVERLAYS.forEach((id, overlay) -> event.registerAboveAll(id, (guiGraphics, deltaTracker) -> {
            Minecraft minecraft = Minecraft.getInstance();
            overlay.render(
                    minecraft,
                    minecraft.gui,
                    guiGraphics,
                    deltaTracker.getGameTimeDeltaPartialTick(false),
                    minecraft.getWindow().getGuiScaledWidth(),
                    minecraft.getWindow().getGuiScaledHeight()
            );
        }));
    }

    public interface IngameOverlay {
        void render(Minecraft minecraft, Gui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height);
    }
}
