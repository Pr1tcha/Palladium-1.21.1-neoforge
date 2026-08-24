package net.threetag.palladium.item.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.threetag.palladium.item.PalladiumItems;
import net.threetag.palladium.util.ItemStackDataUtil;
import org.jetbrains.annotations.NotNull;

public class MultiversalExtrapolatorCloningRecipe extends CustomRecipe {

    public MultiversalExtrapolatorCloningRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        int designated = 0;
        int circuits = 0;
        int diamonds = 0;

        for (int j = 0; j < container.size(); j++) {
            ItemStack stack = container.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.is(PalladiumItems.MULTIVERSAL_EXTRAPOLATOR.get())) {
                    if (!ItemStackDataUtil.get(stack).getString("Universe").isEmpty()) {
                        designated++;
                    }
                } else if (stack.is(PalladiumItems.VIBRANIUM_CIRCUIT.get())) {
                    circuits++;
                } else if (stack.is(Items.DIAMOND)) {
                    diamonds++;
                }
            }
        }

        return designated == 1 && circuits == 1 && diamonds == 7;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        if (this.matches(container, null)) {
            for (int j = 0; j < container.size(); j++) {
                ItemStack stack = container.getItem(j);
                if (!stack.isEmpty() && stack.is(PalladiumItems.MULTIVERSAL_EXTRAPOLATOR.get()) && !ItemStackDataUtil.get(stack).getString("Universe").isEmpty()) {
                    var result = PalladiumItems.MULTIVERSAL_EXTRAPOLATOR.get().getDefaultInstance();
                    var universeId = ItemStackDataUtil.get(stack).getString("Universe");
                    ItemStackDataUtil.update(result, tag -> tag.putString("Universe", universeId));
                    return result;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.size(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.hasCraftingRemainingItem()) {
                remaining.set(i, itemStack.getCraftingRemainingItem());
            } else if (itemStack.is(PalladiumItems.MULTIVERSAL_EXTRAPOLATOR.get()) && ItemStackDataUtil.get(itemStack).contains("Universe")) {
                remaining.set(i, itemStack.copyWithCount(1));
                break;
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PalladiumRecipeSerializers.MULTIVERSAL_EXTRAPOLATOR_CLONING.get();
    }
}
