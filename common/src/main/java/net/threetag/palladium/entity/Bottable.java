package net.threetag.palladium.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.threetag.palladium.util.ItemStackDataUtil;

import java.util.Optional;

public interface Bottable {

    boolean fromBottle();

    void setFromBottle(boolean bl);

    void saveToBottleTag(ItemStack itemStack);

    void loadFromBottleTag(CompoundTag compoundTag);

    ItemStack getBottleItemStack();

    SoundEvent getPickupSound();

    static void saveDefaultDataToBottleTag(Mob mob, ItemStack itemStack) {
        if (mob.hasCustomName()) {
            itemStack.set(DataComponents.CUSTOM_NAME, mob.getCustomName());
        }

        ItemStackDataUtil.update(itemStack, compoundTag -> {
            if (mob.isNoAi()) {
                compoundTag.putBoolean("NoAI", true);
            }
            if (mob.isSilent()) {
                compoundTag.putBoolean("Silent", true);
            }
            if (mob.isNoGravity()) {
                compoundTag.putBoolean("NoGravity", true);
            }
            if (mob.hasGlowingTag()) {
                compoundTag.putBoolean("Glowing", true);
            }
            if (mob.isInvulnerable()) {
                compoundTag.putBoolean("Invulnerable", true);
            }
            compoundTag.putFloat("Health", mob.getHealth());
        });
    }

    static void loadDefaultDataFromBottleTag(Mob mob, CompoundTag compoundTag) {
        if (compoundTag.contains("NoAI")) {
            mob.setNoAi(compoundTag.getBoolean("NoAI"));
        }

        if (compoundTag.contains("Silent")) {
            mob.setSilent(compoundTag.getBoolean("Silent"));
        }

        if (compoundTag.contains("NoGravity")) {
            mob.setNoGravity(compoundTag.getBoolean("NoGravity"));
        }

        if (compoundTag.contains("Glowing")) {
            mob.setGlowingTag(compoundTag.getBoolean("Glowing"));
        }

        if (compoundTag.contains("Invulnerable")) {
            mob.setInvulnerable(compoundTag.getBoolean("Invulnerable"));
        }

        if (compoundTag.contains("Health", 99)) {
            mob.setHealth(compoundTag.getFloat("Health"));
        }

    }

    static <T extends LivingEntity & Bottable> Optional<InteractionResult> bottleMobPickup(Player player, InteractionHand interactionHand, T livingEntity) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.GLASS_BOTTLE && livingEntity.isAlive()) {
            if (livingEntity.getPickupSound() != null) {
                livingEntity.playSound(livingEntity.getPickupSound(), 1.0F, 1.0F);
            }
            ItemStack itemStack2 = livingEntity.getBottleItemStack();
            livingEntity.saveToBottleTag(itemStack2);
            ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, player, itemStack2, false);
            player.setItemInHand(interactionHand, itemStack3);
            Level level = livingEntity.level();
//            if (!level.isClientSide) {
//                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemStack2);
//            }

            livingEntity.discard();
            return Optional.of(InteractionResult.sidedSuccess(level.isClientSide));
        } else {
            return Optional.empty();
        }
    }

}
