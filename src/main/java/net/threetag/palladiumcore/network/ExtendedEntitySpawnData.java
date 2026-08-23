package net.threetag.palladiumcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

/** Bridges the old Palladium spawn-data callbacks to NeoForge's 1.21 entity hook. */
public interface ExtendedEntitySpawnData extends IEntityWithComplexSpawn {

    void saveAdditionalSpawnData(FriendlyByteBuf buf);

    void loadAdditionalSpawnData(FriendlyByteBuf buf);

    @Override
    default void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        saveAdditionalSpawnData(buffer);
    }

    @Override
    default void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        loadAdditionalSpawnData(additionalData);
    }
}
