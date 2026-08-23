package net.threetag.palladium.power.ability;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.client.particleemitter.ParticleEmitterManager;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ParticleTypeProperty;
import net.threetag.palladium.util.property.ResourceLocationListProperty;
import net.threetag.palladium.util.property.StringProperty;

import java.util.Collections;
import java.util.List;

public class ParticleAbility extends Ability {

    public static final PalladiumProperty<List<ResourceLocation>> PARTICLE_EMITTER = new ResourceLocationListProperty("emitter").configurable("Configuration for where the particle spawns at. Check wiki for information.");
    public static final PalladiumProperty<ParticleType<?>> PARTICLE = new ParticleTypeProperty("particle_type").configurable("ID of the particle you want to spawn.");
    public static final PalladiumProperty<String> OPTIONS = new StringProperty("options").configurable("Additional options for the particle (like color of a dust particle)");

    public ParticleAbility() {
        this.withProperty(PARTICLE_EMITTER, Collections.singletonList(ResourceLocation.parse("example:emitter")))
                .withProperty(PARTICLE, ParticleTypes.DUST)
                .withProperty(OPTIONS, "");
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled && entity.level().isClientSide) {
            this.tickClient(entity, entry);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Environment(EnvType.CLIENT)
    private void tickClient(LivingEntity entity, AbilityInstance entry) {
        if (entity instanceof AbstractClientPlayer player) {
            try {
                ParticleType type = entry.getProperty(PARTICLE);
                ParticleOptions options = type.getDeserializer().fromCommand(type, new StringReader(" " + entry.getProperty(OPTIONS).trim() + " "));

                for (ResourceLocation id : entry.getProperty(PARTICLE_EMITTER)) {
                    var emitter = ParticleEmitterManager.INSTANCE.get(id);

                    if (emitter != null) {
                        emitter.spawnParticles(entity.level(), player, options, Minecraft.getInstance().getDeltaFrameTime());
                    }
                }
            } catch (CommandSyntaxException ignored) {

            }
        }
    }
}
