package net.threetag.palladium.data.forge;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.PalladiumDamageTypes;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PalladiumDamageTypeProvider extends JsonCodecProvider<DamageType> {

    public PalladiumDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(
                output,
                PackOutput.Target.DATA_PACK,
                "damage_type",
                PackType.SERVER_DATA,
                DamageType.DIRECT_CODEC,
                lookupProvider,
                Palladium.MOD_ID,
                existingFileHelper
        );
    }

    @Override
    protected void gather() {
        getEntries().forEach(this::unconditional);
    }

    private static Map<ResourceLocation, DamageType> getEntries() {
        return Map.of(PalladiumDamageTypes.ENERGY_BEAM.location(), new DamageType("palladium.energy_beam", 0.1F));
    }
}
