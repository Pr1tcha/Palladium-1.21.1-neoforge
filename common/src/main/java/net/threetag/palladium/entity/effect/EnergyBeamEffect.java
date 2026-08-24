package net.threetag.palladium.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.client.energybeam.EnergyBeamManager;
import net.threetag.palladium.entity.EffectEntity;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladium.power.ability.AnimationTimer;
import net.threetag.palladium.power.ability.EnergyBeamAbility;
import net.threetag.palladium.util.property.AbilityReferenceProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;

public class EnergyBeamEffect extends EntityEffect {

    public static final PalladiumProperty<AbilityReference> ABILITY = new AbilityReferenceProperty("ability");

    @Override
    public void registerProperties(PropertyManager manager) {
        super.registerProperties(manager);
        manager.register(ABILITY, null);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(EffectEntity entity, Entity anchor, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, boolean isFirstPerson, float partialTicks) {
        if (anchor instanceof AbstractClientPlayer player) {
            AbilityInstance instance = this.get(entity, ABILITY).getEntry(player);

            if (instance != null) {
                EnergyBeamAbility.updateTargetPos(player, instance, partialTicks);
                var beam = EnergyBeamManager.INSTANCE.get(instance.getProperty(EnergyBeamAbility.BEAM));

                if (beam != null) {
                    var entityPos = entity.getPosition(partialTicks);
                    var target = instance.getProperty(EnergyBeamAbility.TARGET);
                    beam.render(player, entityPos, target, getLengthMultiplier(instance, partialTicks), poseStack, bufferSource, packedLightIn, isFirstPerson, partialTicks);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public float getLengthMultiplier(AbilityInstance instance, float partialTick) {
        if (instance.getConfiguration().getAbility() instanceof AnimationTimer animationTimer) {
            return animationTimer.getAnimationTimer(instance, partialTick, instance.getProperty(EnergyBeamAbility.SPEED) <= 0F);
        }

        return 1F;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void tick(EffectEntity entity, Entity anchor) {
        if (anchor instanceof AbstractClientPlayer player) {
            AbilityInstance instance = this.get(entity, ABILITY).getEntry(player);

            if (instance != null) {
                var beam = EnergyBeamManager.INSTANCE.get(instance.getProperty(EnergyBeamAbility.BEAM));

                if (beam == null) {
                    this.stopPlaying(entity);
                    return;
                }

                boolean isDonePlaying = instance.getProperty(EnergyBeamAbility.SPEED) <= 0 ? !instance.isEnabled() : getLengthMultiplier(instance, 0F) <= 0F && !instance.isEnabled();
                if (isDonePlaying != this.get(entity, IS_DONE_PLAYING)) {
                    this.stopPlaying(entity);
                }
            } else {
                this.stopPlaying(entity);
            }
        } else {
            this.stopPlaying(entity);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void start(Player player, AbilityReference abilityReference) {
        EffectEntity effectEntity = new EffectEntity(player.level(), player, EntityEffects.ENERGY_BEAM.get());
        ABILITY.set(effectEntity, abilityReference);
        Minecraft.getInstance().level.addEntity(effectEntity);
    }
}
