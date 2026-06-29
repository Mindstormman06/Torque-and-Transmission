package com.mindstormman.torque_and_transmissions.content.block;

import com.mindstormman.torque_and_transmissions.content.blockentity.AceEngineBlockEntity;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AceEngineBlock extends DirectionalKineticBlock implements IBE<AceEngineBlockEntity> {
    public AceEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof AceEngineBlockEntity ace && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(
                    Component.translatable(
                            "message.torque_and_transmissions.ace_status",
                            String.format("%.1f", ace.getCurrentRpm()),
                            String.format("%.1f", ace.getTargetRpm()),
                            String.format("%.1f", ace.getHorsepower()),
                            String.format("%.2f", ace.getThrottlePosition()),
                            String.format("%.2f", ace.getGearLoadFactor()),
                            ace.isClutchEngaged() ? "engaged" : "disengaged"),
                    true);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<AceEngineBlockEntity> getBlockEntityClass() {
        return AceEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AceEngineBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ACE_ENGINE.get();
    }
}
