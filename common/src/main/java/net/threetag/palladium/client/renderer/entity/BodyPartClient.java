package net.threetag.palladium.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import net.threetag.palladium.accessory.Accessory;
import net.threetag.palladium.accessory.AccessorySlot;
import net.threetag.palladium.accessory.RenderLayerAccessory;
import net.threetag.palladium.client.model.animation.PalladiumAnimation;
import net.threetag.palladium.client.renderer.accessory.AccessoryRenderer;
import net.threetag.palladium.client.renderer.item.armor.ArmorRendererData;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.compat.mermod.MermodClientCompat;
import net.threetag.palladium.entity.BodyPart;
import net.threetag.palladium.entity.PlayerModelCacheExtension;
import net.threetag.palladium.item.ArmorWithRenderer;
import net.threetag.palladium.mixin.client.PlayerRendererInvoker;
import net.threetag.palladium.power.ability.Abilities;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.power.ability.HideBodyPartAbility;
import net.threetag.palladium.power.ability.RemoveBodyPartAbility;
import net.threetag.palladium.util.SizeUtil;
import net.threetag.palladium.util.context.DataContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Map;

public final class BodyPartClient {

    private BodyPartClient() {
    }

    @Nullable
    public static ModelPart getModelPart(BodyPart part, HumanoidModel<?> model) {
        PlayerModel<?> playerModel = model instanceof PlayerModel<?> player ? player : null;
        return switch (part) {
            case HEAD -> model.head;
            case HEAD_OVERLAY -> model.hat;
            case CHEST -> model.body;
            case CHEST_OVERLAY -> playerModel != null ? playerModel.jacket : null;
            case RIGHT_ARM -> model.rightArm;
            case RIGHT_ARM_OVERLAY -> playerModel != null ? playerModel.rightSleeve : null;
            case LEFT_ARM -> model.leftArm;
            case LEFT_ARM_OVERLAY -> playerModel != null ? playerModel.leftSleeve : null;
            case RIGHT_LEG -> model.rightLeg;
            case RIGHT_LEG_OVERLAY -> playerModel != null ? playerModel.rightPants : null;
            case LEFT_LEG -> model.leftLeg;
            case LEFT_LEG_OVERLAY -> playerModel != null ? playerModel.leftPants : null;
            case CAPE -> playerModel != null ? playerModel.cloak : null;
        };
    }

    public static void setVisibility(BodyPart part, HumanoidModel<?> model, boolean visible) {
        ModelPart modelPart = getModelPart(part, model);
        if (modelPart != null) {
            modelPart.visible = visible;
        }
    }

    public static void hideHiddenOrRemovedParts(HumanoidModel<?> model, BodyPart.ModifiedBodyPartResult result) {
        for (BodyPart part : BodyPart.values()) {
            if (result.isHiddenOrRemoved(part)) {
                setVisibility(part, model, false);
            }
        }
    }

    public static void hideRemovedParts(HumanoidModel<?> model, BodyPart.ModifiedBodyPartResult result) {
        for (BodyPart part : BodyPart.values()) {
            if (result.isRemoved(part)) {
                setVisibility(part, model, false);
            }
        }
    }

    public static void resetBodyParts(LivingEntity entity, HumanoidModel<?> model) {
        if (!(entity instanceof Player player)) {
            model.setAllVisible(true);
            return;
        }

        if (player.isSpectator()) {
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = true;
            return;
        }

        model.setAllVisible(true);
        model.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
        if (model instanceof PlayerModel<?> playerModel) {
            playerModel.jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
            playerModel.leftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playerModel.rightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playerModel.leftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playerModel.rightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playerModel.cloak.visible = player.isModelPartShown(PlayerModelPart.CAPE);

            if (MermodClientCompat.INSTANCE.shouldRenderTail(player)) {
                playerModel.rightLeg.visible = false;
                playerModel.rightPants.visible = false;
                playerModel.leftLeg.visible = false;
                playerModel.leftPants.visible = false;
            }
        }
    }

    public static BodyPart.ModifiedBodyPartResult getModifiedBodyParts(LivingEntity entity, boolean isFirstPerson) {
        return getModifiedBodyParts(entity, isFirstPerson, true);
    }

    public static BodyPart.ModifiedBodyPartResult getModifiedBodyParts(LivingEntity entity, boolean isFirstPerson, boolean includeAccessories) {
        BodyPart.ModifiedBodyPartResult result = new BodyPart.ModifiedBodyPartResult();

        if (entity instanceof AbstractClientPlayer player) {
            collectArmorModifications(player, result);
            if (includeAccessories) {
                collectAccessoryModifications(player, isFirstPerson, result);
            }
        }

        for (AbilityInstance instance : AbilityUtil.getEnabledInstances(entity, Abilities.HIDE_BODY_PART.get())) {
            if (!isFirstPerson || instance.getProperty(HideBodyPartAbility.AFFECTS_FIRST_PERSON)) {
                instance.getProperty(HideBodyPartAbility.BODY_PARTS).forEach(result::hide);
            }
        }

        for (AbilityInstance instance : AbilityUtil.getEnabledInstances(entity, Abilities.REMOVE_BODY_PART.get())) {
            if (!isFirstPerson || instance.getProperty(RemoveBodyPartAbility.AFFECTS_FIRST_PERSON)) {
                instance.getProperty(RemoveBodyPartAbility.BODY_PARTS).forEach(result::remove);
            }
        }

        PackRenderLayerManager.forEachLayer(entity, (context, layer) -> layer.getHiddenBodyParts(entity).forEach(result::hide));
        return result;
    }

