package net.threetag.palladium.compat.curios.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.threetag.palladium.power.*;
import net.threetag.palladium.power.provider.PowerProvider;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CuriosPowerProvider extends PowerProvider {

    @Override
    public void providePowers(LivingEntity entity, IPowerHandler handler, PowerCollector collector) {
        CuriosApi.getCuriosInventory(entity).ifPresent(curios -> {
            curios.getCurios().forEach((slot, stacks) -> {
                for (int i = 0; i < stacks.getStacks().getSlots(); i++) {
                    ItemStack stack = stacks.getStacks().getStackInSlot(i);

                    if (!stack.isEmpty()) {
                        List<Power> powers = ItemPowerManager.getInstance().getPowerForItem("curios:" + slot, stack.getItem());

                        if (powers != null) {
                            for (Power power : powers) {
                                collector.addPower(power, () -> new Validator(stack.getItem(), slot));
                            }
                        }
                    }
                }
            });
        });
    }

    public record Validator(Item item, String slot) implements IPowerValidator {

        @Override
        public boolean stillValid(LivingEntity entity, Power power) {
            AtomicBoolean available = new AtomicBoolean(false);
            CuriosApi.getCuriosInventory(entity).ifPresent(curios -> {
                curios.getStacksHandler(this.slot).ifPresent(stacks -> {
                    for (int i = 0; i < stacks.getStacks().getSlots(); i++) {
                        ItemStack stack = stacks.getStacks().getStackInSlot(i);

                        if (stack.is(this.item)) {
                            available.set(true);
                        }
                    }
                });
            });
            return available.get();
        }
    }
}
