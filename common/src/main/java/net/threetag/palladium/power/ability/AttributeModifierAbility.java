package net.threetag.palladium.power.ability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.PalladiumAttributes;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.util.icon.ItemIcon;
import net.threetag.palladium.util.property.*;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

public class AttributeModifierAbility extends Ability {

    public static final PalladiumProperty<Attribute> ATTRIBUTE = new AttributeProperty("attribute").configurable("Determines which attribute should be modified. Possible attributes: " + getAttributeList());
    public static final PalladiumProperty<Double> AMOUNT = new DoubleProperty("amount").configurable("The amount for the giving attribute modifier");
    public static final PalladiumProperty<Integer> OPERATION = new IntegerProperty("operation").configurable("The operation for the giving attribute modifier (More: https://minecraft.gamepedia.com/Attribute#Operations)");
    public static final PalladiumProperty<UUID> UUID = new UUIDProperty("uuid").configurable("Sets the unique identifier for this attribute modifier. If not specified it will generate a random one");

    public AttributeModifierAbility() {
        this.withProperty(ICON, new ItemIcon(Items.IRON_CHESTPLATE));
        this.withProperty(ATTRIBUTE, Attributes.ARMOR.value());
        this.withProperty(AMOUNT, 1D);
        this.withProperty(OPERATION, 0);
        this.withProperty(UUID, java.util.UUID.fromString("498be4fb-af04-42f2-8948-e6ccdc0d99e1"));
    }

    @Override
    public void tick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        if (enabled) {
            Attribute attribute = entry.getProperty(ATTRIBUTE);
            AttributeInstance instance = entity.getAttribute(PalladiumAttributes.holder(attribute));

            if (instance == null || entity.level().isClientSide) {
                return;
            }

            UUID uuid = entry.getProperty(UUID);
            ResourceLocation modifierId = modifierId(uuid);
            AttributeModifier modifier = instance.getModifier(modifierId);

            // Remove modifier if amount or operation dont match
            if (modifier != null && (modifier.amount() != entry.getProperty(AMOUNT) || modifier.operation().id() != entry.getProperty(OPERATION))) {
                instance.removeModifier(modifierId);
                modifier = null;
            }

            if (modifier == null) {
                modifier = new AttributeModifier(modifierId, entry.getProperty(AMOUNT), AttributeModifier.Operation.BY_ID.apply(entry.getProperty(OPERATION)));
                instance.addTransientModifier(modifier);
            }
        } else {
            this.lastTick(entity, entry, holder, false);
        }
    }

    @Override
    public void lastTick(LivingEntity entity, AbilityInstance entry, IPowerHolder holder, boolean enabled) {
        AttributeInstance instance = entity.getAttribute(PalladiumAttributes.holder(entry.getProperty(ATTRIBUTE)));
        if (instance != null) {
            instance.removeModifier(modifierId(entry.getProperty(UUID)));
        }
    }

    public static String getAttributeList() {
        return BuiltInRegistries.ATTRIBUTE.keySet().stream().map(ResourceLocation::toString).sorted(Comparator.naturalOrder()).collect(Collectors.joining(", "));
    }

    private static ResourceLocation modifierId(UUID uuid) {
        return Palladium.id(uuid.toString());
    }

    @Override
    public String getDocumentationDescription() {
        return "Adds an attribute modifier to the entity while the ability is enabled.";
    }
}
