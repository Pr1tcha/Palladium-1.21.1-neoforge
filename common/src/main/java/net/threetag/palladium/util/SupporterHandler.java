package net.threetag.palladium.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.accessory.Accessory;
import net.threetag.palladiumcore.event.PlayerEvents;
import net.threetag.palladiumcore.util.Platform;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SupporterHandler {

    private static final String BASE_URL = "https://squirrelcontrol.threetag.net/api/";
    private static final Map<UUID, PlayerData> DATA = Maps.newHashMap();
    private static BiConsumer<PlayerData, String> CLOAK_TEXTURE_LOADER = (data, url) -> {
    };
    private static boolean CHECK = false;

    public static void init() {
        PlayerEvents.JOIN.register(player -> {
            SupporterHandler.loadPlayerData(player.getUUID());

            if (player instanceof ServerPlayer serverPlayer && CHECK && !SupporterHandler.getPlayerData(player.getUUID()).hasModAccess()) {
                serverPlayer.connection.disconnect(Component.literal("You are not allowed to use this mod!"));
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit() {
        PlayerEvents.CLIENT_JOIN.register(player -> SupporterHandler.loadPlayerData(player.getUUID()));
        PlayerEvents.CLIENT_QUIT.register(player -> {
            if (player != null)
                DATA.remove(player.getUUID());
        });
    }

    public static void loadPlayerData(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try {
                JsonObject json = readJsonFromUrl(BASE_URL + "player/" + uuid.toString());
                PlayerData data = new PlayerData(uuid, GsonHelper.getAsJsonObject(json, "data"));
                DATA.put(uuid, data);
                Palladium.LOGGER.info("Successfully read user's supporter data! ({})", uuid);

                if (Platform.getCurrentServer() != null) {
                    Player player = Platform.getCurrentServer().getPlayerList().getPlayer(uuid);

                    if (player != null) {
                        Accessory.getPlayerData(player).ifPresent(accessoryData -> accessoryData.validate(player));
                    }
                }

                return;
            } catch (Exception e) {
                if (!Platform.isProduction()) {
                    Palladium.LOGGER.warn("Was not able to read user's supporter data! ({})", uuid.toString());
                }
            }
            PlayerData data = new PlayerData(uuid, new JsonObject());
            DATA.put(uuid, data);
        }, Util.backgroundExecutor()).join();
    }

    public static void enableSupporterCheck() {
        if (!CHECK) {
            CHECK = true;
            Palladium.LOGGER.info("The supporter check has been enabled!");
        }
    }

    public static void setCloakTextureLoader(BiConsumer<PlayerData, String> loader) {
        CLOAK_TEXTURE_LOADER = loader;
    }

    public static boolean isSupporterCheckEnabled() {
        return CHECK;
    }

    public static PlayerData getPlayerData(UUID uuid) {
        if (DATA.containsKey(uuid)) {
            return DATA.get(uuid);
        } else {
            PlayerData data = new PlayerData(uuid, new JsonObject());
            DATA.put(uuid, data);
            return data;
        }
    }

    public static PlayerData getPlayerDataUnsafe(UUID uuid) {
        return DATA.get(uuid);
    }

    public static JsonObject readJsonFromUrl(String url) throws Exception {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject json = (new JsonParser()).parse(rd).getAsJsonObject();

            if (GsonHelper.getAsInt(json, "error") != 200) {
                throw new Exception("Error while reading json: " + GsonHelper.getAsString(json, "message"));
            }

            return json;
        } finally {
            is.close();
        }
    }

    public static class PlayerData {

        private final UUID uuid;
        private final List<Accessory> accessories;
        private final boolean modAccess;
        private final boolean hasCloak;
        private ResourceLocation cloakTexture;

        public PlayerData(UUID uuid, JsonObject json) {
            this.uuid = uuid;
            this.accessories = new ArrayList<>();
            JsonArray data = GsonHelper.getAsJsonArray(json, "accessoires", new JsonArray());

            for (int i = 0; i < data.size(); i++) {
                ResourceLocation id = ResourceLocation.parse(data.get(i).getAsString());

                if (id.getNamespace().equalsIgnoreCase("threecore")) {
                    id = Palladium.id(id.getPath());
                }

                if (Accessory.REGISTRY.containsKey(id)) {
                    this.accessories.add(Accessory.REGISTRY.get(id));
                }
            }

            this.modAccess = GsonHelper.getAsBoolean(json, "mod_access", false);

            if (GsonHelper.isValidNode(json, "cloak")) {
                this.hasCloak = true;
                CLOAK_TEXTURE_LOADER.accept(this, GsonHelper.getAsString(json, "cloak"));
            } else {
                this.hasCloak = false;
            }
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public void setCloakTexture(ResourceLocation cloakTexture) {
            this.cloakTexture = cloakTexture;
        }

        public boolean hasModAccess() {
            return this.modAccess;
        }

        public boolean hasAccessory(Accessory accessory) {
            return this.accessories.contains(accessory) || !Platform.isProduction();
        }

        public List<Accessory> getAccessories() {
            return ImmutableList.copyOf(this.accessories);
        }

        public boolean hasCloak() {
            return this.hasCloak;
        }

        @Nullable
        public ResourceLocation getCloakTexture() {
            return this.cloakTexture;
        }
    }

}
