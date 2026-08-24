package net.threetag.palladium.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.sound.FlightSound;

public final class FlightSoundHandler {

    private static FlightSound cachedSound;

    private FlightSoundHandler() {
    }

    public static void startSound(Player player) {
        if (player != Minecraft.getInstance().player) {
            return;
        }

        if (cachedSound != null) {
            cachedSound.stop = true;
        }

        cachedSound = new FlightSound(player, SoundEvents.ELYTRA_FLYING, player.getSoundSource());
        Minecraft.getInstance().getSoundManager().play(cachedSound);
    }

    public static void clear(FlightSound sound) {
        if (cachedSound == sound) {
            cachedSound = null;
        }
    }
}
