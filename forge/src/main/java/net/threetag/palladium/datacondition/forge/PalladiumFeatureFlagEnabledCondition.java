package net.threetag.palladium.datacondition.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.feature.PalladiumFeatureFlags;

public record PalladiumFeatureFlagEnabledCondition(PalladiumFeatureFlags.Type featureFlag) implements ICondition {

    private static final Codec<PalladiumFeatureFlags.Type> FEATURE_FLAG_CODEC = Codec.STRING.comapFlatMap(name -> {
        var featureFlag = PalladiumFeatureFlags.getFeatureFlag(name);
        return featureFlag == null
                ? DataResult.error(() -> "Unknown Palladium feature flag: " + name)
                : DataResult.success(featureFlag);
    }, PalladiumFeatureFlags.Type::getSerializedName);

    public static final MapCodec<PalladiumFeatureFlagEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FEATURE_FLAG_CODEC.fieldOf("feature_flag").forGetter(PalladiumFeatureFlagEnabledCondition::featureFlag)
    ).apply(instance, PalladiumFeatureFlagEnabledCondition::new));

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Palladium.MOD_ID);

    static {
        CONDITION_CODECS.register("feature_flag_enabled", () -> CODEC);
    }

    public static void register(IEventBus modEventBus) {
        CONDITION_CODECS.register(modEventBus);
    }

    @Override
    public boolean test(IContext context) {
        return PalladiumFeatureFlags.isEnabled(this.featureFlag);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
