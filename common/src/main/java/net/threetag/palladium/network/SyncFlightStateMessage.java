package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class SyncFlightStateMessage extends MessageS2C {

    private final int entityId;
    private final boolean flying;

    public SyncFlightStateMessage(int entityId, boolean flying) {
        this.entityId = entityId;
        this.flying = flying;
    }

    public SyncFlightStateMessage(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.flying = buf.readBoolean();
    }

    @Override
    public @NotNull MessageType getType() {
        return PalladiumNetwork.SYNC_FLYING_STATE;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.flying);
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean isFlying() {
        return this.flying;
    }
}
