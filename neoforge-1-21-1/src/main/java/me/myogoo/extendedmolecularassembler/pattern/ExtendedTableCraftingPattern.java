package me.myogoo.extendedmolecularassembler.pattern;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsTooltip;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.core.localization.GuiText;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.extendedmolecularassembler.init.EMADataComponents;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class ExtendedTableCraftingPattern implements IPatternDetails {
    public static final int MACHINE_GRID_SIDE = 9;
    public static final int MACHINE_GRID_SIZE = MACHINE_GRID_SIDE * MACHINE_GRID_SIDE;

    private final AEItemKey definition;
    private final boolean canSubstitute;
    private final boolean canSubstituteFluids;
    private final RecipeHolder<?> recipeHolder;
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
    private final List<GenericStack> outputsArray;
    @SuppressWarnings("unchecked")
    private final Map<Item, Boolean>[] isValidCache = new Map[MACHINE_GRID_SIZE];
    @SuppressWarnings("unchecked")
    private final Map<Item, ItemStack>[] remainderCache = new Map[MACHINE_GRID_SIZE];

    public ExtendedTableCraftingPattern(AEItemKey definition, Level level) {
        this.definition = definition;
        var encodedPattern = definition.get(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN);
        if (encodedPattern == null) {
            throw new IllegalArgumentException("Given item does not encode an extended crafting pattern: " + definition);
        }
        if (encodedPattern.containsMissingContent()) {
            throw new IllegalArgumentException("Pattern references missing content");
        }

        this.canSubstitute = encodedPattern.canSubstitute();
        this.canSubstituteFluids = encodedPattern.canSubstituteFluids();
        this.recipeHolder = level.getRecipeManager().byKey(encodedPattern.recipeId()).orElse(null);
        if (recipeHolder == null) {
            throw new IllegalArgumentException("Pattern references unknown recipe " + encodedPattern.recipeId());
        }
        this.adapter = TableRecipeAdapters.of(recipeHolder);
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
        this.tableSideLength = encodedPattern.hasTableMetadata()
                ? encodedPattern.tableSideLength()
                : adapter.sideLength();
        this.patternToMachineSlot = createPatternToMachineSlots(this.tableSideLength);
        this.machineToPatternSlot = createMachineToPatternSlots(this.patternToMachineSlot);
        this.sparseInputs = getCraftingInputs(encodedPattern.inputs(), adapter.gridSize());
        this.sparseToCompressed = new int[MACHINE_GRID_SIZE];
        Arrays.fill(this.sparseToCompressed, -1);

        var items = makeAdapterInputItemsFromSparse();
        if (!adapter.matches(items, level)) {
            throw new IllegalStateException("The recipe " + recipeHolder.id() + " no longer matches the encoded input.");
        }

        this.output = adapter.assemble(items, level);
        if (output.isEmpty()) {
            throw new IllegalStateException("The recipe " + recipeHolder.id() + " produced an empty item stack result.");
        }
        this.outputsArray = Collections.singletonList(Objects.requireNonNull(GenericStack.fromItemStack(output)));

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
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return inputs;
    }

    @Override
    public List<GenericStack> getOutputs() {
        return outputsArray;
    }

    public ItemStack assembleFromMachineGrid(IntFunction<ItemStack> machineInput, Level level) {
        var items = makeAdapterInputItemsFromMachineGrid(machineInput);
        if (!adapter.matches(items, level)) {
            return ItemStack.EMPTY;
        }
        var assembled = adapter.assemble(items, level);
        if (ItemStack.matches(output, assembled)) {
            return assembled;
        }

        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItemsFromMachineGrid(IntFunction<ItemStack> machineInput) {
        var cropped = makeAdapterInputItemsFromMachineGrid(machineInput);
        var croppedRemainders = adapter.getRemainingItems(cropped);
        var result = NonNullList.withSize(MACHINE_GRID_SIZE, ItemStack.EMPTY);
        var count = Math.min(croppedRemainders.size(), adapter.gridSize());
        for (int patternSlot = 0; patternSlot < count; patternSlot++) {
            var machineSlot = toMachineSlot(patternSlot);
            var remainder = croppedRemainders.get(patternSlot);
            if (GenericStack.unwrapItemStack(machineInput.apply(machineSlot)) != null) {
                remainder = ItemStack.EMPTY;
            }
            result.set(machineSlot, remainder);
        }
        return result;
    }

    public boolean isItemValid(int slot, AEItemKey key, Level level) {
        var patternSlot = toPatternSlot(slot);
        if (patternSlot < 0) {
            return key == null;
        }

        var template = sparseInputs.get(patternSlot);
        if (!canSubstitute) {
            return template == null && key == null || template != null && template.what().equals(key);
        }

        if (key == null) {
            return template == null;
        }

        var cached = getTestResult(slot, key);
        if (cached != null) {
            return cached;
        }

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
            if (inputId == -1) {
                continue;
            }

            var available = table[inputId];
            var validFluid = getValidFluid(machineSlot);
            if (validFluid != null) {
                var validFluidKey = validFluid.what();
                var amount = available.get(validFluidKey);
                int requiredAmount = (int) validFluid.amount();
                if (amount >= requiredAmount) {
                    gridAccessor.set(machineSlot, GenericStack.wrapInItemStack(validFluidKey, requiredAmount));
                    available.remove(validFluidKey, requiredAmount);
                    continue;
                }
            }

            for (var entry : available) {
                if (entry.getLongValue() > 0 && entry.getKey() instanceof AEItemKey itemKey) {
                    gridAccessor.set(machineSlot, itemKey.toStack());
                    available.remove(itemKey, 1);
                    break;
                }
            }
        }
    }

    @Override
    public boolean supportsPushInputsToExternalInventory() {
        return false;
    }

    @Override
    public PatternDetailsTooltip getTooltip(Level level, TooltipFlag flags) {
        var tooltip = new PatternDetailsTooltip(PatternDetailsTooltip.OUTPUT_TEXT_CRAFTS);
        tooltip.addInputsAndOutputs(this);
        if (canSubstitute) {
            tooltip.addProperty(GuiText.PatternTooltipSubstitutions.text());
        }
        if (canSubstituteFluids) {
            tooltip.addProperty(GuiText.PatternTooltipFluidSubstitutions.text());
        }
        tooltip.addProperty(
                Component.translatable(EMATranslationKey.TOOLTIP.TABLE.key()),
                ExtendedPatternTableTypes.displayName(tableType, tableTier, tableSideLength));
        if (flags.isAdvanced()) {
            tooltip.addProperty(Component.literal("Recipe"), Component.literal(recipeHolder.id().toString()));
        }
        return tooltip;
    }

    public boolean canSubstitute() {
        return canSubstitute;
    }

    public boolean canSubstituteFluids() {
        return canSubstituteFluids;
    }

    public int sideLength() {
        return adapter.sideLength();
    }

    public ResourceLocation tableType() {
        return tableType;
    }

    public int tableTier() {
        return tableTier;
    }

    public int tableSideLength() {
        return tableSideLength;
    }

    public List<GenericStack> getSparseInputs() {
        return sparseInputs;
    }

    public List<GenericStack> getSparseOutputs() {
        return outputsArray;
    }

    public static void encode(ItemStack result, RecipeHolder<?> recipe, ItemStack[] sparseInputs, ItemStack output,
            boolean allowSubstitutes, boolean allowFluidSubstitutes) {
        Objects.requireNonNull(recipe, "recipe");
        Objects.requireNonNull(sparseInputs, "sparseInputs");
        Objects.requireNonNull(output, "output");
        var adapter = TableRecipeAdapters.of(recipe);

        result.set(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN,
                new EncodedExtendedCraftingPattern(
                        Stream.of(sparseInputs).map(ItemStack::copy).toList(),
                        output.copy(),
                        recipe.id(),
                        adapter.tableType(),
                        adapter.tier(),
                        adapter.sideLength(),
                        allowSubstitutes,
                        allowFluidSubstitutes));
    }

    public static PatternDetailsTooltip getInvalidPatternTooltip(ItemStack stack, Level level,
            @Nullable Exception cause, TooltipFlag flags) {
        var tooltip = new PatternDetailsTooltip(PatternDetailsTooltip.OUTPUT_TEXT_CRAFTS);
        var encodedPattern = stack.get(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN);
        if (encodedPattern != null) {
            for (var input : encodedPattern.inputs()) {
                if (!input.isEmpty()) {
                    tooltip.addInput(AEItemKey.of(input), input.getCount());
                }
            }
            tooltip.addOutput(AEItemKey.of(encodedPattern.result()), encodedPattern.result().getCount());
            if (encodedPattern.canSubstitute()) {
                tooltip.addProperty(GuiText.PatternTooltipSubstitutions.text());
            }
            if (encodedPattern.canSubstituteFluids()) {
                tooltip.addProperty(GuiText.PatternTooltipFluidSubstitutions.text());
            }
            if (encodedPattern.hasTableMetadata()) {
                tooltip.addProperty(
                        Component.translatable(EMATranslationKey.TOOLTIP.TABLE.key()),
                        ExtendedPatternTableTypes.displayName(encodedPattern.tableType(), encodedPattern.tableTier(),
                                encodedPattern.tableSideLength()));
            }
            if (flags.isAdvanced()) {
                tooltip.addProperty(Component.literal("Recipe"), Component.literal(encodedPattern.recipeId().toString()));
            }
        }
        return tooltip;
    }

    private int toMachineSlot(int patternSlot) {
        return this.patternToMachineSlot[patternSlot];
    }

    private int toPatternSlot(int machineSlot) {
        return this.machineToPatternSlot[machineSlot];
    }

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

    private List<ItemStack> makeAdapterInputItemsFromMachineGrid(IntFunction<ItemStack> machineInput) {
        var items = NonNullList.withSize(adapter.gridSize(), ItemStack.EMPTY);
        for (int patternSlot = 0; patternSlot < items.size(); patternSlot++) {
            var machineSlot = toMachineSlot(patternSlot);
            items.set(patternSlot, substituteFluidInput(machineSlot, machineInput.apply(machineSlot)));
        }
        return items;
    }

    private ItemStack substituteFluidInput(int machineSlot, ItemStack item) {
        var stack = GenericStack.unwrapItemStack(item);
        if (stack != null) {
            var validFluid = getValidFluid(machineSlot);
            if (validFluid != null && validFluid.equals(stack)) {
                var patternSlot = toPatternSlot(machineSlot);
                if (patternSlot >= 0 && patternSlot < sparseInputs.size()
                        && sparseInputs.get(patternSlot).what() instanceof AEItemKey itemKey) {
                    return itemKey.toStack();
                }
            }
        }
        return item.copy();
    }

    private GenericStack getItemOrFluidInput(int machineSlot, GenericStack item) {
        if (!(item.what() instanceof AEItemKey itemKey)) {
            return item;
        }

        var containedFluid = ContainerItemStrategies.getContainedStack(itemKey.toStack(), AEKeyType.fluids());
        var isBucket = itemKey.getItem() instanceof BucketItem || itemKey.getItem() instanceof MilkBucketItem;
        if (canSubstituteFluids && containedFluid != null && isBucket) {
            var remainder = calculateRecipeRemainder(machineSlot, itemKey);
            if (remainder.getCount() == 1 && remainder.is(Items.BUCKET)) {
                return new GenericStack(containedFluid.what(), containedFluid.amount());
            }
        }

        return item;
    }

    @Nullable
    public GenericStack getValidFluid(int machineSlot) {
        var compressed = sparseToCompressed[machineSlot];
        if (compressed != -1) {
            var itemOrFluid = inputs[compressed].possibleInputs[0];
            if (itemOrFluid.what() instanceof AEFluidKey) {
                return itemOrFluid;
            }
        }
        return null;
    }

    private Ingredient getRecipeIngredient(int machineSlot) {
        var patternSlot = toPatternSlot(machineSlot);
        if (patternSlot < 0) {
            return Ingredient.EMPTY;
        }
        if (patternSlot >= this.slotIngredients.size()) {
            return Ingredient.EMPTY;
        }
        return this.slotIngredients.get(patternSlot);
    }

    @Nullable
    private Boolean getTestResult(int slot, AEItemKey key) {
        if (key == null || key.hasComponents()) {
            return null;
        }
        var cache = isValidCache[slot];
        return cache == null ? null : cache.get(key.getItem());
    }

    private void setTestResult(int slot, AEItemKey key, boolean result) {
        if (key != null && !key.hasComponents()) {
            var cache = isValidCache[slot];
            if (cache == null) {
                cache = isValidCache[slot] = new IdentityHashMap<>();
            }
            cache.put(key.getItem(), result);
        }
    }

    private ItemStack getRecipeRemainder(int machineSlot, AEItemKey key) {
        if (!key.hasComponents()) {
            var cache = this.remainderCache[machineSlot];
            var item = key.getItem();
            if (cache != null && cache.containsKey(item)) {
                return cache.get(item);
            }

            var remainder = calculateRecipeRemainder(machineSlot, key);
            if (cache == null) {
                cache = this.remainderCache[machineSlot] = new IdentityHashMap<>();
            }
            cache.put(item, remainder);
            return remainder;
        }

        return calculateRecipeRemainder(machineSlot, key);
    }

    private ItemStack calculateRecipeRemainder(int machineSlot, AEItemKey key) {
        var patternSlot = toPatternSlot(machineSlot);
        if (patternSlot < 0) {
            return ItemStack.EMPTY;
        }
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
            if (!stack.isEmpty()) {
                result[i] = GenericStack.fromItemStack(stack);
            }
        }
        return Arrays.asList(result);
    }

    private static List<GenericStack> condenseStacks(List<GenericStack> sparseInput) {
        var map = new LinkedHashMap<AEKey, Long>();
        for (var input : sparseInput) {
            if (input != null) {
                map.merge(input.what(), input.amount(), Long::sum);
            }
        }
        if (map.isEmpty()) {
            throw new IllegalStateException("No pattern here!");
        }

        var result = new ArrayList<GenericStack>(map.size());
        for (var entry : map.entrySet()) {
            result.add(new GenericStack(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private class Input implements IInput {
        private final int machineSlot;
        private final GenericStack[] possibleInputs;
        private final long multiplier;

        private Input(int machineSlot, GenericStack slotInput, long multiplier) {
            this.machineSlot = machineSlot;
            this.multiplier = multiplier;

            var itemOrFluidInput = getItemOrFluidInput(machineSlot, slotInput);
            if (!canSubstitute) {
                this.possibleInputs = new GenericStack[] { itemOrFluidInput };
            } else {
                var matchingStacks = getRecipeIngredient(machineSlot).getItems();
                this.possibleInputs = new GenericStack[matchingStacks.length + 1];
                this.possibleInputs[0] = itemOrFluidInput;
                for (int i = 0; i < matchingStacks.length; i++) {
                    this.possibleInputs[i + 1] = GenericStack.fromItemStack(matchingStacks[i]);
                }
            }
        }

        @Override
        public GenericStack[] getPossibleInputs() {
            return possibleInputs;
        }

        @Override
        public long getMultiplier() {
            return multiplier;
        }

        @Override
        public boolean isValid(AEKey input, Level level) {
            if (input.matches(possibleInputs[0])) {
                return true;
            }
            return canSubstitute() && input instanceof AEItemKey itemKey
                    && ExtendedTableCraftingPattern.this.isItemValid(machineSlot, itemKey, level);
        }

        @Nullable
        @Override
        public AEKey getRemainingKey(AEKey template) {
            if (template instanceof AEItemKey itemKey) {
                return AEItemKey.of(getRecipeRemainder(machineSlot, itemKey));
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface CraftingGridAccessor {
        void set(int slot, ItemStack stack);
    }
}
