package net.threetag.palladium.forge;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.entity.PalladiumAttributes;

@Mod.EventBusSubscriber(modid = Palladium.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PalladiumEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed e) {
        if (e.getEntity().getAttributes().hasAttribute(PalladiumAttributes.holder(PalladiumAttributes.DESTROY_SPEED))) {
            e.setNewSpeed((float) (e.getNewSpeed() * e.getEntity().getAttributeValue(PalladiumAttributes.holder(PalladiumAttributes.DESTROY_SPEED))));
        }
    }

}
