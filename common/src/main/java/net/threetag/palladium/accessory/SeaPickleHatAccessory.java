package net.threetag.palladium.accessory;

import java.util.Collection;
import java.util.Collections;

public class SeaPickleHatAccessory extends Accessory {

    @Override
    public Collection<AccessorySlot> getPossibleSlots() {
        return Collections.singletonList(AccessorySlot.HAT);
    }
}
