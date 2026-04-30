package com.mindstormman.torque_and_transmissions.registry;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateTorqueandTransmissions.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.torque_and_transmissions.main"))
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> ModItems.TRANSMISSION.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TRANSMISSION.get());
                        output.accept(ModItems.ACE_ENGINE.get());
                        output.accept(ModItems.STICK_SHIFTER.get());
                        output.accept(ModItems.ACCELERATOR.get());
                        output.accept(ModItems.MECHANICS_WRENCH.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
