package com.mindstormman.torque_and_transmissions.gametest;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.content.blockentity.AcceleratorBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.AceEngineBlockEntity;
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

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void acceleratorLinkAndRpmAffectsTransmissionOutput(GameTestHelper helper) {
        BlockPos transmissionPos = new BlockPos(1, 1, 1);
        BlockPos acceleratorPos = new BlockPos(2, 1, 1);

        helper.setBlock(transmissionPos, ModBlocks.TRANSMISSION.get());
        helper.setBlock(acceleratorPos, ModBlocks.ACCELERATOR.get());

        TransmissionBlockEntity transmission = (TransmissionBlockEntity) helper.getBlockEntity(transmissionPos);
        AcceleratorBlockEntity accelerator = (AcceleratorBlockEntity) helper.getBlockEntity(acceleratorPos);

        accelerator.setLinkedTransmissionPos(helper.absolutePos(transmissionPos));
        accelerator.setTargetRpm(200);
        transmission.shiftBy(1);

        helper.succeedIf(() -> {
            if (accelerator.getTargetRpm() != 200) {
                throw new AssertionError("Accelerator target RPM should remain set.");
            }
            if (transmission.getInputTargetRpm() != 200) {
                throw new AssertionError("Transmission should receive linked accelerator target RPM.");
            }
            if (transmission.getEffectiveOutputRpm() <= 0) {
                throw new AssertionError("Transmission output RPM should be positive in forward gear.");
            }
        });
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void aceNoLoadRampUsesClutchEngagedFactor(GameTestHelper helper) {
        float next = AceEngineBlockEntity.computeNextRpm(0.0F, 256.0F, 8.0F, 1.0D, 5.0D, true);
        helper.succeedIf(() -> {
            if (Math.abs(next - 8.0F) > 0.001F) {
                throw new AssertionError("Expected no-load ramp to use full horsepower step when clutch is engaged.");
            }
        });
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void aceLoadedRampUsesGearLoadWhenClutchDisengaged(GameTestHelper helper) {
        float next = AceEngineBlockEntity.computeNextRpm(0.0F, 256.0F, 8.0F, 1.0D, 4.0D, false);
        helper.succeedIf(() -> {
            if (Math.abs(next - 2.0F) > 0.001F) {
                throw new AssertionError("Expected loaded ramp to divide horsepower by gear load factor.");
            }
        });
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void aceDecayUsesSameStepAsRamp(GameTestHelper helper) {
        float next = AceEngineBlockEntity.computeNextRpm(100.0F, 256.0F, 8.0F, 0.25D, 3.0D, true);
        helper.succeedIf(() -> {
            if (Math.abs(next - 92.0F) > 0.001F) {
                throw new AssertionError("Expected decay to use same horsepower step rule as acceleration.");
            }
        });
    }

    @GameTest(template = "empty5x5", templateNamespace = "minecraft")
    public static void aceRpmClampsToMax(GameTestHelper helper) {
        float next = AceEngineBlockEntity.computeNextRpm(250.0F, 256.0F, 20.0F, 1.0D, 1.0D, true);
        helper.succeedIf(() -> {
            if (Math.abs(next - 256.0F) > 0.001F) {
                throw new AssertionError("Expected ACE RPM to clamp to configured maximum.");
            }
        });
    }
}
