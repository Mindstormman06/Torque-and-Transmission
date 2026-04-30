package com.mindstormman.torque_and_transmissions.datagen;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.block.AcceleratorBlock;
import com.mindstormman.torque_and_transmissions.content.block.StickShifterBlock;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateTorqueandTransmissions.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        var transmissionModel = cubeAll(ModBlocks.TRANSMISSION.get());
        getVariantBuilder(ModBlocks.TRANSMISSION.get())
                .forAllStates(state -> {
                    Direction.Axis axis = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS);
                    int xRot = axis == Direction.Axis.Y ? 0 : 90;
                    int yRot = axis == Direction.Axis.X ? 90 : 0;
                    return ConfiguredModel.builder()
                            .modelFile(transmissionModel)
                            .rotationX(xRot)
                            .rotationY(yRot)
                            .build();
                });
        itemModels().withExistingParent("transmission", modLoc("block/transmission"));

        var shifterModel = cubeAll(ModBlocks.STICK_SHIFTER.get());
        getVariantBuilder(ModBlocks.STICK_SHIFTER.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(StickShifterBlock.FACING);
                    int yRot = switch (facing) {
                        case NORTH -> 180;
                        case SOUTH -> 0;
                        case WEST -> 90;
                        case EAST -> 270;
                        default -> 0;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(shifterModel)
                            .rotationY(yRot)
                            .build();
                });
        itemModels().withExistingParent("stick_shifter", modLoc("block/stick_shifter"));

        var acceleratorModel = cubeAll(ModBlocks.ACCELERATOR.get());
        getVariantBuilder(ModBlocks.ACCELERATOR.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(AcceleratorBlock.FACING);
                    int yRot = switch (facing) {
                        case NORTH -> 180;
                        case SOUTH -> 0;
                        case WEST -> 90;
                        case EAST -> 270;
                        default -> 0;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(acceleratorModel)
                            .rotationY(yRot)
                            .build();
                });
        itemModels().withExistingParent("accelerator", modLoc("block/accelerator"));
    }
}
