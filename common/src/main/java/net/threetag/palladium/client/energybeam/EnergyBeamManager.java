package net.threetag.palladium.client.energybeam;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladium.documentation.HTMLBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EnergyBeamManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static EnergyBeamManager INSTANCE = new EnergyBeamManager();
    private static final Map<ResourceLocation, EnergyBeamRenderer.Serializer> RENDERERS = new HashMap<>();

    public Map<ResourceLocation, EnergyBeamConfiguration> byName = ImmutableMap.of();

    public EnergyBeamManager() {
        super(GSON, "palladium/energy_beams");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, EnergyBeamConfiguration> builder = ImmutableMap.builder();
        object.forEach((id, json) -> {
            try {
                builder.put(id, EnergyBeamConfiguration.fromJson(json));
            } catch (Exception e) {
                AddonPackLog.error("Parsing error loading energy beam {}", id, e);
            }
        });
        this.byName = builder.build();
        AddonPackLog.info("Loaded {} energy beams", this.byName.size());
    }

    @Nullable
    public EnergyBeamConfiguration get(ResourceLocation id) {
        return this.byName.get(id);
    }

    static {
        registerRenderer(new LaserBeamRenderer.Serializer());
        registerRenderer(new LightningBeamRenderer.Serializer());
    }

    public static void registerRenderer(EnergyBeamRenderer.Serializer serializer) {
        RENDERERS.put(serializer.getId(), serializer);
    }

    public static EnergyBeamRenderer.Serializer getRenderer(ResourceLocation id) {
        return RENDERERS.get(id);
    }

    public static HTMLBuilder documentationBuilder() {
        return new HTMLBuilder(ResourceLocation.fromNamespaceAndPath(Palladium.MOD_ID, "energy_beam_renderers"), "Energy Beam Renderers")
                .add(HTMLBuilder.heading("Energy Beam Renderers"))
                .addDocumentationSettings(RENDERERS.values().stream().sorted(Comparator.comparing(o -> o.getId().toString())).collect(Collectors.toList()));
    }
}
