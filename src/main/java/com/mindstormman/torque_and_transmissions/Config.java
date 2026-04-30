package com.mindstormman.torque_and_transmissions;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> GEAR_RATIOS = BUILDER
            .comment("Forward gear ratios in ascending order from low to high.")
            .defineListAllowEmpty("gearRatios", List.of(0.2D, 0.35D, 0.5D, 0.75D, 1.0D), () -> 1.0D, Config::isPositiveNumber);

    public static final ModConfigSpec.DoubleValue REVERSE_RATIO = BUILDER
            .comment("Ratio magnitude used for reverse gear.")
            .defineInRange("reverseRatio", 0.2D, 0.01D, 10.0D);

    public static final ModConfigSpec.IntValue MAX_TARGET_RPM = BUILDER
            .comment("Maximum target RPM used by accelerator-style controls.")
            .defineInRange("maxTargetRpm", 256, 1, 2048);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean isPositiveNumber(final Object obj) {
        if (!(obj instanceof Number number)) {
            return false;
        }
        return number.doubleValue() > 0.0D;
    }
}
