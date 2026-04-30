package com.mindstormman.torque_and_transmissions.registry;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.network.ShiftRequestPayload;
import com.mindstormman.torque_and_transmissions.network.ShiftRequestPayloadHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CreateTorqueandTransmissions.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus eventBus) {
        // Registration is handled via @EventBusSubscriber to keep networking isolated.
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ShiftRequestPayload.TYPE, ShiftRequestPayload.STREAM_CODEC, ShiftRequestPayloadHandler::handle);
    }
}
