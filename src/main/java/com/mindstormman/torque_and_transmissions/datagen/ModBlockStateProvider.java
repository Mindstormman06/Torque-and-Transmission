package com.mindstormman.torque_and_transmissions.datagen;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateTorqueandTransmissions.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.TRANSMISSION.get(), cubeAll(ModBlocks.TRANSMISSION.get()));
        simpleBlockWithItem(ModBlocks.STICK_SHIFTER.get(), cubeAll(ModBlocks.STICK_SHIFTER.get()));
    }
}
