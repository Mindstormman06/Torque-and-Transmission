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
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionBlockEntity extends SplitShaftBlockEntity {
    private int selectedGear;
    private boolean reverse;

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

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) {
            return 1.0F;
        }

        Direction sourceFacing = getSourceFacing();
        if (face == sourceFacing) {
            return 1.0F;
        }
        return (float) getEffectiveRatio();
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
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        selectedGear = tag.getInt("selectedGear");
        reverse = tag.getBoolean("reverse");
    }
}