    private static void collectArmorModifications(AbstractClientPlayer player, BodyPart.ModifiedBodyPartResult result) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) {
                continue;
            }

            var stack = player.getItemBySlot(slot);
            boolean hidesLayer = BodyPart.HIDES_LAYER.contains(stack.getItem())
                    || stack.getItem() instanceof ArmorWithRenderer armor
                    && armor.getCachedArmorRenderer() instanceof ArmorRendererData renderer
                    && renderer.hidesSecondPlayerLayer(DataContext.forArmorInSlot(player, slot));
            if (!hidesLayer) {
                continue;
            }

            if (slot == EquipmentSlot.HEAD) {
                result.remove(BodyPart.HEAD_OVERLAY);
            } else if (slot == EquipmentSlot.CHEST) {
                result.remove(BodyPart.CHEST_OVERLAY);
                result.remove(BodyPart.RIGHT_ARM_OVERLAY);
                result.remove(BodyPart.LEFT_ARM_OVERLAY);
            } else {
                result.remove(BodyPart.RIGHT_LEG_OVERLAY);
                result.remove(BodyPart.LEFT_LEG_OVERLAY);
            }
        }
    }

    private static void collectAccessoryModifications(AbstractClientPlayer player, boolean isFirstPerson, BodyPart.ModifiedBodyPartResult result) {
        Accessory.getPlayerData(player).ifPresent(data -> {
            for (Map.Entry<AccessorySlot, Collection<Accessory>> entry : data.getSlots().entrySet()) {
                AccessorySlot slot = entry.getKey();
                Collection<Accessory> accessories = entry.getValue();
                if (accessories.isEmpty()) {
                    continue;
                }

                slot.getHiddenBodyParts(player).forEach(result::hide);
                for (Accessory accessory : accessories) {
                    if (accessory instanceof RenderLayerAccessory renderLayerAccessory && AccessoryRenderer.isVisible(accessory, slot, player, isFirstPerson)) {
                        var renderLayer = PackRenderLayerManager.getInstance().getLayer(renderLayerAccessory.getRenderLayerId());
                        if (renderLayer != null) {
                            renderLayer.getHiddenBodyParts(player).forEach(result::hide);
                        }
                    }
                }
            }
        });
    }

    public static Matrix4f getTransformationMatrix(BodyPart part, Vector3f offset, HumanoidModel<?> model, @Nullable PalladiumAnimation.PoseStackResult bodyAnimation, AbstractClientPlayer player, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        ModelPart modelPart = getModelPart(part, model);
        if (modelPart == null) {
            return poseStack.last().pose();
        }

        EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (renderer instanceof PlayerRendererInvoker invoker) {
            float width = SizeUtil.getInstance().getModelWidthScale(player, partialTicks);
            float height = SizeUtil.getInstance().getModelHeightScale(player, partialTicks);
            poseStack.scale(width, height, width);

            float bodyRotation = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
            float headRotation = Mth.rotLerp(partialTicks, player.yHeadRotO, player.yHeadRot);
            if (player.isPassenger() && player.getVehicle() instanceof LivingEntity vehicle) {
                bodyRotation = Mth.rotLerp(partialTicks, vehicle.yBodyRotO, vehicle.yBodyRot);
                float difference = Mth.wrapDegrees(headRotation - bodyRotation);
                difference = Mth.clamp(difference, -85.0F, 85.0F);
                bodyRotation = headRotation - difference;
                if (difference * difference > 2500.0F) {
                    bodyRotation += difference * 0.2F;
                }
            }

            invoker.invokeSetupRotations(player, poseStack, player.tickCount + partialTicks, bodyRotation, partialTicks, player.getScale());
            if (bodyAnimation != null) {
                bodyAnimation.apply(poseStack);
            }

            poseStack.scale(-1.0F, -1.0F, 1.0F);
            invoker.invokeScale(player, poseStack, partialTicks);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            modelPart.translateAndRotate(poseStack);
            poseStack.translate(offset.x, offset.y, offset.z);
        }

        return poseStack.last().pose();
    }

    public static Matrix4f getTransformationMatrix(BodyPart part, Vector3f offset, AbstractClientPlayer player, float partialTicks) {
        if (player instanceof PlayerModelCacheExtension extension) {
            return getTransformationMatrix(part, offset, extension.palladium$getCachedModel(), extension.palladium$getBodyAnimationResult(), player, partialTicks);
        }
        return new Matrix4f();
    }

    public static Vec3 getInWorldPosition(BodyPart part, Vector3f offset, HumanoidModel<?> model, @Nullable PalladiumAnimation.PoseStackResult bodyAnimation, AbstractClientPlayer player, float partialTicks) {
        Vector3f transformed = getTransformationMatrix(part, offset, model, bodyAnimation, player, partialTicks).transformPosition(new Vector3f());
        return player.getPosition(partialTicks).add(transformed.x, transformed.y, transformed.z);
    }

    public static Vec3 getInWorldPosition(BodyPart part, Vector3f offset, AbstractClientPlayer player, float partialTicks) {
        if (player instanceof PlayerModelCacheExtension extension) {
            return getInWorldPosition(part, offset, extension.palladium$getCachedModel(), extension.palladium$getBodyAnimationResult(), player, partialTicks);
        }
        return player.getPosition(partialTicks);
    }
}
