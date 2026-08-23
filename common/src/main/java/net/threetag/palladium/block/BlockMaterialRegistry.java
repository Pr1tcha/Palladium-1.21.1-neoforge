package net.threetag.palladium.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used for getting material in addonpack block jsons
 */
public class BlockMaterialRegistry {

    private static final Map<ResourceLocation, MapColor> MATERIAL_COLORS = new HashMap<>();
    private static final Map<ResourceLocation, SoundType> SOUND_TYPES = new HashMap<>();

    public static void registerColor(ResourceLocation id, MapColor materialColor) {
        MATERIAL_COLORS.put(id, materialColor);
    }

    @Nullable
    public static MapColor getColor(ResourceLocation id) {
        return MATERIAL_COLORS.get(id);
    }

    public static Set<ResourceLocation> getAllColorIds() {
        return MATERIAL_COLORS.keySet();
    }

    public static Collection<MapColor> getAllColors() {
        return MATERIAL_COLORS.values();
    }

    public static void registerSoundType(ResourceLocation id, SoundType soundType) {
        SOUND_TYPES.put(id, soundType);
    }

    @Nullable
    public static SoundType getSoundType(ResourceLocation id) {
        return SOUND_TYPES.get(id);
    }

    public static Set<ResourceLocation> getAllSoundTypeIds() {
        return SOUND_TYPES.keySet();
    }

    public static Collection<SoundType> getAllSoundTypes() {
        return SOUND_TYPES.values();
    }

