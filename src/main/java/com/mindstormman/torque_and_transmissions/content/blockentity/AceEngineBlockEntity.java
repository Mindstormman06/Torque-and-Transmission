package com.mindstormman.torque_and_transmissions.content.blockentity;

import com.mindstormman.torque_and_transmissions.Config;
import com.mindstormman.torque_and_transmissions.registry.ModBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AceEngineBlockEntity extends GeneratingKineticBlockEntity {
    private static final int NETWORK_UPDATE_INTERVAL_TICKS = 5;
    private static final float NETWORK_UPDATE_MIN_DELTA = 4.0F;

    private float currentRpm;
    private float publishedRpm;
    private BlockPos linkedTransmissionPos;
    private BlockPos linkedClutchPos;

    public AceEngineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACE_ENGINE.get(), pos, state);
    }

    public float getCurrentRpm() {
        return currentRpm;
    }

    public float getTargetRpm() {
        return (float) (Config.ACE_MAX_RPM.get() * getThrottlePosition());
    }

    public float getHorsepower() {
        return Config.ACE_HORSEPOWER.get().floatValue();
    }

    public double getThrottlePosition() {
        if (level == null || linkedTransmissionPos == null) {
            return 0.0D;
        }
        if (level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission) {
            return transmission.getThrottlePosition();
        }
        return 0.0D;
    }

    public double getGearLoadFactor() {
        if (level == null || linkedTransmissionPos == null) {
            return 1.0D;
        }
        if (level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission) {
            return transmission.getGearLoadFactor();
        }
        return 1.0D;
    }

    public boolean isClutchEngaged() {
        if (level == null || linkedClutchPos == null) {
            return false;
        }
        BlockState clutchState = level.getBlockState(linkedClutchPos);
        if (!(clutchState.getBlock() instanceof ClutchBlock)) {
            return false;
        }
        return clutchState.getValue(BlockStateProperties.POWERED);
    }

    public void setLinkedTransmissionPos(BlockPos pos) {
        if (level != null && linkedTransmissionPos != null && !linkedTransmissionPos.equals(pos)
                && level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity previousTransmission) {
            previousTransmission.setAceLinked(false);
        }

        linkedTransmissionPos = pos;

        if (level != null && level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission) {
            transmission.setAceLinked(true);
        }

        setChanged();
        sendData();
    }

    public void setLinkedClutchPos(BlockPos pos) {
        linkedClutchPos = pos;
        setChanged();
        sendData();
    }

    public static float computeNextRpm(float currentRpm, float maxRpm, float horsepower, double throttlePosition, double gearLoadFactor, boolean isClutchEngaged) {
        float targetRpm = (float) (maxRpm * Mth.clamp(throttlePosition, 0.0D, 1.0D));
        double activeLoadFactor = isClutchEngaged ? 1.0D : Math.max(1.0D, gearLoadFactor);
        float rpmStep = (float) (horsepower / activeLoadFactor);

        float next = currentRpm;
        if (currentRpm < targetRpm) {
            next = Math.min(targetRpm, currentRpm + rpmStep);
        } else if (currentRpm > targetRpm) {
            next = Math.max(targetRpm, currentRpm - rpmStep);
        }
        return Mth.clamp(next, 0.0F, maxRpm);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) {
            return;
        }

        float nextRpm = computeNextRpm(
                currentRpm,
                Config.ACE_MAX_RPM.get(),
                Config.ACE_HORSEPOWER.get().floatValue(),
                getThrottlePosition(),
                getGearLoadFactor(),
                isClutchEngaged());

        if (Math.abs(nextRpm - currentRpm) > 0.0001F) {
            float previousCurrent = currentRpm;
            currentRpm = nextRpm;
            boolean reachedTarget = Math.abs(currentRpm - getTargetRpm()) <= 0.0001F;
            boolean shouldPublishNow = level.getGameTime() % NETWORK_UPDATE_INTERVAL_TICKS == 0 || reachedTarget
                    || previousCurrent == 0.0F || currentRpm == 0.0F;
            boolean hasMeaningfulDelta = Math.abs(currentRpm - publishedRpm) >= NETWORK_UPDATE_MIN_DELTA
                    || reachedTarget || currentRpm == 0.0F;

            if (shouldPublishNow && hasMeaningfulDelta) {
                publishedRpm = currentRpm;
                float theoreticalSpeed = getTheoreticalSpeed();
                if (!hasSource() || Math.abs(getGeneratedSpeed()) > Math.abs(theoreticalSpeed) || publishedRpm == 0.0F) {
                    updateGeneratedRotation();
                    sendData();
                }
            }

            setChanged();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed()) {
            publishedRpm = currentRpm;
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return publishedRpm;
    }

    @Override
    public float calculateAddedStressCapacity() {
        return Math.max(0.0F, Config.ACE_HORSEPOWER.get().floatValue() * 64.0F);
    }

    @Override
    public float calculateStressApplied() {
        return 0.0F;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("currentRpm", currentRpm);
        tag.putFloat("publishedRpm", publishedRpm);
        if (linkedTransmissionPos != null) {
            tag.putLong("linkedTransmission", linkedTransmissionPos.asLong());
        }
        if (linkedClutchPos != null) {
            tag.putLong("linkedClutch", linkedClutchPos.asLong());
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentRpm = tag.getFloat("currentRpm");
        publishedRpm = tag.contains("publishedRpm") ? tag.getFloat("publishedRpm") : currentRpm;
        linkedTransmissionPos = tag.contains("linkedTransmission") ? BlockPos.of(tag.getLong("linkedTransmission")) : null;
        linkedClutchPos = tag.contains("linkedClutch") ? BlockPos.of(tag.getLong("linkedClutch")) : null;

        if (level != null && !level.isClientSide && linkedTransmissionPos != null
                && level.getBlockEntity(linkedTransmissionPos) instanceof TransmissionBlockEntity transmission) {
            transmission.setAceLinked(true);
        }
    }
}
