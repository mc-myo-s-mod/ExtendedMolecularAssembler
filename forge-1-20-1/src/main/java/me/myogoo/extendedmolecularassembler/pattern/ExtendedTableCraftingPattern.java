package me.myogoo.extendedmolecularassembler.pattern;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

public class ExtendedTableCraftingPattern implements IPatternDetails {
    public static final int MACHINE_GRID_SIDE = 9;
    public static final int MACHINE_GRID_SIZE = MACHINE_GRID_SIDE * MACHINE_GRID_SIDE;

    private final AEItemKey definition;
    private final boolean canSubstitute;
    private final Recipe<?> recipe;
    private final IMyotusTableRecipe<?> adapter;
    private final NonNullList<Ingredient> slotIngredients;
    private final ResourceLocation tableType;
    private final int tableTier;
    private final int tableSideLength;
    private final List<GenericStack> sparseInputs;
    private final int[] patternToMachineSlot;
    private final int[] machineToPatternSlot;
    private final int[] sparseToCompressed;
    private final Input[] inputs;
    private final ItemStack output;
    private final GenericStack[] outputsArray;
    @SuppressWarnings("unchecked")
    private final Map<Item, Boolean>[] isValidCache = new Map[MACHINE_GRID_SIZE];
    @SuppressWarnings("unchecked")
    private final Map<Item, ItemStack>[] remainderCache = new Map[MACHINE_GRID_SIZE];

