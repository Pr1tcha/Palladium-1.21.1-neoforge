package net.threetag.palladium.entity;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;

@OnlyIn(Dist.CLIENT)
public interface PlayerModelCacheExtension {

    PlayerModel<AbstractClientPlayer> palladium$getCachedModel();

    PalladiumAnimation.PoseStackResult palladium$getBodyAnimationResult();

    void palladium$setBodyAnimationResult(PalladiumAnimation.PoseStackResult result);

}
