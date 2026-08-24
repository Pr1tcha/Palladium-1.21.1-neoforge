package net.threetag.palladium.util.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.threetag.palladium.util.ComponentUtil;

import java.util.Objects;

public class EnchantmentProperty extends PalladiumProperty<Enchantment> {

    public EnchantmentProperty(String key) {
        super(key);
    }

    @Override
    public Enchantment fromJSON(JsonElement jsonElement) {
        ResourceLocation id = ResourceLocation.parse(jsonElement.getAsString());
        Enchantment enchantment = get(id);
        if (enchantment == null) {
            throw new JsonParseException("Unknown enchantment '" + id + "'");
        }
        return enchantment;
    }

    @Override
    public JsonElement toJSON(Enchantment value) {
        return new JsonPrimitive(getKey(value).toString());
    }

    @Override
    public Enchantment fromNBT(Tag tag, Enchantment defaultValue) {
        if (tag instanceof StringTag stringTag) {
            Enchantment enchantment = get(ResourceLocation.parse(stringTag.getAsString()));
            if (enchantment != null) {
                return enchantment;
            }
        }
        return defaultValue;
    }

    @Override
    public Tag toNBT(Enchantment value) {
        return StringTag.valueOf(getKey(value).toString());
    }

    @Override
    public Enchantment fromBuffer(FriendlyByteBuf buf) {
        return get(buf.readResourceLocation());
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Object value) {
        buf.writeResourceLocation(getKey((Enchantment) value));
    }

    @Override
    public String getString(Enchantment value) {
        return value == null ? null : getKey(value).toString();
    }

    @Override
    public String getPropertyType() {
        return "enchantment";
    }

    private static Enchantment get(ResourceLocation id) {
        return ComponentUtil.registryProvider()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(ResourceKey.create(Registries.ENCHANTMENT, id))
                .map(holder -> holder.value())
                .orElse(null);
    }

    private static ResourceLocation getKey(Enchantment value) {
        return ComponentUtil.registryProvider()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .listElements()
                .filter(holder -> Objects.equals(holder.value(), value))
                .findFirst()
                .map(holder -> holder.key().location())
                .orElseThrow(() -> new IllegalArgumentException("Unregistered enchantment: " + value));
    }
}
