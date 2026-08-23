package net.threetag.palladiumcore.network;

public final class MessageType {

    private final String id;
    private final NetworkManager networkManager;
    private final NetworkManager.MessageDecoder<?> decoder;
    private final boolean toServer;

    MessageType(String id, NetworkManager networkManager, NetworkManager.MessageDecoder<?> decoder, boolean toServer) {
        this.id = id;
        this.networkManager = networkManager;
        this.decoder = decoder;
        this.toServer = toServer;
    }

    public String getId() {
        return id;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public NetworkManager.MessageDecoder<?> getDecoder() {
        return decoder;
    }

    public boolean isToServer() {
        return toServer;
    }

    public boolean isToClient() {
        return !toServer;
    }
}
