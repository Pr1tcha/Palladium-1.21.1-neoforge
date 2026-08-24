package net.threetag.palladium.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ParticleUtil {

    private ParticleUtil() {
    }

    public static ParticleOptions parseOptions(ParticleType<?> type, String options, HolderLookup.Provider registries) throws CommandSyntaxException {
        String particleId = BuiltInRegistries.PARTICLE_TYPE.getKey(type).toString();
        String trimmedOptions = options.trim();
        String serializedParticle = particleId + (trimmedOptions.isEmpty() || trimmedOptions.startsWith("{") ? trimmedOptions : " " + trimmedOptions);
        return ParticleArgument.readParticle(new StringReader(serializedParticle), registries);
    }
}
