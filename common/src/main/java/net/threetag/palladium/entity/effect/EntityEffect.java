package net.threetag.palladium.entity.effect;

import net.minecraft.world.entity.Entity;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.EffectEntity;
import net.threetag.palladium.util.property.BooleanProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;
import net.threetag.palladiumcore.registry.PalladiumRegistry;

import java.util.function.Predicate;

public abstract class EntityEffect {

    public static final PalladiumRegistry<EntityEffect> REGISTRY = PalladiumRegistry.create(EntityEffect.class, Palladium.id("entity_effects"));

    public static final PalladiumProperty<Boolean> IS_DONE_PLAYING = new BooleanProperty("is_done_playing");

    public void registerProperties(PropertyManager manager) {
        manager.register(IS_DONE_PLAYING, false);
    }

    public abstract void tick(EffectEntity entity, Entity anchor);

    public boolean isInRangeToRenderDist(EffectEntity effectEntity, Entity anchor, double distance) {
        return anchor.shouldRenderAtSqrDistance(distance);
    }

    public <T> T get(EffectEntity entity, PalladiumProperty<T> property) {
        return property.get(entity);
    }

    public <T> void set(EffectEntity entity, PalladiumProperty<T> property, T value) {
        property.set(entity, value);
    }

    public boolean isPlaying(EffectEntity entity) {
        return !this.get(entity, IS_DONE_PLAYING);
    }

    public void stopPlaying(EffectEntity entity) {
        this.set(entity, IS_DONE_PLAYING, true);
    }

    public static void stop(Entity anchor, Predicate<EntityEffect> predicate) {
        anchor.level().getEntities(anchor, anchor.getBoundingBox().inflate(2), entity -> entity instanceof EffectEntity && ((EffectEntity) entity).getAnchorEntity() == anchor && predicate.test(((EffectEntity) entity).entityEffect)).forEach(Entity::discard);
    }

    public static void stop(Entity anchor, EntityEffect entityEffectType) {
        stop(anchor, effect -> effect == entityEffectType);
    }

}
