package net.threetag.palladiumcore.registry.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ColorHandlerRegistry {

    private static final List<ItemColorRegistration> ITEM_COLORS = new ArrayList<>();
    private static final List<BlockColorRegistration> BLOCK_COLORS = new ArrayList<>();

    private ColorHandlerRegistry() {
    }

    @SafeVarargs
    public static void registerItemColors(ItemColor color, Supplier<? extends ItemLike>... items) {
        ITEM_COLORS.add(new ItemColorRegistration(color, items));
    }

    @SafeVarargs
    public static void registerBlockColors(BlockColor color, Supplier<? extends Block>... blocks) {
        BLOCK_COLORS.add(new BlockColorRegistration(color, blocks));
    }

    static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (ItemColorRegistration registration : ITEM_COLORS) {
            ItemLike[] items = new ItemLike[registration.items().length];
            for (int index = 0; index < items.length; index++) {
                items[index] = registration.items()[index].get();
            }
            event.register(registration.color(), items);
        }
    }

    static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        for (BlockColorRegistration registration : BLOCK_COLORS) {
            Block[] blocks = new Block[registration.blocks().length];
            for (int index = 0; index < blocks.length; index++) {
                blocks[index] = registration.blocks()[index].get();
            }
            event.register(registration.color(), blocks);
        }
    }

    private record ItemColorRegistration(ItemColor color, Supplier<? extends ItemLike>[] items) {
    }

    private record BlockColorRegistration(BlockColor color, Supplier<? extends Block>[] blocks) {
    }
}
