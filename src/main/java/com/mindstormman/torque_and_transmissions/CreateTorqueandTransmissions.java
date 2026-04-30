package com.mindstormman.torque_and_transmissions;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;
import com.mindstormman.torque_and_transmissions.registry.ModCreativeTabs;
import com.mindstormman.torque_and_transmissions.registry.ModItems;
import com.mindstormman.torque_and_transmissions.registry.ModNetworking;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateTorqueandTransmissions.MODID)
public class CreateTorqueandTransmissions {
    public static final String MODID = "torque_and_transmissions";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateTorqueandTransmissions(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModNetworking.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Create Torque and Transmissions initialized. Default gear ratios: {}", Config.GEAR_RATIOS.get());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Create Torque and Transmissions server starting.");
    }
}
