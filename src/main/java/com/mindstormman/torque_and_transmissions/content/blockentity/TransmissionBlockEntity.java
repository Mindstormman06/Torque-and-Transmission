package com.mindstormman.torque_and_transmissions.content.blockentity;

import java.util.List;

import com.mindstormman.torque_and_transmissions.Config;
import com.mindstormman.torque_and_transmissions.content.block.TransmissionBlock;
import com.mindstormman.torque_and_transmissions.mechanics.GearRatios;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionBlockEntity extends SplitShaftBlockEntity {
    private int selectedGear;
    private boolean reverse;
    private int inputTargetRpm;
    private boolean acceleratorControlled;
    private boolean aceLinked;

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

    public double getStressMultiplier() {
        double ratioMagnitude = Math.abs(getEffectiveRatio());
        if (ratioMagnitude <= 0.0D) {
            return 1.0D;
        }
        return 1.0D / ratioMagnitude;
    }

    public int getInputTargetRpm() {
        return inputTargetRpm;
    }

    public int getEffectiveOutputRpm() {
        return (int) Math.round(getInputTargetRpm() * getEffectiveRatio());
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

    public void setInputTargetRpm(int rpm) {
        int clamped = Math.clamp(rpm, 0, Config.MAX_TARGET_RPM.get());
        if (acceleratorControlled && inputTargetRpm == clamped) {
            return;
        }
        acceleratorControlled = true;
        inputTargetRpm = clamped;
        markDirtyAndSync();
        if (!aceLinked) {
            requestKineticRefresh();
        }
    }

    public void setAceLinked(boolean linked) {
        if (aceLinked == linked) {
            return;
        }
        aceLinked = linked;
        markDirtyAndSync();
        requestKineticRefresh();
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
        return (float) (getEffectiveRatio() * throttleMultiplier);
    }

    public void shiftBy(int direction) {
        if (direction == 0) {
            return;
        }

        if (direction < 0 && selectedGear == 0 && !reverse) {
            reverse = true;
            markDirtyAndSync();
            requestKineticRefresh();
            return;
        }

        if (reverse && direction > 0) {
            reverse = false;
            markDirtyAndSync();
            requestKineticRefresh();
            return;
        }

        reverse = false;
        int maxForwardGear = GearRatios.getMaxForwardGearIndex();
        selectedGear = Math.clamp(selectedGear + Integer.signum(direction), 0, maxForwardGear);
        markDirtyAndSync();
        requestKineticRefresh();
    }

    private void markDirtyAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void requestKineticRefresh() {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (getBlockState().getBlock() instanceof TransmissionBlock transmissionBlock) {
            transmissionBlock.refreshKineticNetwork(level, getBlockPos());
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putInt("selectedGear", selectedGear);
        tag.putBoolean("reverse", reverse);
        tag.putInt("inputTargetRpm", inputTargetRpm);
        tag.putBoolean("acceleratorControlled", acceleratorControlled);
        tag.putBoolean("aceLinked", aceLinked);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        selectedGear = tag.getInt("selectedGear");
        reverse = tag.getBoolean("reverse");
        inputTargetRpm = tag.getInt("inputTargetRpm");
        acceleratorControlled = tag.getBoolean("acceleratorControlled");
        aceLinked = tag.getBoolean("aceLinked");
    }
}
