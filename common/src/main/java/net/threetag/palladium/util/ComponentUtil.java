package net.threetag.palladium.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.threetag.palladiumcore.util.Platform;

public final class ComponentUtil {

    private static final RegistryAccess.Frozen BUILTIN_REGISTRIES =
            RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).freeze();

    private ComponentUtil() {
    }

    public static Component fromJson(JsonElement json) {
        return Component.Serializer.fromJson(json, registryProvider());
    }

    public static Component fromJson(String json) {
        return Component.Serializer.fromJson(json, registryProvider());
    }

    public static JsonElement toJsonTree(Component component) {
        return JsonParser.parseString(toJson(component));
    }

    public static String toJson(Component component) {
        return Component.Serializer.toJson(component, registryProvider());
    }

    public static Component read(FriendlyByteBuf buffer) {
        return ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buffer);
    }

    public static void write(FriendlyByteBuf buffer, Component component) {
        ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buffer, component);
    }

    private static HolderLookup.Provider registryProvider() {
        var server = Platform.getCurrentServer();
        return server != null ? server.registryAccess() : BUILTIN_REGISTRIES;
    }
}
