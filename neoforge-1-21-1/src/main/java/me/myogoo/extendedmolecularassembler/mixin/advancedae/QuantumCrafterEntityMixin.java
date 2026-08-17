package me.myogoo.extendedmolecularassembler.mixin.advancedae;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageHelper;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.datafixers.util.Pair;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.advancedae.EMAAdvancedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.advancedae.ExtendedQuantumCraftingJob;
import me.myogoo.extendedmolecularassembler.integration.advancedae.QuantumCraftingBatch;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Mixin(targets = "net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity", remap = false)
public abstract class QuantumCrafterEntityMixin {
    @Unique
    private static final int EMA$MAX_CRAFT_AMOUNT = 1024;
    @Unique
    private static final String EMA$JOBS_TAG =
            "extendedmolecularassembler:extendedCraftingJobs";

    @Shadow
    @Final
    private AppEngInternalInventory patternInv;
    @Shadow
    @Final
    private AppEngInternalInventory outputInv;
    @Shadow
    @Final
    private IActionSource mySrc;
    @Shadow
    @Final
    private List<GenericStack> sendList;
    @Shadow
    @Final
    private List<Boolean> invalidPatternSlots;
    @Shadow
    @Final
    private List<Boolean> enabledPatternSlots;

    @Unique
    private final List<ExtendedQuantumCraftingJob> ema$extendedJobs = new ArrayList<>();

    @Invoker("isEnabled")
    protected abstract boolean ema$isEnabled();

    @Invoker("isExportToMe")
    protected abstract boolean ema$isExportToMe();

    @Invoker("addToSendList")
    protected abstract void ema$addToSendList(AEKey what, long amount);

    @Inject(method = "makeCraftingRecipeList", at = @At("RETURN"))
    private void ema$makeExtendedCraftingRecipeList(CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        this.ema$refreshJobs();
    }

    @Inject(method = "hasCraftWork", at = @At("RETURN"), cancellable = true)
    private void ema$hasExtendedCraftWork(CallbackInfoReturnable<Boolean> cir) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        if (!cir.getReturnValue() && this.ema$hasExtendedCraftWork()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "performCrafts", at = @At("RETURN"))
    private void ema$performExtendedCrafts(int maxCrafts, CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        this.ema$performExtendedCrafts(maxCrafts);
    }

    @Inject(method = "getPatternConfigInputs", at = @At("HEAD"), cancellable = true)
    private void ema$getExtendedPatternConfigInputs(int index,
            CallbackInfoReturnable<LinkedHashMap<AEKey, Long>> cir) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.ema$getJob(index);
        if (job == null || job.pattern == null) {
            return;
        }

