package net.threetag.palladium.addonpack.forge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.threetag.palladium.addonpack.AddonPackManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.Objects;
import java.util.function.Consumer;

public class AddonPackManagerImpl {

    public static RepositorySource getModRepositorySource() {
        return new ModPackSource(Objects.requireNonNull(AddonPackManager.getPackType()));
    }

    public static class ModPackSource implements RepositorySource {

        private final PackType type;

        public ModPackSource(PackType type) {
            this.type = type;
        }

        @Override
        public void loadPacks(Consumer<Pack> onLoad) {
            for (IModInfo modInfo : ModList.get().getMods()) {
                if (Objects.equals(modInfo.getModId(), "minecraft")) continue;

                final String name = "mod:" + modInfo.getModId();
                var location = new PackLocationInfo(name, Component.literal(modInfo.getDisplayName()), PackSource.DEFAULT, Optional.empty());
                var resources = new ModResourcesSupplier(
                        ResourcePackLoader.createPackForMod(modInfo.getOwningFile()),
                        modInfo,
                        this.type
                );
                final Pack packInfo = Pack.readMetaAndCreate(
                        location,
                        resources,
                        this.type,
                        new PackSelectionConfig(false, Pack.Position.BOTTOM, false)
                );
                if (packInfo == null) {
                    continue;
                }
                onLoad.accept(packInfo);
            }
        }
    }

    public record ModResourcesSupplier(Pack.ResourcesSupplier parent, IModInfo mod, PackType type) implements Pack.ResourcesSupplier {

        @Override
        public PackResources openPrimary(PackLocationInfo location) {
            return new ModResourcePack(location, this.parent.openPrimary(location), this.mod, this.type);
        }

        @Override
        public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
            return new ModResourcePack(location, this.parent.openFull(location, metadata), this.mod, this.type);
        }
    }

    public static class ModResourcePack extends AbstractPackResources {

        private static final Gson GSON = new Gson();

        private final PackResources parent;
        private final IModInfo mod;
        private final PackType type;

        public ModResourcePack(PackLocationInfo location, PackResources parent, IModInfo mod, PackType type) {
            super(location);
            this.parent = parent;
            this.mod = mod;
            this.type = type;
        }

        @Override
        public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
            String fileName = String.join("/", paths);
            var resource = this.parent.getRootResource(paths);

            if ("pack.mcmeta".equals(fileName) && resource == null) {
                JsonObject packData = new JsonObject();
                packData.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(this.type));
                packData.addProperty("id", this.mod.getModId());
                packData.addProperty("version", this.mod.getVersion().toString());
                packData.addProperty("description", this.mod.getDescription());
                JsonObject root = new JsonObject();
                root.add("pack", packData);
                return () -> IOUtils.toInputStream(GSON.toJson(root), StandardCharsets.UTF_8);
            }

            return resource;
        }

        @Override
        public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
            return this.parent.getResource(type, location);
        }

        @Override
        public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
            this.parent.listResources(type, namespace, path, output);
        }

        @Override
        public Set<String> getNamespaces(PackType type) {
            return this.parent.getNamespaces(type);
        }

        @Override
        public void close() {
            this.parent.close();
        }
    }
}
