package net.threetag.palladiumcore.util;

import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

/** NeoForge implementation of the legacy PalladiumCore platform facade. */
public final class Platform {

    private static byte architecturyLoaded;

    private Platform() {
    }

    public static boolean isProduction() {
        return FMLEnvironment.production;
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static Collection<String> getModIds() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
    }

    public static boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    public static boolean isServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static Path getFolder() {
        return FMLPaths.GAMEDIR.get();
    }

    @Nullable
    public static Mod getMod(String modId) {
        IModInfo info = ModList.get().getMods().stream()
                .filter(candidate -> Objects.equals(candidate.getModId(), modId))
                .findFirst()
                .orElse(null);
        return info == null ? null : new Mod(
                info.getModId(),
                info.getVersion().toString(),
                info.getDisplayName(),
                info.getDescription()
        );
    }

    public static boolean isArchitecturyLoaded() {
        if (architecturyLoaded == 0) {
            architecturyLoaded = (byte) (isModLoaded("architectury") ? 2 : 1);
        }
        return architecturyLoaded == 2;
    }

    public record Mod(String modId, String version, String name, String description) {
    }
}
