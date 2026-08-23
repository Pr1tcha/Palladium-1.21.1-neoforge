package net.threetag.palladium.power.forge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.power.ability.Abilities;
import net.threetag.palladium.power.ability.AbilityUtil;

@Mod.EventBusSubscriber(modid = Palladium.MOD_ID)
public class AbilityEventHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> e) {
        if (!AbilityUtil.getEnabledInstances(e.getEntity(), Abilities.INVISIBILITY.get()).isEmpty()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingVisibility(LivingEvent.LivingVisibilityEvent e) {
        if (!AbilityUtil.getEnabledInstances(e.getEntity(), Abilities.INVISIBILITY.get()).isEmpty()) {
            e.modifyVisibility(0);
        }
    }

}
