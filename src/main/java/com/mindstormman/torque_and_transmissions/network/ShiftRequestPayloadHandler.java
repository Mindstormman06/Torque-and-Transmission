package com.mindstormman.torque_and_transmissions.network;

import com.mindstormman.torque_and_transmissions.content.blockentity.StickShifterBlockEntity;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ShiftRequestPayloadHandler {
    private ShiftRequestPayloadHandler() {
    }

    public static void handle(ShiftRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.level().isLoaded(payload.shifterPos()) || player.distanceToSqr(
                    payload.shifterPos().getX() + 0.5D,
                    payload.shifterPos().getY() + 0.5D,
                    payload.shifterPos().getZ() + 0.5D) > 64.0D) {
                return;
            }

            if (player.level().getBlockEntity(payload.shifterPos()) instanceof StickShifterBlockEntity shifter) {
                shifter.applyShiftRequest(player, Integer.signum(payload.direction()));
            }
        });
    }
}
