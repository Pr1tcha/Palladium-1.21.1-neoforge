package net.threetag.palladium.power.ability;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.client.renderer.renderlayer.IPackRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ResourceLocationProperty;

public class RenderLayerAbility extends Ability implements RenderLayerProviderAbility {

    public static final PalladiumProperty<ResourceLocation> RENDER_LAYER = new ResourceLocationProperty("render_layer").configurable("ID of the render layer that will be rendered");

    public RenderLayerAbility() {
        this.withProperty(RENDER_LAYER, ResourceLocation.fromNamespaceAndPath("namespace", "render_layer_id"));
    }

    @Override
    public boolean isEffect() {
        return true;
    }

    @Override
    public String getDocumentationDescription() {
        return "Allows you to add a render layer to the entity.";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IPackRenderLayer getRenderLayer(AbilityInstance entry, LivingEntity entity, PackRenderLayerManager manager) {
        return manager.getLayer(entry.getProperty(RENDER_LAYER));
    }
}
