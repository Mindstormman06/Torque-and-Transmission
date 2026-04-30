package com.mindstormman.torque_and_transmissions.network;

import com.mindstormman.torque_and_transmissions.content.blockentity.AcceleratorBlockEntity;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class AcceleratorAdjustPayloadHandler {
    private AcceleratorAdjustPayloadHandler() {
    }

    public static void handle(AcceleratorAdjustPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!player.level().isLoaded(payload.acceleratorPos()) || player.distanceToSqr(
                    payload.acceleratorPos().getX() + 0.5D,
                    payload.acceleratorPos().getY() + 0.5D,
                    payload.acceleratorPos().getZ() + 0.5D) > 64.0D) {
                return;
            }

            if (player.level().getBlockEntity(payload.acceleratorPos()) instanceof AcceleratorBlockEntity accelerator) {
                accelerator.applyRpmDelta(player, payload.direction());
            }
        });
    }
}
