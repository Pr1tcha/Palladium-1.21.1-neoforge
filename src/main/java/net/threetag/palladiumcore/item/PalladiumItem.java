package net.threetag.palladiumcore.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface PalladiumItem {

    default void armorTick(ItemStack stack, Level level, Player player) {
    }

    @Nullable
    default EquipmentSlot getSlotForItem(ItemStack stack) {
        return null;
    }
}
