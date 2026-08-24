package net.threetag.palladium.addonpack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rewrites command syntax used by pre-1.20.5 addon packs to the equivalent
 * data-component based syntax accepted by Minecraft 1.21.1.
 */
public final class LegacyCommandCompatibility {

    private static final String NUMBER = "([-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+))";
    private static final Pattern LEGACY_DUST = Pattern.compile(
            "\\b(?:minecraft:)?dust[\\t ]+" + NUMBER + "[\\t ]+" + NUMBER + "[\\t ]+" + NUMBER + "[\\t ]+" + NUMBER,
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern SELECTED_ITEM_TAG = Pattern.compile(
            "(SelectedItem\\s*:\\s*\\{[^{}\\r\\n]*?)tag\\s*:\\s*\\{([^{}\\r\\n]+)\\}",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern ITEM_LIST_TAG = Pattern.compile(
            "((?:Inventory|ArmorItems)\\s*:\\s*\\[[^]\\r\\n]*?)tag\\s*:\\s*\\{([^{}\\r\\n]+)\\}",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern LEGACY_ITEM_LITERAL = Pattern.compile(
            "(\\bwith\\s+)([a-z0-9_.-]+:[a-z0-9_./-]+)\\{([^{}\\r\\n]+)\\}",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CUSTOM_MODEL_DATA = Pattern.compile(
            "\\s*CustomModelData\\s*:\\s*([-+]?\\d+)(?:[bBsSlL])?\\s*",
            Pattern.CASE_INSENSITIVE
    );

    private LegacyCommandCompatibility() {
    }

    public static String normalize(String command) {
        if (command == null || command.isBlank()) {
            return command;
        }

        String normalized = replaceLegacyDust(command);
        normalized = replaceItemStackTags(normalized, SELECTED_ITEM_TAG);
        normalized = replaceItemStackTags(normalized, ITEM_LIST_TAG);
        return replaceLegacyItemLiterals(normalized);
    }

    private static String replaceLegacyDust(String command) {
        Matcher matcher = LEGACY_DUST.matcher(command);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = "minecraft:dust{color:["
                    + matcher.group(1) + "f," + matcher.group(2) + "f," + matcher.group(3)
                    + "f],scale:" + matcher.group(4) + "f}";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String replaceItemStackTags(String command, Pattern pattern) {
        String normalized = command;
        while (true) {
            Matcher matcher = pattern.matcher(normalized);
            if (!matcher.find()) {
                return normalized;
            }
            normalized = matcher.replaceFirst(Matcher.quoteReplacement(
                    matcher.group(1) + componentCompound(matcher.group(2))
            ));
        }
    }

    private static String replaceLegacyItemLiterals(String command) {
        Matcher matcher = LEGACY_ITEM_LITERAL.matcher(command);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(
                    matcher.group(1) + matcher.group(2) + componentArgument(matcher.group(3))
            ));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String componentCompound(String legacyTag) {
        Matcher customModelData = CUSTOM_MODEL_DATA.matcher(legacyTag);
        if (customModelData.matches()) {
            return "components:{\"minecraft:custom_model_data\":" + customModelData.group(1) + "}";
        }
        return "components:{\"minecraft:custom_data\":{" + legacyTag.trim() + "}}";
    }

    private static String componentArgument(String legacyTag) {
        Matcher customModelData = CUSTOM_MODEL_DATA.matcher(legacyTag);
        if (customModelData.matches()) {
            return "[minecraft:custom_model_data=" + customModelData.group(1) + "]";
        }
        return "[minecraft:custom_data={" + legacyTag.trim() + "}]";
    }
}
