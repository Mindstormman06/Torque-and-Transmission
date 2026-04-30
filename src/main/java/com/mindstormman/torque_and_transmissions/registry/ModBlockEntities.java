package com.mindstormman.torque_and_transmissions.registry;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.blockentity.StickShifterBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.TransmissionBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateTorqueandTransmissions.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransmissionBlockEntity>> TRANSMISSION = BLOCK_ENTITIES.register(
            "transmission",
            () -> BlockEntityType.Builder.of(TransmissionBlockEntity::new, ModBlocks.TRANSMISSION.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StickShifterBlockEntity>> STICK_SHIFTER = BLOCK_ENTITIES.register(
            "stick_shifter",
            () -> BlockEntityType.Builder.of(StickShifterBlockEntity::new, ModBlocks.STICK_SHIFTER.get()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
