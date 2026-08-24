package net.threetag.palladium.power.ability;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.util.property.*;
import net.threetag.palladiumcore.registry.client.OverlayRegistry;

import java.util.List;

public class TextOverlayAbility extends Ability {

    public static final PalladiumProperty<Component> TEXT = new ComponentProperty("text").sync(SyncType.SELF).configurable("Text component that will be displayed");
    public static final PalladiumProperty<String> PROPERTY = new StringProperty("property").sync(SyncType.SELF).configurable("If given, the value of this property will be appened to the text");
    public static final PalladiumProperty<Vec3> TRANSLATE = new Vec3Property("translate").sync(SyncType.SELF).configurable("Translation of the rendered object");
    public static final PalladiumProperty<Vec3> ROTATE = new Vec3Property("rotate").sync(SyncType.SELF).configurable("Rotation of the rendered object");
    public static final PalladiumProperty<Vec3> SCALE = new Vec3Property("scale").sync(SyncType.SELF).configurable("Scale of the rendered object");
    public static final PalladiumProperty<TextAlignmentProperty.TextAlignment> TEXT_ALIGNMENT = new TextAlignmentProperty("text_alignment").sync(SyncType.SELF).configurable("Determines how the texture is aligned at the position");
    public static final PalladiumProperty<TextureAlignmentProperty.TextureAlignment> ALIGNMENT = new TextureAlignmentProperty("alignment").sync(SyncType.SELF).configurable("Determines how the texture is aligned on the screen");

    public TextOverlayAbility() {
        this.withProperty(TEXT, Component.literal("Hello World"));
        this.withProperty(PROPERTY, null);
        this.withProperty(TRANSLATE, Vec3.ZERO);
        this.withProperty(ROTATE, Vec3.ZERO);
        this.withProperty(SCALE, new Vec3(1, 1, 1));
        this.withProperty(TEXT_ALIGNMENT, TextAlignmentProperty.TextAlignment.LEFT);
        this.withProperty(ALIGNMENT, TextureAlignmentProperty.TextureAlignment.TOP_LEFT);
    }

    @Override
    public boolean isEffect() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements OverlayRegistry.IngameOverlay {

        @Override
        public void render(Minecraft minecraft, Gui gui, GuiGraphics guiGraphics, float partialTicks, int width, int height) {
            if (minecraft.player == null) {
                return;
            }

            List<AbilityInstance> entries = AbilityUtil.getEnabledInstances(minecraft.player, Abilities.TEXT_OVERLAY.get()).stream().sorted((a1, a2) -> (int) (a1.getProperty(TRANSLATE).z - a2.getProperty(TRANSLATE).z)).toList();
            for (AbilityInstance entry : entries) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                var alignment = entry.getProperty(ALIGNMENT);
                var translate = entry.getProperty(TRANSLATE);
                var rotate = entry.getProperty(ROTATE);

                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(translate.x, translate.y, translate.z);

                if (!alignment.isStretched()) {
                    var horizontal = alignment.getHorizontal();
                    var vertical = alignment.getVertical();

                    if (horizontal > 0) {
                        guiGraphics.pose().translate(horizontal == 1 ? width / 2F : width, 0, 0);
                    }

                    if (vertical > 0) {
                        guiGraphics.pose().translate(0, vertical == 1 ? height / 2F : height, 0);
                    }
                }

                if (rotate.x != 0D) {
                    guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float) rotate.x));
                }

                if (rotate.y != 0D) {
                    guiGraphics.pose().mulPose(Axis.YP.rotationDegrees((float) rotate.y));
                }

                if (rotate.z != 0D) {
                    guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees((float) rotate.z));
                }

                var text = entry.getProperty(TEXT).copy();
                var property = entry.getProperty(PROPERTY);

                if (property != null && !property.isBlank()) {
                    EntityPropertyHandler.getHandler(minecraft.player).ifPresent(handler -> {
                        var prop = handler.getPropertyByName(property);

                        if (prop != null) {
                            Object value = handler.get(prop);

                            if (value != null) {
                                text.append(value.toString());
                            }
                        }
                    });
                }

                renderText(guiGraphics, text, entry.getProperty(TEXT_ALIGNMENT), entry.getProperty(SCALE));
                RenderSystem.disableBlend();
                guiGraphics.pose().popPose();
            }
        }

        private void renderText(GuiGraphics guiGraphics, Component text, TextAlignmentProperty.TextAlignment textAlignment, Vec3 scale) {
            guiGraphics.pose().scale((float) scale.x, (float) scale.y, (float) scale.z);
            var font = Minecraft.getInstance().font;
            var width = font.width(text);

            if (textAlignment == TextAlignmentProperty.TextAlignment.CENTER) {
                guiGraphics.drawCenteredString(font, text, 0, 0, 0xffffffff);
            } else {
                guiGraphics.drawString(font, text, textAlignment == TextAlignmentProperty.TextAlignment.LEFT ? 0 : -width, 0, 0xffffffff);
            }
        }
    }

    @Override
    public String getDocumentationDescription() {
        return "Displays a text overlay on the screen.";
    }
}
