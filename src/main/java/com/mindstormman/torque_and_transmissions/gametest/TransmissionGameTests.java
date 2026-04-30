package com.mindstormman.torque_and_transmissions.gametest;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.blockentity.StickShifterBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.TransmissionBlockEntity;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(CreateTorqueandTransmissions.MODID)
@PrefixGameTestTemplate(false)
public final class TransmissionGameTests {
    private TransmissionGameTests() {
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void wrenchLinkAndShiftAppliesGear(GameTestHelper helper) {
        BlockPos transmissionPos = new BlockPos(1, 1, 1);
        BlockPos shifterPos = new BlockPos(2, 1, 1);

        helper.setBlock(transmissionPos, ModBlocks.TRANSMISSION.get());
        helper.setBlock(shifterPos, ModBlocks.STICK_SHIFTER.get());

        TransmissionBlockEntity transmission = (TransmissionBlockEntity) helper.getBlockEntity(transmissionPos);
        StickShifterBlockEntity shifter = (StickShifterBlockEntity) helper.getBlockEntity(shifterPos);

        shifter.setLinkedTransmissionPos(helper.absolutePos(transmissionPos));
        transmission.shiftBy(1);

        helper.succeedIf(() -> {
            if (transmission.getSelectedGear() <= 0) {
                throw new AssertionError("Expected transmission to shift to at least first forward gear.");
            }
        });
    }
}
