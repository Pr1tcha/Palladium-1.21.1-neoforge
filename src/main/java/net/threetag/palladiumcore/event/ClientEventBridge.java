package net.threetag.palladiumcore.event;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.concurrent.atomic.AtomicReference;

/** Client-side half of the legacy PalladiumCore event bridge. */
public final class ClientEventBridge {

    private static boolean registered;

    private ClientEventBridge() {
    }

    public static synchronized void register(IEventBus modEventBus) {
        if (registered) {
            return;
        }
        registered = true;
        modEventBus.addListener(ClientEventBridge::clientSetup);
        NeoForge.EVENT_BUS.register(ClientEventBridge.class);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        LifecycleEvents.CLIENT_SETUP.invoker().run();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void keyPressed(InputEvent.Key event) {
        InputEvents.KEY_PRESSED.invoker().keyPressed(Minecraft.getInstance(), event.getKey(), event.getScanCode(),
                event.getAction(), event.getModifiers());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseButtonPre(InputEvent.MouseButton.Pre event) {
        if (InputEvents.MOUSE_CLICKED_PRE.invoker().mouseClickedPre(Minecraft.getInstance(), event.getButton(),
                event.getAction(), event.getModifiers()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseButtonPost(InputEvent.MouseButton.Post event) {
        InputEvents.MOUSE_CLICKED_POST.invoker().mouseClickedPost(Minecraft.getInstance(), event.getButton(),
                event.getAction(), event.getModifiers());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (InputEvents.MOUSE_SCROLLING.invoker().mouseScrolling(Minecraft.getInstance(), event.getScrollDeltaY(),
                event.isLeftDown(), event.isMiddleDown(), event.isRightDown(), event.getMouseX(), event.getMouseY())
                .cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void movementInput(MovementInputUpdateEvent event) {
        InputEvents.MOVEMENT_INPUT_UPDATE.invoker().movementInputUpdate(event.getEntity(), event.getInput());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void screenOpening(ScreenEvent.Opening event) {
        AtomicReference<Screen> newScreen = new AtomicReference<>(event.getNewScreen());
        if (ScreenEvents.OPENING.invoker().screenOpening(event.getCurrentScreen(), newScreen).cancelsEvent()) {
            event.setCanceled(true);
        }
        if (newScreen.get() != event.getNewScreen()) {
            event.setNewScreen(newScreen.get());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void screenInitPre(ScreenEvent.Init.Pre event) {
        if (ScreenEvents.INIT_PRE.invoker().screenInitPre(event.getScreen()).cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void screenInitPost(ScreenEvent.Init.Post event) {
        ScreenEvents.INIT_POST.invoker().screenInitPost(event.getScreen(), event::addListener);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        PlayerEvents.CLIENT_JOIN.invoker().playerJoin(event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerQuit(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getPlayer() != null) {
            PlayerEvents.CLIENT_QUIT.invoker().playerQuit(event.getPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void clientTickPre(ClientTickEvent.Pre event) {
        ClientTickEvents.CLIENT_PRE.invoker().clientTick(Minecraft.getInstance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void clientTickPost(ClientTickEvent.Post event) {
        ClientTickEvents.CLIENT_POST.invoker().clientTick(Minecraft.getInstance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void clientLevelTickPre(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientTickEvents.CLIENT_LEVEL_PRE.invoker().clientLevelTick(Minecraft.getInstance(), level);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void clientLevelTickPost(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientTickEvents.CLIENT_LEVEL_POST.invoker().clientLevelTick(Minecraft.getInstance(), level);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        AtomicReference<Float> yaw = new AtomicReference<>(event.getYaw());
        AtomicReference<Float> pitch = new AtomicReference<>(event.getPitch());
        AtomicReference<Float> roll = new AtomicReference<>(event.getRoll());
        ViewportEvents.COMPUTE_CAMERA_ANGLES.invoker().computeCameraAngles(event.getRenderer(), event.getCamera(),
                event.getPartialTick(), yaw, pitch, roll);
        event.setYaw(yaw.get());
        event.setPitch(pitch.get());
        event.setRoll(roll.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderFog(ViewportEvent.RenderFog event) {
        AtomicReference<Float> far = new AtomicReference<>(event.getFarPlaneDistance());
        AtomicReference<Float> near = new AtomicReference<>(event.getNearPlaneDistance());
        AtomicReference<FogShape> shape = new AtomicReference<>(event.getFogShape());
        EventResult result = ViewportEvents.RENDER_FOG.invoker().renderFog(event.getRenderer(), event.getCamera(),
                event.getPartialTick(), event.getMode(), event.getType(), far, near, shape);
        event.setFarPlaneDistance(far.get());
        event.setNearPlaneDistance(near.get());
        event.setFogShape(shape.get());
        if (result.cancelsEvent()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void computeFogColor(ViewportEvent.ComputeFogColor event) {
        AtomicReference<Float> red = new AtomicReference<>(event.getRed());
        AtomicReference<Float> green = new AtomicReference<>(event.getGreen());
        AtomicReference<Float> blue = new AtomicReference<>(event.getBlue());
        ViewportEvents.COMPUTE_FOG_COLOR.invoker().computeFogColor(event.getRenderer(), event.getCamera(),
                event.getPartialTick(), red, green, blue);
        event.setRed(red.get());
        event.setGreen(green.get());
        event.setBlue(blue.get());
    }
}
