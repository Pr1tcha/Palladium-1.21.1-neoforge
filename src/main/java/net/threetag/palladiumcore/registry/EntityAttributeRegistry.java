package net.threetag.palladiumcore.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class EntityAttributeRegistry {

    private static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> ATTRIBUTES = new LinkedHashMap<>();
    private static final List<Modification> MODIFICATIONS = new ArrayList<>();

    private EntityAttributeRegistry() {
    }

    public static void register(Supplier<? extends EntityType<? extends LivingEntity>> typeSupplier,
                                Supplier<AttributeSupplier.Builder> builderSupplier) {
        ATTRIBUTES.put(typeSupplier, builderSupplier);
    }

    public static void registerModification(Supplier<? extends EntityType<? extends LivingEntity>> typeSupplier,
                                            Supplier<? extends Attribute> attributeSupplier,
                                            Double value) {
        MODIFICATIONS.add(new Modification(typeSupplier, attributeSupplier, value));
    }

    public static void registerModification(Supplier<? extends EntityType<? extends LivingEntity>> typeSupplier,
                                            Supplier<? extends Attribute> attributeSupplier) {
        registerModification(typeSupplier, attributeSupplier, null);
    }

    static void createAttributes(EntityAttributeCreationEvent event) {
        ATTRIBUTES.forEach((type, attributes) -> event.put(type.get(), attributes.get().build()));
    }

    static void modifyAttributes(EntityAttributeModificationEvent event) {
        for (Modification modification : MODIFICATIONS) {
            Attribute attribute = modification.attributeSupplier().get();
            var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
            if (modification.value() == null) {
                event.add(modification.typeSupplier().get(), holder);
            } else {
                event.add(modification.typeSupplier().get(), holder, modification.value());
            }
        }
    }

    private record Modification(Supplier<? extends EntityType<? extends LivingEntity>> typeSupplier,
                                Supplier<? extends Attribute> attributeSupplier,
                                Double value) {
    }
}
