package net.threetag.palladium.accessory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.parser.AccessoryParser;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.json.GsonUtil;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RenderLayerAccessory extends DefaultAccessory {

    public final ResourceLocation renderLayerId;
    private boolean disableRendering = false;

    public RenderLayerAccessory(ResourceLocation renderLayerId) {
        this.renderLayerId = renderLayerId;
    }

    public RenderLayerAccessory disableRendering() {
        this.disableRendering = true;
        return this;
    }

    public ResourceLocation getRenderLayerId() {
        return this.renderLayerId;
    }

    public boolean isRenderingDisabled() {
        return this.disableRendering;
    }

    public static class Serializer implements AccessoryParser.TypeSerializer {

        @Override
        public DefaultAccessory parse(JsonObject json) {
            var accessory = new RenderLayerAccessory(GsonUtil.getAsResourceLocation(json, "render_layer"));

            if (GsonHelper.getAsBoolean(json, "disable_rendering", false)) {
                accessory.disableRendering();
            }

            return accessory;
        }

        @Override
        public void generateDocumentation(JsonDocumentationBuilder builder) {
            builder.setTitle("Render Layer");
            builder.setDescription("Let's you use a render layer as an accessory");

            builder.addProperty("render_layer", ResourceLocation.class)
                    .description("ID of the render layer that's being used")
                    .required().exampleJson(new JsonPrimitive("namespace:example_layer"));

            builder.addProperty("disable_rendering", Boolean.class)
                    .description("Disables the rendering of the accessory, in case you want to hook it up to a render_layer_from_accessory ability")
                    .fallback(false).exampleJson(new JsonPrimitive(false));

            AccessoryParser.addSlotDocumentation(builder);
        }

        @Override
        public ResourceLocation getId() {
            return Palladium.id("render_layer");
        }
    }
}
