package com.mindstormman.torque_and_transmissions.mechanics;

import java.util.List;

import com.mindstormman.torque_and_transmissions.Config;

public final class GearRatios {
    private GearRatios() {
    }

    public static List<Double> getForwardRatios() {
        return Config.GEAR_RATIOS.get()
                .stream()
                .map(Number::doubleValue)
                .filter(value -> value > 0.0D)
                .toList();
    }

    public static int getMaxForwardGearIndex() {
        return Math.max(0, getForwardRatios().size() - 1);
    }
}
