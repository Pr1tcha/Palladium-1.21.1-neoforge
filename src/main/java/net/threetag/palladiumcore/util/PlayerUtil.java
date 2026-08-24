package net.threetag.palladiumcore.util;

import net.minecraft.world.entity.player.Player;

public final class PlayerUtil {

    private PlayerUtil() {
    }

    public static void refreshDisplayName(Player player) {
        player.refreshDisplayName();
    }
}
