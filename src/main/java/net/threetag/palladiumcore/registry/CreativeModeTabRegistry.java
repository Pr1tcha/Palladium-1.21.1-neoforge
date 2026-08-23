package net.threetag.palladiumcore.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CreativeModeTabRegistry {

    private static final Map<Supplier<CreativeModeTab>, Consumer<ItemGroupEntries>> TAB_MODIFICATIONS = new LinkedHashMap<>();

    private CreativeModeTabRegistry() {
    }

    public static CreativeModeTab create(Component title, Supplier<ItemStack> icon) {
        return CreativeModeTab.builder().title(title).icon(icon).build();
    }

    public static CreativeModeTab create(Consumer<CreativeModeTab.Builder> builderConsumer) {
        CreativeModeTab.Builder builder = CreativeModeTab.builder();
        builderConsumer.accept(builder);
        return builder.build();
    }

    public static void addToTab(Supplier<CreativeModeTab> tab, Consumer<ItemGroupEntries> entriesConsumer) {
        TAB_MODIFICATIONS.put(tab, entriesConsumer);
    }

    public static void addToTab(CreativeModeTab tab, Consumer<ItemGroupEntries> entriesConsumer) {
        addToTab(() -> tab, entriesConsumer);
    }

    public static void addToTab(ResourceKey<CreativeModeTab> tab, Consumer<ItemGroupEntries> entriesConsumer) {
        addToTab(() -> BuiltInRegistries.CREATIVE_MODE_TAB.get(tab), entriesConsumer);
    }

    static void buildContents(BuildCreativeModeTabContentsEvent event) {
        TAB_MODIFICATIONS.forEach((tab, modification) -> {
            if (tab.get() == event.getTab()) {
                modification.accept(new ItemGroupEntriesWrapper(event));
            }
        });
    }

    public interface ItemGroupEntries {
        void add(ItemLike... items);

        void add(CreativeModeTab.TabVisibility visibility, ItemLike... items);

        void addAfter(ItemLike afterLast, ItemLike... items);

        void addAfter(ItemLike afterLast, CreativeModeTab.TabVisibility visibility, ItemLike... items);

        void addBefore(ItemLike beforeFirst, ItemLike... items);

        void addBefore(ItemLike beforeFirst, CreativeModeTab.TabVisibility visibility, ItemLike... items);

        void add(ItemStack... stacks);

        void add(CreativeModeTab.TabVisibility visibility, ItemStack... stacks);

        void addAfter(ItemLike afterLast, ItemStack... stacks);

        void addAfter(ItemLike afterLast, CreativeModeTab.TabVisibility visibility, ItemStack... stacks);

        void addBefore(ItemLike beforeFirst, ItemStack... stacks);

        void addBefore(ItemLike beforeFirst, CreativeModeTab.TabVisibility visibility, ItemStack... stacks);
    }

    private record ItemGroupEntriesWrapper(BuildCreativeModeTabContentsEvent event) implements ItemGroupEntries {

        private List<ItemStack> visibleEntries() {
            List<ItemStack> entries = new ArrayList<>(this.event.getParentEntries());
            for (ItemStack stack : this.event.getSearchEntries()) {
                if (!entries.contains(stack)) {
                    entries.add(stack);
                }
            }
            return entries;
        }

        private ItemStack findFirst(ItemLike item) {
            return visibleEntries().stream().filter(stack -> stack.is(item.asItem())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Creative tab does not contain " + item.asItem()));
        }

        private ItemStack findLast(ItemLike item) {
            List<ItemStack> entries = visibleEntries();
            for (int index = entries.size() - 1; index >= 0; index--) {
                ItemStack stack = entries.get(index);
                if (stack.is(item.asItem())) {
                    return stack;
                }
            }
            throw new IllegalArgumentException("Creative tab does not contain " + item.asItem());
        }

        @Override
        public void add(ItemLike... items) {
            add(CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, items);
        }

        @Override
        public void add(CreativeModeTab.TabVisibility visibility, ItemLike... items) {
            for (ItemLike item : items) {
                this.event.accept(item, visibility);
            }
        }

        @Override
        public void addAfter(ItemLike afterLast, ItemLike... items) {
            addAfter(afterLast, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, items);
        }

        @Override
        public void addAfter(ItemLike afterLast, CreativeModeTab.TabVisibility visibility, ItemLike... items) {
            ItemStack anchor = findLast(afterLast);
            for (ItemLike item : items) {
                ItemStack stack = item.asItem().getDefaultInstance();
                this.event.insertAfter(anchor, stack, visibility);
                anchor = stack;
            }
        }

        @Override
        public void addBefore(ItemLike beforeFirst, ItemLike... items) {
            addBefore(beforeFirst, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, items);
        }

        @Override
        public void addBefore(ItemLike beforeFirst, CreativeModeTab.TabVisibility visibility, ItemLike... items) {
            ItemStack anchor = findFirst(beforeFirst);
            for (ItemLike item : items) {
                this.event.insertBefore(anchor, item.asItem().getDefaultInstance(), visibility);
            }
        }

        @Override
        public void add(ItemStack... stacks) {
            add(CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, stacks);
        }

        @Override
        public void add(CreativeModeTab.TabVisibility visibility, ItemStack... stacks) {
            for (ItemStack stack : stacks) {
                this.event.accept(stack, visibility);
            }
        }

        @Override
        public void addAfter(ItemLike afterLast, ItemStack... stacks) {
            addAfter(afterLast, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, stacks);
        }

        @Override
        public void addAfter(ItemLike afterLast, CreativeModeTab.TabVisibility visibility, ItemStack... stacks) {
            ItemStack anchor = findLast(afterLast);
            for (ItemStack stack : stacks) {
                this.event.insertAfter(anchor, stack, visibility);
                anchor = stack;
            }
        }

        @Override
        public void addBefore(ItemLike beforeFirst, ItemStack... stacks) {
            addBefore(beforeFirst, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, stacks);
        }

        @Override
        public void addBefore(ItemLike beforeFirst, CreativeModeTab.TabVisibility visibility, ItemStack... stacks) {
            ItemStack anchor = findFirst(beforeFirst);
            for (ItemStack stack : stacks) {
                this.event.insertBefore(anchor, stack, visibility);
            }
        }
    }
}
