package net.threetag.palladium.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.accessory.Accessory;
import net.threetag.palladium.accessory.AccessorySlot;
import net.threetag.palladium.accessory.HumanoidModelOverlayAccessory;
import net.threetag.palladium.accessory.OverlayAccessory;
import net.threetag.palladium.accessory.RenderLayerAccessory;
import net.threetag.palladium.accessory.SeaPickleHatAccessory;
import net.threetag.palladium.accessory.WoodenLegAccessory;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladium.client.renderer.renderlayer.IPackRenderLayer;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.client.renderer.entity.BodyPartClient;
import net.threetag.palladium.entity.BodyPart;
import net.threetag.palladium.util.ModelLayerLocationUtil;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.context.DataContext;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class AccessoryRenderer {

    private static final Map<HumanoidModelOverlayAccessory, HumanoidModels> HUMANOID_MODELS = new IdentityHashMap<>();
    private static final Map<WoodenLegAccessory, WoodenLegModels> WOODEN_LEG_MODELS = new IdentityHashMap<>();
    private static final Map<RenderLayerAccessory, IPackRenderLayer> RENDER_LAYERS = new IdentityHashMap<>();

    private AccessoryRenderer() {
    }

    public static void reload(EntityModelSet entityModelSet) {
        HUMANOID_MODELS.clear();
        WOODEN_LEG_MODELS.clear();
        RENDER_LAYERS.clear();

        for (Accessory accessory : Accessory.REGISTRY.getValues()) {
            if (accessory instanceof HumanoidModelOverlayAccessory humanoid) {
                HUMANOID_MODELS.put(humanoid, new HumanoidModels(
                        new HumanoidModel<>(entityModelSet.bakeLayer(toClientLocation(humanoid.getModelLayer(false)))),
                        new HumanoidModel<>(entityModelSet.bakeLayer(toClientLocation(humanoid.getModelLayer(true))))
                ));
            } else if (accessory instanceof WoodenLegAccessory woodenLeg) {
                WOODEN_LEG_MODELS.put(woodenLeg, new WoodenLegModels(
                        new PlayerModel<>(entityModelSet.bakeLayer(new ModelLayerLocation(Palladium.id("humanoid"), "wooden_legs")), false),
                        new PlayerModel<>(entityModelSet.bakeLayer(new ModelLayerLocation(Palladium.id("player"), "shortened_legs")), false)
                ));
            } else if (accessory instanceof RenderLayerAccessory renderLayerAccessory) {
                IPackRenderLayer renderLayer = PackRenderLayerManager.getInstance().getLayer(renderLayerAccessory.getRenderLayerId());
                if (renderLayer != null) {
                    RENDER_LAYERS.put(renderLayerAccessory, renderLayer);
                } else {
                    AddonPackLog.warning("Unknown render layer used in accessory: " + renderLayerAccessory.getRenderLayerId());
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void render(Accessory accessory, RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent, AccessorySlot slot, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (accessory instanceof RenderLayerAccessory renderLayerAccessory) {
            IPackRenderLayer renderLayer = RENDER_LAYERS.get(renderLayerAccessory);
            if (!renderLayerAccessory.isRenderingDisabled() && renderLayer != null) {
                renderLayer.render(DataContext.forEntity(player), poseStack, bufferSource, (EntityModel<Entity>) (EntityModel) renderLayerParent.getModel(), packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        } else if (accessory instanceof HumanoidModelOverlayAccessory humanoid) {
            HumanoidModels models = HUMANOID_MODELS.get(humanoid);
            if (models != null) {
                HumanoidModel model = models.get(PlayerUtil.hasSmallArms(player));
                renderLayerParent.getModel().copyPropertiesTo(model);
                setVisibility(humanoid, model, player, slot);
                ResourceLocation texture = humanoid.getTexture(PlayerUtil.hasSmallArms(player)).getTexture(DataContext.forEntity(player));
                RenderType renderType = humanoid.isGlowing() ? RenderType.eyes(texture) : getRenderType(player, texture, renderLayerParent.getModel());
                if (renderType != null) {
                    model.renderToBuffer(poseStack, bufferSource.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
                }
            }
        } else if (accessory instanceof OverlayAccessory overlay) {
            PlayerModel<AbstractClientPlayer> model = renderLayerParent.getModel();
            setVisibility(overlay, model, player, slot);
            ResourceLocation texture = overlay.getTexture(PlayerUtil.hasSmallArms(player)).getTexture(DataContext.forEntity(player));
            RenderType renderType = overlay.isGlowing() ? RenderType.eyes(texture) : getRenderType(player, texture, model);
            if (renderType != null) {
                model.renderToBuffer(poseStack, bufferSource.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
            }
        } else if (accessory instanceof WoodenLegAccessory woodenLeg) {
            renderWoodenLeg(woodenLeg, renderLayerParent, slot, poseStack, bufferSource, packedLight, player);
        } else if (accessory instanceof SeaPickleHatAccessory) {
            renderSeaPickle(renderLayerParent, poseStack, bufferSource, packedLight, player);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void renderArm(Accessory accessory, HumanoidArm arm, AbstractClientPlayer player, PlayerRenderer playerRenderer, ModelPart armPart, ModelPart armWearPart, AccessorySlot slot, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (accessory instanceof RenderLayerAccessory renderLayerAccessory) {
            IPackRenderLayer renderLayer = RENDER_LAYERS.get(renderLayerAccessory);
            if (!renderLayerAccessory.isRenderingDisabled() && renderLayer != null) {
                renderLayer.renderArm(DataContext.forEntity(player), arm, playerRenderer, poseStack, bufferSource, packedLight);
            }
        } else if (accessory instanceof HumanoidModelOverlayAccessory humanoid) {
            HumanoidModels models = HUMANOID_MODELS.get(humanoid);
            if (models != null) {
                HumanoidModel model = models.get(PlayerUtil.hasSmallArms(player));
                ResourceLocation texture = humanoid.getTexture(PlayerUtil.hasSmallArms(player)).getTexture(DataContext.forEntity(player));
                RenderType renderType = humanoid.isGlowing() ? RenderType.eyes(texture) : Objects.requireNonNull(getRenderType(player, texture, playerRenderer.getModel()));
                ModelPart modelArm = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                modelArm.copyFrom(armPart);
                modelArm.visible = true;
                modelArm.render(poseStack, bufferSource.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
            }
        } else if (accessory instanceof OverlayAccessory overlay) {
            ResourceLocation texture = overlay.getTexture(PlayerUtil.hasSmallArms(player)).getTexture(DataContext.forEntity(player));
            RenderType renderType = overlay.isGlowing() ? RenderType.eyes(texture) : Objects.requireNonNull(getRenderType(player, texture, playerRenderer.getModel()));
            armPart.xRot = 0.0F;
            armPart.visible = true;
            armPart.render(poseStack, bufferSource.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
            armWearPart.xRot = 0.0F;
            armWearPart.visible = true;
            armWearPart.render(poseStack, bufferSource.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    public static boolean isVisible(Accessory accessory, AccessorySlot slot, AbstractClientPlayer player, boolean isFirstPerson) {
        BodyPart.ModifiedBodyPartResult result = BodyPartClient.getModifiedBodyParts(player, isFirstPerson, false);
        boolean hidden = slot.getHiddenBodyParts(player).stream().filter(part -> !part.isOverlay()).anyMatch(result::isHiddenOrRemoved);
        return (slot.getCorrespondingEquipmentSlot() == null || player.getItemBySlot(slot.getCorrespondingEquipmentSlot()).isEmpty()) && !hidden;
    }

    public static boolean canRenderAsArm(Accessory accessory, AccessorySlot slot, HumanoidArm arm, AbstractClientPlayer player) {
        if (player.getMainArm() == HumanoidArm.RIGHT) {
            if (slot == AccessorySlot.MAIN_ARM || slot == AccessorySlot.MAIN_HAND) {
                return arm == HumanoidArm.RIGHT;
            } else if (slot == AccessorySlot.OFF_ARM || slot == AccessorySlot.OFF_HAND) {
                return arm == HumanoidArm.LEFT;
            }
        } else if (slot == AccessorySlot.MAIN_ARM || slot == AccessorySlot.MAIN_HAND) {
            return arm == HumanoidArm.LEFT;
        } else if (slot == AccessorySlot.OFF_ARM || slot == AccessorySlot.OFF_HAND) {
            return arm == HumanoidArm.RIGHT;
        }
        return false;
    }

    private static void setVisibility(OverlayAccessory accessory, HumanoidModel<?> model, AbstractClientPlayer player, AccessorySlot slot) {
        if (!accessory.onlyRendersSlot()) {
            model.setAllVisible(true);
            return;
        }

        model.setAllVisible(false);
        slot.getHiddenBodyParts(player).forEach(part -> BodyPartClient.setVisibility(part, model, true));

        if (accessory.hasHandVisibilityFix()) {
            if (slot == AccessorySlot.MAIN_HAND) {
                AccessorySlot.MAIN_ARM.getHiddenBodyParts(player).forEach(part -> BodyPartClient.setVisibility(part, model, true));
            } else if (slot == AccessorySlot.OFF_HAND) {
                AccessorySlot.OFF_ARM.getHiddenBodyParts(player).forEach(part -> BodyPartClient.setVisibility(part, model, true));
            }
        }
    }

    private static void renderWoodenLeg(WoodenLegAccessory accessory, RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent, AccessorySlot slot, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {
        WoodenLegModels models = WOODEN_LEG_MODELS.get(accessory);
        if (models == null) {
            return;
        }

        renderLayerParent.getModel().copyPropertiesTo(models.woodenLegs());
        renderLayerParent.getModel().copyPropertiesTo(models.shortenedLegs());
        models.woodenLegs().setAllVisible(false);
        models.shortenedLegs().setAllVisible(false);
        for (BodyPart part : slot.getHiddenBodyParts(player)) {
            BodyPartClient.setVisibility(part, models.woodenLegs(), true);
            BodyPartClient.setVisibility(part, models.shortenedLegs(), true);
        }

        RenderType skinRenderType = Objects.requireNonNull(getRenderType(player, player.getSkin().texture(), renderLayerParent.getModel()));
        models.shortenedLegs().renderToBuffer(poseStack, bufferSource.getBuffer(skinRenderType), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        RenderType woodenLegRenderType = Objects.requireNonNull(getRenderType(player, WoodenLegAccessory.TEXTURE, renderLayerParent.getModel()));
        models.woodenLegs().renderToBuffer(poseStack, bufferSource.getBuffer(woodenLegRenderType), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    private static void renderSeaPickle(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {
        poseStack.pushPose();
        renderLayerParent.getModel().head.translateAndRotate(poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(180F));
        poseStack.translate(-0.5F, 0.5F, -0.5F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                player.isEyeInFluid(FluidTags.WATER) ? Blocks.SEA_PICKLE.defaultBlockState() : Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.WATERLOGGED, false),
                poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();
    }

    @Nullable
    private static RenderType getRenderType(AbstractClientPlayer player, ResourceLocation texture, Model model) {
        boolean visible = !player.isInvisible();
        boolean visibleToLocalPlayer = !visible && !player.isInvisibleTo(Minecraft.getInstance().player);
        if (visibleToLocalPlayer) {
            return RenderType.entityTranslucent(texture);
        } else if (visible) {
            return model.renderType(texture);
        } else {
            return Minecraft.getInstance().shouldEntityAppearGlowing(player) ? RenderType.outline(texture) : null;
        }
    }

    private static ModelLayerLocation toClientLocation(ModelLayerLocationUtil location) {
        return new ModelLayerLocation(location.model(), location.layer());
    }

    private record HumanoidModels(HumanoidModel<?> normal, HumanoidModel<?> slim) {

        public HumanoidModel<?> get(boolean slimPlayer) {
            return slimPlayer ? this.slim : this.normal;
        }
    }

    private record WoodenLegModels(PlayerModel<AbstractClientPlayer> woodenLegs, PlayerModel<AbstractClientPlayer> shortenedLegs) {
    }

    public static class ReloadManager implements ResourceManagerReloadListener {

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            AccessoryRenderer.reload(Minecraft.getInstance().getEntityModels());
        }
    }
}
