package net.threetag.palladium.energy.forge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.threetag.palladium.item.EnergyItem;

public class EnergyHelperImpl {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(EnergyItem.class::isInstance)
                .forEach(item -> event.registerItem(
                        Capabilities.EnergyStorage.ITEM,
                        (stack, ignored) -> new ItemEnergyStorage(stack, (EnergyItem) stack.getItem()),
                        item));
    }

}