    public ExtendedTableCraftingPattern(AEItemKey definition, Level level) {
        this.definition = definition;
        var encodedPattern = EncodedExtendedCraftingPattern.get(definition.toStack());
        if (encodedPattern == null) {
            throw new IllegalArgumentException("Given item does not encode an extended crafting pattern: " + definition);
        }
        if (encodedPattern.containsMissingContent()) {
            throw new IllegalArgumentException("Pattern references missing content");
        }

        this.canSubstitute = encodedPattern.canSubstitute();
        this.recipe = level.getRecipeManager().byKey(encodedPattern.recipeId()).orElse(null);
        if (recipe == null) {
            throw new IllegalArgumentException("Pattern references unknown recipe " + encodedPattern.recipeId());
        }
        this.adapter = TableRecipeAdapters.of(recipe);
        this.slotIngredients = adapter.slotIngredients();
        if (encodedPattern.hasTableMetadata()
                && (!encodedPattern.tableType().equals(adapter.tableType())
                        || encodedPattern.tableTier() != adapter.tier()
                        || encodedPattern.tableSideLength() != adapter.sideLength())) {
            throw new IllegalStateException("Pattern was encoded for table " + encodedPattern.tableType()
                    + " tier " + encodedPattern.tableTier()
                    + " (" + encodedPattern.tableSideLength() + "x" + encodedPattern.tableSideLength()
                    + "), but recipe now uses table " + adapter.tableType()
                    + " tier " + adapter.tier()
                    + " (" + adapter.sideLength() + "x" + adapter.sideLength() + ")");
        }
        this.tableType = encodedPattern.hasTableMetadata() ? encodedPattern.tableType() : adapter.tableType();
        this.tableTier = encodedPattern.hasTableMetadata() ? encodedPattern.tableTier() : adapter.tier();
        this.tableSideLength = encodedPattern.hasTableMetadata() ? encodedPattern.tableSideLength() : adapter.sideLength();
        this.patternToMachineSlot = createPatternToMachineSlots(this.tableSideLength);
        this.machineToPatternSlot = createMachineToPatternSlots(this.patternToMachineSlot);
        this.sparseInputs = getCraftingInputs(encodedPattern.inputs(), adapter.gridSize());
        this.sparseToCompressed = new int[MACHINE_GRID_SIZE];
        Arrays.fill(this.sparseToCompressed, -1);

        var items = makeAdapterInputItemsFromSparse();
        if (!adapter.matches(items, level)) {
            throw new IllegalStateException("The recipe " + recipe.getId() + " no longer matches the encoded input.");
        }

        this.output = adapter.assemble(items, level);
        if (output.isEmpty()) {
            throw new IllegalStateException("The recipe " + recipe.getId() + " produced an empty item stack result.");
        }
        this.outputsArray = new GenericStack[] { Objects.requireNonNull(GenericStack.fromItemStack(output)) };

        var condensedInputs = condenseStacks(sparseInputs);
        this.inputs = new Input[condensedInputs.size()];
        for (int compressed = 0; compressed < condensedInputs.size(); compressed++) {
            var condensedInput = condensedInputs.get(compressed);
            for (int patternSlot = 0; patternSlot < sparseInputs.size(); patternSlot++) {
                var input = sparseInputs.get(patternSlot);
                if (input != null && input.what().equals(condensedInput.what())) {
                    var machineSlot = toMachineSlot(patternSlot);
                    if (inputs[compressed] == null) {
                        inputs[compressed] = new Input(machineSlot, input, condensedInput.amount());
                    }
                    sparseToCompressed[machineSlot] = compressed;
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return this.definition.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass() == getClass()
                && ((ExtendedTableCraftingPattern) obj).definition.equals(this.definition);
    }

    @Override public AEItemKey getDefinition() { return definition; }
    @Override public IInput[] getInputs() { return inputs; }
    @Override public GenericStack[] getOutputs() { return outputsArray; }

    public ItemStack assembleFromMachineGrid(IntFunction<ItemStack> machineInput, Level level) {
        var items = cropMachineInput(machineInput);
        if (!adapter.matches(items, level)) {
            return ItemStack.EMPTY;
        }
        var assembled = adapter.assemble(items, level);
        return ItemStack.matches(output, assembled) ? assembled : ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItemsFromMachineGrid(IntFunction<ItemStack> machineInput) {
        var cropped = cropMachineInput(machineInput);
        var croppedRemainders = adapter.getRemainingItems(cropped);
        var result = NonNullList.withSize(MACHINE_GRID_SIZE, ItemStack.EMPTY);
        var count = Math.min(croppedRemainders.size(), adapter.gridSize());
        for (int patternSlot = 0; patternSlot < count; patternSlot++) {
            result.set(toMachineSlot(patternSlot), croppedRemainders.get(patternSlot));
        }
        return result;
    }

    public boolean isItemValid(int slot, AEItemKey key, Level level) {
        var patternSlot = toPatternSlot(slot);
        if (patternSlot < 0) return key == null;
        var template = sparseInputs.get(patternSlot);
        if (!canSubstitute) return template == null && key == null || template != null && template.what().equals(key);
        if (key == null) return template == null;
        var cached = getTestResult(slot, key);
        if (cached != null) return cached;
        var items = makeAdapterInputItemsFromSparse();
        items.set(patternSlot, key.toStack());
        var valid = adapter.matches(items, level) && ItemStack.matches(output, adapter.assemble(items, level));
        setTestResult(slot, key, valid);
        return valid;
    }

    public boolean isSlotEnabled(int slot) {
        var patternSlot = toPatternSlot(slot);
        return patternSlot >= 0 && sparseInputs.get(patternSlot) != null;
    }

    public void fillCraftingGrid(KeyCounter[] table, CraftingGridAccessor gridAccessor) {
        for (int machineSlot = 0; machineSlot < MACHINE_GRID_SIZE; machineSlot++) {
            int inputId = sparseToCompressed[machineSlot];
            if (inputId == -1) continue;
            var available = table[inputId];
            for (var entry : available) {
                if (entry.getLongValue() > 0 && entry.getKey() instanceof AEItemKey itemKey) {
                    gridAccessor.set(machineSlot, itemKey.toStack());
                    available.remove(itemKey, 1);
                    break;
                }
            }
        }
    }

    public boolean canSubstitute() { return canSubstitute; }
    public int sideLength() { return adapter.sideLength(); }
    public ResourceLocation tableType() { return tableType; }
    public int tableTier() { return tableTier; }
    public int tableSideLength() { return tableSideLength; }
    public List<GenericStack> getSparseInputs() { return sparseInputs; }
    public GenericStack[] getSparseOutputs() { return outputsArray; }

    public static void encode(ItemStack result, Recipe<?> recipe, ItemStack[] sparseInputs, ItemStack output,
            boolean allowSubstitutes) {
        Objects.requireNonNull(recipe, "recipe");
        Objects.requireNonNull(sparseInputs, "sparseInputs");
        Objects.requireNonNull(output, "output");
        var adapter = TableRecipeAdapters.of(recipe);
        EncodedExtendedCraftingPattern.set(result, new EncodedExtendedCraftingPattern(
                Arrays.stream(sparseInputs).map(ItemStack::copy).toList(),
                output.copy(),
                recipe.getId(),
                adapter.tableType(),
                adapter.tier(),
                adapter.sideLength(),
                allowSubstitutes));
    }

    private int toMachineSlot(int patternSlot) { return this.patternToMachineSlot[patternSlot]; }
    private int toPatternSlot(int machineSlot) { return this.machineToPatternSlot[machineSlot]; }

    private List<ItemStack> cropMachineInput(IntFunction<ItemStack> machineInput) {
        var result = NonNullList.withSize(adapter.gridSize(), ItemStack.EMPTY);
        for (int patternSlot = 0; patternSlot < result.size(); patternSlot++) {
            result.set(patternSlot, machineInput.apply(toMachineSlot(patternSlot)));
        }
        return result;
    }

    private List<ItemStack> makeAdapterInputItemsFromSparse() {
        var items = NonNullList.withSize(adapter.gridSize(), ItemStack.EMPTY);
        for (int i = 0; i < sparseInputs.size(); i++) {
            var input = sparseInputs.get(i);
            if (input != null && input.what() instanceof AEItemKey itemKey) {
                items.set(i, itemKey.toStack());
            }
        }
        return items;
    }

    private Ingredient getRecipeIngredient(int machineSlot) {
        var patternSlot = toPatternSlot(machineSlot);
        if (patternSlot < 0 || patternSlot >= this.slotIngredients.size()) return Ingredient.EMPTY;
        return this.slotIngredients.get(patternSlot);
    }

    @Nullable private Boolean getTestResult(int slot, AEItemKey key) {
        if (key == null || key.hasTag()) return null;
        var cache = isValidCache[slot];
        return cache == null ? null : cache.get(key.getItem());
    }

    private void setTestResult(int slot, AEItemKey key, boolean result) {
        if (key != null && !key.hasTag()) {
            var cache = isValidCache[slot];
            if (cache == null) cache = isValidCache[slot] = new IdentityHashMap<>();
            cache.put(key.getItem(), result);
        }
    }

    private ItemStack getRecipeRemainder(int machineSlot, AEItemKey key) {
        if (!key.hasTag()) {
            var cache = this.remainderCache[machineSlot];
            var item = key.getItem();
            if (cache != null && cache.containsKey(item)) return cache.get(item);
            var remainder = calculateRecipeRemainder(machineSlot, key);
            if (cache == null) cache = this.remainderCache[machineSlot] = new IdentityHashMap<>();
            cache.put(item, remainder);
            return remainder;
        }
        return calculateRecipeRemainder(machineSlot, key);
    }

    private ItemStack calculateRecipeRemainder(int machineSlot, AEItemKey key) {
        var patternSlot = toPatternSlot(machineSlot);
        if (patternSlot < 0) return ItemStack.EMPTY;
        var items = makeAdapterInputItemsFromSparse();
        items.set(patternSlot, key.toStack());
        var remainingItems = adapter.getRemainingItems(items);
        return patternSlot < remainingItems.size() ? remainingItems.get(patternSlot) : ItemStack.EMPTY;
    }

    private static int[] createPatternToMachineSlots(int side) {
        var result = new int[side * side];
        var offset = Math.floorDiv(MACHINE_GRID_SIDE - side, 2);
        for (int patternSlot = 0; patternSlot < result.length; patternSlot++) {
            var x = patternSlot % side;
            var y = patternSlot / side;
            result[patternSlot] = (x + offset) + (y + offset) * MACHINE_GRID_SIDE;
        }
        return result;
    }

    private static int[] createMachineToPatternSlots(int[] patternToMachineSlot) {
        var result = new int[MACHINE_GRID_SIZE];
        Arrays.fill(result, -1);
        for (int patternSlot = 0; patternSlot < patternToMachineSlot.length; patternSlot++) {
            result[patternToMachineSlot[patternSlot]] = patternSlot;
        }
        return result;
    }

    private static List<GenericStack> getCraftingInputs(List<ItemStack> stacks, int size) {
        var result = new GenericStack[size];
        var count = Math.min(stacks.size(), size);
        for (int i = 0; i < count; i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) result[i] = GenericStack.fromItemStack(stack);
        }
        return Arrays.asList(result);
    }

    private static List<GenericStack> condenseStacks(List<GenericStack> sparseInput) {
        var map = new LinkedHashMap<AEKey, Long>();
        for (var input : sparseInput) {
            if (input != null) map.merge(input.what(), input.amount(), Long::sum);
        }
        if (map.isEmpty()) throw new IllegalStateException("No pattern here!");
        var result = new ArrayList<GenericStack>(map.size());
        for (var entry : map.entrySet()) result.add(new GenericStack(entry.getKey(), entry.getValue()));
        return result;
    }

    private class Input implements IInput {
        private final int machineSlot;
        private final GenericStack[] possibleInputs;
        private final long multiplier;

        private Input(int machineSlot, GenericStack slotInput, long multiplier) {
            this.machineSlot = machineSlot;
            this.multiplier = multiplier;
            if (!canSubstitute) {
                this.possibleInputs = new GenericStack[] { slotInput };
            } else {
                var matchingStacks = getRecipeIngredient(machineSlot).getItems();
                this.possibleInputs = new GenericStack[matchingStacks.length + 1];
                this.possibleInputs[0] = slotInput;
                for (int i = 0; i < matchingStacks.length; i++) {
                    this.possibleInputs[i + 1] = GenericStack.fromItemStack(matchingStacks[i]);
                }
            }
        }

        @Override public GenericStack[] getPossibleInputs() { return possibleInputs; }
        @Override public long getMultiplier() { return multiplier; }
        @Override public boolean isValid(AEKey input, Level level) {
            if (input.matches(possibleInputs[0])) return true;
            return canSubstitute() && input instanceof AEItemKey itemKey
                    && ExtendedTableCraftingPattern.this.isItemValid(machineSlot, itemKey, level);
        }
        @Nullable @Override public AEKey getRemainingKey(AEKey template) {
            if (template instanceof AEItemKey itemKey) {
                var remainder = getRecipeRemainder(machineSlot, itemKey);
                return remainder.isEmpty() ? null : AEItemKey.of(remainder);
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface CraftingGridAccessor {
        void set(int slot, ItemStack stack);
    }
}
