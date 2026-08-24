package net.threetag.palladium.client.energybeam;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.client.particleemitter.ParticleEmitter;
import net.threetag.palladium.entity.BodyPart;
import net.threetag.palladium.client.renderer.entity.BodyPartClient;
import net.threetag.palladium.util.ParticleUtil;
import net.threetag.palladium.util.PerspectiveValue;
import net.threetag.palladium.util.json.GsonUtil;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EnergyBeam {

    private final BodyPart anchor;
    private final PerspectiveValue<Vector3f> offset;
    private final EnergyBeamRenderer renderer;
    private final List<Particle> particles;

    public EnergyBeam(BodyPart anchor, PerspectiveValue<Vector3f> offset, EnergyBeamRenderer renderer, List<Particle> particles) {
        this.anchor = anchor;
        this.offset = offset;
        this.renderer = renderer;
        this.particles = particles;
    }

    public Vec3 getOriginPosition(AbstractClientPlayer player, float partialTick) {
        return BodyPartClient.getInWorldPosition(this.anchor, this.offset.get(), player, partialTick);
    }

    public void spawnParticles(Level level, Vec3 pos) {
        for (Particle particle : this.particles) {
            particle.spawn(level, pos);
        }
    }

    public void render(AbstractClientPlayer player, Vec3 anchor, Vec3 target, float lengthMultiplier, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, boolean isFirstPerson, float partialTick) {
        var origin = this.getOriginPosition(player, partialTick).subtract(anchor);
        target = target.subtract(anchor);

        poseStack.pushPose();
        poseStack.translate(origin.x, origin.y, origin.z);
        this.renderer.render(player, origin, target, lengthMultiplier, poseStack, bufferSource, packedLightIn, isFirstPerson, partialTick);
        poseStack.popPose();
    }

    public static EnergyBeam fromJson(JsonObject json) {
        var serializer = EnergyBeamManager.getRenderer(GsonUtil.getAsResourceLocation(json, "type", LaserBeamRenderer.Serializer.ID));

        if (serializer == null) {
            throw new JsonParseException("Unknown energy beam renderer '" + GsonUtil.getAsResourceLocation(json, "type", LaserBeamRenderer.Serializer.ID) + "'");
        }

        return new EnergyBeam(
                BodyPart.fromJson(GsonHelper.getAsString(json, "body_part")),
                PerspectiveValue.getFromJson(json, "offset", j -> GsonUtil.convertToVector3f(j, "offset").div(16, -16, 16), new Vector3f()),
                serializer.fromJson(json),
                json.has("particles") ? GsonUtil.fromListOrPrimitive(json.get("particles"), jsonElement -> Particle.fromJson(GsonHelper.convertToJsonObject(jsonElement, "particles[].$"))) : Collections.emptyList()
        );
    }

    public record Particle(ParticleType<?> particleType, String options, ParticleEmitter emitter) {

        public static Particle fromJson(JsonObject json) {
            var particleTypeId = GsonUtil.getAsResourceLocation(json, "particle_type");

            if (!BuiltInRegistries.PARTICLE_TYPE.containsKey(particleTypeId)) {
                throw new JsonParseException("Unknown particle type '" + particleTypeId + "'");
            }

            var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleTypeId);
            var options = GsonHelper.getAsString(json, "options", "");
            var emitter = ParticleEmitter.fromJson(json);
            return new Particle(particleType, options, emitter);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void spawn(Level level, Vec3 pos) {
            try {
                ParticleType type = this.particleType;
                ParticleOptions options = ParticleUtil.parseOptions(type, this.options, level.registryAccess());
                this.emitter.spawnAtPosition(level, pos, options);
            } catch (CommandSyntaxException ignored) {

            }
        }
    }

}
