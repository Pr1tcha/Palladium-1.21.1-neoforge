package net.threetag.palladium.util.property;

import java.util.Locale;

public class TextAlignmentProperty extends EnumPalladiumProperty<TextAlignmentProperty.TextAlignment> {

    public TextAlignmentProperty(String key) {
        super(key);
    }

    @Override
    public TextAlignment[] getValues() {
        return TextAlignment.values();
    }

    @Override
    public String getNameFromEnum(TextAlignment value) {
        return value.name().toLowerCase(Locale.ROOT);
    }

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
