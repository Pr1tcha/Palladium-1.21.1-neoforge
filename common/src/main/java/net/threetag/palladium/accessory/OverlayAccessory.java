package net.threetag.palladium.accessory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.addonpack.parser.AccessoryParser;
import net.threetag.palladium.client.dynamictexture.TextureReference;
import net.threetag.palladium.documentation.JsonDocumentationBuilder;
import net.threetag.palladium.util.SkinTypedValue;

public class OverlayAccessory extends DefaultAccessory {

    protected final TextureReference texture;
    protected final TextureReference textureSlim;
    protected boolean glowing = false;
    protected boolean onlyRenderSlot = false;
    protected boolean handVisibilityFix = false;

    public OverlayAccessory(TextureReference texture, TextureReference textureSlim) {
        this.texture = texture;
        this.textureSlim = textureSlim;
    }

    public OverlayAccessory(TextureReference texture) {
        this.texture = this.textureSlim = texture;
    }

    public OverlayAccessory(ResourceLocation texture, ResourceLocation textureSlim) {
        this(TextureReference.normal(texture), TextureReference.normal(textureSlim));
    }

    public OverlayAccessory(ResourceLocation texture) {
        this(TextureReference.normal(texture));
    }

    public OverlayAccessory(String texture, String textureSlim) {
        this(Palladium.id("textures/models/accessories/" + texture + ".png"),
                Palladium.id("textures/models/accessories/" + textureSlim + ".png"));
    }

    public OverlayAccessory(String texture) {
        this(Palladium.id("textures/models/accessories/" + texture + ".png"));
    }

    public OverlayAccessory glowing() {
        this.glowing = true;
        return this;
    }

    public OverlayAccessory onlyRenderSlot() {
        this.onlyRenderSlot = true;
        return this;
    }

    public OverlayAccessory handVisibilityFix() {
        this.handVisibilityFix = true;
        return this.onlyRenderSlot();
    }

    public TextureReference getTexture(boolean slim) {
        return slim ? this.textureSlim : this.texture;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public boolean onlyRendersSlot() {
        return this.onlyRenderSlot;
    }

    public boolean hasHandVisibilityFix() {
        return this.handVisibilityFix;
    }

    public static class Serializer implements AccessoryParser.TypeSerializer {

        @Override
        public DefaultAccessory parse(JsonObject json) {
            SkinTypedValue<TextureReference> texture = SkinTypedValue.fromJSON(json.get("texture"), j -> TextureReference.parse(j.getAsString()));
            var accessory = new OverlayAccessory(texture.normal(), texture.slim());

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
            builder.setTitle("Overlay");
            builder.setDescription("Renders a texture skin-tight on the player");

            builder.addProperty("texture", TextureReference.class)
                    .description("Texture of the overlay. Can be skin-typed by specifying 'normal' and 'slim' in a json object.")
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
            return Palladium.id("overlay");
        }
    }

}
