package net.threetag.palladium.forge;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.PalladiumAttributes;
import net.threetag.palladium.addonpack.log.AddonPackLog;
import net.threetag.palladium.loot.LootTableModificationManager;

@EventBusSubscriber(modid = Palladium.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PalladiumEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed e) {
        if (e.getEntity().getAttributes().hasAttribute(PalladiumAttributes.holder(PalladiumAttributes.DESTROY_SPEED))) {
            e.setNewSpeed((float) (e.getNewSpeed() * e.getEntity().getAttributeValue(PalladiumAttributes.holder(PalladiumAttributes.DESTROY_SPEED))));
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        var modification = LootTableModificationManager.getInstance().getFor(event.getName());
        if (modification == null || !modification.markApplied()) {
            return;
        }

        try {
            modification.getLootPools(event.getRegistries()).forEach(event.getTable()::addPool);
        } catch (Exception exception) {
            AddonPackLog.error("Could not apply loot table modification for {}", event.getName(), exception);
        }
    }

}
