package net.threetag.palladium.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

public class DualWieldingPlayerHandler {

    @SuppressWarnings("unchecked")
    public static Function<Player, AttributeMap> ATTRIBUTE_MAP_FACTORY = player -> new AttributeMap(DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) player.getType()));
    private final Player player;
    private int attackStrengthTicker;
    private ItemStack lastItemInOffHand = ItemStack.EMPTY;

    public DualWieldingPlayerHandler(Player player) {
        this.player = player;
    }

    public void tick() {
        ++this.attackStrengthTicker;
        ItemStack itemStack = this.player.getOffhandItem();
        if (!ItemStack.matches(this.lastItemInOffHand, itemStack)) {
            if (!ItemStack.isSameItem(this.lastItemInOffHand, itemStack)) {
                this.resetAttackStrengthTicker();
            }

            this.lastItemInOffHand = itemStack.copy();
        }
    }

    public void swing() {
        player.swing(InteractionHand.OFF_HAND, true);
    }

    public void attackWithOffHand(Entity target) {
        if (!CommonHooks.onPlayerAttackTarget(this.player, target)) {
            return;
        }

        if (target.isAttackable()) {
            if (!target.skipAttackInteraction(this.player)) {
                float f = (float) getOffHandAttackStrength(this.player);
                ItemStack weapon = this.player.getOffhandItem();
                DamageSource damageSource = this.player.damageSources().playerAttack(this.player);
                float g = 0.0F;
                if (this.player.level() instanceof ServerLevel serverLevel) {
                    g = EnchantmentHelper.modifyDamage(serverLevel, weapon, target, damageSource, f) - f;
                }

                float h = getOffHandAttackStrengthScale(0.5F);
                f *= 0.2F + h * h * 0.8F;
                g *= h;
                this.resetAttackStrengthTicker();
                if (f > 0.0F || g > 0.0F) {
                    boolean bl = h > 0.9F;
                    boolean bl2 = false;
                    float i = this.player.level() instanceof ServerLevel serverLevel
                            ? EnchantmentHelper.modifyKnockback(serverLevel, weapon, target, damageSource, 0.0F)
                            : 0.0F;
                    if (this.player.isSprinting() && bl) {
                        this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.player.getSoundSource(), 1.0F, 1.0F);
                        ++i;
                        bl2 = true;
                    }

                    boolean bl3 = bl
                            && this.player.fallDistance > 0.0F
                            && !this.player.onGround()
                            && !this.player.onClimbable()
                            && !this.player.isInWater()
                            && !this.player.hasEffect(MobEffects.BLINDNESS)
                            && !this.player.isPassenger()
                            && target instanceof LivingEntity;
                    bl3 = bl3 && !this.player.isSprinting();
                    f += weapon.getItem().getAttackDamageBonus(target, f, damageSource);
                    var criticalHit = CommonHooks.fireCriticalHit(this.player, target, bl3, bl3 ? 1.5F : 1.0F);
                    bl3 = criticalHit.isCriticalHit();
                    if (bl3) {
                        f *= criticalHit.getDamageMultiplier();
                    }

                    f += g;
                    boolean bl4 = false;
                    double d = this.player.walkDist - this.player.walkDistO;
                    boolean criticalBlocksSweep = criticalHit.isCriticalHit() && criticalHit.disableSweep();
                    if (bl && !criticalBlocksSweep && !bl2 && this.player.onGround() && d < (double) this.player.getSpeed()) {
                        bl4 = weapon.canPerformAction(ItemAbilities.SWORD_SWEEP);
                    }
                    bl4 = CommonHooks.fireSweepAttack(this.player, target, bl4).isSweeping();

                    float j = 0.0F;
                    if (target instanceof LivingEntity) {
                        j = ((LivingEntity) target).getHealth();
                    }

                    Vec3 vec3 = target.getDeltaMovement();
                    boolean bl6 = target.hurt(damageSource, f);
                    if (bl6) {
                        this.swing();

                        if (i > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity) target)
                                        .knockback(
                                                (float) i * 0.5F,
                                                Mth.sin(this.player.getYRot() * (float) (Math.PI / 180.0)),
                                                -Mth.cos(this.player.getYRot() * (float) (Math.PI / 180.0))
                                        );
                            } else {
                                target.push(
                                        -Mth.sin(this.player.getYRot() * (float) (Math.PI / 180.0)) * (float) i * 0.5F,
                                        0.1,
                                        Mth.cos(this.player.getYRot() * (float) (Math.PI / 180.0)) * (float) i * 0.5F
                                );
                            }

                            this.player.setDeltaMovement(this.player.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                            this.player.setSprinting(false);
                        }

                        if (bl4) {
                            float l = 1.0F + (float) getOffHandAttributeValue(this.player, Attributes.SWEEPING_DAMAGE_RATIO) * f;

                            for (LivingEntity livingEntity : this.player.level().getEntitiesOfClass(LivingEntity.class, weapon.getSweepHitBox(this.player, target))) {
                                if (livingEntity != this.player
                                        && livingEntity != target
                                        && !this.player.isAlliedTo(livingEntity)
                                        && (!(livingEntity instanceof ArmorStand) || !((ArmorStand) livingEntity).isMarker())
                                        && this.player.distanceToSqr(livingEntity) < Mth.square(this.player.entityInteractionRange())) {
                                    livingEntity.knockback(
                                            0.4F, Mth.sin(this.player.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.player.getYRot() * (float) (Math.PI / 180.0))
                                    );
                                    float sweepingDamage = this.player.level() instanceof ServerLevel serverLevel
                                            ? EnchantmentHelper.modifyDamage(serverLevel, weapon, livingEntity, damageSource, l) * h
                                            : l;
                                    livingEntity.hurt(damageSource, sweepingDamage);
                                    if (this.player.level() instanceof ServerLevel serverLevel) {
                                        EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, livingEntity, damageSource, weapon);
                                    }
                                }
                            }

                            this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.player.getSoundSource(), 1.0F, 1.0F);
                            this.player.sweepAttack();
                        }

                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3);
                        }

                        if (bl3) {
                            this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.player.getSoundSource(), 1.0F, 1.0F);
                            this.player.crit(target);
                        }

                        if (!bl3 && !bl4) {
                            if (bl) {
                                this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.player.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.player.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        if (g > 0.0F) {
                            this.player.magicCrit(target);
                        }

                        this.player.setLastHurtMob(target);
                        ItemStack itemStack2 = this.player.getOffhandItem();
                        Entity entity = target;
                        if (target instanceof PartEntity<?> partEntity) {
                            entity = partEntity.getParent();
                        }

                        boolean damageWeapon = false;
                        ItemStack originalWeapon = itemStack2.copy();
                        if (this.player.level() instanceof ServerLevel serverLevel) {
                            if (entity instanceof LivingEntity livingEntity) {
                                damageWeapon = itemStack2.hurtEnemy(livingEntity, this.player);
                            }
                            EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, damageSource, itemStack2);
                        }

                        if (!this.player.level().isClientSide && !itemStack2.isEmpty() && entity instanceof LivingEntity livingEntity) {
                            if (damageWeapon) {
                                itemStack2.postHurtEnemy(livingEntity, this.player);
                            }
                            if (itemStack2.isEmpty()) {
                                EventHooks.onPlayerDestroyItem(this.player, originalWeapon, InteractionHand.OFF_HAND);
                                this.player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (target instanceof LivingEntity) {
                            float m = j - ((LivingEntity) target).getHealth();
                            this.player.awardStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));
                            if (this.player.level() instanceof ServerLevel && m > 2.0F) {
                                int n = (int) ((double) m * 0.5);
                                ((ServerLevel) this.player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), n, 0.1, 0.0, 0.1, 0.2);
                            }
                        }

                        this.player.causeFoodExhaustion(0.1F);
                    } else {
                        this.player.level().playSound(null, this.player.getX(), this.player.getY(), this.player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.player.getSoundSource(), 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    public float getOffHandAttackStrengthScale(float adjustTicks) {
        return Mth.clamp(((float) this.attackStrengthTicker + adjustTicks) / player.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public static double getOffHandAttackStrength(Player player) {
        return getOffHandAttributeValue(player, Attributes.ATTACK_DAMAGE);
    }

    public static double getOffHandAttributeValue(Player player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute) {
        var attributeMap = ATTRIBUTE_MAP_FACTORY.apply(player);
        var result = Objects.requireNonNull(attributeMap.getInstance(attribute));
        var mainHandModifierIds = new HashSet<net.minecraft.resources.ResourceLocation>();
        player.getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND, (modifierAttribute, modifier) -> {
            if (modifierAttribute.equals(attribute)) {
                mainHandModifierIds.add(modifier.id());
            }
        });

        if (player.getAttributes().hasAttribute(attribute)) {
            for (AttributeModifier modifier : Objects.requireNonNull(player.getAttributes().getInstance(attribute)).getModifiers()) {
                if (!mainHandModifierIds.contains(modifier.id())) {
                    result.addOrUpdateTransientModifier(modifier);
                }
            }
        }

        var stack = player.getOffhandItem();
        stack.forEachModifier(EquipmentSlot.MAINHAND, (modifierAttribute, modifier) -> {
            if (modifierAttribute.equals(attribute)) {
                result.addOrUpdateTransientModifier(modifier);
            }
        });

        return result.getValue();
    }

}
