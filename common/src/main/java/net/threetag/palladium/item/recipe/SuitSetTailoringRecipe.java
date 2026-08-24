package net.threetag.palladium.item.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.threetag.palladium.item.SuitSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SuitSetTailoringRecipe extends TailoringRecipe {

    private final SuitSet suitSet;

    public SuitSetTailoringRecipe(SuitSet suitSet, List<SizedIngredient> ingredients,
                                  Ingredient toolIngredient, boolean consumeTool, ResourceLocation toolIcon, ResourceLocation categoryId,
                                  boolean requiresUnlocking) {
        super(buildResults(suitSet), ingredients, toolIngredient, consumeTool, toolIcon, categoryId, requiresUnlocking);
        this.suitSet = suitSet;
    }

    private static Map<EquipmentSlot, ItemStack> buildResults(SuitSet suitSet) {
        Map<EquipmentSlot, ItemStack> map = new HashMap<>();
        map.put(EquipmentSlot.HEAD, suitSet.getHelmet() != null ? suitSet.getHelmet().getDefaultInstance() : ItemStack.EMPTY);
        map.put(EquipmentSlot.CHEST, suitSet.getChestplate() != null ? suitSet.getChestplate().getDefaultInstance() : ItemStack.EMPTY);
        map.put(EquipmentSlot.LEGS, suitSet.getLeggings() != null ? suitSet.getLeggings().getDefaultInstance() : ItemStack.EMPTY);
        map.put(EquipmentSlot.FEET, suitSet.getBoots() != null ? suitSet.getBoots().getDefaultInstance() : ItemStack.EMPTY);
        return map;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PalladiumRecipeSerializers.SUIT_SET_TAILORING.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable(this.suitSet.getDescriptionId());
    }

    public static class Serializer implements RecipeSerializer<SuitSetTailoringRecipe> {

        private static final Codec<SuitSet> SUIT_SET_CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            SuitSet suitSet = SuitSet.REGISTRY.get(id);
            return suitSet != null ? DataResult.success(suitSet) : DataResult.error(() -> "Unknown suit set " + id);
        }, suitSet -> DataResult.success(SuitSet.REGISTRY.getKey(suitSet)));
        private static final MapCodec<SuitSetTailoringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SUIT_SET_CODEC.fieldOf("suit_set").forGetter(recipe -> recipe.suitSet),
                SizedIngredient.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(recipe -> recipe.toolIngredient),
                Codec.BOOL.optionalFieldOf("consume_tool", false).forGetter(recipe -> recipe.consumeTool),
                ResourceLocation.CODEC.optionalFieldOf("tool_icon").forGetter(recipe -> Optional.ofNullable(recipe.toolIcon)),
                ResourceLocation.CODEC.optionalFieldOf("category").forGetter(recipe -> Optional.ofNullable(recipe.categoryId)),
                Codec.BOOL.optionalFieldOf("requires_unlocking", true).forGetter(recipe -> recipe.requiresUnlocking)
        ).apply(instance, (suitSet, ingredients, tool, consumeTool, toolIcon, category, requiresUnlocking) ->
                new SuitSetTailoringRecipe(suitSet, ingredients, tool, consumeTool, toolIcon.orElse(null), category.orElse(null), requiresUnlocking)));
        private static final StreamCodec<RegistryFriendlyByteBuf, SuitSetTailoringRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public SuitSetTailoringRecipe decode(RegistryFriendlyByteBuf buffer) {
                SuitSet suitSet = SuitSet.REGISTRY.get(buffer.readResourceLocation());
                List<SizedIngredient> ingredients = new ArrayList<>();
                int ingredientCount = buffer.readVarInt();
                for (int i = 0; i < ingredientCount; i++) {
                    ingredients.add(SizedIngredient.STREAM_CODEC.decode(buffer));
                }

                return new SuitSetTailoringRecipe(
                        suitSet,
                        ingredients,
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        buffer.readBoolean(),
                        buffer.readBoolean() ? buffer.readResourceLocation() : null,
                        buffer.readBoolean() ? buffer.readResourceLocation() : null,
                        buffer.readBoolean()
                );
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, SuitSetTailoringRecipe recipe) {
                buffer.writeResourceLocation(SuitSet.REGISTRY.getKey(recipe.suitSet));
                buffer.writeVarInt(recipe.ingredients.size());
                recipe.ingredients.forEach(ingredient -> SizedIngredient.STREAM_CODEC.encode(buffer, ingredient));
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.toolIngredient);
                buffer.writeBoolean(recipe.consumeTool);
                buffer.writeBoolean(recipe.toolIcon != null);
                if (recipe.toolIcon != null) {
                    buffer.writeResourceLocation(recipe.toolIcon);
                }
                buffer.writeBoolean(recipe.categoryId != null);
                if (recipe.categoryId != null) {
                    buffer.writeResourceLocation(recipe.categoryId);
                }
                buffer.writeBoolean(recipe.requiresUnlocking);
            }
        };

        @Override
        public MapCodec<SuitSetTailoringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SuitSetTailoringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
