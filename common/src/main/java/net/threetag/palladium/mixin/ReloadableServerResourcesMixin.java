package net.threetag.palladium.mixin;

import net.minecraft.server.ReloadableServerResources;
import net.threetag.palladium.loot.LootTableModificationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @SuppressWarnings("rawtypes")
    @Redirect(method = "listeners", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private <E> List changeList(E e1, E e2, E e3, E e4) {
        return List.of(LootTableModificationManager.getInstance(), e1, e2, e3, e4);
    }

}
