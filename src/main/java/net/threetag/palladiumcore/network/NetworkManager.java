package net.threetag.palladiumcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.threetag.palladiumcore.network.forge.NetworkManagerImpl;

import java.util.LinkedHashMap;
import java.util.Map;

/** Legacy message facade backed by NeoForge 1.21 custom payloads. */
public abstract class NetworkManager {

    protected final ResourceLocation channelName;
    protected final Map<String, MessageType> toServer = new LinkedHashMap<>();
    protected final Map<String, MessageType> toClient = new LinkedHashMap<>();

    private final CustomPacketPayload.Type<ToServerPayload> toServerPayloadType;
    private final CustomPacketPayload.Type<ToClientPayload> toClientPayloadType;

    private final StreamCodec<RegistryFriendlyByteBuf, ToServerPayload> toServerCodec = new StreamCodec<>() {
        @Override
        public ToServerPayload decode(RegistryFriendlyByteBuf buffer) {
            String messageId = buffer.readUtf();
            MessageType type = requireType(toServer, messageId, "server");
            return new ToServerPayload((MessageC2S) type.getDecoder().decode(buffer));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ToServerPayload payload) {
            buffer.writeUtf(payload.message().getType().getId());
            payload.message().toBytes(buffer);
        }
    };

    private final StreamCodec<RegistryFriendlyByteBuf, ToClientPayload> toClientCodec = new StreamCodec<>() {
        @Override
        public ToClientPayload decode(RegistryFriendlyByteBuf buffer) {
            String messageId = buffer.readUtf();
            MessageType type = requireType(toClient, messageId, "client");
            return new ToClientPayload((MessageS2C) type.getDecoder().decode(buffer));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ToClientPayload payload) {
            buffer.writeUtf(payload.message().getType().getId());
            payload.message().toBytes(buffer);
        }
    };

    public static NetworkManager create(ResourceLocation channelName) {
        return new NetworkManagerImpl(channelName);
    }

    protected NetworkManager(ResourceLocation channelName) {
        this.channelName = channelName;
        this.toServerPayloadType = new CustomPacketPayload.Type<>(payloadId("to_server"));
        this.toClientPayloadType = new CustomPacketPayload.Type<>(payloadId("to_client"));
        NetworkRegistration.track(this);
    }

    private ResourceLocation payloadId(String direction) {
        return ResourceLocation.fromNamespaceAndPath(channelName.getNamespace(), channelName.getPath() + "_" + direction);
    }

    public MessageType registerS2C(String id, MessageDecoder<MessageS2C> decoder) {
        MessageType type = new MessageType(id, this, decoder, false);
        if (toClient.putIfAbsent(id, type) != null) {
            throw new IllegalArgumentException("Duplicate clientbound message id " + id + " on " + channelName);
        }
        return type;
    }

    public MessageType registerC2S(String id, MessageDecoder<MessageC2S> decoder) {
        MessageType type = new MessageType(id, this, decoder, true);
        if (toServer.putIfAbsent(id, type) != null) {
            throw new IllegalArgumentException("Duplicate serverbound message id " + id + " on " + channelName);
        }
        return type;
    }

    void registerPayloads(PayloadRegistrar registrar) {
        registrar.playToServer(toServerPayloadType, toServerCodec, (payload, context) ->
                payload.message().handle(() -> context.player() instanceof ServerPlayer player ? player : null));
        registrar.playToClient(toClientPayloadType, toClientCodec, (payload, context) ->
                payload.message().handle(() -> null));
    }

    private static MessageType requireType(Map<String, MessageType> types, String id, String target) {
        MessageType type = types.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown message id received on " + target + ": " + id);
        }
        return type;
    }

    protected boolean owns(Message message, boolean serverbound) {
        Map<String, MessageType> types = serverbound ? toServer : toClient;
        return types.get(message.getType().getId()) == message.getType();
    }

    protected final ToServerPayload serverboundPayload(MessageC2S message) {
        return new ToServerPayload(message);
    }

    protected final ToClientPayload clientboundPayload(MessageS2C message) {
        return new ToClientPayload(message);
    }

    public abstract void sendToServer(MessageC2S message);

    public abstract void sendToPlayer(ServerPlayer player, MessageS2C message);

    public abstract void sendToTracking(Entity entity, MessageS2C message);

    public abstract void sendToTrackingAndSelf(ServerPlayer player, MessageS2C message);

    public abstract void sendToDimension(Level level, MessageS2C message);

    /**
     * Kept for source compatibility. NeoForge 1.21 sends complex spawn data automatically
     * for entities implementing {@link ExtendedEntitySpawnData}.
     */
    @Deprecated(forRemoval = true)
    public static Packet<ClientGamePacketListener> createAddEntityPacket(Entity entity) {
        throw new UnsupportedOperationException("NeoForge 1.21 creates entity spawn packets through ServerEntity");
    }

    @FunctionalInterface
    public interface MessageDecoder<T extends Message> {
        T decode(FriendlyByteBuf buf);
    }

    public final class ToServerPayload implements CustomPacketPayload {
        private final MessageC2S message;

        private ToServerPayload(MessageC2S message) {
            this.message = message;
        }

        public MessageC2S message() {
            return message;
        }

        @Override
        public CustomPacketPayload.Type<ToServerPayload> type() {
            return toServerPayloadType;
        }
    }

    public final class ToClientPayload implements CustomPacketPayload {
        private final MessageS2C message;

        private ToClientPayload(MessageS2C message) {
            this.message = message;
        }

        public MessageS2C message() {
            return message;
        }

        @Override
        public CustomPacketPayload.Type<ToClientPayload> type() {
            return toClientPayloadType;
        }
    }
}
