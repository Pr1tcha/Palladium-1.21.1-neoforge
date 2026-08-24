package net.threetag.palladium.client.renderer.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.threetag.palladium.client.model.SuitStandBasePlateModel;
import net.threetag.palladium.client.model.SuitStandModel;
import net.threetag.palladium.client.renderer.entity.SuitStandRenderer;
import net.threetag.palladium.entity.SuitStand;

public class ColorableSuitStandLayer extends RenderLayer<SuitStand, SuitStandBasePlateModel> {

    private final SuitStandModel model;

    public ColorableSuitStandLayer(RenderLayerParent<SuitStand, SuitStandBasePlateModel> renderLayerParent, SuitStandModel suitStandModel) {
        super(renderLayerParent);
        this.model = suitStandModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SuitStand livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!livingEntity.isInvisible()) {
            int color;
            if (livingEntity.hasCustomName() && "jeb_".equals(livingEntity.getName().getString())) {
                int j = livingEntity.tickCount / 25 + livingEntity.getId();
                int k = DyeColor.values().length;
                int l = j % k;
                int m = (j + 1) % k;
                float f = ((float) (livingEntity.tickCount % 25) + partialTick) / 25.0F;
                color = FastColor.ARGB32.lerp(f, Sheep.getColor(DyeColor.byId(l)), Sheep.getColor(DyeColor.byId(m)));
            } else {
                color = Sheep.getColor(livingEntity.getDyeColor());
            }

            this.getParentModel().copyPropertiesTo(this.model);
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SuitStandRenderer.DEFAULT_SKIN_LOCATION, poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, color);

        }
    }
}
