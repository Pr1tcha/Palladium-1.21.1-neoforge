package net.threetag.palladium.mixin;

import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.command.EntitySelectorParserExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntitySelectorParser.class)
public class EntitySelectorParserMixin implements EntitySelectorParserExtension {

    @Unique
    ResourceLocation palladium$powerId = null;

    @Override
    public ResourceLocation palladium$getPower() {
        return this.palladium$powerId;
    }

    @Override
    public void palladium$setPower(ResourceLocation powerId) {
        this.palladium$powerId = powerId;
    }
}
