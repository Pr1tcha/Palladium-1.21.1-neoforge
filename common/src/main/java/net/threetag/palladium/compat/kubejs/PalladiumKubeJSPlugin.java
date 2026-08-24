package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.builtin.event.EntityEvents;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.AttachedData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.threetag.palladium.compat.kubejs.ability.AbilityBuilder;
import net.threetag.palladium.compat.kubejs.condition.ConditionBuilder;
import net.threetag.palladium.entity.CustomProjectile;
import net.threetag.palladium.event.PalladiumEvents;
import net.threetag.palladium.power.SuperpowerUtil;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityUtil;
import net.threetag.palladium.util.Easing;
import net.threetag.palladium.util.PlayerSlot;

public class PalladiumKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(Ability.REGISTRY.getRegistryKey(), callback -> {
            callback.addDefault(AbilityBuilder.class, AbilityBuilder::new);
            callback.add(KubeJS.id("basic"), AbilityBuilder.class, AbilityBuilder::new);
        });
        registry.of(net.threetag.palladium.condition.ConditionSerializer.REGISTRY.getRegistryKey(), callback -> {
            callback.addDefault(ConditionBuilder.class, ConditionBuilder::new);
            callback.add(KubeJS.id("basic"), ConditionBuilder.class, ConditionBuilder::new);
        });
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        EntityEvents.GROUP.getHandlers().putIfAbsent("hurt", EntityEvents.BEFORE_HURT);
        registry.register(PalladiumJSEvents.GROUP);
    }

    @Override
    public void init() {
        CustomProjectile.KUBEJS_EVENT_HANDLER = customProjectile -> PalladiumJSEvents.CUSTOM_PROJECTILE_TICK.post(new ProjectileTickEventJS(customProjectile));

        PalladiumEvents.REGISTER_PROPERTY.register(handler -> {
            if (handler.getEntity().level().isClientSide) {
                PalladiumJSEvents.CLIENT_REGISTER_PROPERTIES.post(new RegisterPalladiumPropertyEventJS(handler.getEntity(), handler));
            } else {
                PalladiumJSEvents.REGISTER_PROPERTIES.post(new RegisterPalladiumPropertyEventJS(handler.getEntity(), handler));
            }
        });
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("palladium", new PalladiumBinding());
        bindings.add("superpowerUtil", SuperpowerUtil.class);
        bindings.add("abilityUtil", AbilityUtil.class);
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        if (registry.scriptType().isClient()) {
            registry.register(Easing.class, o -> Easing.fromString(o.toString()));
            registry.register(PlayerSlot.class, o -> PlayerSlot.get(o.toString()));
        }
    }

    @Override
    public void attachPlayerData(AttachedData<Player> event) {
        event.add("powers", new PowerHandlerJS(event.getParent()));
    }

    @Override
    public void attachLevelData(AttachedData<Level> event) {
        event.add("powers", new PowerManagerJS(event.getParent()));
    }

}
