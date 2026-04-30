package com.mindstormman.torque_and_transmissions.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mindstormman.torque_and_transmissions.registry.ModBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider);
    }

    private static class ModBlockLoot extends BlockLootSubProvider {
        protected ModBlockLoot(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.TRANSMISSION.get());
            dropSelf(ModBlocks.STICK_SHIFTER.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return registries.lookupOrThrow(Registries.BLOCK)
                    .listElements()
                    .filter(reference -> "torque_and_transmissions".equals(reference.key().location().getNamespace()))
                    .map(reference -> reference.value())
                    .toList();
        }
    }
}
