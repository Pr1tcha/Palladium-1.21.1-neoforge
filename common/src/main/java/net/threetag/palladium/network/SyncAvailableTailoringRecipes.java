package net.threetag.palladium.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladiumcore.network.MessageContext;
import net.threetag.palladiumcore.network.MessageS2C;
import net.threetag.palladiumcore.network.MessageType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SyncAvailableTailoringRecipes extends MessageS2C {

    private final List<ResourceLocation> recipes;

    public SyncAvailableTailoringRecipes(List<ResourceLocation> recipes) {
        this.recipes = recipes;
    }

    public SyncAvailableTailoringRecipes(FriendlyByteBuf buf) {
        this.recipes = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    @Override
    public @NotNull MessageType getType() {
        return PalladiumNetwork.SYNC_AVAILABLE_TAILORING_RECIPES;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(this.recipes, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handle(MessageContext context) {
        PalladiumNetwork.handleClient(this);
    }

    public List<ResourceLocation> getRecipes() {
        return this.recipes;
    }
}
