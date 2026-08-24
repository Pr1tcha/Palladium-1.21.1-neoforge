package net.threetag.palladium.mixin;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.script.*;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.threetag.palladium.addonpack.AddonPackManager;
import net.threetag.palladium.compat.kubejs.AddonPackScriptFileInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ScriptManager.class)
public class ScriptManagerMixin {

    @Shadow(remap = false)
    @Final
    public Map<String, ScriptPack> packs;

    @Shadow(remap = false)
    @Final
    public ScriptType scriptType;

    @Inject(at = @At("RETURN"), method = "loadFromDirectory", remap = false)
    public void loadFromDirectory(CallbackInfo ci) {
        AddonPackManager.getInstance().getPackList().reload();
        AddonPackManager.getInstance().getPackList().setSelected(AddonPackManager.getInstance().getPackList().getAvailableIds());

        Map<String, Pair<ScriptPackInfo, List<ScriptFileInfo>>> scriptFileInfoMap = new HashMap<>();

        for (Pack pack : AddonPackManager.getInstance().getPackList().getAvailablePacks()) {
            var packType = this.scriptType == ScriptType.CLIENT ? PackType.CLIENT_RESOURCES : (this.scriptType == ScriptType.SERVER ? PackType.SERVER_DATA : AddonPackManager.getPackType());
            var packResources = pack.open();

            for (String namespace : packResources.getNamespaces(packType)) {
                packResources.listResources(packType, namespace, "kubejs_scripts", (path, inputStreamIoSupplier) -> {
                    if (path.getPath().endsWith(".js")) {
                        var files = scriptFileInfoMap.computeIfAbsent(namespace,
                                s -> Pair.of(new ScriptPackInfo("addonpack_" + s, ""), new ArrayList<>()));
                        try {
                            files.getSecond().add(new AddonPackScriptFileInfo(files.getFirst(), path.getPath(), inputStreamIoSupplier));
                        } catch (UncheckedIOException exception) {
                            scriptType.console.error("Failed to cache addon-pack script " + path, exception);
                        }
                    }
                });
            }

            packResources.close();
        }

        for (Map.Entry<String, Pair<ScriptPackInfo, List<ScriptFileInfo>>> e : scriptFileInfoMap.entrySet()) {
            var scriptPack = new ScriptPack((ScriptManager) (Object) this, e.getValue().getFirst());
            scriptPack.info.scripts.addAll(e.getValue().getSecond());

            for (var fileInfo : scriptPack.info.scripts) {
                try {
                    var scriptFile = new ScriptFile(scriptPack, fileInfo);
                    var skip = scriptFile.skipLoading();

                    if (skip.isEmpty()) {
                        scriptPack.scripts.add(scriptFile);
                    } else {
                        scriptType.console.info("Skipped " + fileInfo.location + ": " + skip);
                    }
                } catch (Throwable error) {
                    scriptType.console.error("Failed to pre-load script file " + fileInfo.location, error);
                }
            }

            scriptPack.scripts.sort(null);
            this.packs.put(scriptPack.info.namespace, scriptPack);
        }
    }

}
