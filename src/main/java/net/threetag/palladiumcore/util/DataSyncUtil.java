package net.threetag.palladiumcore.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.threetag.palladiumcore.event.LifecycleEvents;
import net.threetag.palladiumcore.event.PlayerEvents;
import net.threetag.palladiumcore.network.MessageS2C;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class DataSyncUtil {

    private static final List<DataSync> DATA_SYNC = new CopyOnWriteArrayList<>();
    private static final List<EntitySync> ENTITY_SYNC = new CopyOnWriteArrayList<>();
    private static boolean eventsInstalled;

    private DataSyncUtil() {
    }

    public static void registerDataSync(DataSync dataSync) {
        DATA_SYNC.add(dataSync);
    }

    public static void registerEntitySync(EntitySync entitySync) {
        ENTITY_SYNC.add(entitySync);
    }

    public static synchronized void setupEvents() {
        if (eventsInstalled) {
            return;
        }
        eventsInstalled = true;

        LifecycleEvents.DATAPACK_SYNC.register((playerList, player) -> DATA_SYNC.forEach(dataSync -> {
            if (player == null) {
                dataSync.gatherMessages(message -> playerList.getPlayers().forEach(message::send));
            } else {
                dataSync.gatherMessages(message -> message.send(player));
            }
        }));

        PlayerEvents.JOIN.register(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                ENTITY_SYNC.forEach(sync -> sync.gatherMessages(serverPlayer, message -> message.send(serverPlayer)));
            }
        });

        PlayerEvents.START_TRACKING.register((tracker, target) -> {
            if (tracker instanceof ServerPlayer serverPlayer) {
                ENTITY_SYNC.forEach(sync -> sync.gatherMessages(target, message -> message.send(serverPlayer)));
            }
        });

        PlayerEvents.RESPAWN.register((player, endConquered) -> syncPlayerAndTrackers(player));
        PlayerEvents.CHANGED_DIMENSION.register((player, destination) -> syncPlayerAndTrackers(player));
    }

    private static void syncPlayerAndTrackers(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            ENTITY_SYNC.forEach(sync -> sync.gatherMessages(entity, message -> message.sendToTrackingAndSelf(serverPlayer)));
        }
    }

    @FunctionalInterface
    public interface EntitySync {
        void gatherMessages(Entity entity, Consumer<MessageS2C> consumer);
    }

    @FunctionalInterface
    public interface DataSync {
        void gatherMessages(Consumer<MessageS2C> consumer);
    }
}
