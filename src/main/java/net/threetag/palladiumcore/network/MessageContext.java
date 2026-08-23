package net.threetag.palladiumcore.network;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface MessageContext {

    @Nullable
    ServerPlayer getPlayer();
}
