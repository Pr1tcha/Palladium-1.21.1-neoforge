package net.threetag.palladiumcore.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Legacy-compatible registry handle backed by a NeoForge deferred holder or a
 * direct value supplier.
 */
public class RegistrySupplier<T> implements Supplier<T> {

    private final ResourceLocation id;
    private final Supplier<? extends T> supplier;

    public RegistrySupplier(ResourceLocation id, Supplier<? extends T> supplier) {
        this.id = Objects.requireNonNull(id, "id");
        this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    public RegistrySupplier(ResourceLocation id, T raw) {
        this(id, () -> raw);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public T get() {
        return this.supplier.get();
    }
}
