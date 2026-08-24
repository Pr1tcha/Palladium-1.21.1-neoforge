package net.threetag.palladium.compat.mermod;

import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.Palladium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MermodClientCompatImpl extends MermodClientCompat {

    private final Method shouldRenderTail;

    private MermodClientCompatImpl(Method shouldRenderTail) {
        this.shouldRenderTail = shouldRenderTail;
    }

    public static void init() {
        try {
            var mermodClient = Class.forName("io.github.thatpreston.mermod.MermodClient");
            var shouldRenderTail = mermodClient.getMethod("shouldRenderTail", Player.class);
            MermodClientCompat.INSTANCE = new MermodClientCompatImpl(shouldRenderTail);
        } catch (ReflectiveOperationException | LinkageError exception) {
            Palladium.LOGGER.warn("Could not initialize Mermod compatibility", exception);
        }
    }

    @Override
    public boolean shouldRenderTail(Player player) {
        try {
            return Boolean.TRUE.equals(this.shouldRenderTail.invoke(null, player));
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }
}
