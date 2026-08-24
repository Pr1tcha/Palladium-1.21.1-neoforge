package net.threetag.palladium.client.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.client.energybeam.EnergyBeamManager;
import net.threetag.palladium.entity.EffectEntity;
import net.threetag.palladium.entity.effect.EnergyBeamEffect;
import net.threetag.palladium.entity.effect.EntityEffect;
import net.threetag.palladium.entity.effect.EntityEffects;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladium.power.ability.AnimationTimer;
import net.threetag.palladium.power.ability.EnergyBeamAbility;

public final class EnergyBeamEffectClient implements EnergyBeamEffect.ClientHandler {

    @Override
    public void tick(EnergyBeamEffect effect, EffectEntity entity, Entity anchor) {
        if (!(anchor instanceof AbstractClientPlayer player)) {
            effect.stopPlaying(entity);
            return;
        }

        AbilityReference reference = effect.get(entity, EnergyBeamEffect.ABILITY);
        AbilityInstance instance = reference != null ? reference.getEntry(player) : null;
        if (instance == null || EnergyBeamManager.INSTANCE.get(instance.getProperty(EnergyBeamAbility.BEAM)) == null) {
            effect.stopPlaying(entity);
            return;
        }

        boolean donePlaying = instance.getProperty(EnergyBeamAbility.SPEED) <= 0
                ? !instance.isEnabled()
                : getLengthMultiplier(instance, 0F) <= 0F && !instance.isEnabled();
        if (donePlaying != effect.get(entity, EntityEffect.IS_DONE_PLAYING)) {
            effect.stopPlaying(entity);
        }
    }

    @Override
    public void start(Player player, AbilityReference abilityReference) {
        EffectEntity effectEntity = new EffectEntity(player.level(), player, EntityEffects.ENERGY_BEAM.get());
        EnergyBeamEffect.ABILITY.set(effectEntity, abilityReference);
        Minecraft.getInstance().level.addEntity(effectEntity);
    }

    public static void render(EnergyBeamEffect effect, EffectEntity entity, Entity anchor, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, boolean isFirstPerson, float partialTicks) {
        if (!(anchor instanceof AbstractClientPlayer player)) {
            return;
        }

        AbilityReference reference = effect.get(entity, EnergyBeamEffect.ABILITY);
        AbilityInstance instance = reference != null ? reference.getEntry(player) : null;
        if (instance == null) {
            return;
        }

        EnergyBeamAbility.updateTargetPos(player, instance, partialTicks);
        var beam = EnergyBeamManager.INSTANCE.get(instance.getProperty(EnergyBeamAbility.BEAM));
        if (beam != null) {
            beam.render(player, entity.getPosition(partialTicks), instance.getProperty(EnergyBeamAbility.TARGET), getLengthMultiplier(instance, partialTicks), poseStack, bufferSource, packedLight, isFirstPerson, partialTicks);
        }
    }

    private static float getLengthMultiplier(AbilityInstance instance, float partialTick) {
        if (instance.getConfiguration().getAbility() instanceof AnimationTimer animationTimer) {
            return animationTimer.getAnimationTimer(instance, partialTick, instance.getProperty(EnergyBeamAbility.SPEED) <= 0F);
        }

        return 1F;
    }
}
