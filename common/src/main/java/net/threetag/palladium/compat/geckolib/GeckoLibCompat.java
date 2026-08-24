package net.threetag.palladium.compat.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.threetag.palladium.addonpack.parser.ItemParser;
import net.threetag.palladium.client.renderer.renderlayer.PackRenderLayerManager;
import net.threetag.palladium.compat.geckolib.armor.AddonGeoArmorItem;
import net.threetag.palladium.compat.geckolib.forge.GeckoLibCompatImpl;
import net.threetag.palladium.compat.geckolib.renderlayer.GeckoRenderLayer;
import software.bernie.geckolib.GeckoLibConstants;

public class GeckoLibCompat {

    public static void init() {
        ItemParser.registerTypeSerializer(new AddonGeoArmorItem.Parser());
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient() {
        PackRenderLayerManager.registerParser(ResourceLocation.fromNamespaceAndPath(GeckoLibConstants.MODID, "default"), GeckoRenderLayer::parse);
    }

    public static AddonGeoArmorItem createArmorItem(Holder<ArmorMaterial> materialIn, ArmorItem.Type type, Item.Properties builder) {
        return GeckoLibCompatImpl.createArmorItem(materialIn, type, builder);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderFirstPerson(AbstractClientPlayer player, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, ModelPart rendererArm, boolean rightArm) {
        GeckoLibCompatImpl.renderFirstPerson(player, stack, poseStack, buffer, combinedLight, rendererArm, rightArm);
    }


}
