package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemPropertyRegistry {

    private ItemPropertyRegistry() {
    }

    public static void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        ItemProperties.register(item, name, property);
    }

    public static void registerGeneric(ResourceLocation name, ClampedItemPropertyFunction property) {
        ItemProperties.registerGeneric(name, property);
    }
}
