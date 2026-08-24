package net.threetag.palladium.item.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemTailoringRecipe extends TailoringRecipe {

    private final Component title;

    public ItemTailoringRecipe(Map<EquipmentSlot, ItemStack> results,
                               List<SizedIngredient> ingredients, Ingredient toolIngredient, boolean consumeTool, Component title,
                               ResourceLocation toolIcon, ResourceLocation categoryId, boolean requiresUnlocking) {
        super(results, ingredients, toolIngredient, consumeTool, toolIcon, categoryId, requiresUnlocking);
        this.title = title;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PalladiumRecipeSerializers.ITEM_TAILORING.get();
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    public static class Serializer implements RecipeSerializer<ItemTailoringRecipe> {

        private static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.either(
                BuiltInRegistries.ITEM.byNameCodec(),
                RecordCodecBuilder.<ItemStack>create(instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
                ).apply(instance, (item, count) -> new ItemStack(item, count)))
        ).xmap(value -> value.map(ItemStack::new, stack -> stack), stack -> Either.right(stack));
        private static final Codec<Map<EquipmentSlot, ItemStack>> RESULTS_CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ITEM_STACK_CODEC)
                .validate(results -> results.isEmpty() ? DataResult.error(() -> "Tailoring result needs at least one item") : DataResult.success(results));
        private static final MapCodec<ItemTailoringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RESULTS_CODEC.fieldOf("results").forGetter(recipe -> recipe.results),
                SizedIngredient.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(recipe -> recipe.toolIngredient),
                Codec.BOOL.optionalFieldOf("consume_tool", false).forGetter(recipe -> recipe.consumeTool),
                ComponentSerialization.CODEC.fieldOf("title").forGetter(recipe -> recipe.title),
                ResourceLocation.CODEC.optionalFieldOf("tool_icon").forGetter(recipe -> Optional.ofNullable(recipe.toolIcon)),
                ResourceLocation.CODEC.optionalFieldOf("category").forGetter(recipe -> Optional.ofNullable(recipe.categoryId)),
                Codec.BOOL.optionalFieldOf("requires_unlocking", true).forGetter(recipe -> recipe.requiresUnlocking)
        ).apply(instance, (results, ingredients, tool, consumeTool, title, toolIcon, category, requiresUnlocking) ->
                new ItemTailoringRecipe(results, ingredients, tool, consumeTool, title, toolIcon.orElse(null), category.orElse(null), requiresUnlocking)));
        private static final StreamCodec<RegistryFriendlyByteBuf, ItemTailoringRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public ItemTailoringRecipe decode(RegistryFriendlyByteBuf buffer) {
                Map<EquipmentSlot, ItemStack> results = new EnumMap<>(EquipmentSlot.class);
                int resultCount = buffer.readVarInt();
                for (int i = 0; i < resultCount; i++) {
                    results.put(buffer.readEnum(EquipmentSlot.class), ItemStack.STREAM_CODEC.decode(buffer));
                }

                List<SizedIngredient> ingredients = new ArrayList<>();
                int ingredientCount = buffer.readVarInt();
                for (int i = 0; i < ingredientCount; i++) {
                    ingredients.add(SizedIngredient.STREAM_CODEC.decode(buffer));
                }

                return new ItemTailoringRecipe(
                        results,
                        ingredients,
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        buffer.readBoolean(),
                        ComponentSerialization.STREAM_CODEC.decode(buffer),
                        buffer.readBoolean() ? buffer.readResourceLocation() : null,
                        buffer.readBoolean() ? buffer.readResourceLocation() : null,
                        buffer.readBoolean()
                );
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, ItemTailoringRecipe recipe) {
                buffer.writeVarInt(recipe.results.size());
                recipe.results.forEach((slot, stack) -> {
                    buffer.writeEnum(slot);
                    ItemStack.STREAM_CODEC.encode(buffer, stack);
                });
                buffer.writeVarInt(recipe.ingredients.size());
                recipe.ingredients.forEach(ingredient -> SizedIngredient.STREAM_CODEC.encode(buffer, ingredient));
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.toolIngredient);
                buffer.writeBoolean(recipe.consumeTool);
                ComponentSerialization.STREAM_CODEC.encode(buffer, recipe.title);
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
        public MapCodec<ItemTailoringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemTailoringRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
