package me.myogoo.extendedmolecularassembler.integration.advancedae;

import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;

/**
 * EMA-owned block entity type for the extended Quantum Crafter block.
 *
 * <p>It intentionally keeps AdvancedAE's runtime behavior by extending {@link QuantumCrafterEntity}. The EMA mixin that
 * targets QuantumCrafterEntity therefore also applies to this subclass via inherited methods.</p>
 */
public class ExtendedQuantumCrafterBlockEntity extends QuantumCrafterEntity {
    public ExtendedQuantumCrafterBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.getMainNode().setIdlePowerUsage(EMAConfig.extendedQuantumCrafterIdlePowerUsage());
    }
}
