package net.threetag.palladium.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class VibraniumWeaveArmorItem extends ArmorItem implements ArmorWithRenderer {

    private Object renderer;

    public VibraniumWeaveArmorItem(Holder<ArmorMaterial> armorMaterial, ArmorItem.Type type, Properties properties) {
        super(armorMaterial, type, properties);
    }

    @Override
    public void setCachedArmorRenderer(Object object) {
        this.renderer = object;
    }

    @Override
    public Object getCachedArmorRenderer() {
        return this.renderer;
    }
}
