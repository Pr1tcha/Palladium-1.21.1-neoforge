package net.threetag.palladium.power.ability;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.threetag.palladium.util.ComponentUtil;

public class AbilityDescription {

    private final Component lockedDescription;
    private final Component unlockedDescription;

    public AbilityDescription(Component lockedDescription, Component unlockedDescription) {
        this.lockedDescription = lockedDescription;
        this.unlockedDescription = unlockedDescription;
    }

    public AbilityDescription(Component description) {
        this.lockedDescription = this.unlockedDescription = description;
    }

    public Component get(boolean unlocked) {
        return unlocked ? this.unlockedDescription : this.lockedDescription;
    }

    public static AbilityDescription fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AbilityDescription(ComponentUtil.fromJson(jsonElement));
        } else if (jsonElement.isJsonObject()) {
            var obj = jsonElement.getAsJsonObject();

            if (obj.has("locked") && obj.has("unlocked")) {
                return new AbilityDescription(
                        ComponentUtil.fromJson(obj.get("locked")),
                        ComponentUtil.fromJson(obj.get("unlocked"))
                );
            } else {
                return new AbilityDescription(ComponentUtil.fromJson(jsonElement));
            }
        } else {
            return new AbilityDescription(ComponentUtil.fromJson(jsonElement));
        }
    }

    public JsonElement toJson() {
        if (this.lockedDescription == this.unlockedDescription) {
            return ComponentUtil.toJsonTree(this.lockedDescription);
        } else {
            JsonObject json = new JsonObject();
            json.add("locked", ComponentUtil.toJsonTree(this.lockedDescription));
            json.add("unlocked", ComponentUtil.toJsonTree(this.unlockedDescription));
            return json;
        }
    }

    public static AbilityDescription fromNbt(CompoundTag nbt) {
        var locked = nbt.getString("Locked");
        var unlocked = nbt.getString("Unlocked");

        if (locked.equals(unlocked)) {
            return new AbilityDescription(ComponentUtil.fromJson(locked));
        } else {
            return new AbilityDescription(ComponentUtil.fromJson(locked), ComponentUtil.fromJson(unlocked));
        }
    }

    public CompoundTag toNbt() {
        var nbt = new CompoundTag();
        nbt.putString("Locked", ComponentUtil.toJson(this.lockedDescription));
        nbt.putString("Unlocked", ComponentUtil.toJson(this.unlockedDescription));
        return nbt;
    }

    public static AbilityDescription fromBuffer(FriendlyByteBuf buf) {
        boolean same = buf.readBoolean();
        if (same) {
            return new AbilityDescription(ComponentUtil.read(buf));
        } else {
            return new AbilityDescription(ComponentUtil.read(buf), ComponentUtil.read(buf));
        }
    }

    public void toBuffer(FriendlyByteBuf buf) {
        buf.writeBoolean(this.lockedDescription == this.unlockedDescription);
        ComponentUtil.write(buf, this.lockedDescription);
        if (this.lockedDescription != this.unlockedDescription) {
            ComponentUtil.write(buf, this.unlockedDescription);
        }
    }

}
