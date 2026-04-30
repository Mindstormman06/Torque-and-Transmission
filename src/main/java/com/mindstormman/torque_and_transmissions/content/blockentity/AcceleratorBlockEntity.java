package com.mindstormman.torque_and_transmissions.content.blockentity;

import java.util.Optional;

import com.mindstormman.torque_and_transmissions.Config;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AcceleratorBlockEntity extends BlockEntity {
    private static final int DEFAULT_RPM_STEP = 64;

    private int targetRpm;
    private BlockPos linkedTransmissionPos;

    public AcceleratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ACCELERATOR.get(), pos, blockState);
    }

    public int getTargetRpm() {
        return targetRpm;
    }

    public Optional<BlockPos> getLinkedTransmissionPos() {
        return Optional.ofNullable(linkedTransmissionPos);
    }

    public void setLinkedTransmissionPos(BlockPos pos) {
        linkedTransmissionPos = pos;
        pushTargetRpmToTransmission();
        markDirtyAndSync();
    }

    public void setTargetRpm(int rpm) {
        int clamped = Mth.clamp(rpm, 0, Config.MAX_TARGET_RPM.get());
        if (targetRpm == clamped) {
            return;
        }
        targetRpm = clamped;
        pushTargetRpmToTransmission();
        markDirtyAndSync();
    }

    public void applyRpmDelta(ServerPlayer player, int delta) {
        if (level == null || linkedTransmissionPos == null) {
            player.displayClientMessage(Component.translatable("message.torque_and_transmissions.accelerator_not_linked"), true);
            return;
        }

        int step = delta == 0 ? 0 : Integer.signum(delta) * DEFAULT_RPM_STEP;
        setTargetRpm(targetRpm + step);
        int percent = (int) Math.round((targetRpm * 100.0D) / Math.max(1, Config.MAX_TARGET_RPM.get()));
        player.displayClientMessage(
                Component.translatable("message.torque_and_transmissions.accelerator_set_rpm", targetRpm, percent),
                true);
    }

    private void pushTargetRpmToTransmission() {
        if (level == null || linkedTransmissionPos == null) {
            return;
        }
        if (level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission) {
            transmission.setInputTargetRpm(targetRpm);
        }
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
        targetRpm = tag.getInt("targetRpm");
        if (tag.contains("linkedTransmission")) {
            linkedTransmissionPos = BlockPos.of(tag.getLong("linkedTransmission"));
        } else {
            linkedTransmissionPos = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("targetRpm", targetRpm);
        if (linkedTransmissionPos != null) {
            tag.putLong("linkedTransmission", linkedTransmissionPos.asLong());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("targetRpm", targetRpm);
        if (linkedTransmissionPos != null) {
            tag.putLong("linkedTransmission", linkedTransmissionPos.asLong());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        targetRpm = tag.getInt("targetRpm");
        if (tag.contains("linkedTransmission")) {
            linkedTransmissionPos = BlockPos.of(tag.getLong("linkedTransmission"));
        } else {
            linkedTransmissionPos = null;
        }
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
            targetRpm = tag.getInt("targetRpm");
            if (tag.contains("linkedTransmission")) {
                linkedTransmissionPos = BlockPos.of(tag.getLong("linkedTransmission"));
            }
        }
    }
}
