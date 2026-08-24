package net.threetag.palladium.accessory;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.PalladiumPlayerExtension;
import net.threetag.palladium.util.SupporterHandler;
import net.threetag.palladiumcore.registry.PalladiumRegistry;
import net.threetag.palladiumcore.util.Platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class Accessory {

    public static final PalladiumRegistry<Accessory> REGISTRY = PalladiumRegistry.create(Accessory.class, Palladium.id("accessories"));
    private boolean exclusive = false;

    public boolean isAvailable(Player entity) {
        return this.isAvailable(SupporterHandler.getPlayerData(entity.getUUID()));
    }

    public boolean isAvailable(SupporterHandler.PlayerData data) {
        return !this.exclusive || !Platform.isProduction() || data.hasAccessory(this);
    }

    public Component getDisplayName() {
        return Component.translatable(Util.makeDescriptionId("accessory", REGISTRY.getKey(this)));
    }

    @Override
    public String toString() {
        return REGISTRY.getKey(this).toString();
    }

    public Accessory setExclusive() {
        this.exclusive = true;
        return this;
    }

    public abstract Collection<AccessorySlot> getPossibleSlots();

    public static List<Accessory> getAvailableAccessories(SupporterHandler.PlayerData data) {
        List<Accessory> list = new ArrayList<>();

        for (Accessory accessory : Accessory.REGISTRY.getValues()) {
            if (accessory.isAvailable(data)) {
                list.add(accessory);
            }
        }

        return list;
    }

    public static List<Accessory> getAvailableAccessories(SupporterHandler.PlayerData data, AccessorySlot slot) {
        List<Accessory> list = new ArrayList<>();

        for (Accessory accessory : Accessory.REGISTRY.getValues()) {
            if (accessory.getPossibleSlots().contains(slot) && accessory.isAvailable(data)) {
                list.add(accessory);
            }
        }

        return list;
    }

    public static Optional<AccessoryPlayerData> getPlayerData(Player player) {
        if (player instanceof PalladiumPlayerExtension ext) {
            return Optional.of(ext.palladium$getAccessories());
        } else {
            return Optional.empty();
        }
    }

}
