package net.threetag.palladium.accessory;

import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.Palladium;

import java.util.Arrays;
import java.util.Collection;

public class WoodenLegAccessory extends Accessory {

    public static final ResourceLocation TEXTURE = Palladium.id("textures/models/accessories/wooden_leg.png");

    @Override
    public Collection<AccessorySlot> getPossibleSlots() {
        return Arrays.asList(AccessorySlot.LEFT_LEG, AccessorySlot.RIGHT_LEG);
    }
}
