package com.mindstormman.torque_and_transmissions.registry;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.item.MechanicsWrenchItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateTorqueandTransmissions.MODID);

    public static final DeferredItem<BlockItem> TRANSMISSION = ITEMS.registerSimpleBlockItem("transmission", ModBlocks.TRANSMISSION);
    public static final DeferredItem<BlockItem> STICK_SHIFTER = ITEMS.registerSimpleBlockItem("stick_shifter", ModBlocks.STICK_SHIFTER);
    public static final DeferredItem<BlockItem> ACCELERATOR = ITEMS.registerSimpleBlockItem("accelerator", ModBlocks.ACCELERATOR);
    public static final DeferredItem<BlockItem> ACE_ENGINE = ITEMS.registerSimpleBlockItem("ace_engine", ModBlocks.ACE_ENGINE);

    public static final DeferredItem<Item> MECHANICS_WRENCH = ITEMS.register(
            "mechanics_wrench",
            () -> new MechanicsWrenchItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
