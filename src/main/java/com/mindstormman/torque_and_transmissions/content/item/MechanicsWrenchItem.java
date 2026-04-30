package com.mindstormman.torque_and_transmissions.content.item;

import java.util.Optional;

import com.mindstormman.torque_and_transmissions.content.blockentity.AcceleratorBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.StickShifterBlockEntity;
import com.mindstormman.torque_and_transmissions.content.blockentity.TransmissionBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MechanicsWrenchItem extends Item {
    private static final String LINKED_TRANSMISSION_TAG = "linkedTransmission";

    public MechanicsWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.getBlockEntity(clickedPos) instanceof TransmissionBlockEntity) {
            storeTransmissionPos(stack, clickedPos);
            if (!level.isClientSide() && context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable("message.torque_and_transmissions.wrench_stored"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (level.getBlockEntity(clickedPos) instanceof StickShifterBlockEntity shifter) {
            Optional<BlockPos> storedPos = getStoredTransmissionPos(stack);
            if (storedPos.isEmpty()) {
                if (!level.isClientSide() && context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(Component.translatable("message.torque_and_transmissions.wrench_missing_target"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            if (!level.isClientSide() && context.getPlayer() != null) {
                shifter.setLinkedTransmissionPos(storedPos.get());
                context.getPlayer().displayClientMessage(Component.translatable("message.torque_and_transmissions.wrench_linked"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (level.getBlockEntity(clickedPos) instanceof AcceleratorBlockEntity accelerator) {
            Optional<BlockPos> storedPos = getStoredTransmissionPos(stack);
            if (storedPos.isEmpty()) {
                if (!level.isClientSide() && context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(Component.translatable("message.torque_and_transmissions.wrench_missing_target"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            if (!level.isClientSide() && context.getPlayer() != null) {
                accelerator.setLinkedTransmissionPos(storedPos.get());
                context.getPlayer().displayClientMessage(Component.translatable("message.torque_and_transmissions.wrench_linked_accelerator"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useOn(context);
    }

    private static void storeTransmissionPos(ItemStack stack, BlockPos pos) {
        CompoundTag dataTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        dataTag.putLong(LINKED_TRANSMISSION_TAG, pos.asLong());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(dataTag));
    }

    private static Optional<BlockPos> getStoredTransmissionPos(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }
        CompoundTag dataTag = customData.copyTag();
        if (!dataTag.contains(LINKED_TRANSMISSION_TAG)) {
            return Optional.empty();
        }
        return Optional.of(BlockPos.of(dataTag.getLong(LINKED_TRANSMISSION_TAG)));
    }
}
