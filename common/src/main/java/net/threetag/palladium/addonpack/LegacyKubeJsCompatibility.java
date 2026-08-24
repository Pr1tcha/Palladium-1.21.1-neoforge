package net.threetag.palladium.addonpack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizes scalar values which old Rhino versions implicitly unwrapped from
 * single-element arrays. Modern KubeJS preserves the array notation when the
 * value is converted to a string, breaking commands and scoreboard names.
 */
public final class LegacyKubeJsCompatibility {

    private static final Pattern INDEXED_SCALAR_ARRAY = Pattern.compile(
            "(?m)^(\\h*global\\.(?:ability|ability_cost|geo_type|geo_cost)_\\d+\\h*=\\h*)"
                    + "\\[\\h*(['\"])([^'\"\\r\\n]*)\\2\\h*](\\h*)$"
    );

    private LegacyKubeJsCompatibility() {
    }

    public static String normalize(String script) {
        if (script == null || script.isBlank()) {
            return script;
        }

        Matcher matcher = INDEXED_SCALAR_ARRAY.matcher(script);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String quote = matcher.group(2);
            matcher.appendReplacement(result, Matcher.quoteReplacement(
                    matcher.group(1) + quote + matcher.group(3) + quote + matcher.group(4)
            ));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
