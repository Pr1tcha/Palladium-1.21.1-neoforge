package net.threetag.palladium.util.property;

import com.google.gson.JsonElement;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.threetag.palladium.util.ComponentUtil;

public class ComponentProperty extends PalladiumProperty<Component> {

    public ComponentProperty(String key) {
        super(key);
    }

    @Override
    public Component fromJSON(JsonElement jsonElement) {
        return ComponentUtil.fromJson(jsonElement);
    }

    @Override
    public JsonElement toJSON(Component value) {
        return ComponentUtil.toJsonTree(value);
    }

    @Override
    public Component fromNBT(Tag tag, Component defaultValue) {
        if (tag instanceof StringTag stringTag) {
            return ComponentUtil.fromJson(stringTag.getAsString());
        }
        return defaultValue;
    }

    @Override
    public Tag toNBT(Component value) {
        return StringTag.valueOf(ComponentUtil.toJson(value));
    }

    @Override
    public Component fromBuffer(FriendlyByteBuf buf) {
        return ComponentUtil.read(buf);
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Object value) {
        ComponentUtil.write(buf, (Component) value);
    }

    @Override
    public String getPropertyType() {
        return "component";
    }
}
