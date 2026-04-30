package com.mindstormman.torque_and_transmissions.content.blockentity;

import java.util.List;

import com.mindstormman.torque_and_transmissions.Config;
import com.mindstormman.torque_and_transmissions.mechanics.GearRatios;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TransmissionBlockEntity extends BlockEntity {
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

    public void shiftBy(int direction) {
        if (direction == 0) {
            return;
        }

        if (direction < 0 && selectedGear == 0 && !reverse) {
            reverse = true;
            markDirtyAndSync();
            return;
        }

        if (reverse && direction > 0) {
            reverse = false;
            markDirtyAndSync();
            return;
        }

        reverse = false;
        int maxForwardGear = GearRatios.getMaxForwardGearIndex();
        selectedGear = Math.clamp(selectedGear + Integer.signum(direction), 0, maxForwardGear);
        markDirtyAndSync();
    }

    private void markDirtyAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        selectedGear = tag.getInt("selectedGear");
        reverse = tag.getBoolean("reverse");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("selectedGear", selectedGear);
        tag.putBoolean("reverse", reverse);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("selectedGear", selectedGear);
        tag.putBoolean("reverse", reverse);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        selectedGear = tag.getInt("selectedGear");
        reverse = tag.getBoolean("reverse");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            selectedGear = tag.getInt("selectedGear");
            reverse = tag.getBoolean("reverse");
        }
    }
}
