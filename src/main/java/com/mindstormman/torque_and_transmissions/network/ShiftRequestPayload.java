package com.mindstormman.torque_and_transmissions.network;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ShiftRequestPayload(BlockPos shifterPos, int direction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShiftRequestPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreateTorqueandTransmissions.MODID, "shift_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShiftRequestPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ShiftRequestPayload::shifterPos,
            ByteBufCodecs.INT,
            ShiftRequestPayload::direction,
            ShiftRequestPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
