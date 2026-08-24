package net.threetag.palladiumcore.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Legacy-compatible view of a native NeoForge custom registry.
 */
public final class PalladiumRegistry<T> implements Iterable<T> {

    private final ResourceKey<Registry<T>> registryKey;
    private final Registry<T> registry;

    private PalladiumRegistry(ResourceKey<Registry<T>> registryKey, Registry<T> registry) {
        this.registryKey = registryKey;
        this.registry = registry;
    }

    public static <T> PalladiumRegistry<T> create(Class<T> ignoredType, ResourceLocation id) {
        ResourceKey<Registry<T>> registryKey = ResourceKey.createRegistryKey(id);
        net.neoforged.neoforge.registries.DeferredRegister<T> registryRegistrar =
                net.neoforged.neoforge.registries.DeferredRegister.create(registryKey, id.getNamespace());
        Registry<T> registry = registryRegistrar.makeRegistry(builder -> {
        });
        registryRegistrar.register(ModEventBusRegistry.get(id.getNamespace()));
        return new PalladiumRegistry<>(registryKey, registry);
    }

    public ResourceKey<Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    @Nullable
    public T get(ResourceLocation key) {
        return this.registry.get(key);
    }

    @Nullable
    public ResourceLocation getKey(T object) {
        return this.registry.getKey(object);
    }

    public boolean containsKey(ResourceLocation key) {
        return this.registry.containsKey(key);
    }

    public Set<ResourceLocation> getKeys() {
        return Set.copyOf(this.registry.keySet());
    }

    public Collection<T> getValues() {
        return List.copyOf(this.registry.stream().toList());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.getValues().iterator();
    }
}
