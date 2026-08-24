package net.threetag.palladium.entity;

import com.google.gson.JsonParseException;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BodyPart {

    HEAD("head", false),
    HEAD_OVERLAY("head_overlay", true),
    CHEST("chest", false),
    CHEST_OVERLAY("chest_overlay", true),
    RIGHT_ARM("right_arm", false),
    RIGHT_ARM_OVERLAY("right_arm_overlay", true),
    LEFT_ARM("left_arm", false),
    LEFT_ARM_OVERLAY("left_arm_overlay", true),
    RIGHT_LEG("right_leg", false),
    RIGHT_LEG_OVERLAY("right_leg_overlay", true),
    LEFT_LEG("left_leg", false),
    LEFT_LEG_OVERLAY("left_leg_overlay", true),
    CAPE("cape", false);

    public static final List<Item> HIDES_LAYER = new ArrayList<>();

    private final String name;
    private final boolean overlay;

    BodyPart(String name, boolean overlay) {
        this.name = name;
        this.overlay = overlay;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOverlay() {
        return this.overlay;
    }

    public static BodyPart fromJson(String name) {
        BodyPart part = byName(name);
        if (part != null) {
            return part;
        }
        throw new JsonParseException("Unknown body part '" + name + "'");
    }

    public static BodyPart byName(String name) {
        for (BodyPart bodyPart : values()) {
            if (name.equalsIgnoreCase(bodyPart.name)) {
                return bodyPart;
            }
        }
        return null;
    }

    public static class ModifiedBodyPartResult {

        private final Map<BodyPart, Integer> states = new HashMap<>();

        public ModifiedBodyPartResult hide(BodyPart part) {
            return this.set(part, false);
        }

        public ModifiedBodyPartResult remove(BodyPart part) {
            return this.set(part, true);
        }

        public ModifiedBodyPartResult set(BodyPart part, boolean remove) {
            int modification = remove ? 2 : 1;
            this.states.merge(part, modification, Math::max);
            return this;
        }

        public boolean isHiddenOrRemoved(BodyPart bodyPart) {
            return this.states.containsKey(bodyPart);
        }

        public boolean isHidden(BodyPart bodyPart) {
            return this.states.getOrDefault(bodyPart, 0) == 1;
        }

        public boolean isRemoved(BodyPart bodyPart) {
            return this.states.getOrDefault(bodyPart, 0) == 2;
        }
    }
}
