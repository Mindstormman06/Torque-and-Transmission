package com.mindstormman.torque_and_transmissions.content.blockentity;

import java.util.List;

import com.mindstormman.torque_and_transmissions.Config;
import com.mindstormman.torque_and_transmissions.mechanics.GearRatios;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionBlockEntity extends SplitShaftBlockEntity {
    private static final double RATIO_EPSILON = 0.0001D;

    private int selectedGear;
    private boolean reverse;
    private int inputTargetRpm;
    private boolean acceleratorControlled;
    private boolean aceLinked;
    private double appliedRatio = 1.0D;
    private BlockPos linkedClutchPos;

    public TransmissionBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TRANSMISSION.get(), pos, blockState);
    }

    public int getSelectedGear() {
        return selectedGear;
    }

    public boolean isReverse() {
        return reverse;
    }

    public String getGearLabel() {
        if (reverse) {
            return "R";
        }
        return "G" + (selectedGear + 1);
    }

    public double getEffectiveRatio() {
        if (reverse) {
            return -Config.REVERSE_RATIO.get();
        }

        List<Double> ratios = GearRatios.getForwardRatios();
        if (ratios.isEmpty()) {
            return 1.0D;
        }
        int index = Math.min(selectedGear, ratios.size() - 1);
        return ratios.get(index);
    }

    public double getAppliedRatio() {
        return appliedRatio;
    }

    public double getStressMultiplier() {
        double ratioMagnitude = Math.abs(appliedRatio);
        if (ratioMagnitude <= 0.0D) {
            return 1.0D;
        }
        return 1.0D / ratioMagnitude;
    }

    public int getInputTargetRpm() {
        return inputTargetRpm;
    }

    public int getEffectiveOutputRpm() {
        return (int) Math.round(getInputTargetRpm() * appliedRatio);
    }

    public int getThrottlePercent() {
        return (int) Math.round(getThrottleFactor() * 100.0D);
    }

    public double getThrottlePosition() {
        return getThrottleFactor();
    }

    public double getGearLoadFactor() {
        if (reverse) {
            return 1.0D;
        }
        return Math.max(1.0D, selectedGear + 1.0D);
    }

    public String getAxisLabel() {
        Direction.Axis axis = getBlockState().getValue(BlockStateProperties.AXIS);
        return switch (axis) {
            case X -> "east-west";
            case Z -> "north-south";
            case Y -> "vertical";
        };
    }

    public String getSourceLabel() {
        if (!hasSource()) {
            return "none";
        }
        return getSourceFacing().getName();
    }

    public void setLinkedClutchPos(BlockPos pos) {
        linkedClutchPos = pos;
        markDirtyAndSync();
    }

    public boolean isClutchReadyForShift() {
        if (level == null || linkedClutchPos == null) {
            return false;
        }
        BlockState clutchState = level.getBlockState(linkedClutchPos);
        if (!(clutchState.getBlock() instanceof ClutchBlock)) {
            return false;
        }
        return clutchState.getValue(BlockStateProperties.POWERED);
    }

    public void setInputTargetRpm(int rpm) {
        int clamped = Math.clamp(rpm, 0, Config.MAX_TARGET_RPM.get());
        if (acceleratorControlled && inputTargetRpm == clamped) {
            return;
        }
        acceleratorControlled = true;
        inputTargetRpm = clamped;
        markDirtyAndSync();
        if (!aceLinked) {
            requestSpeedUpdate();
        }
    }

    public void setAceLinked(boolean linked) {
        if (aceLinked == linked) {
            return;
        }
        aceLinked = linked;
        markDirtyAndSync();
        requestSpeedUpdate();
    }

    private double getThrottleFactor() {
        if (!acceleratorControlled) {
            return 1.0D;
        }
        int maxRpm = Math.max(1, Config.MAX_TARGET_RPM.get());
        return Math.clamp((double) inputTargetRpm / maxRpm, 0.0D, 1.0D);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) {
            return 1.0F;
        }

        Direction sourceFacing = getSourceFacing();
        if (face == sourceFacing) {
            return 1.0F;
        }
        double throttleMultiplier = aceLinked ? 1.0D : getThrottleFactor();
        return (float) (appliedRatio * throttleMultiplier);
    }

    public boolean shiftBy(int direction) {
        if (direction == 0) {
            return false;
        }

        if (Config.REQUIRE_CLUTCH_FOR_SHIFT.get() && !isClutchReadyForShift()) {
            return false;
        }

        if (direction < 0 && selectedGear == 0 && !reverse) {
            reverse = true;
            markDirtyAndSync();
            return true;
        }

        if (reverse && direction > 0) {
            reverse = false;
            markDirtyAndSync();
            return true;
        }

        reverse = false;
        int maxForwardGear = GearRatios.getMaxForwardGearIndex();
        int nextGear = Math.clamp(selectedGear + Integer.signum(direction), 0, maxForwardGear);
        if (nextGear == selectedGear) {
            return false;
        }
        selectedGear = nextGear;
        markDirtyAndSync();
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) {
            return;
        }

        double targetRatio = getEffectiveRatio();
        if (Math.abs(appliedRatio - targetRatio) <= RATIO_EPSILON) {
            if (appliedRatio != targetRatio) {
                appliedRatio = targetRatio;
                requestSpeedUpdate();
            }
            return;
        }

        double previousApplied = appliedRatio;
        appliedRatio += (targetRatio - appliedRatio) * Config.GEAR_RATIO_BLEND_RATE.get();
        if (Math.abs(appliedRatio - previousApplied) > RATIO_EPSILON) {
            requestSpeedUpdate();
            setChanged();
        }
    }

    private void markDirtyAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void requestSpeedUpdate() {
        if (level == null || level.isClientSide()) {
            return;
        }
        updateSpeed = true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("selectedGear", selectedGear);
        tag.putBoolean("reverse", reverse);
        tag.putInt("inputTargetRpm", inputTargetRpm);
        tag.putBoolean("acceleratorControlled", acceleratorControlled);
        tag.putBoolean("aceLinked", aceLinked);
        tag.putDouble("appliedRatio", appliedRatio);
        if (linkedClutchPos != null) {
            tag.putLong("linkedClutch", linkedClutchPos.asLong());
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        selectedGear = tag.getInt("selectedGear");
        reverse = tag.getBoolean("reverse");
        inputTargetRpm = tag.getInt("inputTargetRpm");
        acceleratorControlled = tag.getBoolean("acceleratorControlled");
        aceLinked = tag.getBoolean("aceLinked");
        appliedRatio = tag.contains("appliedRatio") ? tag.getDouble("appliedRatio") : getEffectiveRatio();
        linkedClutchPos = tag.contains("linkedClutch") ? BlockPos.of(tag.getLong("linkedClutch")) : null;
    }
}
