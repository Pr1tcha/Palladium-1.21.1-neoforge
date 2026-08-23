package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ParticleProviderRegistry {

    private static final Map<Supplier<? extends ParticleType<?>>, ParticleProvider<?>> PROVIDERS = new LinkedHashMap<>();
    private static final Map<Supplier<? extends ParticleType<?>>, ParticleEngine.SpriteParticleRegistration<?>> SPRITES = new LinkedHashMap<>();

    private ParticleProviderRegistry() {
    }

    public static <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> type,
                                                            ParticleProvider<T> provider) {
        PROVIDERS.put(type, provider);
    }

    public static <T extends ParticleOptions> void register(Supplier<? extends ParticleType<T>> type,
                                                            ParticleEngine.SpriteParticleRegistration<T> registration) {
        SPRITES.put(type, registration);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void registerProviders(RegisterParticleProvidersEvent event) {
        PROVIDERS.forEach((type, provider) -> event.registerSpecial((ParticleType) type.get(), (ParticleProvider) provider));
        SPRITES.forEach((type, registration) -> event.registerSpriteSet((ParticleType) type.get(), (ParticleEngine.SpriteParticleRegistration) registration));
    }
}
