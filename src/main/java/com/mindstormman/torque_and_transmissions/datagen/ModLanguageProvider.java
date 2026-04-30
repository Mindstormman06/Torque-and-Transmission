package com.mindstormman.torque_and_transmissions.datagen;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.registry.ModBlocks;
import com.mindstormman.torque_and_transmissions.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, CreateTorqueandTransmissions.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.torque_and_transmissions.main", "Create: Torque and Transmissions");

        addBlock(ModBlocks.TRANSMISSION, "Transmission");
        addBlock(ModBlocks.STICK_SHIFTER, "Stick Shifter");
        addItem(ModItems.MECHANICS_WRENCH, "Mechanic's Wrench");

        add("message.torque_and_transmissions.shifter_not_linked", "Shifter is not linked to a transmission.");
        add("message.torque_and_transmissions.link_target_missing", "Linked transmission is missing.");
        add("message.torque_and_transmissions.shifted", "Shifted to %s (ratio %s)");
        add("message.torque_and_transmissions.transmission_status", "Transmission %s (ratio %s)");
        add("message.torque_and_transmissions.wrench_stored", "Stored transmission link target.");
        add("message.torque_and_transmissions.wrench_missing_target", "Wrench does not contain a saved transmission.");
        add("message.torque_and_transmissions.wrench_linked", "Linked shifter to saved transmission.");

        add("torque_and_transmissions.configuration.title", "Create: Torque and Transmissions Configs");
        add("torque_and_transmissions.configuration.section.torque_and_transmissions.common.toml", "Gameplay");
        add("torque_and_transmissions.configuration.section.torque_and_transmissions.common.toml.title", "Create: Torque and Transmissions");
        add("torque_and_transmissions.configuration.gearRatios", "Forward Gear Ratios");
        add("torque_and_transmissions.configuration.reverseRatio", "Reverse Gear Ratio");
        add("torque_and_transmissions.configuration.maxTargetRpm", "Maximum Target RPM");
    }
}
