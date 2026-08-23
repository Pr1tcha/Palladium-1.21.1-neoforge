package net.threetag.palladiumcore.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VillagerTradeRegistry {

    private static final Map<VillagerProfession, Int2ObjectMap<List<VillagerTrades.ItemListing>>> TRADES = new HashMap<>();
    private static final List<VillagerTrades.ItemListing> WANDERER = new ArrayList<>();
    private static final List<VillagerTrades.ItemListing> WANDERER_RARE = new ArrayList<>();

    private VillagerTradeRegistry() {
    }

    public static void registerForProfession(VillagerProfession profession, int level, VillagerTrades.ItemListing... trades) {
        var byLevel = TRADES.computeIfAbsent(profession, ignored -> new Int2ObjectOpenHashMap<>());
        Collections.addAll(byLevel.computeIfAbsent(level, ignored -> new ArrayList<>()), trades);
    }

    public static void registerForWanderingTrader(boolean rare, VillagerTrades.ItemListing... trades) {
        Collections.addAll(rare ? WANDERER_RARE : WANDERER, trades);
    }

    static void addVillagerTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> byLevel = TRADES.get(event.getType());
        if (byLevel != null) {
            byLevel.int2ObjectEntrySet().forEach(entry ->
                    event.getTrades().computeIfAbsent(entry.getIntKey(), ignored -> new ArrayList<>()).addAll(entry.getValue()));
        }
    }

    static void addWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().addAll(WANDERER);
        event.getRareTrades().addAll(WANDERER_RARE);
    }
}
