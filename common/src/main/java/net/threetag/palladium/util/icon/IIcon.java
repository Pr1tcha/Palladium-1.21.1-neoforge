package net.threetag.palladium.util.icon;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.threetag.palladium.util.context.DataContext;

public interface IIcon {

    @OnlyIn(Dist.CLIENT)
    default void draw(Minecraft mc, GuiGraphics guiGraphics, DataContext context, int x, int y) {
        this.draw(mc, guiGraphics, context, x, y, 16, 16);
    }

    @OnlyIn(Dist.CLIENT)
    void draw(Minecraft mc, GuiGraphics guiGraphics, DataContext context, int x, int y, int width, int height);

    IconSerializer<?> getSerializer();

}
