package net.threetag.palladium.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public abstract class EnergyItem extends Item {

    private final int capacity, maxInput, maxOutput;

    public EnergyItem(Properties properties, int capacity, int maxInput, int maxOutput) {
        super(properties);
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    public int getEnergyCapacity(ItemStack stack) {
        return capacity;
    }

    public int getEnergyMaxInput(ItemStack stack) {
        return maxInput;
    }

    public int getEnergyMaxOutput(ItemStack stack) {
        return maxOutput;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (!tag.contains("energy")) {
                tag.putInt("energy", 0);
            }
        });
    }
}
