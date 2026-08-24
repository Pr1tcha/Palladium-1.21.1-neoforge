package net.threetag.palladium.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;

public class SyncAbilityEntryPropertyMessage extends MessageS2C {

    private final int entityId;
    private final AbilityReference reference;
    private final String propertyKey;
    private final CompoundTag tag;

    public SyncAbilityEntryPropertyMessage(int entityId, AbilityReference reference, String propertyKey, CompoundTag tag) {
        this.entityId = entityId;
        this.reference = reference;
        this.propertyKey = propertyKey;
        this.tag = tag;
    }

    public SyncAbilityEntryPropertyMessage(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.reference = AbilityReference.fromBuffer(buf);
        this.propertyKey = buf.readUtf();
        this.tag = buf.readNbt();
    }

    @Override
    public MessageType getType() {
        return PalladiumNetwork.SYNC_ABILITY_ENTRY_PROPERTY;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        this.reference.toBuffer(buf);
        buf.writeUtf(this.propertyKey);
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public AbilityReference getReference() {
        return this.reference;
    }

    public String getPropertyKey() {
        return this.propertyKey;
    }

    public CompoundTag getTag() {
        return this.tag;
    }
}
