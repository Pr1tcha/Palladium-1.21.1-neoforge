package net.threetag.palladium.mixin;

import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.threetag.palladium.world.TrackedScoresManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "updateEntireScoreboard", at = @At("RETURN"))
    protected void updateEntireScoreboard(ServerScoreboard scoreboard, ServerPlayer player, CallbackInfo ci) {
        for (String tracked : TrackedScoresManager.INSTANCE.getTracked()) {
            var objective = scoreboard.getObjective(tracked);

            if (objective != null) {
                if (scoreboard.getObjectiveDisplaySlotCount(objective) == 0) {
                    player.connection.send(new ClientboundSetObjectivePacket(objective, 0));

                    for (var score : scoreboard.listPlayerScores(objective)) {
                        player.connection.send(new ClientboundSetScorePacket(score.owner(), objective.getName(), score.value(), Optional.ofNullable(score.display()), Optional.ofNullable(score.numberFormatOverride())));
                    }
                }

                scoreboard.trackedObjectives.add(objective);
            }
        }
    }

}
