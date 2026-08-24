package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;

public class SyncAbilityStateMessage extends MessageS2C {

    private final int entityId;
    private final AbilityReference reference;
    private final boolean unlocked, enabled;
    private final int maxCooldown, cooldown;
    private final int maxActivationTimer, activationTimer;

    public SyncAbilityStateMessage(int entityId, AbilityReference reference, boolean unlocked, boolean enabled, int maxCooldown, int cooldown, int maxActivationTimer, int activationTimer) {
        this.entityId = entityId;
        this.reference = reference;
        this.unlocked = unlocked;
        this.enabled = enabled;
        this.maxCooldown = maxCooldown;
        this.cooldown = cooldown;
        this.maxActivationTimer = maxActivationTimer;
        this.activationTimer = activationTimer;
    }

    public SyncAbilityStateMessage(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.reference = AbilityReference.fromBuffer(buf);
        this.unlocked = buf.readBoolean();
        this.enabled = buf.readBoolean();
        this.maxCooldown = buf.readInt();
        this.cooldown = buf.readInt();
        this.maxActivationTimer = buf.readInt();
        this.activationTimer = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return PalladiumNetwork.SYNC_ABILITY_STATE;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        this.reference.toBuffer(buf);
        buf.writeBoolean(this.unlocked);
        buf.writeBoolean(this.enabled);
        buf.writeInt(this.maxCooldown);
        buf.writeInt(this.cooldown);
        buf.writeInt(this.maxActivationTimer);
        buf.writeInt(this.activationTimer);
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

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxCooldown() {
        return this.maxCooldown;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int getMaxActivationTimer() {
        return this.maxActivationTimer;
    }

    public int getActivationTimer() {
        return this.activationTimer;
    }
}
