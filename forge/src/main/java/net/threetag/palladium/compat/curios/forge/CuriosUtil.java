package net.threetag.palladium.compat.curios.forge;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.threetag.palladium.client.renderer.item.CurioTrinketRenderer;
import net.threetag.palladium.compat.curiostinkets.CurioTrinket;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsSlotInv;
import net.threetag.palladium.compat.curiostinkets.CuriosTrinketsUtil;
import net.threetag.palladium.power.ability.RestrictSlotsAbility;
import net.threetag.palladium.util.PlayerSlot;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CuriosUtil extends CuriosTrinketsUtil {

    @SubscribeEvent
    public void onCurioEquip(CurioCanEquipEvent e) {
        var key = "curios:" + e.getSlotContext().identifier();

        if (RestrictSlotsAbility.isRestricted(e.getEntity(), key)) {
            e.setEquipResult(TriState.FALSE);
        }
    }

    @Override
    public boolean isCurios() {
        return true;
    }

    @Override
    public void registerCurioTrinket(Item item, CurioTrinket curioTrinket) {
        CuriosApi.registerCurio(item, new Handler(curioTrinket));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerRenderer(Item item, CurioTrinketRenderer renderer) {
        CuriosRendererRegistry.register(item, () -> new Renderer(renderer));
    }

    @Override
    public CuriosTrinketsSlotInv getSlot(LivingEntity entity, String slot) {
        final CuriosTrinketsSlotInv[] slotHandler = {CuriosTrinketsSlotInv.EMPTY};
        CuriosApi.getCuriosInventory(entity).ifPresent(curios -> {
            curios.getStacksHandler(slot).ifPresent(stacks -> {
                slotHandler[0] = new SlotInv(stacks.getStacks());
            });
        });
        return slotHandler[0];
    }

    public static class SlotInv implements CuriosTrinketsSlotInv {

        private final IDynamicStackHandler stackHandler;

        public SlotInv(IDynamicStackHandler stackHandler) {
            this.stackHandler = stackHandler;
        }

        @Override
        public int getSlots() {
            return this.stackHandler.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return this.stackHandler.getStackInSlot(index);
        }

        @Override
        public void setStackInSlot(int index, ItemStack stack) {
            this.stackHandler.setStackInSlot(index, stack);
        }
    }

    public static class Handler implements ICurioItem {

        private final CurioTrinket curioTrinket;

        public Handler(CurioTrinket curioTrinket) {
            this.curioTrinket = curioTrinket;
        }

        @Override
        public void curioTick(SlotContext slotContext, ItemStack stack) {
            this.curioTrinket.tick(slotContext.entity(), stack);
        }

        @Override
        public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
            this.curioTrinket.onEquip(stack, slotContext.entity());
        }

        @Override
        public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
            this.curioTrinket.onUnequip(stack, slotContext.entity());
        }

        @Override
        public boolean canEquip(SlotContext slotContext, ItemStack stack) {
            return this.curioTrinket.canEquip(stack, slotContext.entity());
        }

        @Override
        public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
            return this.curioTrinket.canUnequip(stack, slotContext.entity());
        }

        @Override
        public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
            return this.curioTrinket.canRightClickEquip();
        }

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
            Multimap<Holder<Attribute>, AttributeModifier> map = ArrayListMultimap.create();
            map.putAll(ICurioItem.super.getAttributeModifiers(slotContext, id, stack));
            this.curioTrinket.getModifiers(PlayerSlot.get("curios:" + slotContext.identifier()), slotContext.entity())
                    .forEach((attribute, modifier) -> map.put(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier));
            return map;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer {

        private final CurioTrinketRenderer renderer;

        public Renderer(CurioTrinketRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            this.renderer.render(stack, matrixStack, renderLayerParent.getModel(), slotContext.entity(), renderTypeBuffer, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

}
