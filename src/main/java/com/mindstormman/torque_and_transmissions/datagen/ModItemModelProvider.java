package com.mindstormman.torque_and_transmissions.datagen;

import com.mindstormman.torque_and_transmissions.CreateTorqueandTransmissions;
import com.mindstormman.torque_and_transmissions.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateTorqueandTransmissions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(ModItems.TRANSMISSION.getId().getPath(), modLoc("block/" + ModItems.TRANSMISSION.getId().getPath()));
        withExistingParent(ModItems.STICK_SHIFTER.getId().getPath(), modLoc("block/" + ModItems.STICK_SHIFTER.getId().getPath()));
        withExistingParent(ModItems.ACCELERATOR.getId().getPath(), modLoc("block/" + ModItems.ACCELERATOR.getId().getPath()));
        singleTexture(
                ModItems.MECHANICS_WRENCH.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0",
                modLoc("item/" + ModItems.MECHANICS_WRENCH.getId().getPath()));
    }
}
