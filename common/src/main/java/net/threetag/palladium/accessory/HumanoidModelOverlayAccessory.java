package net.threetag.palladium.accessory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.parser.AccessoryParser;
import net.threetag.palladium.client.dynamictexture.TextureReference;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.ModelLayerLocationUtil;
import net.threetag.palladium.util.SkinTypedValue;
import net.threetag.palladium.util.json.GsonUtil;

import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "PatternVariableHidesField"})
public class HumanoidModelOverlayAccessory extends OverlayAccessory {

    private final Supplier<ModelLayerLocationUtil> modelLayer, modelLayerSlim;

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, TextureReference texture, TextureReference textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, TextureReference texture, TextureReference textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, TextureReference texture) {
        super(texture);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, TextureReference texture) {
        super(texture);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, ResourceLocation texture, ResourceLocation textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, ResourceLocation texture, ResourceLocation textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, ResourceLocation texture) {
        super(texture);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, ResourceLocation texture) {
        super(texture);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, String texture, String textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, String texture, String textureSlim) {
        super(texture, textureSlim);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, String texture) {
        super(texture);
        this.modelLayer = this.modelLayerSlim = modelLayer;
    }

    public HumanoidModelOverlayAccessory(Supplier<ModelLayerLocationUtil> modelLayer, Supplier<ModelLayerLocationUtil> modelLayerSlim, String texture) {
        super(texture);
        this.modelLayer = modelLayer;
        this.modelLayerSlim = modelLayerSlim;
    }

    public ModelLayerLocationUtil getModelLayer(boolean slim) {
        return (slim ? this.modelLayerSlim : this.modelLayer).get();
    }

    public static class Serializer implements AccessoryParser.TypeSerializer {

        @Override
        public DefaultAccessory parse(JsonObject json) {
            SkinTypedValue<TextureReference> texture = SkinTypedValue.fromJSON(json.get("texture"), j -> TextureReference.parse(j.getAsString()));
            SkinTypedValue<ModelLayerLocationUtil> modelLayer = SkinTypedValue.fromJSON(json.get("model_layer"), j -> GsonUtil.convertToModelLayerLocationUtil(j, "model_layer"));
            var accessory = new HumanoidModelOverlayAccessory(modelLayer::normal, modelLayer::slim, texture.normal(), texture.slim());

            if (GsonHelper.getAsBoolean(json, "glowing", false)) {
                accessory.glowing();
            }

            if (GsonHelper.getAsBoolean(json, "only_render_for_slot", false)) {
                accessory.onlyRenderSlot();
            }

            if (GsonHelper.getAsBoolean(json, "hand_visibility_fix", false)) {
                accessory.handVisibilityFix();
            }

            return accessory;
        }

        @Override
        public void generateDocumentation(JsonDocumentationBuilder builder) {
            builder.setTitle("Humanoid Model Layer");
            builder.setDescription("Renders a humanoid model layer on the player");

            builder.addProperty("texture", TextureReference.class)
                    .description("Texture for the accessory. Can be skin-typed by specifying 'normal' and 'slim' in a json object.")
                    .required().exampleJson(new JsonPrimitive("example:textures/accessory/test.png"));

            builder.addProperty("model_layer", ModelLayerLocationUtil.class)
                    .description("Model layer for the accessory. Can be skin-typed by specifying 'normal' and 'slim' in a json object.")
                    .required().exampleJson(new JsonPrimitive("example:textures/accessory/test.png"));

            builder.addProperty("glowing", Boolean.class)
                    .description("Makes the overlay glow")
                    .fallback(false).exampleJson(new JsonPrimitive(false));

            builder.addProperty("only_render_for_slot", Boolean.class)
                    .description("If set to true, the texture will only render for the specified slot of the accessory. Example: The texture has a full player skin and the slot is set to 'head', only the head part of it will render.")
                    .fallback(false).exampleJson(new JsonPrimitive(false));

            builder.addProperty("hand_visibility_fix", Boolean.class)
                    .description("If 'only_render_for_slot' is set to true while the slot is for a hand but you want to have the accessory on the arm, set this to true.")
                    .fallback(false).exampleJson(new JsonPrimitive(false));

            AccessoryParser.addSlotDocumentation(builder);
        }

        @Override
        public ResourceLocation getId() {
            return Palladium.id("humanoid_model_layer");
        }
    }
}
