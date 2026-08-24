package net.threetag.palladium;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.threetag.palladium.client.screen.AbilityBarRenderer;
import net.threetag.palladium.power.ability.AbilityReference;

import java.util.Arrays;
import java.util.List;

public class PalladiumConfig {

    public static class Client {

        public static ModConfigSpec.EnumValue<AbilityBarRenderer.Position> ABILITY_BAR_POSITION;
        public static ModConfigSpec.BooleanValue ADDON_PACK_DEV_MODE;
        public static ModConfigSpec.BooleanValue ACCESSORY_BUTTON;

        public static ModConfigSpec generateConfig() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            ABILITY_BAR_POSITION = builder.defineEnum("abilityBarPosition", AbilityBarRenderer.Position.BOTTOM_RIGHT);
            ADDON_PACK_DEV_MODE = builder.define("addonPackDevMode", false);
            ACCESSORY_BUTTON = builder.define("accessoryButton", true);
            return builder.build();
        }

    }

    public static class Server {

        public static ModConfigSpec.BooleanValue REDSTONE_FLUX_CRYSTAL_GEODE_GENERATION;
        public static ModConfigSpec.ConfigValue<List<? extends String>> DISABLED_ABILITIES;

        public static ModConfigSpec generateConfig() {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            REDSTONE_FLUX_CRYSTAL_GEODE_GENERATION = builder.define("worldGen.redstoneFluxCrystalGeneration", true);
            builder.comment("Allows you to disable specific abilities from addons. Structure: 'power_namespace:power_id#ability_key'. The ability_key can be found in the json of a power.");
            DISABLED_ABILITIES = builder.defineListAllowEmpty(Arrays.asList("general", "disabledAbilities"), List::of, o -> true);
            return builder.build();
        }

        public static boolean isAbilityDisabled(AbilityReference reference) {
            for (String s : DISABLED_ABILITIES.get()) {
                if (AbilityReference.validateFull(s)) {
                    var ref = AbilityReference.fromString(s);

                    if (ref.equals(reference)) {
                        return true;
                    }
                } else if (ResourceLocation.tryParse(s) != null) {
                    var powerId = ResourceLocation.tryParse(s);

                    if (reference.powerId() != null && reference.powerId().equals(powerId)) {
                        return true;
                    }
                } else if (reference.powerId() != null && reference.powerId().getNamespace().equals(s)) {
                    return true;
                }
            }

            return false;
        }

    }

}
