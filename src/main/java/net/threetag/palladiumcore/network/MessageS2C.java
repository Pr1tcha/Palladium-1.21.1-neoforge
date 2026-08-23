package net.threetag.palladiumcore.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class MessageS2C extends Message {

    public void send(ServerPlayer player) {
        getType().getNetworkManager().sendToPlayer(player, this);
    }

    public void sendToTracking(Entity entity) {
        getType().getNetworkManager().sendToTracking(entity, this);
    }

    public void sendToTrackingAndSelf(ServerPlayer player) {
        getType().getNetworkManager().sendToTrackingAndSelf(player, this);
    }

    public void sendToDimension(Level level) {
        getType().getNetworkManager().sendToDimension(level, this);
    }
}
