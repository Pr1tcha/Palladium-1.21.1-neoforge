package net.threetag.palladiumcore.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

import java.util.List;

public interface EntityEvents {

    Event<JoinLevel> JOIN_LEVEL = new Event<>(JoinLevel.class, listeners -> (entity, level) ->
            listeners.forEach(listener -> listener.entityJoinLevel(entity, level)));
    Event<LightningStrike> LIGHTNING_STRIKE = new Event<>(LightningStrike.class, listeners -> (entities, bolt) ->
            listeners.forEach(listener -> listener.lightningStrike(entities, bolt)));

    @FunctionalInterface interface JoinLevel { void entityJoinLevel(Entity entity, Level level); }
    @FunctionalInterface interface LightningStrike { void lightningStrike(List<Entity> entities, LightningBolt lightningBolt); }
}
