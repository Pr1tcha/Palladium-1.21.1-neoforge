package net.threetag.palladium.compat.apothicattributes.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.DualWieldingPlayerHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ApothicAttributesCompat {

    @SuppressWarnings("unchecked")
    public static void init() {
        try {
            var entityOwned = Class.forName("dev.shadowsoffire.attributeslib.util.IEntityOwned");
            var setOwner = Arrays.stream(entityOwned.getMethods())
                    .filter(method -> method.getName().equals("setOwner") && method.getParameterCount() == 1)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("IEntityOwned#setOwner"));

            DualWieldingPlayerHandler.ATTRIBUTE_MAP_FACTORY = player -> {
                var map = new AttributeMap(DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) player.getType()));

                try {
                    setOwner.invoke(map, player);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new IllegalStateException("Could not assign the Apothic Attributes owner", exception);
                }

                return map;
            };
        } catch (ReflectiveOperationException | LinkageError exception) {
            Palladium.LOGGER.warn("Could not initialize Apothic Attributes compatibility", exception);
        }
    }

}
