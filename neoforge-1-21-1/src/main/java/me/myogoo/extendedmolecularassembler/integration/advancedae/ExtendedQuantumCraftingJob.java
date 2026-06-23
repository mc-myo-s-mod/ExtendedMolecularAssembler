package me.myogoo.extendedmolecularassembler.integration.advancedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ExtendedQuantumCraftingJob {
    private static final long DEFAULT_KEEP_INPUT = 0;
    private static final long DEFAULT_KEEP_OUTPUT = 0;

    @Nullable
    public ExtendedTableCraftingPattern pattern;
    public List<ItemStack> remainingItems = List.of();
    private final List<Long> keepMinInput = new ArrayList<>();
    public long limitMaxOutput = DEFAULT_KEEP_OUTPUT;
    public boolean consumesDurability = false;
    private boolean hasDataChange = false;
    private final HashMap<AEItemKey, Integer> keysThatConsumeDurability = new HashMap<>();

    public ExtendedQuantumCraftingJob(@Nullable ExtendedTableCraftingPattern pattern) {
        this.setPattern(pattern);
    }

    public static ExtendedQuantumCraftingJob fromTag(CompoundTag data) {
        var job = new ExtendedQuantumCraftingJob(null);
        if (data.contains("listMinInput")) {
            var listMinTag = data.getList("listMinInput", Tag.TAG_LONG);
            for (int i = 0; i < listMinTag.size(); i++) {
                job.keepMinInput.add(((LongTag) listMinTag.get(i)).getAsLong());
            }
        }
        job.limitMaxOutput = data.contains("limitMaxOutput") ? data.getLong("limitMaxOutput") : 0;
        return job;
    }

    public void setPattern(@Nullable ExtendedTableCraftingPattern pattern) {
        this.pattern = pattern;
        this.remainingItems = pattern == null ? List.of() : createRemainingItems(pattern);
        this.keysThatConsumeDurability.clear();
        this.consumesDurability = false;
        this.hasDataChange = false;
        this.alignKeepInputSize(pattern == null ? 0 : pattern.getInputs().length);
        this.analyzePattern();
    }

    public void writeToNBT(CompoundTag data) {
        var listMinTag = new ListTag();
        for (var value : this.keepMinInput) {
            listMinTag.add(LongTag.valueOf(value));
        }
        data.put("listMinInput", listMinTag);
        data.putLong("limitMaxOutput", this.limitMaxOutput);
    }

    private void analyzePattern() {
        if (this.pattern == null) {
            return;
        }

        for (var input : this.pattern.getInputs()) {
            var possibleInputs = input.getPossibleInputs();
            if (possibleInputs.length == 0 || !(possibleInputs[0].what() instanceof AEItemKey inputKey)) {
                continue;
            }

            var inputStack = inputKey.toStack();
            if (!this.keysThatConsumeDurability.containsKey(inputKey)) {
                var remainingStack = this.findMatchingRemainingItem(possibleInputs[0]);
                if (remainingStack != ItemStack.EMPTY) {
                    var damage = remainingStack.getDamageValue() - inputStack.getDamageValue();
                    if (damage > 0) {
                        this.keysThatConsumeDurability.put(inputKey, damage);
                        this.consumesDurability = true;
                        continue;
                    }
                }
            }

            if (!this.hasDataChange) {
                var outputStack = this.findMatchingOutput(possibleInputs[0]);
                if (outputStack != ItemStack.EMPTY
                        && !ItemStack.isSameItemSameComponents(
                                inputStack.copyWithCount(outputStack.getCount()), outputStack)) {
                    this.hasDataChange = true;
                }
            }
        }
    }

    public long minimumInputToKeep(IPatternDetails.IInput stack) {
        if (this.pattern == null) {
            return DEFAULT_KEEP_INPUT;
        }

        var inputs = this.pattern.getInputs();
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].equals(stack) && this.keepMinInput.size() > i) {
                return this.keepMinInput.get(i);
            }
        }
        return DEFAULT_KEEP_INPUT;
    }

    public void setMinimumInputToKeep(int inputIndex, long value) {
        if (this.pattern == null || inputIndex >= this.pattern.getInputs().length || inputIndex < 0) {
            return;
        }

        this.keepMinInput.set(inputIndex, value);
    }

    private int requiredDurability(GenericStack input) {
        if (input.what() instanceof AEItemKey inputKey) {
            return this.keysThatConsumeDurability.getOrDefault(inputKey, 0);
        }
        return 0;
    }

    public long requiredInputTotal(GenericStack input, int toCraft) {
        if (this.pattern == null) {
            return 0;
        }

        long multiplier = 0;
        for (var patternInput : this.pattern.getInputs()) {
            var found = false;
            for (var genericInput : patternInput.getPossibleInputs()) {
                if (genericInput != null && input.what().matches(genericInput)) {
                    multiplier = patternInput.getMultiplier() * input.amount();
                    found = true;
                    break;
                }
            }

            if (found) {
                break;
            }
        }

        var durability = this.requiredDurability(input);
        if (durability > 0) {
            return durability * multiplier * toCraft;
        }

        var remainingItem = this.findMatchingRemainingItem(input);
        if (input.amount() <= remainingItem.getCount()) {
            return multiplier;
        }

        if (input.what() instanceof AEItemKey key) {
            var stack = key.toStack();
            for (var item : this.remainingItems) {
                if (item.is(stack.getItem())) {
                    return multiplier;
                }
            }
            return multiplier * toCraft;
        }
        if (input.what() instanceof AEFluidKey) {
            return multiplier * toCraft;
        }
        return 0;
    }

    private ItemStack findMatchingOutput(GenericStack input) {
        if (this.pattern == null || !(input.what() instanceof AEItemKey inputKey)) {
            return ItemStack.EMPTY;
        }

        var inputStack = inputKey.toStack();
        for (var output : this.pattern.getOutputs()) {
            if (output.what() instanceof AEItemKey outputKey) {
                var outputStack = outputKey.toStack((int) output.amount());
                if (inputStack.is(outputStack.getItem())) {
                    return outputStack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isInputConsumed(GenericStack input) {
        var remaining = this.findMatchingRemainingItem(input);
        if (remaining.getCount() >= input.amount()) {
            return false;
        }

        var output = this.findMatchingOutput(input);
        return !(!output.isEmpty() && !this.hasDataChange);
    }

    private ItemStack findMatchingRemainingItem(GenericStack input) {
        for (var item : this.remainingItems) {
            if (input.what() instanceof AEItemKey inputKey && inputKey.is(item.getItem())) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isStackAnInput(ItemStack stack) {
        if (this.pattern == null) {
            return false;
        }

        for (var input : this.pattern.getInputs()) {
            for (var genericInput : input.getPossibleInputs()) {
                if (genericInput != null && genericInput.what().wrapForDisplayOrFilter().is(stack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    public long outputAmountPerCraft(GenericStack stack) {
        if (this.pattern == null) {
            return stack.amount();
        }

        for (var input : this.pattern.getInputs()) {
            for (var genericInput : input.getPossibleInputs()) {
                if (genericInput != null && stack.what().matches(genericInput)) {
                    return stack.amount() - genericInput.amount();
                }
            }
        }
        return stack.amount();
    }

    private void alignKeepInputSize(int size) {
        while (this.keepMinInput.size() < size) {
            this.keepMinInput.add(DEFAULT_KEEP_INPUT);
        }
        while (this.keepMinInput.size() > size) {
            this.keepMinInput.remove(this.keepMinInput.size() - 1);
        }
    }

    private static List<ItemStack> createRemainingItems(ExtendedTableCraftingPattern pattern) {
        var machineInput = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        var sparseInputs = pattern.getSparseInputs();
        var side = pattern.tableSideLength();
        var offset = Math.floorDiv(ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - side, 2);
        for (int patternSlot = 0; patternSlot < sparseInputs.size(); patternSlot++) {
            var input = sparseInputs.get(patternSlot);
            if (input == null || !(input.what() instanceof AEItemKey key)) {
                continue;
            }

            var x = patternSlot % side;
            var y = patternSlot / side;
            var machineSlot = x + offset + (y + offset) * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE;
            if (machineSlot >= 0 && machineSlot < machineInput.size()) {
                machineInput.set(machineSlot, key.toStack((int) input.amount()));
            }
        }

        var remainingItems = pattern.getRemainingItemsFromMachineGrid(machineInput::get);
        var condensed = new HashMap<Item, ItemStack>();
        for (var item : remainingItems) {
            if (item.isEmpty()) {
                continue;
            }

            condensed.compute(item.getItem(), (ignored, current) -> {
                if (current == null) {
                    return item.copy();
                }
                return item.copyWithCount(current.getCount() + item.getCount());
            });
        }
        return List.copyOf(condensed.values());
    }
}
