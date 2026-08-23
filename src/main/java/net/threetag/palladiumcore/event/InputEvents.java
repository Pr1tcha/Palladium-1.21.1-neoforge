package net.threetag.palladiumcore.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;

public interface InputEvents {

    Event<KeyPressed> KEY_PRESSED = new Event<>(KeyPressed.class, listeners ->
            (minecraft, key, scanCode, action, mods) -> listeners.forEach(listener ->
                    listener.keyPressed(minecraft, key, scanCode, action, mods)));
    Event<MouseClickedPre> MOUSE_CLICKED_PRE = new Event<>(MouseClickedPre.class, listeners ->
            (minecraft, button, action, mods) -> Event.result(listeners,
                    listener -> listener.mouseClickedPre(minecraft, button, action, mods)));
    Event<MouseClickedPost> MOUSE_CLICKED_POST = new Event<>(MouseClickedPost.class, listeners ->
            (minecraft, button, action, mods) -> listeners.forEach(listener ->
                    listener.mouseClickedPost(minecraft, button, action, mods)));
    Event<MouseScrolling> MOUSE_SCROLLING = new Event<>(MouseScrolling.class, listeners ->
            (minecraft, delta, left, middle, right, mouseX, mouseY) -> Event.result(listeners,
                    listener -> listener.mouseScrolling(minecraft, delta, left, middle, right, mouseX, mouseY)));
    Event<MovementInputUpdate> MOVEMENT_INPUT_UPDATE = new Event<>(MovementInputUpdate.class, listeners ->
            (player, input) -> listeners.forEach(listener -> listener.movementInputUpdate(player, input)));

    @FunctionalInterface interface KeyPressed { void keyPressed(Minecraft minecraft, int keyCode, int scanCode, int action, int mods); }
    @FunctionalInterface interface MouseClickedPre { EventResult mouseClickedPre(Minecraft minecraft, int button, int action, int mods); }
    @FunctionalInterface interface MouseClickedPost { void mouseClickedPost(Minecraft minecraft, int button, int action, int mods); }
    @FunctionalInterface interface MouseScrolling { EventResult mouseScrolling(Minecraft minecraft, double scrollDelta, boolean leftDown, boolean middleDown, boolean rightDown, double mouseX, double mouseY); }
    @FunctionalInterface interface MovementInputUpdate { void movementInputUpdate(Player player, Input input); }
}
