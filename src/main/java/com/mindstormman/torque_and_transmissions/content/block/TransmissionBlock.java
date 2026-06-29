package com.mindstormman.torque_and_transmissions.content.block;

import com.mindstormman.torque_and_transmissions.content.blockentity.TransmissionBlockEntity;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

public class TransmissionBlock extends AbstractEncasedShaftBlock implements IBE<TransmissionBlockEntity> {
    public TransmissionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = getPreferredAxis(context);
        Direction.Axis axis;

        if (preferredAxis != null) {
            axis = preferredAxis;
        } else {
            Direction clickedFace = context.getClickedFace();
            if (clickedFace.getAxis() == Direction.Axis.Y) {
                // When placing on floor/ceiling, default to a horizontal shaft axis.
                axis = context.getHorizontalDirection().getAxis();
            } else {
                axis = clickedFace.getAxis();
            }
        }

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            axis = context.getClickedFace().getAxis();
        }

        return defaultBlockState().setValue(AXIS, axis);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof TransmissionBlockEntity transmission && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(
                    Component.translatable(
                            "message.torque_and_transmissions.transmission_status",
                            transmission.getGearLabel(),
                            String.format("%.2f", transmission.getAppliedRatio()),
                            String.format("%.2f", transmission.getStressMultiplier()),
                            transmission.getEffectiveOutputRpm(),
                            transmission.getThrottlePercent(),
                            transmission.getAxisLabel(),
                            transmission.getSourceLabel()),
                    true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public Class<TransmissionBlockEntity> getBlockEntityClass() {
        return TransmissionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransmissionBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TRANSMISSION.get();
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TransmissionBlockEntity transmission) {
            return transmission.isReverse() ? 0 : Math.min(15, transmission.getSelectedGear() + 1);
        }
        return 0;
    }
}