    static {
        registerColor(ResourceLocation.parse("none"), MapColor.NONE);
        registerColor(ResourceLocation.parse("grass"), MapColor.GRASS);
        registerColor(ResourceLocation.parse("sand"), MapColor.SAND);
        registerColor(ResourceLocation.parse("wool"), MapColor.WOOL);
        registerColor(ResourceLocation.parse("fire"), MapColor.FIRE);
        registerColor(ResourceLocation.parse("ice"), MapColor.ICE);
        registerColor(ResourceLocation.parse("metal"), MapColor.METAL);
        registerColor(ResourceLocation.parse("plant"), MapColor.PLANT);
        registerColor(ResourceLocation.parse("snow"), MapColor.SNOW);
        registerColor(ResourceLocation.parse("clay"), MapColor.CLAY);
        registerColor(ResourceLocation.parse("dirt"), MapColor.DIRT);
        registerColor(ResourceLocation.parse("stone"), MapColor.STONE);
        registerColor(ResourceLocation.parse("water"), MapColor.WATER);
        registerColor(ResourceLocation.parse("wood"), MapColor.WOOD);
        registerColor(ResourceLocation.parse("quartz"), MapColor.QUARTZ);
        registerColor(ResourceLocation.parse("color_orange"), MapColor.COLOR_ORANGE);
        registerColor(ResourceLocation.parse("color_magenta"), MapColor.COLOR_MAGENTA);
        registerColor(ResourceLocation.parse("color_light_blue"), MapColor.COLOR_LIGHT_BLUE);
        registerColor(ResourceLocation.parse("color_yellow"), MapColor.COLOR_YELLOW);
        registerColor(ResourceLocation.parse("color_light_green"), MapColor.COLOR_LIGHT_GREEN);
        registerColor(ResourceLocation.parse("color_pink"), MapColor.COLOR_PINK);
        registerColor(ResourceLocation.parse("color_gray"), MapColor.COLOR_GRAY);
        registerColor(ResourceLocation.parse("color_light_gray"), MapColor.COLOR_LIGHT_GRAY);
        registerColor(ResourceLocation.parse("color_cyan"), MapColor.COLOR_CYAN);
        registerColor(ResourceLocation.parse("color_purple"), MapColor.COLOR_PURPLE);
        registerColor(ResourceLocation.parse("color_blue"), MapColor.COLOR_BLUE);
        registerColor(ResourceLocation.parse("color_brown"), MapColor.COLOR_BROWN);
        registerColor(ResourceLocation.parse("color_green"), MapColor.COLOR_GREEN);
        registerColor(ResourceLocation.parse("color_red"), MapColor.COLOR_RED);
        registerColor(ResourceLocation.parse("color_black"), MapColor.COLOR_BLACK);
        registerColor(ResourceLocation.parse("gold"), MapColor.GOLD);
        registerColor(ResourceLocation.parse("diamond"), MapColor.DIAMOND);
        registerColor(ResourceLocation.parse("lapis"), MapColor.LAPIS);
        registerColor(ResourceLocation.parse("emerald"), MapColor.EMERALD);
        registerColor(ResourceLocation.parse("podzol"), MapColor.PODZOL);
        registerColor(ResourceLocation.parse("nether"), MapColor.NETHER);
        registerColor(ResourceLocation.parse("terracotta_orange"), MapColor.TERRACOTTA_ORANGE);
        registerColor(ResourceLocation.parse("terracotta_magenta"), MapColor.TERRACOTTA_MAGENTA);
        registerColor(ResourceLocation.parse("terracotta_light_blue"), MapColor.TERRACOTTA_LIGHT_BLUE);
        registerColor(ResourceLocation.parse("terracotta_yellow"), MapColor.TERRACOTTA_YELLOW);
        registerColor(ResourceLocation.parse("terracotta_light_green"), MapColor.TERRACOTTA_LIGHT_GREEN);
        registerColor(ResourceLocation.parse("terracotta_pink"), MapColor.TERRACOTTA_PINK);
        registerColor(ResourceLocation.parse("terracotta_gray"), MapColor.TERRACOTTA_GRAY);
        registerColor(ResourceLocation.parse("terracotta_light_gray"), MapColor.TERRACOTTA_LIGHT_GRAY);
        registerColor(ResourceLocation.parse("terracotta_cyan"), MapColor.TERRACOTTA_CYAN);
        registerColor(ResourceLocation.parse("terracotta_purple"), MapColor.TERRACOTTA_PURPLE);
        registerColor(ResourceLocation.parse("terracotta_blue"), MapColor.TERRACOTTA_BLUE);
        registerColor(ResourceLocation.parse("terracotta_brown"), MapColor.TERRACOTTA_BROWN);
        registerColor(ResourceLocation.parse("terracotta_green"), MapColor.TERRACOTTA_GREEN);
        registerColor(ResourceLocation.parse("terracotta_red"), MapColor.TERRACOTTA_RED);
        registerColor(ResourceLocation.parse("terracotta_black"), MapColor.TERRACOTTA_BLACK);
        registerColor(ResourceLocation.parse("crimson_nylium"), MapColor.CRIMSON_NYLIUM);
        registerColor(ResourceLocation.parse("crimson_stem"), MapColor.CRIMSON_STEM);
        registerColor(ResourceLocation.parse("crimson_hyphae"), MapColor.CRIMSON_HYPHAE);
        registerColor(ResourceLocation.parse("warped_nylium"), MapColor.WARPED_NYLIUM);
        registerColor(ResourceLocation.parse("warped_stem"), MapColor.WARPED_STEM);
        registerColor(ResourceLocation.parse("warped_hyphae"), MapColor.WARPED_HYPHAE);
        registerColor(ResourceLocation.parse("warped_wart_block"), MapColor.WARPED_WART_BLOCK);
        registerColor(ResourceLocation.parse("deepslate"), MapColor.DEEPSLATE);
        registerColor(ResourceLocation.parse("raw_iron"), MapColor.RAW_IRON);
        registerColor(ResourceLocation.parse("glow_lichen"), MapColor.GLOW_LICHEN);

        registerSoundType(ResourceLocation.parse("wood"), SoundType.WOOD);
        registerSoundType(ResourceLocation.parse("gravel"), SoundType.GRAVEL);
        registerSoundType(ResourceLocation.parse("grass"), SoundType.GRASS);
        registerSoundType(ResourceLocation.parse("lily_pad"), SoundType.LILY_PAD);
        registerSoundType(ResourceLocation.parse("stone"), SoundType.STONE);
        registerSoundType(ResourceLocation.parse("metal"), SoundType.METAL);
        registerSoundType(ResourceLocation.parse("glass"), SoundType.GLASS);
        registerSoundType(ResourceLocation.parse("wool"), SoundType.WOOL);
        registerSoundType(ResourceLocation.parse("sand"), SoundType.SAND);
        registerSoundType(ResourceLocation.parse("snow"), SoundType.SNOW);
        registerSoundType(ResourceLocation.parse("powder_snow"), SoundType.POWDER_SNOW);
        registerSoundType(ResourceLocation.parse("ladder"), SoundType.LADDER);
        registerSoundType(ResourceLocation.parse("anvil"), SoundType.ANVIL);
        registerSoundType(ResourceLocation.parse("slime_block"), SoundType.SLIME_BLOCK);
        registerSoundType(ResourceLocation.parse("honey_block"), SoundType.HONEY_BLOCK);
        registerSoundType(ResourceLocation.parse("wet_grass"), SoundType.WET_GRASS);
        registerSoundType(ResourceLocation.parse("coral_block"), SoundType.CORAL_BLOCK);
        registerSoundType(ResourceLocation.parse("bamboo"), SoundType.BAMBOO);
        registerSoundType(ResourceLocation.parse("bamboo_sapling"), SoundType.BAMBOO_SAPLING);
        registerSoundType(ResourceLocation.parse("scaffolding"), SoundType.SCAFFOLDING);
        registerSoundType(ResourceLocation.parse("sweet_berry_bush"), SoundType.SWEET_BERRY_BUSH);
        registerSoundType(ResourceLocation.parse("crop"), SoundType.CROP);
        registerSoundType(ResourceLocation.parse("hard_crop"), SoundType.HARD_CROP);
        registerSoundType(ResourceLocation.parse("vine"), SoundType.VINE);
        registerSoundType(ResourceLocation.parse("nether_wart"), SoundType.NETHER_WART);
        registerSoundType(ResourceLocation.parse("lantern"), SoundType.LANTERN);
        registerSoundType(ResourceLocation.parse("stem"), SoundType.STEM);
        registerSoundType(ResourceLocation.parse("nylium"), SoundType.NYLIUM);
        registerSoundType(ResourceLocation.parse("fungus"), SoundType.FUNGUS);
        registerSoundType(ResourceLocation.parse("root"), SoundType.ROOTS);
        registerSoundType(ResourceLocation.parse("shroomlight"), SoundType.SHROOMLIGHT);
        registerSoundType(ResourceLocation.parse("weeping_vines"), SoundType.WEEPING_VINES);
        registerSoundType(ResourceLocation.parse("twisting_vines"), SoundType.TWISTING_VINES);
        registerSoundType(ResourceLocation.parse("soul_sand"), SoundType.SOUL_SAND);
        registerSoundType(ResourceLocation.parse("soul_soil"), SoundType.SOUL_SOIL);
        registerSoundType(ResourceLocation.parse("basalt"), SoundType.BASALT);
        registerSoundType(ResourceLocation.parse("wart_block"), SoundType.WART_BLOCK);
        registerSoundType(ResourceLocation.parse("netherrack"), SoundType.NETHERRACK);
        registerSoundType(ResourceLocation.parse("nether_bricks"), SoundType.NETHER_BRICKS);
        registerSoundType(ResourceLocation.parse("nether_sprouts"), SoundType.NETHER_SPROUTS);
        registerSoundType(ResourceLocation.parse("nether_ore"), SoundType.NETHER_ORE);
        registerSoundType(ResourceLocation.parse("bone_block"), SoundType.BONE_BLOCK);
        registerSoundType(ResourceLocation.parse("netherite_block"), SoundType.NETHERITE_BLOCK);
        registerSoundType(ResourceLocation.parse("ancient_debris"), SoundType.ANCIENT_DEBRIS);
        registerSoundType(ResourceLocation.parse("lodestone"), SoundType.LODESTONE);
        registerSoundType(ResourceLocation.parse("chain"), SoundType.CHAIN);
        registerSoundType(ResourceLocation.parse("nether_gold_ore"), SoundType.NETHER_GOLD_ORE);
        registerSoundType(ResourceLocation.parse("gilded_blackstone"), SoundType.GILDED_BLACKSTONE);
        registerSoundType(ResourceLocation.parse("candle"), SoundType.CANDLE);
        registerSoundType(ResourceLocation.parse("amethyst"), SoundType.AMETHYST);
        registerSoundType(ResourceLocation.parse("amethyst_cluster"), SoundType.AMETHYST_CLUSTER);
        registerSoundType(ResourceLocation.parse("small_amethyst_bud"), SoundType.SMALL_AMETHYST_BUD);
        registerSoundType(ResourceLocation.parse("medium_amethyst_bud"), SoundType.MEDIUM_AMETHYST_BUD);
        registerSoundType(ResourceLocation.parse("large_amethyst_bud"), SoundType.LARGE_AMETHYST_BUD);
        registerSoundType(ResourceLocation.parse("tuff"), SoundType.TUFF);
        registerSoundType(ResourceLocation.parse("calcite"), SoundType.CALCITE);
        registerSoundType(ResourceLocation.parse("dripstone_block"), SoundType.DRIPSTONE_BLOCK);
        registerSoundType(ResourceLocation.parse("pointed_dripstone"), SoundType.POINTED_DRIPSTONE);
        registerSoundType(ResourceLocation.parse("copper"), SoundType.COPPER);
        registerSoundType(ResourceLocation.parse("cave_vines"), SoundType.CAVE_VINES);
        registerSoundType(ResourceLocation.parse("spore_blossom"), SoundType.SPORE_BLOSSOM);
        registerSoundType(ResourceLocation.parse("azalea"), SoundType.AZALEA);
        registerSoundType(ResourceLocation.parse("flowering_azalea"), SoundType.FLOWERING_AZALEA);
        registerSoundType(ResourceLocation.parse("moss_carpet"), SoundType.MOSS_CARPET);
        registerSoundType(ResourceLocation.parse("moss"), SoundType.MOSS);
        registerSoundType(ResourceLocation.parse("big_dripleaf"), SoundType.BIG_DRIPLEAF);
        registerSoundType(ResourceLocation.parse("small_dripleaf"), SoundType.SMALL_DRIPLEAF);
        registerSoundType(ResourceLocation.parse("rooted_dirt"), SoundType.ROOTED_DIRT);
        registerSoundType(ResourceLocation.parse("hanging_roots"), SoundType.HANGING_ROOTS);
        registerSoundType(ResourceLocation.parse("azelea_leaves"), SoundType.AZALEA_LEAVES);
        registerSoundType(ResourceLocation.parse("sculk_sensor"), SoundType.SCULK_SENSOR);
        registerSoundType(ResourceLocation.parse("sculk_catalyst"), SoundType.SCULK_CATALYST);
        registerSoundType(ResourceLocation.parse("sculk"), SoundType.SCULK);
        registerSoundType(ResourceLocation.parse("sculk_vein"), SoundType.SCULK_VEIN);
        registerSoundType(ResourceLocation.parse("sculk_shrieker"), SoundType.SCULK_SHRIEKER);
        registerSoundType(ResourceLocation.parse("glow_lichen"), SoundType.GLOW_LICHEN);
        registerSoundType(ResourceLocation.parse("deepslate"), SoundType.DEEPSLATE);
        registerSoundType(ResourceLocation.parse("deepslate_bricks"), SoundType.DEEPSLATE_BRICKS);
        registerSoundType(ResourceLocation.parse("deepslate_tiles"), SoundType.DEEPSLATE_TILES);
        registerSoundType(ResourceLocation.parse("polished_deepslate"), SoundType.POLISHED_DEEPSLATE);
        registerSoundType(ResourceLocation.parse("froglight"), SoundType.FROGLIGHT);
        registerSoundType(ResourceLocation.parse("frogspawn"), SoundType.FROGSPAWN);
        registerSoundType(ResourceLocation.parse("mangrove_roots"), SoundType.MANGROVE_ROOTS);
        registerSoundType(ResourceLocation.parse("muddy_mangrove_roots"), SoundType.MUDDY_MANGROVE_ROOTS);
        registerSoundType(ResourceLocation.parse("mud"), SoundType.MUD);
        registerSoundType(ResourceLocation.parse("mud_bricks"), SoundType.MUD_BRICKS);
        registerSoundType(ResourceLocation.parse("packed_mud"), SoundType.PACKED_MUD);
    }

}
