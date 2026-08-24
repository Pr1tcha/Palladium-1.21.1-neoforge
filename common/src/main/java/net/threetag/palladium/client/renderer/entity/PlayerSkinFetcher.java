package net.threetag.palladium.client.renderer.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.threetag.palladium.Palladium;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PlayerSkinFetcher {

    private static final Map<String, GameProfile> CACHED_NAME_PROFILES = new ConcurrentHashMap<>();
    private static final Map<String, PlayerSkinInfo> CACHED_NAME_SKINS = new ConcurrentHashMap<>();
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    @Nullable
    public static ResourceLocation getOrLoadPlayerSkin(String username) {
        var PlayerSkinInfo = getPlayerSkinInfo(username);

        if (PlayerSkinInfo.hasFailed() || PlayerSkinInfo.isLoading()) {
            return null;
        } else {
            return PlayerSkinInfo.getTexture();
        }
    }

    @Nullable
    public static String getOrLoadModelType(String username) {
        var PlayerSkinInfo = getPlayerSkinInfo(username);

        if (PlayerSkinInfo.hasFailed() || PlayerSkinInfo.isLoading()) {
            return null;
        } else {
            return PlayerSkinInfo.getModelName();
        }
    }

    @Nullable
    public static PlayerSkinInfo getLoadedPlayerSkinInfo(final @NotNull String username) {
        var PlayerSkinInfo = getPlayerSkinInfo(username);

        if (PlayerSkinInfo.hasFailed() || PlayerSkinInfo.isLoading()) {
            return null;
        } else {
            return PlayerSkinInfo;
        }
    }

    public static PlayerSkinInfo getPlayerSkinInfo(final @NotNull String username) {
        var mcPlayer = Minecraft.getInstance().player;

        if (mcPlayer != null && username.equalsIgnoreCase(mcPlayer.getGameProfile().getName())) {
            return CACHED_NAME_SKINS.computeIfAbsent(username.toLowerCase(Locale.ROOT), s -> {
                var PlayerSkinInfo = new PlayerSkinInfo();
                var skin = mcPlayer.getSkin();
                PlayerSkinInfo.set(getLegacyModelName(skin), skin.texture());
                return PlayerSkinInfo;
            });
        }

        return CACHED_NAME_SKINS.computeIfAbsent(username.toLowerCase(Locale.ROOT), (key) -> {
            Util.backgroundExecutor().execute(() -> {
                var profile = getGameProfile(username);

                if (profile != null) {
                    Minecraft.getInstance().getSkinManager().getOrLoad(profile).thenAccept(skin ->
                            CACHED_NAME_SKINS.computeIfAbsent(profile.getName().toLowerCase(Locale.ROOT), n -> new PlayerSkinInfo())
                                    .set(getLegacyModelName(skin), skin.texture())
                    ).exceptionally(throwable -> {
                        CACHED_NAME_SKINS.computeIfAbsent(username.toLowerCase(Locale.ROOT), n -> new PlayerSkinInfo()).setFailed();
                        Palladium.LOGGER.warn("Failed to load skin for {}", username, throwable);
                        return null;
                    });
                } else {
                    CACHED_NAME_SKINS.computeIfAbsent(username.toLowerCase(Locale.ROOT), (n) -> new PlayerSkinInfo()).setFailed();
                }
            });

            return new PlayerSkinInfo();
        });
    }

    public static @Nullable GameProfile getGameProfile(final @NotNull String name) {
        return CACHED_NAME_PROFILES.computeIfAbsent(name, (key) -> {
            JsonObject object = parseURL("https://api.mojang.com/users/profiles/minecraft/" + key + "?unsigned=false");
            if (object == null) return null;
            UUID uuid = fromStringWithoutDashes(GsonHelper.getAsString(object, "id"));
            String realName = GsonHelper.getAsString(object, "name");
            return new GameProfile(uuid, realName);
        });
    }

    private static @Nullable JsonObject parseURL(final @NotNull String purl) {
        try {
            URI uri = new URI(purl);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Failed to do an HTTP request to: " + purl + " Response code: " + response.statusCode());
            }

            String responseBody = response.body();
            return JsonParser.parseString(responseBody).getAsJsonObject();
        } catch (Exception e) {
            Palladium.LOGGER.warn("Failed to fetch URL: {}", purl);
        }
        return null;
    }

    private static UUID fromStringWithoutDashes(final @NotNull String uuid) {
        String correctedUUID = UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
        return UUID.fromString(correctedUUID);
    }

    private static String getLegacyModelName(PlayerSkin skin) {
        return skin.model() == PlayerSkin.Model.SLIM ? "slim" : "default";
    }

}
