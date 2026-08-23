package net.threetag.palladiumcore.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Routes NeoForge events into Palladium's legacy event API. */
public final class EventBridge {

    private static boolean registered;

    private EventBridge() {
    }

    public static synchronized void register(IEventBus modEventBus) {
        if (registered) {
            return;
        }
        registered = true;
        modEventBus.addListener(EventBridge::commonSetup);
        NeoForge.EVENT_BUS.register(EventBridge.class);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        LifecycleEvents.SETUP.invoker().run();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandEvents.REGISTER.invoker().register(event.getDispatcher(), event.getCommandSelection());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEvents.JOIN.invoker().playerJoin(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerEvents.QUIT.invoker().playerQuit(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerClone(PlayerEvent.Clone event) {
        PlayerEvents.CLONE.invoker().playerClone(event.getOriginal(), event.getEntity(), event.isWasDeath());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEvents.RESPAWN.invoker().playerRespawn(event.getEntity(), event.isEndConquered());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEvents.CHANGED_DIMENSION.invoker().playerChangedDimension(event.getEntity(), event.getTo());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerNameFormat(PlayerEvent.NameFormat event) {
        AtomicReference<Component> displayName = new AtomicReference<>(event.getDisplayname());
        PlayerEvents.NAME_FORMAT.invoker().playerNameFormat(event.getEntity(), event.getUsername(), displayName);
        event.setDisplayname(displayName.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void startTracking(PlayerEvent.StartTracking event) {
        PlayerEvents.START_TRACKING.invoker().playerTracking(event.getEntity(), event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void stopTracking(PlayerEvent.StopTracking event) {
        PlayerEvents.STOP_TRACKING.invoker().playerTracking(event.getEntity(), event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        EntityEvents.JOIN_LEVEL.invoker().entityJoinLevel(event.getEntity(), event.getLevel());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void livingDeath(LivingDeathEvent event) {
        if (LivingEntityEvents.DEATH.invoker().livingEntityDeath(event.getEntity(), event.getSource()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void livingDamage(LivingIncomingDamageEvent event) {
        float originalAmount = event.getAmount();
        if (LivingEntityEvents.ATTACK.invoker().livingEntityAttack(event.getEntity(), event.getSource(), originalAmount).cancelsEvent()) {
            event.setCanceled(true);
            return;
        }

        AtomicReference<Float> amount = new AtomicReference<>(originalAmount);
        if (LivingEntityEvents.HURT.invoker().livingEntityHurt(event.getEntity(), event.getSource(), amount).cancelsEvent()) {
            event.setCanceled(true);
        }
        event.setAmount(amount.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void livingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            LivingEntityEvents.TICK.invoker().livingEntityTick(livingEntity);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntityEvents.JUMP.invoker().livingEntityJump(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemUseStart(LivingEntityUseItemEvent.Start event) {
        handleCancellableItemUse(event, LivingEntityEvents.ITEM_USE_START);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemUseTick(LivingEntityUseItemEvent.Tick event) {
        handleCancellableItemUse(event, LivingEntityEvents.ITEM_USE_TICK);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemUseStop(LivingEntityUseItemEvent.Stop event) {
        handleCancellableItemUse(event, LivingEntityEvents.ITEM_USE_STOP);
    }

    private static void handleCancellableItemUse(LivingEntityUseItemEvent event,
                                                  Event<LivingEntityEvents.ItemUse> palladiumEvent) {
        AtomicInteger duration = new AtomicInteger(event.getDuration());
        if (palladiumEvent.invoker().livingEntityItemUse(event.getEntity(), event.getItem(), duration).cancelsEvent()) {
            if (event instanceof net.neoforged.bus.api.ICancellableEvent cancellable) {
                cancellable.setCanceled(true);
            }
        }
        event.setDuration(duration.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void itemUseFinish(LivingEntityUseItemEvent.Finish event) {
        AtomicInteger duration = new AtomicInteger(event.getDuration());
        LivingEntityEvents.ITEM_USE_FINISH.invoker().livingEntityItemUseFinish(event.getEntity(), event.getItem(), duration);
        event.setDuration(duration.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void blockBreak(BlockEvent.BreakEvent event) {
        if (BlockEvents.BREAK.invoker().breakBlock(event.getLevel(), event.getPos(), event.getState(), event.getPlayer()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void blockPlace(BlockEvent.EntityPlaceEvent event) {
        if (BlockEvents.PLACE.invoker().placeBlock(event.getLevel(), event.getPos(), event.getPlacedBlock(),
                event.getPlacedAgainst(), event.getEntity()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        LifecycleEvents.SERVER_ABOUT_TO_START.invoker().server(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void serverStarting(ServerStartingEvent event) {
        LifecycleEvents.SERVER_STARTING.invoker().server(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void serverStarted(ServerStartedEvent event) {
        LifecycleEvents.SERVER_STARTED.invoker().server(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void serverStopping(ServerStoppingEvent event) {
        LifecycleEvents.SERVER_STOPPING.invoker().server(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void serverStopped(ServerStoppedEvent event) {
        LifecycleEvents.SERVER_STOPPED.invoker().server(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void datapackSync(OnDatapackSyncEvent event) {
        LifecycleEvents.DATAPACK_SYNC.invoker().onDatapackSync(event.getPlayerList(), event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void anvilUpdate(AnvilUpdateEvent event) {
        AtomicInteger cost = new AtomicInteger((int) Math.min(Integer.MAX_VALUE, event.getCost()));
        AtomicInteger materialCost = new AtomicInteger(event.getMaterialCost());
        AtomicReference<ItemStack> output = new AtomicReference<>(event.getOutput());
        if (PlayerEvents.ANVIL_UPDATE.invoker().anvilUpdate(event.getPlayer(), event.getLeft(), event.getRight(),
                event.getName(), cost, materialCost, output).cancelsEvent()) {
            event.setCanceled(true);
        }
        event.setCost(cost.get());
        event.setMaterialCost(materialCost.get());
        event.setOutput(output.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void chatSubmitted(ServerChatEvent event) {
        if (ChatEvents.SERVER_SUBMITTED.invoker().chatMessageSubmitted(event.getPlayer(), event.getRawText(),
                event.getMessage()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }
}
