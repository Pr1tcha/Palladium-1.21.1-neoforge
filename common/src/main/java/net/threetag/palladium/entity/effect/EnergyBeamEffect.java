package net.threetag.palladium.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.entity.EffectEntity;
import net.threetag.palladium.power.ability.AbilityReference;
import net.threetag.palladium.util.property.AbilityReferenceProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;

public class EnergyBeamEffect extends EntityEffect {

    public static final PalladiumProperty<AbilityReference> ABILITY = new AbilityReferenceProperty("ability");
    private static ClientHandler clientHandler = new ClientHandler() {
    };

    @Override
    public void registerProperties(PropertyManager manager) {
        super.registerProperties(manager);
        manager.register(ABILITY, null);
    }

    @Override
    public void tick(EffectEntity entity, Entity anchor) {
        clientHandler.tick(this, entity, anchor);
    }

    public static void start(Player player, AbilityReference abilityReference) {
        clientHandler.start(player, abilityReference);
    }

    public static void setClientHandler(ClientHandler handler) {
        clientHandler = handler;
    }

    public interface ClientHandler {

        default void tick(EnergyBeamEffect effect, EffectEntity entity, Entity anchor) {
            effect.stopPlaying(entity);
        }

        default void start(Player player, AbilityReference abilityReference) {
        }
    }
}
