package net.threetag.palladium.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.threetag.palladium.multiverse.MultiverseManager;
import net.threetag.palladium.multiverse.Universe;
import net.threetag.palladium.sound.PalladiumSoundEvents;
import net.threetag.palladium.util.ItemStackDataUtil;
import net.threetag.palladium.util.context.DataContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiversalExtrapolatorItem extends Item {

    public MultiversalExtrapolatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack != newStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        var universe = getUniverse(stack, context.level());

        if (universe != null) {
            tooltipComponents.add(universe.getTitle().copy().withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);

        if (getUniverse(stack, level) != null) {
            return InteractionResultHolder.pass(stack);
        }

        ItemStackDataUtil.update(stack, tag -> {
            tag.putBoolean("Searching", true);
            tag.putInt("SearchTimer", 0);
        });

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        var customData = ItemStackDataUtil.get(stack);
        if (entity instanceof Player player && customData.getBoolean("Searching")) {
            int progress = customData.getInt("SearchTimer");

            if (progress < 80) {
                if (!level.isClientSide()) {
                    ItemStackDataUtil.update(stack, tag -> tag.putInt("SearchTimer", progress + 1));

                    if (progress == 60) {
                        var universe = MultiverseManager.getInstance(level).getRandomAvailableUniverse(DataContext.forEntity(entity));

                        if (universe != null) {
                            setUniverse(stack, universe);
                        }
                    }
                } else {
                    if (progress < 61) {
                        RandomSource random = RandomSource.create(entity.tickCount / 2);
                        String number = random.nextInt(10) + "" + random.nextInt(10) + random.nextInt(10);
                        player.displayClientMessage(Universe.getGenericUniverseComponent(number), true);

                        if (player.tickCount % 2 == 0) {
                            player.playSound(PalladiumSoundEvents.MULTIVERSE_SEARCH.get(), 1F, 1F);
                        }
                    } else {
                        var universe = getUniverse(stack, level);

                        if (universe != null) {
                            player.displayClientMessage(universe.getTitle().copy().withStyle(ChatFormatting.GOLD), true);
                        } else {
                            player.displayClientMessage(Component.translatable("item.palladium.multiversal_extrapolator.desc.error").withStyle(ChatFormatting.RED), true);
                        }
                    }
                }
            } else if (!level.isClientSide()) {
                ItemStackDataUtil.update(stack, tag -> {
                    tag.remove("Searching");
                    tag.remove("SearchTimer");
                });
            }
        }
    }

    public static void setUniverse(ItemStack stack, Universe universe) {
        ItemStackDataUtil.update(stack, tag -> tag.putString("Universe", universe.getId().toString()));
    }

    public static Universe getUniverse(ItemStack stack, Level level) {
        var customData = ItemStackDataUtil.get(stack);
        return customData.contains("Universe") ?
                MultiverseManager.getInstance(level).get(ResourceLocation.tryParse(customData.getString("Universe"))) :
                null;
    }
}
