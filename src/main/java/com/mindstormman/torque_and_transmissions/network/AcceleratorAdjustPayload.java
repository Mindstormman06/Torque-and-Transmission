package com.mindstormman.torque_and_transmissions.network;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AcceleratorAdjustPayload(BlockPos acceleratorPos, int direction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AcceleratorAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreateTorqueandTransmissions.MODID, "accelerator_adjust"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AcceleratorAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AcceleratorAdjustPayload::acceleratorPos,
            ByteBufCodecs.INT,
            AcceleratorAdjustPayload::direction,
            AcceleratorAdjustPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
