package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class BlockEntityRendererRegistry {

    private static final Map<Supplier<? extends BlockEntityType<?>>, BlockEntityRendererProvider<?>> RENDERERS = new LinkedHashMap<>();

    private BlockEntityRendererRegistry() {
    }

    public static <T extends BlockEntity> void register(Supplier<? extends BlockEntityType<T>> type,
                                                        BlockEntityRendererProvider<? super T> provider) {
        RENDERERS.put(type, provider);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RENDERERS.forEach((type, provider) ->
                event.registerBlockEntityRenderer((BlockEntityType) type.get(), (BlockEntityRendererProvider) provider));
    }
}
