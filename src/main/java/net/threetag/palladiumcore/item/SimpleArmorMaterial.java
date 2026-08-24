package net.threetag.palladiumcore.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Creates a direct 1.21 armor-material holder from the legacy JSON fields. */
public final class SimpleArmorMaterial {

    private final Holder<ArmorMaterial> holder;
    private final int durabilityMultiplier;

    public SimpleArmorMaterial(ResourceLocation assetId, int durabilityMultiplier,
                               Map<ArmorItem.Type, Integer> slotProtections, int enchantmentValue,
                               Supplier<SoundEvent> equipSound, float toughness, float knockbackResistance,
                               Supplier<Ingredient> repairMaterial) {
        this(assetId, durabilityMultiplier, slotProtections, enchantmentValue, equipSound, toughness,
                knockbackResistance, repairMaterial, false);
    }

    public SimpleArmorMaterial(ResourceLocation assetId, int durabilityMultiplier,
                               Map<ArmorItem.Type, Integer> slotProtections, int enchantmentValue,
                               Supplier<SoundEvent> equipSound, float toughness, float knockbackResistance,
                               Supplier<Ingredient> repairMaterial, boolean dyeable) {
        this.durabilityMultiplier = durabilityMultiplier;
        ArmorMaterial material = new ArmorMaterial(
                Map.copyOf(slotProtections),
                enchantmentValue,
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound.get()),
                repairMaterial,
                List.of(new ArmorMaterial.Layer(assetId, "", dyeable)),
                toughness,
                knockbackResistance
        );
        this.holder = Holder.direct(material);
    }

    public Holder<ArmorMaterial> holder() {
        return holder;
    }

    public int durabilityMultiplier() {
        return durabilityMultiplier;
    }
}
