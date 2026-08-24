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
import java.util.Set;

public class LegacyAddonPackResources implements PackResources {

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
        return this.wrap(type, location, this.delegate.getResource(type, location));
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
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
        boolean damageType = location.getPath().startsWith("damage_type/");
        boolean dimensionType = location.getPath().startsWith("dimension_type/");
        boolean commandFunction = location.getPath().startsWith("functions/") && location.getPath().endsWith(".mcfunction");
        if (supplier == null || type != PackType.SERVER_DATA
                || (!damageType && !dimensionType && !commandFunction)) {
            return supplier;
        }

        return () -> {
            byte[] original;
            try (InputStream stream = supplier.get()) {
                original = stream.readAllBytes();
            }

            if (commandFunction) {
                String commands = new String(original, StandardCharsets.UTF_8);
                String normalized = LegacyCommandCompatibility.normalize(commands);
                return new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8));
            }

            JsonObject json = JsonParser.parseString(new String(original, StandardCharsets.UTF_8)).getAsJsonObject();
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

}
