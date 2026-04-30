package com.mindstormman.torque_and_transmissions.content.blockentity;

import java.util.Optional;

import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StickShifterBlockEntity extends BlockEntity {
    private BlockPos linkedTransmissionPos;

    public StickShifterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STICK_SHIFTER.get(), pos, blockState);
    }

    public Optional<BlockPos> getLinkedTransmissionPos() {
        return Optional.ofNullable(linkedTransmissionPos);
    }

    public void setLinkedTransmissionPos(BlockPos pos) {
        linkedTransmissionPos = pos;
        markDirtyAndSync();
    }

    public boolean applyShiftRequest(ServerPlayer player, int direction) {
        if (level == null || linkedTransmissionPos == null) {
            player.displayClientMessage(Component.translatable("message.torque_and_transmissions.shifter_not_linked"), true);
            return false;
        }

        if (!(level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission)) {
            player.displayClientMessage(Component.translatable("message.torque_and_transmissions.link_target_missing"), true);
            return false;
        }

        transmission.shiftBy(direction);
        player.displayClientMessage(
                Component.translatable(
                        "message.torque_and_transmissions.shifted",
                        transmission.getGearLabel(),
                        String.format("%.2f", transmission.getEffectiveRatio()),
                        String.format("%.2f", transmission.getStressMultiplier())),
                true);
        return true;
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
        if (tag.contains("linkedTransmission")) {
            linkedTransmissionPos = BlockPos.of(tag.getLong("linkedTransmission"));
        } else {
            linkedTransmissionPos = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (linkedTransmissionPos != null) {
            tag.putLong("linkedTransmission", linkedTransmissionPos.asLong());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (linkedTransmissionPos != null) {
            tag.putLong("linkedTransmission", linkedTransmissionPos.asLong());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
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
        if (tag != null && tag.contains("linkedTransmission")) {
            linkedTransmissionPos = BlockPos.of(tag.getLong("linkedTransmission"));
        }
    }
}
