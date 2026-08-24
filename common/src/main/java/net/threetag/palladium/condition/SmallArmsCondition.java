package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.util.PlayerUtil;
import net.threetag.palladium.util.context.DataContext;
import net.threetag.palladiumcore.util.Platform;

public class SmallArmsCondition extends Condition {

    @Override
    public boolean active(DataContext context) {
        if (Platform.isClient()) {
            return this.has(context.getPlayer());
        } else {
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private boolean has(Player player) {
        return PlayerUtil.hasSmallArms(player);
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.SMALL_ARMS.get();
    }

    public static class Serializer extends ConditionSerializer {

        @Override
        public Condition make(JsonObject json) {
            return new SmallArmsCondition();
        }

        @Override
        public ConditionEnvironment getContextEnvironment() {
            return ConditionEnvironment.ASSETS;
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity has small arms. Returns false if the entity is not a player or if this condition is being checked sever-side.";
        }
    }
}
