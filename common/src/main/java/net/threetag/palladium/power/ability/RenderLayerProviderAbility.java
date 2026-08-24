package net.threetag.palladium.power.ability;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.client.renderer.renderlayer.IPackRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;

public interface RenderLayerProviderAbility {

    @OnlyIn(Dist.CLIENT)
    IPackRenderLayer getRenderLayer(AbilityInstance entry, LivingEntity entity, PackRenderLayerManager manager);

}
