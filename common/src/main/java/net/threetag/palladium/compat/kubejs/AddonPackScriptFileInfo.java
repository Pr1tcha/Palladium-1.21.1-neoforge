package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import dev.latvian.mods.kubejs.script.ScriptPackInfo;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AddonPackScriptFileInfo extends ScriptFileInfo {

    public AddonPackScriptFileInfo(ScriptPackInfo pack, String file, IoSupplier<InputStream> inputStreamSupplier) {
        super(pack, materialize(pack, file, inputStreamSupplier), file);
    }

    private static Path materialize(ScriptPackInfo pack, String file, IoSupplier<InputStream> inputStreamSupplier) {
        Path cacheRoot = KubeJSPaths.dir(KubeJSPaths.LOCAL.resolve("addonpack_scripts").resolve(pack.namespace));
        Path target = cacheRoot.resolve(file).normalize();

        if (!target.startsWith(cacheRoot)) {
            throw new IllegalArgumentException("Addon-pack script escapes its cache directory: " + file);
        }

        try {
            Files.createDirectories(target.getParent());
            try (InputStream inputStream = inputStreamSupplier.get()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return target;
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to cache addon-pack script " + file, exception);
        }
    }
}
