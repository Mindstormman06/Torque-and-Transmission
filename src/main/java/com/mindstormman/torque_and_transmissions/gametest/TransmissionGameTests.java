package com.mindstormman.torque_and_transmissions.gametest;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.blockentity.StickShifterBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.TransmissionBlockEntity;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void reverseGearFlipsRatioAndStressMultiplier(GameTestHelper helper) {
        BlockPos transmissionPos = new BlockPos(1, 1, 1);
        helper.setBlock(transmissionPos, ModBlocks.TRANSMISSION.get());

        TransmissionBlockEntity transmission = (TransmissionBlockEntity) helper.getBlockEntity(transmissionPos);
        transmission.shiftBy(-1);

        helper.succeedIf(() -> {
            if (!transmission.isReverse()) {
                throw new AssertionError("Expected reverse mode after shifting down from first gear.");
            }
            if (transmission.getEffectiveRatio() >= 0) {
                throw new AssertionError("Reverse ratio should be negative.");
            }
            if (transmission.getStressMultiplier() <= 0) {
                throw new AssertionError("Stress multiplier should stay positive.");
            }
        });
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void splitShaftModifierUsesSelectedGearRatio(GameTestHelper helper) {
        BlockPos transmissionPos = new BlockPos(1, 1, 1);
        helper.setBlock(transmissionPos, ModBlocks.TRANSMISSION.get());

        TransmissionBlockEntity transmission = (TransmissionBlockEntity) helper.getBlockEntity(transmissionPos);
        transmission.shiftBy(1);
        transmission.setSource(helper.absolutePos(transmissionPos.relative(Direction.WEST)));

        helper.succeedIf(() -> {
            float outgoingModifier = transmission.getRotationSpeedModifier(Direction.EAST);
            float incomingModifier = transmission.getRotationSpeedModifier(Direction.WEST);

            if (incomingModifier != 1.0F) {
                throw new AssertionError("Incoming source side should keep 1x speed modifier.");
            }
            if (Math.abs(outgoingModifier) <= 0.0F) {
                throw new AssertionError("Outgoing side should apply a non-zero gear ratio modifier.");
            }
        });
    }
}
