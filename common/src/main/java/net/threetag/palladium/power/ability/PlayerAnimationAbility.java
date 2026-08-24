package net.threetag.palladium.power.ability;

import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.client.model.animation.IAnimatablePlayer;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ResourceLocationProperty;

public class PlayerAnimationAbility extends Ability {

    public static final PalladiumProperty<ResourceLocation> ANIMATION = new ResourceLocationProperty("animation").configurable("ID of the animation");

    public PlayerAnimationAbility() {
        this.withProperty(ANIMATION, ResourceLocation.parse("example:animation"));
    }

    @Override
    public void firstTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity.level().isClientSide && entity instanceof IAnimatablePlayer animatablePlayer) {
            var animationContainer = animatablePlayer.palladium_getModifierLayer();
            var animation = PlayerAnimationRegistry.getAnimation(entry.getProperty(ANIMATION));

            if (animation != null) {
                animationContainer.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value), animation.playAnimation());
            }
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Allows you to play a custom player animation.";
    }

    @Override
    public boolean isExperimental() {
        return true;
    }
}
