package com.mindstormman.torque_and_transmissions.registry;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.block.AcceleratorBlock;
import com.mindstormman.torque_and_transmissions.content.block.AceEngineBlock;
import com.mindstormman.torque_and_transmissions.content.block.StickShifterBlock;
import com.mindstormman.torque_and_transmissions.content.block.TransmissionBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateTorqueandTransmissions.MODID);

    public static final DeferredBlock<Block> TRANSMISSION = BLOCKS.register(
            "transmission",
            () -> new TransmissionBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5F)
                            .sound(SoundType.NETHERITE_BLOCK)));

    public static final DeferredBlock<Block> STICK_SHIFTER = BLOCKS.register(
            "stick_shifter",
            () -> new StickShifterBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.5F)
                            .sound(SoundType.METAL)
                            .noOcclusion()));

    public static final DeferredBlock<Block> ACCELERATOR = BLOCKS.register(
            "accelerator",
            () -> new AcceleratorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(2.5F)
                            .sound(SoundType.METAL)
                            .noOcclusion()));

    public static final DeferredBlock<Block> ACE_ENGINE = BLOCKS.register(
            "ace_engine",
            () -> new AceEngineBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(4.0F)
                            .sound(SoundType.NETHERITE_BLOCK)));

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
