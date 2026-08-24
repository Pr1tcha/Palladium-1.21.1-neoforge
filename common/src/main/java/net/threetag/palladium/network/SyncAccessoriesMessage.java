package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.threetag.palladium.accessory.Accessory;
import net.threetag.palladium.accessory.AccessorySlot;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncAccessoriesMessage extends MessageS2C {

    public int entityId;
    public Map<AccessorySlot, Collection<Accessory>> accessories;

    public SyncAccessoriesMessage(int entityId, Map<AccessorySlot, Collection<Accessory>> accessories) {
        this.entityId = entityId;
        this.accessories = accessories;
    }

    public SyncAccessoriesMessage(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        int amount = buf.readInt();
        this.accessories = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            AccessorySlot slot = AccessorySlot.getSlotByName(buf.readResourceLocation());
            List<Accessory> accessories1 = new ArrayList<>();
            int slotAmount = buf.readInt();
            for (int j = 0; j < slotAmount; j++) {
                Accessory accessory = Accessory.REGISTRY.get(buf.readResourceLocation());
                if (accessory != null) {
                    accessories1.add(accessory);
                }
            }
            this.accessories.put(slot, accessories1);
        }
    }

    @Override
    public MessageType getType() {
        return PalladiumNetwork.SYNC_ACCESSORIES;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.accessories.size());

        this.accessories.forEach((slot, accessories) -> {
            buf.writeResourceLocation(slot.getName());
            buf.writeInt(accessories.size());
            for (Accessory accessory : accessories) {
                buf.writeResourceLocation(Accessory.REGISTRY.getKey(accessory));
            }
        });
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }
}
