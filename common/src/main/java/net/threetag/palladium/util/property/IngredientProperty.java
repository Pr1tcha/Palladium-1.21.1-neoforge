package net.threetag.palladium.util.property;

import com.google.gson.JsonElement;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.threetag.palladium.util.json.GsonUtil;

public class IngredientProperty extends PalladiumProperty<Ingredient> {

    public IngredientProperty(String key) {
        super(key);
    }

    @Override
    public Ingredient fromJSON(JsonElement jsonElement) {
        return GsonUtil.parseIngredient(jsonElement);
    }

    @Override
    public JsonElement toJSON(Ingredient value) {
        return GsonUtil.ingredientToJson(value);
    }

    @Override
    public Ingredient fromNBT(Tag tag, Ingredient defaultValue) {
        if (tag instanceof StringTag stringTag) {
            return GsonUtil.parseIngredient(GsonHelper.parse(stringTag.getAsString()));
        }
        return defaultValue;
    }

    @Override
    public Tag toNBT(Ingredient value) {
        return StringTag.valueOf(GsonUtil.ingredientToJson(value).toString());
    }

    @Override
    public Ingredient fromBuffer(FriendlyByteBuf buf) {
        return GsonUtil.parseIngredient(GsonHelper.parse(buf.readUtf()));
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Object value) {
        buf.writeUtf(GsonUtil.ingredientToJson((Ingredient) value).toString());
    }

    @Override
    public String getString(Ingredient value) {
        return GsonUtil.ingredientToJson(value).toString();
    }

    @Override
    public String getPropertyType() {
        return "ingredient";
    }
}
