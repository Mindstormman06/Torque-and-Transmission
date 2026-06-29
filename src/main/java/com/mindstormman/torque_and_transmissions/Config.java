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

    public static final ModConfigSpec.IntValue ACE_MAX_RPM = BUILDER
            .comment("Maximum RPM cap for the ACE source engine.")
            .defineInRange("aceMaxRpm", 256, 1, 2048);

    public static final ModConfigSpec.DoubleValue ACE_HORSEPOWER = BUILDER
            .comment("Base ACE acceleration/deceleration step at load factor 1.0 (RPM per tick).")
            .defineInRange("aceHorsepower", 4.0D, 0.1D, 512.0D);

    public static final ModConfigSpec.DoubleValue GEAR_RATIO_BLEND_RATE = BUILDER
            .comment("Fraction of the remaining ratio error applied each tick when shifting (0.01-1.0). Lower values shift more gradually.")
            .defineInRange("gearRatioBlendRate", 0.08D, 0.01D, 1.0D);

    public static final ModConfigSpec.BooleanValue REQUIRE_CLUTCH_FOR_SHIFT = BUILDER
            .comment("When true, the linked Create clutch must be powered (disengaged) before gears can change.")
            .define("requireClutchForShift", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean isPositiveNumber(final Object obj) {
        if (!(obj instanceof Number number)) {
            return false;
        }
        return number.doubleValue() > 0.0D;
    }
}
