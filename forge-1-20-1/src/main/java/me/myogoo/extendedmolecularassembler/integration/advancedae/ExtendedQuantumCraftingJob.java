package me.myogoo.extendedmolecularassembler.integration.advancedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.stacks.GenericStack;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    private List<GenericStack> craftingInputs = List.of();
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
        this.craftingInputs = new ArrayList<>();
        if (pattern != null) {
            for (var input : pattern.getInputs()) {
                this.craftingInputs.add(input.getPossibleInputs()[0]);
            }
        }
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

        for (var input : this.craftingInputs) {
            if (!(input.what() instanceof AEItemKey inputKey)) {
                continue;
            }

            var inputStack = inputKey.toStack();
            if (!this.keysThatConsumeDurability.containsKey(inputKey)) {
                var remainingStack = this.remainingItems.stream()
                        .filter(stack -> isDurabilityRemainder(inputStack, stack))
                        .findFirst().orElse(ItemStack.EMPTY);
                if (remainingStack != ItemStack.EMPTY) {
                    var damage = remainingStack.getDamageValue() - inputStack.getDamageValue();
                    if (damage > 0) {
                        this.keysThatConsumeDurability.put(inputKey, damage);
                        this.consumesDurability = true;
                        continue;
                    }
                }
            }

        }
    }

    private static boolean isDurabilityRemainder(ItemStack input, ItemStack remainder) {
        if (!input.isDamageableItem() || !remainder.isDamageableItem()
                || remainder.getDamageValue() <= input.getDamageValue()) {
            return false;
        }
        // Tool wear changes Damage alone; all other item data must retain the input key.
        var wornInput = input.copy();
        wornInput.setDamageValue(remainder.getDamageValue());
        return java.util.Objects.equals(AEItemKey.of(wornInput), AEItemKey.of(remainder));
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

    public long requiredInputTotal(int inputIndex, int toCraft) {
        if (this.pattern == null) {
            return 0;
        }
        var input = this.craftingInputs.get(inputIndex);
        long amount = this.pattern.getInputs()[inputIndex].getMultiplier() * input.amount();
        return this.requiredInputTotal(input, amount, toCraft);
    }

    /**
     * @deprecated Use {@link #requiredInputTotal(int, int)} to select an unambiguous input group.
     * This overload retains the legacy first matching group's multiplier and the supplied stack amount.
     */
    @Deprecated(forRemoval = true)
    public long requiredInputTotal(GenericStack input, int toCraft) {
        if (this.pattern == null) {
            return 0;
        }
        var inputs = this.pattern.getInputs();
        for (int i = 0; i < inputs.length; i++) {
            if (input.what().equals(this.craftingInputs.get(i).what())
                    || java.util.Arrays.stream(inputs[i].getPossibleInputs())
                            .anyMatch(possible -> possible != null && input.what().equals(possible.what()))) {
                return this.requiredInputTotal(input, inputs[i].getMultiplier() * input.amount(), toCraft);
            }
        }
        return 0;
    }

    private long requiredInputTotal(GenericStack input, long amount, int toCraft) {
        var durability = this.requiredDurability(input);
        if (durability > 0) {
            return durability * amount * toCraft;
        }
        return this.isInputConsumed(input) ? amount * toCraft : amount;
    }

    private ItemStack findMatchingOutput(GenericStack input) {
        if (this.pattern == null || !(input.what() instanceof AEItemKey inputKey)) {
            return ItemStack.EMPTY;
        }

        for (var output : this.pattern.getOutputs()) {
            if (output.what() instanceof AEItemKey outputKey) {
                var outputStack = outputKey.toStack((int) output.amount());
                if (inputKey.equals(outputKey)) {
                    return outputStack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isInputConsumed(GenericStack input) {
        var remaining = this.findMatchingRemainingItem(input);
        if (remaining.getCount() >= Math.max(input.amount(), this.inputAmountPerCraft(input.what()))) {
            return false;
        }

        var output = this.findMatchingOutput(input);
        return output.isEmpty();
    }

    private ItemStack findMatchingRemainingItem(GenericStack input) {
        for (var item : this.remainingItems) {
            if (input.what().equals(AEItemKey.of(item))) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isStackAnInput(ItemStack stack) {
        if (this.pattern == null) {
            return false;
        }

        for (var input : this.craftingInputs) {
            if (input.what().equals(AEItemKey.of(stack)) && !this.isInputConsumed(input)) {
                return true;
            }
        }
        return false;
    }

    public long outputAmountPerCraft(GenericStack stack) {
        if (this.pattern == null) {
            return stack.amount();
        }

        return Math.max(0, stack.amount() - this.inputAmountPerCraft(stack.what()));
    }

    public List<GenericStack> craftingInputs() {
        return this.craftingInputs;
    }

    private long inputAmountPerCraft(AEKey key) {
        long amount = 0;
        for (int i = 0; i < this.craftingInputs.size(); i++) {
            var input = this.craftingInputs.get(i);
            if (key.equals(input.what())) {
                amount += input.amount() * this.pattern.getInputs()[i].getMultiplier();
            }
        }
        return amount;
    }

    @Nullable
    public ExtendedQuantumCraftingJob withInputs(List<GenericStack> selectedInputs, Level level) {
        if (this.pattern == null || selectedInputs.size() != this.pattern.getInputs().length) {
            return null;
        }
        var counters = new KeyCounter[selectedInputs.size()];
        for (int i = 0; i < counters.length; i++) {
            var input = selectedInputs.get(i);
            var patternInput = this.pattern.getInputs()[i];
            if (input == null || input.amount() <= 0 || !patternInput.isValid(input.what(), level)) {
                return null;
            }
            counters[i] = new KeyCounter();
            counters[i].add(input.what(), input.amount() * patternInput.getMultiplier());
        }
        var grid = NonNullList.withSize(this.pattern.machineGridSide() * this.pattern.machineGridSide(), ItemStack.EMPTY);
        this.pattern.fillCraftingGrid(counters, grid::set);
        if (this.pattern.assembleFromMachineGrid(grid::get, level).isEmpty()) {
            return null;
        }
        var resolved = new ExtendedQuantumCraftingJob(null);
        resolved.pattern = this.pattern;
        resolved.craftingInputs = List.copyOf(selectedInputs);
        resolved.remainingItems = condenseRemainingItems(this.pattern.getRemainingItemsFromMachineGrid(grid::get));
        resolved.keepMinInput.addAll(this.keepMinInput);
        resolved.limitMaxOutput = this.limitMaxOutput;
        resolved.analyzePattern();
        return resolved.consumesDurability ? null : resolved;
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
        int gridSide = pattern.machineGridSide();
        var machineInput = NonNullList.withSize(gridSide * gridSide, ItemStack.EMPTY);
        var sparseInputs = pattern.getSparseInputs();
        for (int patternSlot = 0; patternSlot < sparseInputs.size(); patternSlot++) {
            var input = sparseInputs.get(patternSlot);
            if (input == null || !(input.what() instanceof AEItemKey key)) {
                continue;
            }

            var machineSlot = pattern.toMachineGridIndex(patternSlot, gridSide);
            if (machineSlot >= 0 && machineSlot < machineInput.size()) {
                machineInput.set(machineSlot, key.toStack((int) input.amount()));
            }
        }

        var remainingItems = pattern.getRemainingItemsFromMachineGrid(machineInput::get);
        return condenseRemainingItems(remainingItems);
    }

    private static List<ItemStack> condenseRemainingItems(List<ItemStack> remainingItems) {
        var condensed = new HashMap<AEItemKey, ItemStack>();
        for (var item : remainingItems) {
            if (item.isEmpty()) {
                continue;
            }

            condensed.compute(AEItemKey.of(item), (ignored, current) -> {
                if (current == null) {
                    return item.copy();
                }
                return item.copyWithCount(current.getCount() + item.getCount());
            });
        }
        return List.copyOf(condensed.values());
    }
}
