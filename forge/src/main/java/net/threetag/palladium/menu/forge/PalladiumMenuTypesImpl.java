package net.threetag.palladium.menu.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.threetag.palladium.menu.ExtendedMenuProvider;
import net.threetag.palladium.menu.PalladiumMenuTypes;

public class PalladiumMenuTypesImpl {

    public static void openExtendedMenu(ServerPlayer player, ExtendedMenuProvider provider) {
        player.openMenu(provider, provider::addAdditionalData);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> ofExtended(PalladiumMenuTypes.ExtendedMenuTypeFactory<T> factory) {
        return IMenuTypeExtension.create(factory::create);
    }

}
