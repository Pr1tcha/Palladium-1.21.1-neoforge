package net.threetag.palladium.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergyHelper {

    public static int getEnergyStoredInItem(ItemStack stack) {
        AtomicInteger energyStored = new AtomicInteger(0);
        getFromItemStack(stack).ifPresent(storage -> energyStored.set(storage.getEnergyAmount()));
        return energyStored.get();
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

    public record Wrapper(net.neoforged.neoforge.energy.IEnergyStorage neoForgeStorage) implements IEnergyStorage {

        @Override
        public boolean canInsert() {
            return this.neoForgeStorage.canReceive();
        }

        @Override
        public int insertEnergy(int maxAmount, boolean simulate) {
            return this.neoForgeStorage.receiveEnergy(maxAmount, simulate);
        }

        @Override
        public boolean canWithdraw() {
            return this.neoForgeStorage.canExtract();
        }

        @Override
        public int withdrawEnergy(int maxAmount, boolean simulate) {
            return this.neoForgeStorage.extractEnergy(maxAmount, simulate);
        }

        @Override
        public int getEnergyAmount() {
            return this.neoForgeStorage.getEnergyStored();
        }

        @Override
        public int getEnergyCapacity() {
            return this.neoForgeStorage.getMaxEnergyStored();
        }
    }

}
