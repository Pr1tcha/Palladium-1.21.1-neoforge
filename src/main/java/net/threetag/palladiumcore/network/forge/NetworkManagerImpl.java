package net.threetag.palladiumcore.network.forge;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.threetag.palladiumcore.network.Message;
import net.threetag.palladiumcore.network.MessageC2S;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.NetworkManager;
import org.slf4j.Logger;

public class NetworkManagerImpl extends NetworkManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    public NetworkManagerImpl(ResourceLocation channelName) {
        super(channelName);
    }

    @Override
    public void sendToServer(MessageC2S message) {
        if (validate(message, true)) {
            PacketDistributor.sendToServer(serverboundPayload(message));
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, MessageS2C message) {
        if (validate(message, false)) {
            PacketDistributor.sendToPlayer(player, clientboundPayload(message));
        }
    }

    @Override
    public void sendToTracking(Entity entity, MessageS2C message) {
        if (validate(message, false)) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, clientboundPayload(message));
        }
    }

    @Override
    public void sendToTrackingAndSelf(ServerPlayer player, MessageS2C message) {
        if (validate(message, false)) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, clientboundPayload(message));
        }
    }

    @Override
    public void sendToDimension(Level level, MessageS2C message) {
        if (level instanceof ServerLevel serverLevel && validate(message, false)) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, clientboundPayload(message));
        }
    }

    private boolean validate(Message message, boolean serverbound) {
        if (owns(message, serverbound)) {
            return true;
        }
        LOGGER.warn("Message type is not registered on channel {}: {}", channelName, message.getType().getId());
        return false;
    }
}
