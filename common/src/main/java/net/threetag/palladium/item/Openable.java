package net.threetag.palladium.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.threetag.palladium.util.ItemStackDataUtil;

public interface Openable {

    String OPEN_TAG = "Palladium:Opened";
    String OPENING_TAG = "Palladium:Opening";

    default boolean canBeOpened(LivingEntity entity, ItemStack stack) {
        return true;
    }

    /**
     * Determines how long the opening process takes
     *
     * @param stack The relevant item stack
     * @return Return 0 for instant opening, anything greater represents the amount of ticks
     */
    default int getOpeningTime(ItemStack stack) {
        return 0;
    }

    default void setOpen(LivingEntity entity, ItemStack stack, boolean open) {
        if (!entity.level().isClientSide && this.isOpen(stack) != open) {
            if (!open || canBeOpened(entity, stack)) {
                ItemStackDataUtil.update(stack, nbt -> nbt.putBoolean(OPEN_TAG, open));
                this.onOpeningStateChange(entity, stack, open);

                if (getOpeningTime(stack) <= 0) {
                    if (open) {
                        this.onFullyOpened(entity, stack);
                    } else {
                        this.onFullyClosed(entity, stack);
                    }
                }
            }
        }
    }

    default boolean isOpen(ItemStack stack) {
        return ItemStackDataUtil.get(stack).getBoolean(OPEN_TAG);
    }

    default int getOpeningProgress(ItemStack stack) {
        return ItemStackDataUtil.get(stack).getInt(OPENING_TAG);
    }

    default void onOpeningStateChange(LivingEntity entity, ItemStack stack, boolean open) {

    }

    default void onFullyOpened(LivingEntity entity, ItemStack stack) {

    }

    default void onFullyClosed(LivingEntity entity, ItemStack stack) {

    }

    static void onTick(LivingEntity entity, ItemStack stack) {
        if (!entity.level().isClientSide && stack.getItem() instanceof Openable openable) {
            var nbt = ItemStackDataUtil.get(stack);
            var max = openable.getOpeningTime(stack);

            if (max <= 0) {
                // nothing
            } else {
                var timer = nbt.getInt(OPENING_TAG);
                var isOpen = openable.isOpen(stack);

                if (isOpen && !openable.canBeOpened(entity, stack)) {
                    isOpen = false;
                    boolean finalIsOpen = isOpen;
                    ItemStackDataUtil.update(stack, tag -> tag.putBoolean(OPEN_TAG, finalIsOpen));
                    openable.onOpeningStateChange(entity, stack, isOpen);
                }

                if (isOpen && timer < max) {
                    timer += 1;
                    int finalTimer = timer;
                    ItemStackDataUtil.update(stack, tag -> tag.putInt(OPENING_TAG, finalTimer));

                    if (timer == max) {
                        openable.onFullyOpened(entity, stack);
                    }
                } else if (!isOpen && timer > 0) {
                    timer -= 1;
                    int finalTimer = timer;
                    ItemStackDataUtil.update(stack, tag -> tag.putInt(OPENING_TAG, finalTimer));

                    if (timer == 0) {
                        openable.onFullyClosed(entity, stack);
                    }
                }
            }
        }
    }

}
