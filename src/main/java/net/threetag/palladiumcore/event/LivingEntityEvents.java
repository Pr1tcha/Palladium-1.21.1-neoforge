package net.threetag.palladiumcore.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface LivingEntityEvents {

    Event<Death> DEATH = new Event<>(Death.class, listeners -> (entity, source) ->
            Event.result(listeners, listener -> listener.livingEntityDeath(entity, source)));
    Event<Hurt> HURT = new Event<>(Hurt.class, listeners -> (entity, source, amount) ->
            Event.result(listeners, listener -> listener.livingEntityHurt(entity, source, amount)));
    Event<Attack> ATTACK = new Event<>(Attack.class, listeners -> (entity, source, amount) ->
            Event.result(listeners, listener -> listener.livingEntityAttack(entity, source, amount)));
    Event<Tick> TICK = new Event<>(Tick.class, listeners -> entity ->
            listeners.forEach(listener -> listener.livingEntityTick(entity)));
    Event<ItemUse> ITEM_USE_START = itemUseEvent();
    Event<ItemUse> ITEM_USE_TICK = itemUseEvent();
    Event<ItemUse> ITEM_USE_STOP = itemUseEvent();
    Event<ItemUseFinish> ITEM_USE_FINISH = new Event<>(ItemUseFinish.class, listeners -> (entity, stack, duration) ->
            listeners.forEach(listener -> listener.livingEntityItemUseFinish(entity, stack, duration)));
    Event<Jump> JUMP = new Event<>(Jump.class, listeners -> entity ->
            listeners.forEach(listener -> listener.livingEntityJump(entity)));

    private static Event<ItemUse> itemUseEvent() {
        return new Event<>(ItemUse.class, listeners -> (entity, stack, duration) ->
                Event.result(listeners, listener -> listener.livingEntityItemUse(entity, stack, duration)));
    }

    @FunctionalInterface interface Death { EventResult livingEntityDeath(LivingEntity entity, DamageSource damageSource); }
    @FunctionalInterface interface Hurt { EventResult livingEntityHurt(LivingEntity entity, DamageSource damageSource, AtomicReference<Float> amount); }
    @FunctionalInterface interface Attack { EventResult livingEntityAttack(LivingEntity entity, DamageSource damageSource, float amount); }
    @FunctionalInterface interface Tick { void livingEntityTick(LivingEntity entity); }
    @FunctionalInterface interface ItemUse { EventResult livingEntityItemUse(LivingEntity entity, @NotNull ItemStack stack, AtomicInteger duration); }
    @FunctionalInterface interface ItemUseFinish { void livingEntityItemUseFinish(LivingEntity entity, @NotNull ItemStack stack, AtomicInteger duration); }
    @FunctionalInterface interface Jump { void livingEntityJump(LivingEntity entity); }
}
