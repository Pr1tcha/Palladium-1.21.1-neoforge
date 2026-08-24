package net.threetag.palladium.addonpack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Converts pre-component item modifier functions to their 1.21 equivalents. */
public final class LegacyItemModifierCompatibility {

    private static final Pattern CUSTOM_MODEL_DATA = Pattern.compile(
            "^\\{\\s*CustomModelData\\s*:\\s*([-+]?\\d+)(?:[bBsSlL])?\\s*}$",
            Pattern.CASE_INSENSITIVE
    );

    private LegacyItemModifierCompatibility() {
    }

    public static String normalize(String source) {
        JsonElement root = JsonParser.parseString(source);
        return rewrite(root) ? root.toString() : source;
    }

    private static boolean rewrite(JsonElement element) {
        boolean changed = false;

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                changed |= rewrite(child);
            }
            return changed;
        }

        if (!element.isJsonObject()) {
            return false;
        }

        JsonObject object = element.getAsJsonObject();
        if (object.has("function") && object.get("function").isJsonPrimitive()
                && object.get("function").getAsString().matches("(?:minecraft:)?set_nbt")
                && object.has("tag") && object.get("tag").isJsonPrimitive()) {
            Matcher customModelData = CUSTOM_MODEL_DATA.matcher(object.get("tag").getAsString());
            if (customModelData.matches()) {
                object.addProperty("function", "minecraft:set_custom_model_data");
                object.remove("tag");
                object.addProperty("value", Integer.parseInt(customModelData.group(1)));
            } else {
                object.addProperty("function", "minecraft:set_custom_data");
            }
            changed = true;
        }

        for (JsonElement child : object.asMap().values()) {
            changed |= rewrite(child);
        }
        return changed;
    }
}