        var inputs = new LinkedHashMap<AEKey, Long>();
        for (var input : job.pattern.getInputs()) {
            var possibleInputs = input.getPossibleInputs();
            if (possibleInputs.length > 0 && possibleInputs[0] != null) {
                inputs.put(possibleInputs[0].what(), job.minimumInputToKeep(input));
            }
        }
        cir.setReturnValue(inputs);
    }

    @Inject(method = "getPatternConfigOutput", at = @At("HEAD"), cancellable = true)
    private void ema$getExtendedPatternConfigOutput(int index,
            CallbackInfoReturnable<Pair<AEKey, Long>> cir) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.ema$getJob(index);
        if (job == null || job.pattern == null || job.pattern.getOutputs().isEmpty()) {
            return;
        }

        cir.setReturnValue(new Pair<>(job.pattern.getOutputs().getFirst().what(), job.limitMaxOutput));
    }

    @Inject(method = "setStockAmount", at = @At("HEAD"), cancellable = true)
    private void ema$setExtendedStockAmount(int index, int inputIndex, long amount,
            CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.ema$getJob(index);
        if (job == null || job.pattern == null) {
            return;
        }

        job.setMinimumInputToKeep(inputIndex, amount);
        this.ema$self().saveChanges();
        ci.cancel();
    }

    @Inject(method = "setMaxCrafted", at = @At("HEAD"), cancellable = true)
    private void ema$setExtendedMaxCrafted(int index, long amount, CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.ema$getJob(index);
        if (job == null || job.pattern == null) {
            return;
        }

        job.limitMaxOutput = amount;
        this.ema$self().saveChanges();
        ci.cancel();
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void ema$saveExtendedQuantumCraftingJobs(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        var jobTags = new ListTag();
        var count = this.patternInv.size();
        this.ema$resizeJobs(count);
        for (int i = 0; i < count; i++) {
            var tag = new CompoundTag();
            var job = this.ema$extendedJobs.get(i);
            if (job != null) {
                job.writeToNBT(tag);
            }
            jobTags.add(tag);
        }
        data.put(EMA$JOBS_TAG, jobTags);
    }

    @Inject(method = "loadTag", at = @At("RETURN"))
    private void ema$loadExtendedQuantumCraftingJobs(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!this.ema$isExtendedQuantumCrafter()) {
            return;
        }
        this.ema$resizeJobs(this.patternInv.size());
        if (!data.contains(EMA$JOBS_TAG)) {
            return;
        }

        var jobTags = data.getList(EMA$JOBS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < this.patternInv.size() && i < jobTags.size(); i++) {
            var tag = jobTags.getCompound(i);
            this.ema$extendedJobs.set(i, tag.isEmpty() ? null : ExtendedQuantumCraftingJob.fromTag(tag));
        }
        this.ema$refreshJobs();
    }

    @Unique
    private boolean ema$hasExtendedCraftWork() {
        if (!this.ema$isEnabled()) {
            return false;
        }

        this.ema$resizeJobs(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var job = this.ema$extendedJobs.get(i);
            if (job == null
                    || job.pattern == null
                    || this.invalidPatternSlots.get(i)
                    || !this.enabledPatternSlots.get(i)) {
                continue;
            }

            if (this.ema$maxCrafts(job, 1) > 0
                    && this.ema$hasAvailableOutputStorage(job)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean ema$isExtendedQuantumCrafter() {
        return EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER != null
                && this.ema$self().getBlockState()
                        .is(EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER.get());
    }

    @Unique
    private void ema$performExtendedCrafts(int maxCrafts) {
        this.ema$resizeJobs(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var job = this.ema$extendedJobs.get(i);
            if (job == null
                    || job.pattern == null
                    || this.invalidPatternSlots.get(i)
                    || !this.enabledPatternSlots.get(i)) {
                continue;
            }

            var toCraft = this.ema$maxCrafts(job, maxCrafts);
            if (toCraft > 0) {
                this.ema$performCraft(job, toCraft);
            }
        }
    }

    @Unique
    private void ema$refreshJobs() {
        var level = this.ema$self().getLevel();
        if (level == null) {
            return;
        }

        this.ema$resizeJobs(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var stack = this.patternInv.getStackInSlot(i);
            if (stack.isEmpty() || !stack.is(EMAItems.EXTENDED_CRAFTING_PATTERN.get())) {
                this.ema$extendedJobs.set(i, null);
                continue;
            }

            var details = PatternDetailsHelper.decodePattern(stack, level);
            if (details instanceof ExtendedTableCraftingPattern pattern) {
                var job = this.ema$extendedJobs.get(i);
                if (job == null) {
                    job = new ExtendedQuantumCraftingJob(pattern);
                    this.ema$extendedJobs.set(i, job);
                } else {
                    job.setPattern(pattern);
                }
                this.invalidPatternSlots.set(i, job.consumesDurability);
            } else {
                this.ema$extendedJobs.set(i, null);
            }
        }
    }

    @Unique
    @Nullable
    private ExtendedQuantumCraftingJob ema$getJob(int index) {
        this.ema$resizeJobs(this.patternInv.size());
        if (index < 0 || index >= this.ema$extendedJobs.size()) {
            return null;
        }
        return this.ema$extendedJobs.get(index);
    }

    @Unique
    private void ema$resizeJobs(int size) {
        while (this.ema$extendedJobs.size() < size) {
            this.ema$extendedJobs.add(null);
        }
        while (this.ema$extendedJobs.size() > size) {
            this.ema$extendedJobs.remove(this.ema$extendedJobs.size() - 1);
        }
    }

    @Unique
    private int ema$maxCrafts(ExtendedQuantumCraftingJob job, int upperBound) {
        var node = this.ema$self().getGridNode();
        if (node == null || job == null || job.pattern == null) {
            return 0;
        }

        var inputs = job.pattern.getInputs();
        var outputs = job.pattern.getOutputs();
        if (outputs.isEmpty()) {
            return 0;
        }

        var grid = node.getGrid();
        if (!this.ema$canCraftExtendedPattern(job)) {
            return 0;
        }

        var totalCrafts = Math.max(0, Math.min(EMA$MAX_CRAFT_AMOUNT, upperBound));
        if (totalCrafts == 0) {
            return 0;
        }
        for (var input : inputs) {
            var minStock = job.minimumInputToKeep(input);
            var success = false;
            for (var genericInput : input.getPossibleInputs()) {
                if (genericInput == null) {
                    continue;
                }

                var inputAmount = input.getMultiplier() * genericInput.amount();
                if (inputAmount <= 0) {
                    continue;
                }

                var toExtract = job.requiredInputTotal(genericInput, totalCrafts);
                if (job.isInputConsumed(genericInput)) {
                    toExtract += minStock;
                }

                var extracted = grid.getStorageService()
                        .getInventory()
                        .extract(genericInput.what(), toExtract, Actionable.SIMULATE, this.mySrc);

                if (!job.isInputConsumed(genericInput) && extracted >= toExtract) {
                    success = true;
                    break;
                } else if (extracted > minStock) {
                    success = true;
                    if (extracted > Integer.MAX_VALUE) {
                        extracted = Integer.MAX_VALUE;
                    }
                    var possibleCrafts = (int) Math.floor((double) (extracted - minStock) / inputAmount);
                    totalCrafts = Math.min(possibleCrafts, totalCrafts);
                    break;
                }
            }

            if (!success) {
                return 0;
            }
        }

        var output = outputs.getFirst();
        var maxStock = job.limitMaxOutput;
        if (maxStock > 0) {
            var extracted = grid.getStorageService()
                    .getInventory()
                    .extract(output.what(), maxStock, Actionable.SIMULATE, this.mySrc);
            var amountInOutput = 0;
            for (int i = 0; i < this.outputInv.size(); i++) {
                var stack = this.outputInv.getStackInSlot(i);
                if (output.what().matches(GenericStack.fromItemStack(stack))) {
                    amountInOutput += stack.getCount();
                }
            }

            var producedAmount = job.outputAmountPerCraft(output);
            var limitByOutput = (int) Math.floor((double) (maxStock - extracted - amountInOutput) / producedAmount);
            totalCrafts = Math.max(0, Math.min(totalCrafts, limitByOutput));
        }

        return QuantumCraftingBatch.maximumCrafts(totalCrafts,
                crafts -> this.ema$canStoreLocalOutputs(job, crafts));
    }

    @Unique
    private boolean ema$canCraftExtendedPattern(ExtendedQuantumCraftingJob job) {
        if (!EMAConfig.exportMode()) {
            return true;
        }
        if (job == null || job.pattern == null) {
            return false;
        }

        try {
            ExportMECraftingProviderTier.requiredFor(job.pattern.tableType(), job.pattern.tableTier());
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        var node = this.ema$self().getGridNode();
        if (node == null) {
            return false;
        }
        var grid = node.getGrid();
        for (var provider : grid.getActiveMachines(ExportMECraftingProviderBlockEntity.class)) {
            if (provider.isOnline() && provider.getTier().provides(job.pattern.tableType(), job.pattern.tableTier())) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean ema$hasAvailableOutputStorage(ExtendedQuantumCraftingJob job) {
        if (job.pattern == null || job.pattern.getOutputs().isEmpty()) {
            return false;
        }

        if (this.ema$isExportToMe()) {
            return this.sendList.stream().noneMatch(p -> p.what().matches(job.pattern.getOutputs().getFirst()));
        }
        return true;
    }

    @Unique
    private boolean ema$canStoreLocalOutputs(ExtendedQuantumCraftingJob job, int crafts) {
        var localOutputs = this.ema$getLocalOutputs(job, crafts);
        if (localOutputs.isEmpty()) {
            return true;
        }

        var simulatedOutput = new AppEngInternalInventory(this.outputInv.size());
        for (int i = 0; i < this.outputInv.size(); i++) {
            simulatedOutput.setMaxStackSize(i, this.outputInv.getSlotLimit(i));
            simulatedOutput.setItemDirect(i, this.outputInv.getStackInSlot(i).copy());
        }

        for (var stack : localOutputs) {
            if (!this.ema$insertOutput(simulatedOutput, stack).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private List<ItemStack> ema$getLocalOutputs(ExtendedQuantumCraftingJob job, int crafts) {
        var localOutputs = new ArrayList<ItemStack>();
        if (!this.ema$isExportToMe()) {
            for (var output : job.pattern.getOutputs()) {
                if (output.what() instanceof AEItemKey key) {
                    var amount = job.outputAmountPerCraft(output) * crafts;
                    if (amount > 0) {
                        localOutputs.add(key.toStack((int) Math.min(amount, Integer.MAX_VALUE)));
                    }
                }
            }
        }

        for (var stack : job.remainingItems) {
            if (!job.isStackAnInput(stack)) {
                var amount = (long) stack.getCount() * crafts;
                if (amount > 0) {
                    localOutputs.add(stack.copyWithCount((int) Math.min(amount, Integer.MAX_VALUE)));
                }
            }
        }
        return localOutputs;
    }

    @Unique
    private ItemStack ema$insertOutput(AppEngInternalInventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
            stack = inventory.insertItem(i, stack, false);
        }
        return stack;
    }

    @Unique
    private void ema$insertLocalOutput(ItemStack stack) {
        var remainder = this.ema$insertOutput(this.outputInv, stack);
        if (!remainder.isEmpty()) {
            var key = AEItemKey.of(remainder);
            if (key != null) {
                this.ema$addToSendList(key, remainder.getCount());
            }
        }
    }

    @Unique
    private void ema$performCraft(ExtendedQuantumCraftingJob job, int toCraft) {
        var node = this.ema$self().getGridNode();
        if (node == null || job == null || job.pattern == null || toCraft <= 0) {
            return;
        }

        var inputs = job.pattern.getInputs();
        var outputs = job.pattern.getOutputs();
        var requiredPerCraft = new ArrayList<Long>();
        var extractedItems = new ArrayList<GenericStack>();
        var extractions = new ArrayList<QuantumCraftingBatch.Extraction>();
        var grid = node.getGrid();
        var energy = grid.getEnergyService();
        var storage = grid.getStorageService();

        for (var input : inputs) {
            var extractedInput = false;
            for (var genericInput : input.getPossibleInputs()) {
                if (genericInput == null) {
                    continue;
                }

                var inputAmount = input.getMultiplier() * genericInput.amount();
                var toExtract = job.requiredInputTotal(genericInput, toCraft);
                if (inputAmount <= 0 || toExtract <= 0) {
                    continue;
                }

                var extracted = StorageHelper.poweredExtraction(
                        energy, storage.getInventory(), genericInput.what(), toExtract, this.mySrc);
                if (extracted > 0) {
                    requiredPerCraft.add(inputAmount);
                    extractedItems.add(new GenericStack(genericInput.what(), extracted));
                    extractions.add(new QuantumCraftingBatch.Extraction(toExtract, extracted));
                    extractedInput = extracted == toExtract;
                    break;
                }
            }
            if (!extractedInput) {
                break;
            }
        }

        var completeRecipes = QuantumCraftingBatch.completedCrafts(toCraft, inputs.length, extractions);
        if (completeRecipes > 0) {
            if (this.ema$isExportToMe()) {
                for (var output : outputs) {
                    if (output.what() instanceof AEItemKey key) {
                        this.ema$addToSendList(
                                key, job.outputAmountPerCraft(output) * completeRecipes);
                    }
                }
            }

            for (var stack : this.ema$getLocalOutputs(job, completeRecipes)) {
                this.ema$insertLocalOutput(stack);
            }
        }

        for (int i = 0; i < extractedItems.size(); i++) {
            var required = requiredPerCraft.get(i);
            var input = extractedItems.get(i);
            var toReturn = input.amount();
            if (job.isInputConsumed(input)) {
                toReturn -= required * completeRecipes;
            }

            if (toReturn > 0) {
                var successfulReturn =
                        storage.getInventory().insert(input.what(), toReturn, Actionable.MODULATE, this.mySrc);
                if (successfulReturn < toReturn) {
                    this.ema$addToSendList(input.what(), toReturn - successfulReturn);
                }
            }
        }
    }

    @Unique
    private AENetworkedPoweredBlockEntity ema$self() {
        return (AENetworkedPoweredBlockEntity) (Object) this;
    }

}
