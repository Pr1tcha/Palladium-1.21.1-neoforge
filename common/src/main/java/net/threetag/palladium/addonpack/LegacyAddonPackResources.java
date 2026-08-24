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
        if (supplier == null || type != PackType.SERVER_DATA || !location.getPath().startsWith("damage_type/")
                || !location.getPath().endsWith(".json")) {
            return supplier;
        }

        return () -> {
            byte[] original;
            try (InputStream stream = supplier.get()) {
                original = stream.readAllBytes();
            }

            JsonObject json = JsonParser.parseString(new String(original, StandardCharsets.UTF_8)).getAsJsonObject();
            if (json.has("effects") && json.get("effects").isJsonPrimitive()
                    && json.getAsJsonPrimitive("effects").isString()) {
                String effects = json.get("effects").getAsString();
                String trimmed = effects.trim();
                if (!trimmed.equals(effects)) {
                    json.addProperty("effects", trimmed);
                    return new ByteArrayInputStream(AddonParser.GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
                }
            }

            return new ByteArrayInputStream(original);
        };
    }

}
