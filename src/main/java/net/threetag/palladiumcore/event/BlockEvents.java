package net.threetag.palladiumcore.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockEvents {

    Event<BreakBlock> BREAK = new Event<>(BreakBlock.class, listeners -> (level, pos, state, player) ->
            Event.result(listeners, listener -> listener.breakBlock(level, pos, state, player)));
    Event<PlaceBlock> PLACE = new Event<>(PlaceBlock.class, listeners -> (level, pos, placed, against, entity) ->
            Event.result(listeners, listener -> listener.placeBlock(level, pos, placed, against, entity)));

    @FunctionalInterface interface BreakBlock { EventResult breakBlock(LevelAccessor level, BlockPos pos, BlockState state, Player player); }
    @FunctionalInterface interface PlaceBlock { EventResult placeBlock(LevelAccessor level, BlockPos pos, BlockState placed, BlockState against, @Nullable Entity entity); }
}
