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
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.advancedae.EMAAdvancedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.advancedae.ExtendedQuantumCraftingJob;
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
    private static final int EXTENDEDMOLECULARASSEMBLER$MAX_CRAFT_AMOUNT = 1024;
    @Unique
    private static final String EXTENDEDMOLECULARASSEMBLER$JOBS_TAG =
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
    private final List<ExtendedQuantumCraftingJob> extendedmolecularassembler$extendedJobs = new ArrayList<>();

    @Invoker("isEnabled")
    protected abstract boolean extendedmolecularassembler$isEnabled();

    @Invoker("isExportToMe")
    protected abstract boolean extendedmolecularassembler$isExportToMe();

    @Invoker("addToSendList")
    protected abstract void extendedmolecularassembler$addToSendList(AEKey what, long amount);

    @Inject(method = "makeCraftingRecipeList", at = @At("RETURN"))
    private void extendedmolecularassembler$makeExtendedCraftingRecipeList(CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        this.extendedmolecularassembler$updateExtendedQuantumCraftingJobs();
    }

    @Inject(method = "hasCraftWork", at = @At("RETURN"), cancellable = true)
    private void extendedmolecularassembler$hasExtendedCraftWork(CallbackInfoReturnable<Boolean> cir) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        if (!cir.getReturnValue() && this.extendedmolecularassembler$hasExtendedCraftWork()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "performCrafts", at = @At("RETURN"))
    private void extendedmolecularassembler$performExtendedCrafts(int maxCrafts, CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        this.extendedmolecularassembler$performExtendedCrafts(maxCrafts);
    }

    @Inject(method = "getPatternConfigInputs", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$getExtendedPatternConfigInputs(int index,
            CallbackInfoReturnable<LinkedHashMap<AEKey, Long>> cir) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.extendedmolecularassembler$getExtendedQuantumCraftingJob(index);
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
    private void extendedmolecularassembler$getExtendedPatternConfigOutput(int index,
            CallbackInfoReturnable<Pair<AEKey, Long>> cir) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.extendedmolecularassembler$getExtendedQuantumCraftingJob(index);
        if (job == null || job.pattern == null || job.pattern.getOutputs().isEmpty()) {
            return;
        }

        cir.setReturnValue(new Pair<>(job.pattern.getOutputs().getFirst().what(), job.limitMaxOutput));
    }

    @Inject(method = "setStockAmount", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$setExtendedStockAmount(int index, int inputIndex, long amount,
            CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.extendedmolecularassembler$getExtendedQuantumCraftingJob(index);
        if (job == null || job.pattern == null) {
            return;
        }

        job.setMinimumInputToKeep(inputIndex, amount);
        this.extendedmolecularassembler$self().saveChanges();
        ci.cancel();
    }

    @Inject(method = "setMaxCrafted", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$setExtendedMaxCrafted(int index, long amount, CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        var job = this.extendedmolecularassembler$getExtendedQuantumCraftingJob(index);
        if (job == null || job.pattern == null) {
            return;
        }

        job.limitMaxOutput = amount;
        this.extendedmolecularassembler$self().saveChanges();
        ci.cancel();
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void extendedmolecularassembler$saveExtendedQuantumCraftingJobs(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        var jobTags = new ListTag();
        var count = this.patternInv.size();
        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(count);
        for (int i = 0; i < count; i++) {
            var tag = new CompoundTag();
            var job = this.extendedmolecularassembler$extendedJobs.get(i);
            if (job != null) {
                job.writeToNBT(tag);
            }
            jobTags.add(tag);
        }
        data.put(EXTENDEDMOLECULARASSEMBLER$JOBS_TAG, jobTags);
    }

    @Inject(method = "loadTag", at = @At("RETURN"))
    private void extendedmolecularassembler$loadExtendedQuantumCraftingJobs(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(this.patternInv.size());
        if (!data.contains(EXTENDEDMOLECULARASSEMBLER$JOBS_TAG)) {
            return;
        }

        var jobTags = data.getList(EXTENDEDMOLECULARASSEMBLER$JOBS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < this.patternInv.size() && i < jobTags.size(); i++) {
            var tag = jobTags.getCompound(i);
            this.extendedmolecularassembler$extendedJobs.set(i, tag.isEmpty() ? null : ExtendedQuantumCraftingJob.fromTag(tag));
        }
        this.extendedmolecularassembler$updateExtendedQuantumCraftingJobs();
    }

    @Unique
    private boolean extendedmolecularassembler$hasExtendedCraftWork() {
        if (!this.extendedmolecularassembler$isEnabled()) {
            return false;
        }

        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var job = this.extendedmolecularassembler$extendedJobs.get(i);
            if (job == null
                    || job.pattern == null
                    || this.invalidPatternSlots.get(i)
                    || !this.enabledPatternSlots.get(i)) {
                continue;
            }

            if (this.extendedmolecularassembler$maximumCraftableAmount(job) > 0
                    && this.extendedmolecularassembler$hasAvailableOutputStorage(job)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean extendedmolecularassembler$isExtendedQuantumCrafter() {
        return EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER != null
                && this.extendedmolecularassembler$self().getBlockState()
                        .is(EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER.get());
    }

    @Unique
    private void extendedmolecularassembler$performExtendedCrafts(int maxCrafts) {
        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var job = this.extendedmolecularassembler$extendedJobs.get(i);
            if (job == null
                    || job.pattern == null
                    || this.invalidPatternSlots.get(i)
                    || !this.enabledPatternSlots.get(i)) {
                continue;
            }

            var craftAmount = this.extendedmolecularassembler$maximumCraftableAmount(job);
            var toCraft = Math.min(craftAmount, maxCrafts);
            if (toCraft > 0) {
                this.extendedmolecularassembler$performCraft(job, toCraft);
            }
        }
    }

    @Unique
    private void extendedmolecularassembler$updateExtendedQuantumCraftingJobs() {
        var level = this.extendedmolecularassembler$self().getLevel();
        if (level == null) {
            return;
        }

        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(this.patternInv.size());
        for (int i = 0; i < this.patternInv.size(); i++) {
            var stack = this.patternInv.getStackInSlot(i);
            if (stack.isEmpty() || !stack.is(EMAItems.EXTENDED_CRAFTING_PATTERN.get())) {
                this.extendedmolecularassembler$extendedJobs.set(i, null);
                continue;
            }

            var details = PatternDetailsHelper.decodePattern(stack, level);
            if (details instanceof ExtendedTableCraftingPattern pattern) {
                var job = this.extendedmolecularassembler$extendedJobs.get(i);
                if (job == null) {
                    job = new ExtendedQuantumCraftingJob(pattern);
                    this.extendedmolecularassembler$extendedJobs.set(i, job);
                } else {
                    job.setPattern(pattern);
                }
                this.invalidPatternSlots.set(i, job.consumesDurability);
            } else {
                this.extendedmolecularassembler$extendedJobs.set(i, null);
            }
        }
    }

    @Unique
    @Nullable
    private ExtendedQuantumCraftingJob extendedmolecularassembler$getExtendedQuantumCraftingJob(int index) {
        this.extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(this.patternInv.size());
        if (index < 0 || index >= this.extendedmolecularassembler$extendedJobs.size()) {
            return null;
        }
        return this.extendedmolecularassembler$extendedJobs.get(index);
    }

    @Unique
    private void extendedmolecularassembler$ensureExtendedQuantumCraftingJobSlots(int size) {
        while (this.extendedmolecularassembler$extendedJobs.size() < size) {
            this.extendedmolecularassembler$extendedJobs.add(null);
        }
        while (this.extendedmolecularassembler$extendedJobs.size() > size) {
            this.extendedmolecularassembler$extendedJobs.remove(this.extendedmolecularassembler$extendedJobs.size() - 1);
        }
    }

    @Unique
    private int extendedmolecularassembler$maximumCraftableAmount(ExtendedQuantumCraftingJob job) {
        var node = this.extendedmolecularassembler$self().getGridNode();
        if (node == null || job == null || job.pattern == null) {
            return 0;
        }

        var inputs = job.pattern.getInputs();
        var outputs = job.pattern.getOutputs();
        if (outputs.isEmpty()) {
            return 0;
        }

        var grid = node.getGrid();
        if (!this.extendedmolecularassembler$canCraftExtendedPattern(job)) {
            return 0;
        }

        var totalCrafts = EXTENDEDMOLECULARASSEMBLER$MAX_CRAFT_AMOUNT;
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
                if (stack.is(output.what().wrapForDisplayOrFilter().getItem())) {
                    amountInOutput += stack.getCount();
                }
            }

            var producedAmount = job.outputAmountPerCraft(output);
            var limitByOutput = (int) Math.floor((double) (maxStock - extracted - amountInOutput) / producedAmount);
            totalCrafts = Math.max(0, Math.min(totalCrafts, limitByOutput));
        }

        return QuantumCraftingBatch.maximumCrafts(totalCrafts,
                crafts -> this.extendedmolecularassembler$canStoreLocalOutputs(job, crafts));
    }

    @Unique
    private boolean extendedmolecularassembler$canCraftExtendedPattern(ExtendedQuantumCraftingJob job) {
        if (!EMAConfig.tieredMode()) {
            return true;
        }
        if (job == null || job.pattern == null) {
            return false;
        }

        try {
            TieredMECraftingProviderTier.requiredFor(job.pattern.tableType(), job.pattern.tableTier());
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        var node = this.extendedmolecularassembler$self().getGridNode();
        if (node == null) {
            return false;
        }
        var grid = node.getGrid();
        for (var provider : grid.getActiveMachines(TieredMECraftingProviderBlockEntity.class)) {
            if (provider.isOnline() && provider.getTier().provides(job.pattern.tableType(), job.pattern.tableTier())) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean extendedmolecularassembler$hasAvailableOutputStorage(ExtendedQuantumCraftingJob job) {
        if (job.pattern == null || job.pattern.getOutputs().isEmpty()) {
            return false;
        }

        if (this.extendedmolecularassembler$isExportToMe()) {
            return this.sendList.stream().noneMatch(p -> p.what().matches(job.pattern.getOutputs().getFirst()));
        }
        return true;
    }

    @Unique
    private boolean extendedmolecularassembler$canStoreLocalOutputs(ExtendedQuantumCraftingJob job, int crafts) {
        var simulatedOutput = new AppEngInternalInventory(this.outputInv.size());
        for (int i = 0; i < this.outputInv.size(); i++) {
            simulatedOutput.setMaxStackSize(i, this.outputInv.getSlotLimit(i));
            simulatedOutput.setItemDirect(i, this.outputInv.getStackInSlot(i).copy());
        }

        for (var stack : this.extendedmolecularassembler$getLocalOutputs(job, crafts)) {
            if (!this.extendedmolecularassembler$insertOutput(simulatedOutput, stack).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private List<ItemStack> extendedmolecularassembler$getLocalOutputs(ExtendedQuantumCraftingJob job, int crafts) {
        var localOutputs = new ArrayList<ItemStack>();
        if (!this.extendedmolecularassembler$isExportToMe()) {
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
    private ItemStack extendedmolecularassembler$insertOutput(AppEngInternalInventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
            stack = inventory.insertItem(i, stack, false);
        }
        return stack;
    }

    @Unique
    private void extendedmolecularassembler$insertLocalOutput(ItemStack stack) {
        var remainder = this.extendedmolecularassembler$insertOutput(this.outputInv, stack);
        if (!remainder.isEmpty()) {
            var key = AEItemKey.of(remainder);
            if (key != null) {
                this.extendedmolecularassembler$addToSendList(key, remainder.getCount());
            }
        }
    }

    @Unique
    private void extendedmolecularassembler$performCraft(ExtendedQuantumCraftingJob job, int toCraft) {
        var node = this.extendedmolecularassembler$self().getGridNode();
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
            if (this.extendedmolecularassembler$isExportToMe()) {
                for (var output : outputs) {
                    if (output.what() instanceof AEItemKey key) {
                        this.extendedmolecularassembler$addToSendList(
                                key, job.outputAmountPerCraft(output) * completeRecipes);
                    }
                }
            }

            for (var stack : this.extendedmolecularassembler$getLocalOutputs(job, completeRecipes)) {
                this.extendedmolecularassembler$insertLocalOutput(stack);
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
                    this.extendedmolecularassembler$addToSendList(input.what(), toReturn - successfulReturn);
                }
            }
        }
    }

    @Unique
    private AENetworkedPoweredBlockEntity extendedmolecularassembler$self() {
        return (AENetworkedPoweredBlockEntity) (Object) this;
    }

}
