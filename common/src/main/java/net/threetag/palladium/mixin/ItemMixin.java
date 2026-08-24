package net.threetag.palladium.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.KnowledgeBookItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeManager;
import net.threetag.palladium.item.recipe.TailoringRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "appendHoverText", at = @At("RETURN"))
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced, CallbackInfo ci) {
        if ((Object) this instanceof KnowledgeBookItem) {
            var level = context.level();
            var recipes = stack.getOrDefault(DataComponents.RECIPES, List.<ResourceLocation>of());

            if (level != null && !recipes.isEmpty()) {
                RecipeManager recipeManager = level.getRecipeManager();
                tooltipComponents.add(Component.translatable("item.palladium.knowledge_book.grants").withStyle(ChatFormatting.GRAY));

                for (ResourceLocation id : recipes) {
                    recipeManager.byKey(id).ifPresent(recipe -> {
                        var recipeValue = recipe.value();
                        if (recipeValue instanceof TailoringRecipe tailoringRecipe) {
                            tooltipComponents.add(CommonComponents.space().append(tailoringRecipe.getTitle().copy().withStyle(ChatFormatting.BLUE)));
                        } else {
                            tooltipComponents.add(CommonComponents.space().append(recipeValue.getResultItem(level.registryAccess()).getHoverName().copy().withStyle(ChatFormatting.BLUE)));
                        }
                    });
                }
            }

        }
    }

}
