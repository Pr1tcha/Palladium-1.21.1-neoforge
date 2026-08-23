package net.threetag.palladium.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladium.documentation.HTMLBuilder;
import net.threetag.palladium.util.BedrockModelUtil;
import net.threetag.palladium.util.json.GsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ModelLayerManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public ModelLayerManager() {
        super(GSON, "palladium/model_layers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ModelLayerLocation, LayerDefinition> jsonRoots = new HashMap<>();
        EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
        var codeRoots = entityModels.roots;
        var roots = new HashMap<>(codeRoots);

        object.forEach((id, jsonElement) -> {
            ModelLayerLocation layerLocation = mapPathToModelLayerLoc(id);
            try {
                LayerDefinition layerDefinition = parseLayerDefinition(jsonElement.getAsJsonObject(), layerLocation != null ? layerLocation.toString() : id.toString());
                if (layerLocation != null)
                    jsonRoots.put(layerLocation, layerDefinition);
                jsonRoots.put(new ModelLayerLocation(id, "main"), layerDefinition);
            } catch (Exception e) {
                AddonPackLog.error("Error parsing entity model json " + id, e);
            }
        });

        roots.putAll(jsonRoots);
        entityModels.roots = ImmutableMap.copyOf(roots);

        dumpLayers();
    }

    public static LayerDefinition parseLayerDefinition(JsonObject json, String id) {
        if (GsonHelper.isValidNode(json, "minecraft:geometry")) {
            return BedrockModelUtil.parseAsLayerDefinition(json);
        }

        AddonPackLog.warning("Model layer '" + id + "' still uses Palladium Entity Format! Please switch to Bedrock Entity Format.");

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        JsonObject parts = GsonHelper.getAsJsonObject(json, "mesh");

        for (Map.Entry<String, JsonElement> entry : parts.entrySet()) {
            String key = entry.getKey();
            JsonObject part = entry.getValue().getAsJsonObject();
            parseCubeListBuilder(key, root, part);
        }

        return LayerDefinition.create(meshDefinition, GsonHelper.getAsInt(json, "texture_width"), GsonHelper.getAsInt(json, "texture_height"));
    }

    public static void parseCubeListBuilder(String name, PartDefinition parent, JsonObject json) {
        JsonArray cubes = GsonHelper.getAsJsonArray(json, "cubes");
        CubeListBuilder builder = CubeListBuilder.create();

        for (JsonElement j : cubes) {
            JsonObject cubeJson = j.getAsJsonObject();
            float[] origin = GsonUtil.getFloatArray(cubeJson, 3, "origin");
            float[] dimensions = GsonUtil.getFloatArray(cubeJson, 3, "dimensions");
            int[] textureOffset = GsonUtil.getIntArray(cubeJson, 2, "texture_offset", 0, 0);
            float[] textureScale = GsonUtil.getFloatArray(cubeJson, 2, "texture_scale", 1F, 1F);
            float[] deformation = GsonUtil.getFloatArray(cubeJson, 3, "deformation", 0F, 0F, 0F);

            builder.mirror(GsonHelper.getAsBoolean(cubeJson, "mirror", false));
            builder.texOffs(textureOffset[0], textureOffset[1]);
            builder.addBox(
                    origin[0],
                    origin[1],
                    origin[2],
                    dimensions[0],
                    dimensions[1],
                    dimensions[2],
                    new CubeDeformation(deformation[0], deformation[1], deformation[2]),
                    textureScale[0],
                    textureScale[1]
            );
        }

        PartPose partPose = PartPose.ZERO;

        if (GsonHelper.isValidNode(json, "part_pose")) {
            JsonObject partPoseJson = GsonHelper.getAsJsonObject(json, "part_pose");
            float[] offset = GsonUtil.getFloatArray(partPoseJson, 3, "offset", 0F, 0F, 0F);
            float[] rotation1 = GsonUtil.getFloatArray(partPoseJson, 3, "rotation", 0F, 0F, 0F);
            double[] rotation = new double[3];
            for (int i = 0; i < 3; i++) {
                rotation[i] = Math.toRadians(rotation1[i]);
            }
            partPose = PartPose.offsetAndRotation(offset[0], offset[1], offset[2], (float) rotation[0], (float) rotation[1], (float) rotation[2]);
        }

        PartDefinition partDefinition = parent.addOrReplaceChild(name, builder, partPose);

        if (GsonHelper.isValidNode(json, "children")) {
            JsonObject children = GsonHelper.getAsJsonObject(json, "children");

            for (Map.Entry<String, JsonElement> entry : children.entrySet()) {
                String key = entry.getKey();
                JsonObject part = entry.getValue().getAsJsonObject();
                parseCubeListBuilder(key, partDefinition, part);
            }
        }

    }

    private static ModelLayerLocation mapPathToModelLayerLoc(ResourceLocation path) {
        int idx = path.getPath().indexOf('/');
        if (idx == -1) {
            return null;
        }

        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath().substring(idx + 1)), path.getPath().substring(0, idx));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void dumpLayers() {
        var oldPlayerFile = new File(HTMLBuilder.SUBFOLDER, "palladium/models/minecraft/main/player.json");
        var oldFile = new File(HTMLBuilder.SUBFOLDER, "palladium/models/");
        var folder = new File(HTMLBuilder.SUBFOLDER, "palladium/vanilla_models/");

        if (oldPlayerFile.exists()) {
            deleteDirectory(oldFile);
            System.out.println("HALLO DELETE");
        }

        Minecraft.getInstance().getEntityModels().roots.forEach((modelLayerLocation, layerDefinition) -> {
            if (!modelLayerLocation.getLayer().equalsIgnoreCase("main") || !modelLayerLocation.getModel().getNamespace().equalsIgnoreCase("minecraft")) {
                return;
            }

            File outputFile = new File(folder, modelLayerLocation.getModel().getPath() + ".json");

            try {
                JsonObject json = BedrockModelUtil.toJsonModel(layerDefinition, modelLayerLocation.toString());

                if (!outputFile.exists()) {
                    outputFile.getParentFile().mkdirs();
                }

                Files.writeString(outputFile.toPath(), GSON.toJson(json));
            } catch (IOException e) {
                Palladium.LOGGER.error("Error while dumping model {}", outputFile, e);
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

}
