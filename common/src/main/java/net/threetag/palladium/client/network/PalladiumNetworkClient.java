package net.threetag.palladium.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.accessory.Accessory;
import net.threetag.palladium.client.screen.AccessoryScreen;
import net.threetag.palladium.client.screen.TailoringScreen;
import net.threetag.palladium.client.screen.power.BuyAbilityScreen;
import net.threetag.palladium.client.screen.power.PowersScreen;
import net.threetag.palladium.entity.FlightHandler;
import net.threetag.palladium.entity.PalladiumPlayerExtension;
import net.threetag.palladium.multiverse.ClientMultiversalItemVariantsManager;
import net.threetag.palladium.multiverse.ClientMultiverseManager;
import net.threetag.palladium.network.OpenAbilityBuyScreenMessage;
import net.threetag.palladium.network.SetEnergyBarMessage;
import net.threetag.palladium.network.SyncAbilityEntryPropertyMessage;
import net.threetag.palladium.network.SyncAbilityStateMessage;
import net.threetag.palladium.network.SyncAccessoriesMessage;
import net.threetag.palladium.network.SyncAvailableTailoringRecipes;
import net.threetag.palladium.network.SyncFlightStateMessage;
import net.threetag.palladium.network.SyncMultiversalItemVariantsMessage;
import net.threetag.palladium.network.SyncMultiverseMessage;
import net.threetag.palladium.network.SyncPowersMessage;
import net.threetag.palladium.network.SyncPropertyMessage;
import net.threetag.palladium.network.UpdatePowersMessage;
import net.threetag.palladium.power.ClientPowerManager;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.ability.AbilityInstance;
import net.threetag.palladium.power.ability.NameChangeAbility;
import net.threetag.palladium.util.property.EntityPropertyHandler;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.util.PlayerUtil;

import java.util.Objects;

public final class PalladiumNetworkClient {

    private PalladiumNetworkClient() {
    }

    public static void handle(MessageS2C message) {
        if (message instanceof SyncPowersMessage syncPowers) {
            ClientPowerManager.updatePowers(syncPowers.getPowers());
        } else if (message instanceof UpdatePowersMessage updatePowers) {
            handleUpdatePowers(updatePowers);
        } else if (message instanceof SyncAbilityStateMessage syncAbilityState) {
            handleAbilityState(syncAbilityState);
        } else if (message instanceof SyncPropertyMessage syncProperty) {
            handleProperty(syncProperty);
        } else if (message instanceof SyncAbilityEntryPropertyMessage syncAbilityProperty) {
            handleAbilityProperty(syncAbilityProperty);
        } else if (message instanceof SyncAccessoriesMessage syncAccessories) {
            handleAccessories(syncAccessories);
        } else if (message instanceof OpenAbilityBuyScreenMessage openBuyScreen) {
            handleOpenBuyScreen(openBuyScreen);
        } else if (message instanceof SyncFlightStateMessage syncFlightState) {
            handleFlightState(syncFlightState);
        } else if (message instanceof SetEnergyBarMessage setEnergyBar) {
            handleEnergyBar(setEnergyBar);
        } else if (message instanceof SyncAvailableTailoringRecipes syncRecipes) {
            handleTailoringRecipes(syncRecipes);
        } else if (message instanceof SyncMultiverseMessage syncMultiverse) {
            ClientMultiverseManager.updateUniverses(syncMultiverse.getUniverses());
        } else if (message instanceof SyncMultiversalItemVariantsMessage syncVariants) {
            ClientMultiversalItemVariantsManager.updateEntries(syncVariants.getVariantsByUniverseId());
        }
    }

