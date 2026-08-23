package net.threetag.palladiumcore.registry.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class EntityRendererRegistry {

    private static final Map<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>> RENDERERS = new LinkedHashMap<>();
    private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> MODEL_LAYERS = new LinkedHashMap<>();
    private static final List<Pair<Predicate<EntityType<?>>, Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>>>> RENDER_LAYERS = new ArrayList<>();

    private EntityRendererRegistry() {
    }

    public static <T extends Entity> void register(Supplier<? extends EntityType<? extends T>> typeSupplier,
                                                   EntityRendererProvider<T> provider) {
        RENDERERS.put(typeSupplier, provider);
    }

    public static void registerModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definitionSupplier) {
        MODEL_LAYERS.put(location, definitionSupplier);
    }

    public static void addRenderLayer(Predicate<EntityType<?>> entityType,
                                      Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>> renderLayer) {
        RENDER_LAYERS.add(Pair.of(entityType, renderLayer));
    }

    public static void addRenderLayer(Supplier<EntityType<?>> entityType,
                                      Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>> renderLayer) {
        addRenderLayer(type -> type == entityType.get(), renderLayer);
    }

    public static void addRenderLayerToPlayer(Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>> renderLayer) {
        addRenderLayer(type -> type == EntityType.PLAYER, renderLayer);
    }

    public static void addRenderLayerToAll(Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>> renderLayer) {
        addRenderLayer(type -> true, renderLayer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RENDERERS.forEach((type, provider) ->
                event.registerEntityRenderer((EntityType) type.get(), (EntityRendererProvider) provider));
    }

    static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        MODEL_LAYERS.forEach(event::registerLayerDefinition);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void addRenderLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            for (Pair<Predicate<EntityType<?>>, Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>>> registration : RENDER_LAYERS) {
                if (!registration.getFirst().test(entityType)) {
                    continue;
                }

                if (entityType == EntityType.PLAYER) {
                    event.getSkins().forEach(skin -> {
                        LivingEntityRenderer renderer = event.getSkin(skin);
                        if (renderer != null) {
                            renderer.addLayer(registration.getSecond().apply(renderer));
                        }
                    });
                } else {
                    EntityRenderer<?> renderer = event.getRenderer((EntityType) entityType);
                    if (renderer instanceof LivingEntityRenderer livingRenderer) {
                        livingRenderer.addLayer(registration.getSecond().apply(livingRenderer));
                    }
                }
            }
        }
    }
}
