package net.threetag.palladiumcore.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Compatibility facade for PalladiumCore's legacy registration API.
 */
public final class DeferredRegister<T> implements Iterable<RegistrySupplier<T>> {

    public static final List<RegistrySupplier<PoiType>> POI_TYPES_TO_FIX = new ArrayList<>();

    private final String modId;
    private final net.neoforged.neoforge.registries.DeferredRegister<T> delegate;
    private final List<RegistrySupplier<T>> entries = new ArrayList<>();
    private boolean registered;

    private DeferredRegister(String modId, ResourceKey<? extends Registry<T>> registryKey) {
        this.modId = modId;
        this.delegate = net.neoforged.neoforge.registries.DeferredRegister.create(registryKey, modId);
    }

    public void register() {
        if (this.registered) {
            throw new IllegalStateException("Deferred register for '" + this.modId + "' was registered more than once");
        }
        this.registered = true;
        this.delegate.register(ModEventBusRegistry.get(this.modId));
    }

    @SuppressWarnings("unchecked")
    public <R extends T> RegistrySupplier<R> register(String id, Supplier<R> supplier) {
        DeferredHolder<T, R> holder = this.delegate.register(id, supplier);
        RegistrySupplier<R> registrySupplier = new RegistrySupplier<>(holder.getId(), holder);
        this.entries.add((RegistrySupplier<T>) registrySupplier);
        return registrySupplier;
    }

    public Collection<RegistrySupplier<T>> getEntries() {
        return List.copyOf(this.entries);
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return this.getEntries().iterator();
    }

    public static <T> DeferredRegister<T> create(String modId, ResourceKey<? extends Registry<T>> resourceKey) {
        return new DeferredRegister<>(modId, resourceKey);
    }

    public static <T> DeferredRegister<T> create(String modId, PalladiumRegistry<T> registry) {
        return create(modId, registry.getRegistryKey());
    }
}
