package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class RenderTypeRegistry {

    private RenderTypeRegistry() {
    }

    public static void registerBlock(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    public static void registerFluid(RenderType type, Fluid... fluids) {
        for (Fluid fluid : fluids) {
            ItemBlockRenderTypes.setRenderLayer(fluid, type);
        }
    }
}
