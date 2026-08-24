package net.threetag.palladium.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.threetag.palladium.client.PalladiumKeyMappings;
import net.threetag.palladium.entity.PalladiumPlayerExtension;
import net.threetag.palladium.network.PalladiumNetwork;
import net.threetag.palladium.network.RightClickAttackMessage;
import net.threetag.palladium.power.ability.Abilities;
import net.threetag.palladium.power.ability.AbilityUtil;

import java.util.Objects;

public final class DualWieldingClientHandler {

    private DualWieldingClientHandler() {
    }

    public static void attack() {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;

        if (player == null || !AbilityUtil.isTypeEnabled(player, Abilities.DUAL_WIELDING.get())
                || PalladiumKeyMappings.DUAL_WIELDING_RIGHT_CLICK
                || !(player instanceof PalladiumPlayerExtension extension)) {
            return;
        }

        PalladiumKeyMappings.DUAL_WIELDING_RIGHT_CLICK = true;
        var hitResult = Objects.requireNonNull(minecraft.hitResult);
        if (hitResult.getType() == HitResult.Type.MISS) {
            extension.palladium$getDualWieldingHandler().resetAttackStrengthTicker();
            minecraft.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.OFF_HAND);
            PalladiumNetwork.NETWORK.sendToServer(new RightClickAttackMessage(-1));
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            var target = ((EntityHitResult) hitResult).getEntity();
            PalladiumNetwork.NETWORK.sendToServer(new RightClickAttackMessage(target.getId()));
            extension.palladium$getDualWieldingHandler().attackWithOffHand(target);
            extension.palladium$getDualWieldingHandler().resetAttackStrengthTicker();
            minecraft.gameRenderer.itemInHandRenderer.itemUsed(InteractionHand.OFF_HAND);
        } else {
            PalladiumNetwork.NETWORK.sendToServer(new RightClickAttackMessage(-1));
        }
    }
}
