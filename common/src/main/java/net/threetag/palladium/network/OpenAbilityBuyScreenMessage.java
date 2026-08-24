package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladium.power.ability.AbilityConfiguration;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

public class OpenAbilityBuyScreenMessage extends MessageS2C {

    private final AbilityReference reference;
    private final AbilityConfiguration.UnlockData unlockData;
    private final boolean available;

    public OpenAbilityBuyScreenMessage(AbilityReference reference, AbilityConfiguration.UnlockData unlockData, boolean available) {
        this.reference = reference;
        this.unlockData = unlockData;
        this.available = available;
    }

    public OpenAbilityBuyScreenMessage(FriendlyByteBuf buf) {
        this.reference = AbilityReference.fromBuffer(buf);
        this.unlockData = new AbilityConfiguration.UnlockData(buf);
        this.available = buf.readBoolean();
    }

    @NotNull
    @Override
    public MessageType getType() {
        return PalladiumNetwork.OPEN_ABILITY_BUY_SCREEN;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.reference.toBuffer(buf);
        this.unlockData.toBuffer(buf);
        buf.writeBoolean(this.available);
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }

    public AbilityReference getReference() {
        return this.reference;
    }

    public AbilityConfiguration.UnlockData getUnlockData() {
        return this.unlockData;
    }

    public boolean isAvailable() {
        return this.available;
    }
}
