package net.threetag.palladium.energy.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.threetag.palladium.energy.IEnergyStorage;
import net.threetag.palladium.item.EnergyItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyHelperImpl {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(EnergyItem.class::isInstance)
                .forEach(item -> event.registerItem(
                        Capabilities.EnergyStorage.ITEM,
                        (stack, ignored) -> new ItemEnergyStorage(stack, (EnergyItem) stack.getItem()),
                        item));
    }

    public static Optional<IEnergyStorage> getFromItemStack(ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).map(Wrapper::new);
    }

    public static Optional<IEnergyStorage> getFromBlockEntity(Level level, BlockPos pos, @Nullable Direction side) {
        return Optional.ofNullable(level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side)).map(Wrapper::new);
    }

    public static long moveBetweenBlockEntities(Level level, BlockPos from, Direction fromSide, BlockPos to, Direction toSide, long maxAmount) {
        var fromStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, from, fromSide);
        var toStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, to, toSide);
        if (fromStorage == null || toStorage == null || maxAmount <= 0) {
            return 0;
        }

        int requested = (int) Math.min(Integer.MAX_VALUE, maxAmount);
        int maxExtracted = fromStorage.extractEnergy(requested, true);
        int accepted = toStorage.receiveEnergy(maxExtracted, false);
        return fromStorage.extractEnergy(accepted, false);
    }

    public record Wrapper(net.neoforged.neoforge.energy.IEnergyStorage forgeStorage) implements IEnergyStorage {

        @Override
        public boolean canInsert() {
            return this.forgeStorage.canReceive();
        }

        @Override
        public int insertEnergy(int maxAmount, boolean simulate) {
            return this.forgeStorage.receiveEnergy(maxAmount, simulate);
        }

        @Override
        public boolean canWithdraw() {
            return this.forgeStorage.canExtract();
        }

        @Override
        public int withdrawEnergy(int maxAmount, boolean simulate) {
            return this.forgeStorage.extractEnergy(maxAmount, simulate);
        }

        @Override
        public int getEnergyAmount() {
            return this.forgeStorage.getEnergyStored();
        }

        @Override
        public int getEnergyCapacity() {
            return this.forgeStorage.getMaxEnergyStored();
        }
    }

}
