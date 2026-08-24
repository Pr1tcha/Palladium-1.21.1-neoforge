package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.energybar.EnergyBar;
import net.threetag.palladium.power.energybar.EnergyBarReference;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UpdatePowersMessage extends MessageS2C {

    private final int entityId;
    private final List<ResourceLocation> toRemove, toAdd;
    private final List<Triple<EnergyBarReference, Integer, Integer>> energyBars;

    public UpdatePowersMessage(LivingEntity livingEntity, List<ResourceLocation> toRemove, List<ResourceLocation> toAdd) {
        this.entityId = livingEntity.getId();
        this.toRemove = toRemove;
        this.toAdd = toAdd;

        this.energyBars = new ArrayList<>();

        var opt = PowerManager.getPowerHandler(livingEntity);

        if (opt.isPresent()) {
            for (IPowerHolder holder : opt.get().getPowerHolders().values()) {
                for (EnergyBar energyBar : holder.getEnergyBars().values()) {
                    this.energyBars.add(Triple.of(new EnergyBarReference(holder.getPower().getId(), energyBar.getConfiguration().name()), energyBar.get(), energyBar.getMax()));
                }
            }
        }
    }

    public UpdatePowersMessage(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.toRemove = buf.readList(FriendlyByteBuf::readResourceLocation);
        this.toAdd = buf.readList(FriendlyByteBuf::readResourceLocation);
        this.energyBars = buf.readList(buf1 -> {
            var ref = EnergyBarReference.fromBuffer(buf1);
            int val = buf1.readInt();
            int max = buf1.readInt();
            return Triple.of(ref, val, max);
        });
    }

    @Override
    public @NotNull MessageType getType() {
        return PalladiumNetwork.UPDATE_POWERS;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeCollection(this.toRemove, FriendlyByteBuf::writeResourceLocation);
        buf.writeCollection(this.toAdd, FriendlyByteBuf::writeResourceLocation);
        buf.writeCollection(this.energyBars, (buf1, pair) -> {
            pair.getLeft().toBuffer(buf1);
            buf1.writeInt(pair.getMiddle());
            buf1.writeInt(pair.getRight());
        });
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<ResourceLocation> getPowersToRemove() {
        return this.toRemove;
    }

    public List<ResourceLocation> getPowersToAdd() {
        return this.toAdd;
    }

    public List<Triple<EnergyBarReference, Integer, Integer>> getEnergyBars() {
        return this.energyBars;
    }
}
