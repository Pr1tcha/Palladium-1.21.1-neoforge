package net.threetag.palladium.client.dynamictexture;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladium.client.dynamictexture.variable.ITextureVariable;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladium.util.json.GsonUtil;

import java.util.Map;

public abstract class DynamicModelLayerLocation {

    public abstract ModelLayerLocation getModelLayer(DataContext context);

    public static DynamicModelLayerLocation fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new Static(GsonUtil.convertToModelLayerLocation(jsonElement, "$"));
        } else if (jsonElement.isJsonObject()) {
            var json = jsonElement.getAsJsonObject();
            if (GsonHelper.isValidNode(json, "variables")) {
                var location = new Dynamic(GsonHelper.getAsString(json, "base"));
                JsonObject variables = GsonHelper.getAsJsonObject(json, "variables");

                for (Map.Entry<String, JsonElement> entry : variables.entrySet()) {
                    JsonObject variableJson = entry.getValue().getAsJsonObject();
                    ResourceLocation variableId = GsonUtil.getAsResourceLocation(variableJson, "type");
                    var serializer = DynamicTextureManager.getTextureVariableSerializer(variableId);

                    if (serializer != null) {
                        ITextureVariable variable = serializer.parse(variableJson);
                        location.addVariable(entry.getKey(), variable);
                    } else {
                        AddonPackLog.error("Unknown texture variable '" + variableId + "'");
                    }
                }

                return location;
            } else {
                return new Static(GsonUtil.getAsModelLayerLocation(json, "base"));
            }
        } else {
            throw new JsonSyntaxException("Model layer must be a string or an object");
        }
    }

    public static class Static extends DynamicModelLayerLocation {

        private final ModelLayerLocation modelLayer;

        public Static(ModelLayerLocation modelLayer) {
            this.modelLayer = modelLayer;
        }

        @Override
        public ModelLayerLocation getModelLayer(DataContext context) {
            return this.modelLayer;
        }
    }

    public static class Dynamic extends DynamicModelLayerLocation {

        private final String base;
        private final Map<String, ITextureVariable> textureVariableMap = Maps.newHashMap();

        public Dynamic(String base) {
            this.base = base;
        }

        public Dynamic addVariable(String name, ITextureVariable variable) {
            this.textureVariableMap.put(name, variable);
            return this;
        }

        @Override
        public ModelLayerLocation getModelLayer(DataContext context) {
            String model = DefaultDynamicTexture.replaceVariables(this.base, context, this.textureVariableMap);
            String[] s = model.split("#", 2);

            if (s.length == 1) {
                return new ModelLayerLocation(ResourceLocation.parse(s[0]), "main");
            } else {
                return new ModelLayerLocation(ResourceLocation.parse(s[0]), s[1]);
            }
        }
    }

}
