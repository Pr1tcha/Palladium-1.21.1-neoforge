package net.threetag.palladium.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.PalladiumClient;
import net.threetag.palladium.PalladiumConfig;
import net.threetag.palladium.addonpack.AddonPackManager;
import net.threetag.palladium.block.PalladiumBlocks;
import net.threetag.palladium.client.model.ModelLayerManager;
import net.threetag.palladium.compat.apothicattributes.forge.ApothicAttributesCompat;
import net.threetag.palladium.compat.curios.forge.CuriosCompat;
import net.threetag.palladium.compat.geckolib.forge.GeckoLibCompatImpl;
import net.threetag.palladium.data.forge.*;
import net.threetag.palladium.datacondition.forge.PalladiumFeatureFlagEnabledCondition;
import net.threetag.palladium.energy.forge.EnergyHelperImpl;
import net.threetag.palladium.mixin.ReloadableResourceManagerMixin;
import net.threetag.palladium.menu.PalladiumMenuTypes;
import net.threetag.palladiumcore.event.ClientEventBridge;
import net.threetag.palladiumcore.event.EventBridge;
import net.threetag.palladiumcore.network.NetworkRegistration;
import net.threetag.palladiumcore.registry.ModEventBusRegistry;
import net.threetag.palladiumcore.registry.RegistrationEvents;
import net.threetag.palladiumcore.registry.client.ClientRegistrationEvents;
import net.threetag.palladiumcore.util.Platform;

import java.util.List;

@Mod(Palladium.MOD_ID)
public class PalladiumForge {

    public PalladiumForge(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ModEventBusRegistry.register(Palladium.MOD_ID, modEventBus);
        PalladiumFeatureFlagEnabledCondition.register(modEventBus);
        RegistrationEvents.register(modEventBus);
        EventBridge.register(modEventBus);
        NetworkRegistration.register(modEventBus);
        modEventBus.addListener(EnergyHelperImpl::registerCapabilities);
        if (dist == Dist.CLIENT) {
            ClientRegistrationEvents.register(modEventBus);
            ClientEventBridge.register(modEventBus);
            modEventBus.addListener(PalladiumMenuTypes::registerScreens);
        }
        modEventBus.register(PalladiumForge.class);

        Palladium.init();
        modContainer.registerConfig(ModConfig.Type.CLIENT, PalladiumConfig.Client.generateConfig());
        modContainer.registerConfig(ModConfig.Type.SERVER, PalladiumConfig.Server.generateConfig());
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.init();
        }

        if (Platform.isModLoaded("geckolib")) {
            GeckoLibCompatImpl.init();
        }

        if (Platform.isModLoaded("attributeslib")) {
            ApothicAttributesCompat.init();
        }

        if (Platform.isClient()) {
            PalladiumClient.init();

            if (ModList.get().isLoaded("curios")) {
                CuriosCompat.initClient();
            }
        }
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent e) {
        AddonPackManager.waitForLoading();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onConstructMod(FMLConstructModEvent event) {
        event.enqueueWork(AddonPackManager::startLoading);
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent e) {
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(PalladiumBlocks.HEART_SHAPED_HERB.getId(), PalladiumBlocks.POTTED_HEART_SHAPED_HERB);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        Palladium.generateDocumentation();
        var output = e.getGenerator().getPackOutput();

        PalladiumBlockTagsProvider blockTagsProvider = new PalladiumBlockTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper());
        e.getGenerator().addProvider(e.includeServer(), blockTagsProvider);
        e.getGenerator().addProvider(e.includeServer(), new PalladiumItemTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));
        e.getGenerator().addProvider(e.includeServer(), new PalladiumRecipeProvider(output));
        e.getGenerator().addProvider(e.includeServer(), new PalladiumLootTableProvider(output));
        e.getGenerator().addProvider(e.includeServer(), new PalladiumWorldGenProvider(output, e.getLookupProvider()));
        e.getGenerator().addProvider(e.includeServer(), new PalladiumDamageTypeProvider(output, e.getExistingFileHelper()));

        e.getGenerator().addProvider(e.includeClient(), new PalladiumBlockStateProvider(output, e.getExistingFileHelper()));
        e.getGenerator().addProvider(e.includeClient(), new PalladiumItemModelProvider(output, e.getExistingFileHelper()));
        e.getGenerator().addProvider(e.includeClient(), new PalladiumSoundDefinitionsProvider(output, e.getExistingFileHelper()));
        e.getGenerator().addProvider(e.includeClient(), new PalladiumLangProvider.English(output));
        e.getGenerator().addProvider(e.includeClient(), new PalladiumLangProvider.German(output));
        e.getGenerator().addProvider(e.includeClient(), new PalladiumLangProvider.Saxon(output));
    }

    @SubscribeEvent
    public static void packFinder(AddPackFindersEvent e) {
        if (e.getPackType() != AddonPackManager.getPackType()) {
            e.addRepositorySource(AddonPackManager.getInstance().getWrappedPackFinder());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        Minecraft mc = Minecraft.getInstance();
        List<PreparableReloadListener> listeners = ((ReloadableResourceManagerMixin) mc.getResourceManager()).getListeners();
        int idx = listeners.indexOf(mc.getEntityModels());
        listeners.add(idx + 1, new ModelLayerManager());
    }
}
