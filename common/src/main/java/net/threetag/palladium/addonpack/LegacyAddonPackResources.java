package net.threetag.palladium.addonpack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.threetag.palladium.addonpack.parser.AddonParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class LegacyAddonPackResources implements PackResources {

    private static final String ITEM_MODIFIER_DIRECTORY = "item_modifier";
    private static final String LEGACY_ITEM_MODIFIER_DIRECTORY = "item_modifiers";

    private final PackResources delegate;

    public LegacyAddonPackResources(PackResources delegate) {
        this.delegate = delegate;
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        return this.delegate.getRootResource(elements);
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        IoSupplier<InputStream> supplier = this.delegate.getResource(type, location);
        if (supplier == null && isItemModifier(type, location.getPath())) {
            supplier = this.delegate.getResource(type, toLegacyItemModifier(location));
        }
        return this.wrap(type, location, supplier);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type == PackType.SERVER_DATA && ITEM_MODIFIER_DIRECTORY.equals(path)) {
            Set<ResourceLocation> resources = new HashSet<>();
            this.delegate.listResources(type, namespace, path, (location, supplier) -> {
                resources.add(location);
                output.accept(location, this.wrap(type, location, supplier));
            });
            this.delegate.listResources(type, namespace, LEGACY_ITEM_MODIFIER_DIRECTORY, (location, supplier) -> {
                ResourceLocation migrated = toModernItemModifier(location);
                if (resources.add(migrated)) {
                    output.accept(migrated, this.wrap(type, migrated, supplier));
                }
            });
            return;
        }

        this.delegate.listResources(type, namespace, path,
                (location, supplier) -> output.accept(location, this.wrap(type, location, supplier)));
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return this.delegate.getNamespaces(type);
    }

    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) throws IOException {
        return this.delegate.getMetadataSection(serializer);
    }

    @Override
    public PackLocationInfo location() {
        return this.delegate.location();
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    private IoSupplier<InputStream> wrap(PackType type, ResourceLocation location, IoSupplier<InputStream> supplier) {
        boolean serverData = type == PackType.SERVER_DATA;
        boolean damageType = serverData && location.getPath().startsWith("damage_type/") && location.getPath().endsWith(".json");
        boolean dimensionType = serverData && location.getPath().startsWith("dimension_type/") && location.getPath().endsWith(".json");
        boolean itemModifier = isItemModifier(type, location.getPath()) && location.getPath().endsWith(".json");
        boolean commandFunction = serverData && location.getPath().startsWith("functions/") && location.getPath().endsWith(".mcfunction");
        boolean kubeJsScript = location.getPath().startsWith("kubejs_scripts/") && location.getPath().endsWith(".js");
        if (supplier == null || (!damageType && !dimensionType && !itemModifier && !commandFunction && !kubeJsScript)) {
            return supplier;
        }

        return () -> {
            byte[] original;
            try (InputStream stream = supplier.get()) {
                original = stream.readAllBytes();
            }

            String text = new String(original, StandardCharsets.UTF_8);
            if (kubeJsScript) {
                String normalized = LegacyKubeJsCompatibility.normalize(text);
                return new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8));
            }

            if (commandFunction) {
                String normalized = LegacyCommandCompatibility.normalize(text);
                return new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8));
            }

            if (itemModifier) {
                String normalized = LegacyItemModifierCompatibility.normalize(text);
                return new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8));
            }

            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            boolean changed = false;

            if (damageType && json.has("effects") && json.get("effects").isJsonPrimitive()
                    && json.getAsJsonPrimitive("effects").isString()) {
                String effects = json.get("effects").getAsString();
                String trimmed = effects.trim();
                if (!trimmed.equals(effects)) {
                    json.addProperty("effects", trimmed);
                    changed = true;
                }
            }

            if (dimensionType && json.has("monster_spawn_light_level")
                    && json.get("monster_spawn_light_level").isJsonObject()) {
                JsonObject lightLevel = json.getAsJsonObject("monster_spawn_light_level");
                if (lightLevel.has("type") && lightLevel.has("value") && lightLevel.get("value").isJsonObject()) {
                    JsonObject value = lightLevel.remove("value").getAsJsonObject();
                    value.entrySet().forEach(entry -> lightLevel.add(entry.getKey(), entry.getValue()));
                    changed = true;
                }
            }

            byte[] result = changed ? AddonParser.GSON.toJson(json).getBytes(StandardCharsets.UTF_8) : original;
            return new ByteArrayInputStream(result);
        };
    }

    private static boolean isItemModifier(PackType type, String path) {
        return type == PackType.SERVER_DATA && path.startsWith(ITEM_MODIFIER_DIRECTORY + "/");
    }

    private static ResourceLocation toLegacyItemModifier(ResourceLocation location) {
        return location.withPath(LEGACY_ITEM_MODIFIER_DIRECTORY
                + location.getPath().substring(ITEM_MODIFIER_DIRECTORY.length()));
    }

    private static ResourceLocation toModernItemModifier(ResourceLocation location) {
        return location.withPath(ITEM_MODIFIER_DIRECTORY
                + location.getPath().substring(LEGACY_ITEM_MODIFIER_DIRECTORY.length()));
    }

}