    private static void handleUpdatePowers(UpdatePowersMessage message) {
        var level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(message.getEntityId()) instanceof LivingEntity livingEntity)) {
            return;
        }

        PowerManager manager = PowerManager.getInstance(level);
        var toRemove = message.getPowersToRemove().stream().map(manager::getPower).filter(Objects::nonNull).toList();
        var toAdd = message.getPowersToAdd().stream().map(manager::getPower).filter(Objects::nonNull).toList();
        PowerManager.getPowerHandler(livingEntity).ifPresent(handler -> handler.removeAndAddPowers(toRemove, toAdd));

        message.getEnergyBars().forEach(data -> {
            var bar = data.getLeft().getEntry(livingEntity);
            if (bar != null) {
                bar.set(data.getMiddle());
                bar.setMax(data.getRight());
            }
        });
    }

    private static void handleAbilityState(SyncAbilityStateMessage message) {
        Entity entity = requireLevel().getEntity(message.getEntityId());
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        AbilityInstance entry = message.getReference().getEntry(livingEntity);
        if (entry != null) {
            entry.setClientState(livingEntity, entry.getHolder(), message.isUnlocked(), message.isEnabled(), message.getMaxCooldown(), message.getCooldown(), message.getMaxActivationTimer(), message.getActivationTimer());
            refreshPowersScreen();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void handleProperty(SyncPropertyMessage message) {
        Entity entity = requireLevel().getEntity(message.getEntityId());
        if (entity != null) {
            EntityPropertyHandler.getHandler(entity).ifPresent(handler -> {
                for (String key : message.tag.getAllKeys()) {
                    PalladiumProperty property = handler.getPropertyByName(key);
                    if (property != null) {
                        handler.setRaw(property, property.fromNBT(message.tag.get(property.getKey()), handler.getDefault(property)));
                    }
                }
            });
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void handleAbilityProperty(SyncAbilityEntryPropertyMessage message) {
        Entity entity = requireLevel().getEntity(message.getEntityId());
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        AbilityInstance entry = message.getReference().getEntry(livingEntity);
        if (entry == null) {
            return;
        }

        PalladiumProperty property = entry.getPropertyManager().getPropertyByName(message.getPropertyKey());
        if (property != null) {
            entry.getPropertyManager().setRaw(property, property.fromNBT(message.getTag().get(property.getKey()), entry.getPropertyManager().getDefault(property)));
            refreshPowersScreen();
            if (property == NameChangeAbility.NAME_CACHED && entity instanceof Player player) {
                PlayerUtil.refreshDisplayName(player);
            }
        }
    }

    private static void handleAccessories(SyncAccessoriesMessage message) {
        Entity entity = requireLevel().getEntity(message.entityId);
        if (entity instanceof Player player) {
            Accessory.getPlayerData(player).ifPresent(data -> {
                data.clear(player);
                message.accessories.forEach((slot, accessories) -> accessories.forEach(accessory -> data.enable(slot, accessory, player)));
                if (Minecraft.getInstance().screen instanceof AccessoryScreen accessoryScreen) {
                    accessoryScreen.accessoryList.refreshList();
                }
            });
        }
    }

    private static void handleOpenBuyScreen(OpenAbilityBuyScreenMessage message) {
        if (Minecraft.getInstance().screen instanceof PowersScreen powersScreen && message.getReference().getEntry(Minecraft.getInstance().player) != null) {
            powersScreen.openOverlayScreen(new BuyAbilityScreen(message.getReference(), message.getUnlockData(), message.isAvailable(), powersScreen));
        }
    }

    private static void handleFlightState(SyncFlightStateMessage message) {
        Entity entity = requireLevel().getEntity(message.getEntityId());
        if (entity instanceof Player player && entity instanceof PalladiumPlayerExtension extension) {
            var flight = extension.palladium$getFlightHandler();
            if (message.isFlying()) {
                var flightType = FlightHandler.getAvailableFlightType(player);
                if (flightType.isNotNull()) {
                    flight.setFlightType(flightType);
                }
            } else {
                flight.setFlightType(FlightHandler.FlightType.NONE);
            }
            player.getAbilities().flying = false;
        }
    }

    private static void handleEnergyBar(SetEnergyBarMessage message) {
        Entity entity = requireLevel().getEntity(message.getEntityId());
        if (entity instanceof LivingEntity livingEntity) {
            var energyBar = message.getReference().getEntry(livingEntity);
            if (energyBar != null) {
                energyBar.set(message.getValue());
                energyBar.setMax(message.getMaxValue());
            }
        }
    }

    private static void handleTailoringRecipes(SyncAvailableTailoringRecipes message) {
        var recipeManager = requireLevel().getRecipeManager();
        TailoringScreen.setAvailableRecipes(message.getRecipes().stream().map(id -> recipeManager.byKey(id).orElseThrow()).toList());
    }

    private static void refreshPowersScreen() {
        if (Minecraft.getInstance().screen instanceof PowersScreen powersScreen && powersScreen.selectedTab != null) {
            powersScreen.selectedTab.populate();
        }
    }

    private static net.minecraft.client.multiplayer.ClientLevel requireLevel() {
        return Objects.requireNonNull(Minecraft.getInstance().level);
    }
}
