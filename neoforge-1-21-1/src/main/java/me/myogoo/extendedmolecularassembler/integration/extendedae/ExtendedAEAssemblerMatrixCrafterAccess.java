package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;

/**
 * Implemented by EMA Matrix Crafting Core blocks so extended-table jobs run on
 * Matrix-owned execution cores instead of on external EMA machines.
 */
public interface ExtendedAEAssemblerMatrixCrafterAccess {
    boolean extendedmolecularassembler$pushExtendedJob(IPatternDetails patternDetails, KeyCounter[] inputHolder);

    int extendedmolecularassembler$getExtendedUsedThreadCount();

    int extendedmolecularassembler$getExtendedThreadCapacity();

    void extendedmolecularassembler$cancelExtendedJobs();
}
