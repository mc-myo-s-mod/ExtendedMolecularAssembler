package me.myogoo.extendedmolecularassembler.integration.advancedae;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.core.definitions.AEBlocks;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.crafting.*;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Runs the actual mixed-in crafting methods with a mounted storage that can under-deliver and refuse rollback.
public final class QuantumCraftingGameTestChecks {
    private QuantumCraftingGameTestChecks() {
    }

    public static void assertCraftingAndPower(GameTestHelper helper) {
        assertDurabilityIdentity(helper);
        var pattern = fixturePattern(helper);
        var job = new ExtendedQuantumCraftingJob(pattern);
        for (int i = 0; i < job.craftingInputs().size(); i++) {
            helper.assertTrue(job.requiredInputTotal(job.craftingInputs().get(i), 3)
                            == job.requiredInputTotal(i, 3),
                    "Legacy input lookup differs from the indexed input contract");
        }
        helper.assertTrue(job.requiredInputTotal(new GenericStack(AEItemKey.of(Items.DIRT), 1), 3) == 0,
                "Legacy input lookup accepted a key outside the pattern");
        helper.assertTrue(job.remainingItems.size() == 3, "Different return-item data was merged");
        var bucket = new GenericStack(AEItemKey.of(namedBucket("input")), 1);
        var emerald = new GenericStack(AEItemKey.of(Items.EMERALD), 1);
        var gold = new GenericStack(AEItemKey.of(Items.GOLD_INGOT), 1);
        var lapis = new GenericStack(AEItemKey.of(Items.LAPIS_LAZULI), 1);
        helper.assertTrue(pattern.getInputs()[0].isValid(emerald.what(), helper.getLevel())
                && pattern.getInputs()[1].isValid(lapis.what(), helper.getLevel()),
                "Fixture substitutes must each preserve the encoded result alone");
        helper.assertTrue(job.withInputs(List.of(emerald, lapis, bucket), helper.getLevel()) == null,
                "Combined substitutes changed the result but were accepted");
        helper.assertTrue(job.withInputs(List.of(new GenericStack(AEItemKey.of(Items.DIRT), 1), gold, bucket),
                helper.getLevel()) == null, "Invalid substitute was accepted");
        var resolved = job.withInputs(List.of(emerald, gold, bucket), helper.getLevel());
        helper.assertTrue(resolved != null && resolved.remainingItems.size() == 3,
                "Valid substitutes lost distinct return items");
        helper.assertTrue(resolved.remainingItems.stream().anyMatch(stack -> sameKey(stack, namedBucket("emerald"))),
                "Substitute return data was calculated from encoded inputs");

        helper.assertTrue(resolved.isInputConsumed(bucket) && !resolved.isStackAnInput(namedBucket("returned")),
                "Different input and return data was treated as the same key");

        helper.setBlock(new BlockPos(1, 1, 1), EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER.get());
        helper.setBlock(new BlockPos(2, 1, 1), AEBlocks.ENERGY_CELL.block());
        helper.setBlock(new BlockPos(2, 2, 1), AEBlocks.CREATIVE_ENERGY_CELL.block());
        var crafter = (QuantumCrafterEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(crafter.getGridNode() != null, "Quantum test node did not join a grid");
            var grid = crafter.getGridNode().getGrid();
            var inventory = new TestStorage();
            IStorageProvider provider = mounts -> mounts.mount(inventory);
            grid.getStorageService().addGlobalStorageProvider(provider);
            try {
                var output = (AppEngInternalInventory) field(crafter, "outputInv");
                @SuppressWarnings("unchecked")
                var sent = (List<GenericStack>) field(crafter, "sendList");
                inventory.stacks.add(bucket.what(), 8);
                inventory.stacks.add(emerald.what(), 8);
                inventory.stacks.add(lapis.what(), 8);
                invoke(crafter, "ema$performCraft", new Class<?>[] {ExtendedQuantumCraftingJob.class, int.class}, job, 3);
                helper.assertTrue(inventory.stacks.get(emerald.what()) == 8 && inventory.stacks.get(lapis.what()) == 8 && inventory.stacks.get(bucket.what()) == 8
                        && sent.isEmpty() && output.isEmpty(), "Rejected combination changed items");
                inventory.stacks.clear();
                inventory.stacks.add(bucket.what(), 8);
                inventory.stacks.add(emerald.what(), 8);
                inventory.stacks.add(gold.what(), 8);
                invoke(crafter, "ema$performCraft", new Class<?>[] {ExtendedQuantumCraftingJob.class, int.class}, job, 3);
                helper.assertTrue(inventory.stacks.get(emerald.what()) == 5 && inventory.stacks.get(gold.what()) == 5 && inventory.stacks.get(bucket.what()) == 5,
                        "Actual craft did not debit selected inputs");
                helper.assertTrue(sent.stream().anyMatch(stack -> stack.what().equals(AEItemKey.of(Items.DIAMOND))
                        && stack.amount() == 3), "Actual craft did not emit three encoded outputs");
                helper.assertTrue(count(output, namedBucket("emerald")) == 3
                        && count(output, namedBucket("gold_ingot")) == 3 && count(output, namedBucket("returned")) == 3, "Actual craft merged or changed return data");

                output.clear();
                sent.clear();
                inventory.partial = true;
                inventory.refuseInsert = true;
                invoke(crafter, "ema$performCraft", new Class<?>[] {ExtendedQuantumCraftingJob.class, int.class}, job, 3);
                helper.assertTrue(output.isEmpty(), "Partial extraction emitted local returns");
                helper.assertTrue(sent.stream().noneMatch(stack -> stack.what().equals(AEItemKey.of(Items.DIAMOND))),
                        "Partial extraction emitted a craft");
                var rolledBack = sent.stream().filter(stack -> stack.what().equals(emerald.what()))
                        .mapToLong(GenericStack::amount).sum();
                helper.assertTrue(inventory.stacks.get(emerald.what()) + rolledBack == 5
                        && inventory.stacks.get(gold.what()) == 5 && inventory.stacks.get(bucket.what()) == 5, "Refused rollback lost selected inputs");
                sent.clear();
                inventory.partial = false;
                inventory.refuseInsert = false;
                inventory.stacks.clear();
                inventory.stacks.add(emerald.what(), 8);
                inventory.stacks.add(bucket.what(), 8);
                var export = (net.neoforged.neoforge.common.ModConfigSpec.BooleanValue) EMAConfig.COMMON.getValues().get(List.of("general", "export"));
                var previousExport = export.get();
                try {
                    export.set(false);
                    var maximum = (Integer) invoke(crafter, "ema$maxCrafts",
                            new Class<?>[] {ExtendedQuantumCraftingJob.class, int.class}, job, 8);
                    helper.assertTrue(maximum == 4, "Shared substitute stock was counted twice: " + maximum);
                    invoke(crafter, "ema$performCraft", new Class<?>[] {ExtendedQuantumCraftingJob.class, int.class}, job, maximum);
                    helper.assertTrue(inventory.stacks.get(emerald.what()) == 0
                            && inventory.stacks.get(bucket.what()) == 4, "Shared substitute craft debited wrong totals");
                    helper.assertTrue(count(output, namedBucket("emerald")) == 8
                            && count(output, namedBucket("returned")) == 4, "Shared substitute returns were lost");
                } finally {
                    export.set(previousExport);
                }
                sent.clear();
                assertTickPower(helper, crafter, grid);
                var cell = (appeng.blockentity.networking.EnergyCellBlockEntity) helper.getBlockEntity(new BlockPos(2, 1, 1));
                cell.injectAEPower(1000, Actionable.MODULATE);
                helper.setBlock(new BlockPos(2, 2, 1), net.minecraft.world.level.block.Blocks.AIR);
            } finally {
                grid.getStorageService().removeGlobalStorageProvider(provider);
            }
            helper.runAfterDelay(2, () -> {
                var tickGrid = crafter.getGridNode().getGrid();
                tickGrid.getStorageService().addGlobalStorageProvider(provider);
                try {
                    assertPersistedTickCrafting(helper, crafter, inventory);
                } finally {
                    tickGrid.getStorageService().removeGlobalStorageProvider(provider);
                }
                helper.succeed();
            });
        });
    }

    private static void assertTickPower(GameTestHelper helper, QuantumCrafterEntity crafter, IGrid grid) {
        var value = (net.neoforged.neoforge.common.ModConfigSpec.DoubleValue) EMAConfig.COMMON.getValues()
                .get(List.of("blocks", "extended_quantum_crafter", "craftingPowerMultiplier"));
        var previous = value.get();
        try {
            for (var multiplier : new double[] {0, 0.0001, 0.5, 1, 2}) {
                value.set(multiplier);
                crafter.injectAEPower(1000, Actionable.MODULATE);
                var before = crafter.getInternalCurrentPower();
                invoke(crafter, "lambda$tickingRequest$2", new Class<?>[] {int.class, IGrid.class}, 2, grid);
                var actual = before - crafter.getInternalCurrentPower();
                helper.assertTrue(Math.abs(actual - PowerMultiplier.CONFIG.multiply(20 * multiplier)) < 0.001,
                        "Actual tick cost ignored multiplier " + multiplier + ": " + actual);
            }
        } finally {
            value.set(previous);
        }
    }

    private static void assertDurabilityIdentity(GameTestHelper helper) {
        for (var differentData : new boolean[] {false, true}) {
            withFixturePattern(helper, true, differentData, pattern -> {
                var job = new ExtendedQuantumCraftingJob(pattern);
                var tool = new GenericStack(AEItemKey.of(fixtureTool()), 1);
                var inputs = List.of(tool, new GenericStack(AEItemKey.of(Items.GOLD_INGOT), 1),
                        new GenericStack(AEItemKey.of(namedBucket("input")), 1));
                helper.assertTrue(job.consumesDurability != differentData,
                        "Tool wear must match all data except Damage");
                var resolved = job.withInputs(inputs, helper.getLevel());
                if (differentData) {
                    helper.assertTrue(resolved != null && resolved.isInputConsumed(tool),
                            "Distinct tool data must consume the original key");
                    var returned = fixtureTool();
                    returned.setDamageValue(returned.getDamageValue() + 1);
                    setToolData(returned, "returned");
                    helper.assertTrue(resolved.remainingItems.stream().anyMatch(stack -> sameKey(stack, returned))
                            && !resolved.isStackAnInput(returned), "Distinct tool return key was lost");
                } else {
                    helper.assertTrue(resolved == null, "True tool wear must remain unsupported");
                }
            });
        }
    }

    private static ItemStack fixtureTool() {
        var stack = new ItemStack(Items.IRON_PICKAXE);
        stack.setDamageValue(3);
        setToolData(stack, "input");
        return stack;
    }

    private static void setToolData(ItemStack stack, String value) {
        var data = new net.minecraft.nbt.CompoundTag();
        data.putString("emaToolIdentity", value);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(data));
    }

    private static void assertPersistedTickCrafting(GameTestHelper helper, QuantumCrafterEntity crafter,
            TestStorage inventory) {
        withFixturePattern(helper, false, false, pattern -> {
            var patterns = (AppEngInternalInventory) crafter.getPatternInventory();
            var output = (AppEngInternalInventory) crafter.getOutputInv();
            output.clear();
            inventory.stacks.clear();
            var iron = AEItemKey.of(Items.IRON_INGOT);
            var gold = AEItemKey.of(Items.GOLD_INGOT);
            var bucket = AEItemKey.of(namedBucket("input"));
            inventory.stacks.add(iron, 3);
            inventory.stacks.add(gold, 3);
            inventory.stacks.add(bucket, 3);
            patterns.setItemDirect(0, pattern.getDefinition().toStack());
            invoke(crafter, "makeCraftingRecipeList", new Class<?>[0]);
            crafter.setStockAmount(0, 0, 2);
            crafter.setMaxCrafted(0, 1);
            crafter.toggleEnablePattern(0);
            var saved = crafter.saveWithoutMetadata(helper.getLevel().registryAccess());
            patterns.setItemDirect(0, ItemStack.EMPTY);
            crafter.toggleEnablePattern(0);
            crafter.loadTag(saved, helper.getLevel().registryAccess());
            helper.assertTrue(sameKey(patterns.getStackInSlot(0), pattern.getDefinition().toStack())
                    && crafter.getEnabledPatternSlots().get(0)
                    && crafter.getPatternConfigInputs(0).get(iron) == 2
                    && crafter.getPatternConfigOutput(0).getSecond() == 1,
                    "Tick job pattern/stock/output settings did not survive reload");

            var multiplier = (net.neoforged.neoforge.common.ModConfigSpec.DoubleValue) EMAConfig.COMMON.getValues()
                    .get(List.of("blocks", "extended_quantum_crafter", "craftingPowerMultiplier"));
            var export = (net.neoforged.neoforge.common.ModConfigSpec.BooleanValue) EMAConfig.COMMON.getValues().get(List.of("general", "export"));
            var previousMultiplier = multiplier.get();
            var previousExport = export.get();
            var cell = (appeng.blockentity.networking.EnergyCellBlockEntity) helper.getBlockEntity(new BlockPos(2, 1, 1));
            var grid = crafter.getGridNode().getGrid();
            helper.assertTrue(cell.getGridNode() != null && cell.getGridNode().getGrid() == grid
                    && crafter.getMainNode().isActive(), "Finite power cell did not join the active Quantum grid");
            try {
                export.set(false);
                for (var factor : new double[] {0, 0.0001, 0.5, 1, 2}) {
                    multiplier.set(factor);
                    var cost = PowerMultiplier.CONFIG.multiply(10 * factor);
                    output.clear();
                    inventory.stacks.clear();
                    inventory.stacks.add(iron, 3);
                    inventory.stacks.add(gold, 3);
                    inventory.stacks.add(bucket, 3);
                    crafter.extractAEPower(1000000, Actionable.MODULATE, PowerMultiplier.ONE);
                    grid.getEnergyService().extractAEPower(1000000, Actionable.MODULATE, PowerMultiplier.ONE);
                    helper.assertTrue(grid.getEnergyService().extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0,
                            "Unlimited/residual grid energy invalidates low-energy cases");
                    if (cost > 0) {
                        for (var available : new double[] {0, cost / 2}) {
                            crafter.extractAEPower(1000000, Actionable.MODULATE, PowerMultiplier.ONE);
                            crafter.injectAEPower(available, Actionable.MODULATE);
                            var before = crafter.getInternalCurrentPower();
                            crafter.tickingRequest(crafter.getGridNode(), 1);
                            helper.assertTrue(crafter.isWorking() && inventory.stacks.get(iron) == 3
                                    && inventory.stacks.get(gold) == 3 && inventory.stacks.get(bucket) == 3
                                    && output.isEmpty() && inventory.stacks.get(AEItemKey.of(Items.DIAMOND)) == 0
                                    && Math.abs(before - crafter.getInternalCurrentPower()) < 0.000001,
                                    "Low/partial tick energy consumed inputs, emitted items or debited power: " + factor);
                        }
                    }
                    // Tick a persisted job through internal power, then through AAE's grid fallback.
                    for (var internalPower : new boolean[] {true, false}) {
                        output.clear();
                        inventory.stacks.clear();
                        inventory.stacks.add(iron, 3);
                        inventory.stacks.add(gold, 3);
                        inventory.stacks.add(bucket, 3);
                        crafter.extractAEPower(1000000, Actionable.MODULATE, PowerMultiplier.ONE);
                        grid.getEnergyService().extractAEPower(1000000, Actionable.MODULATE, PowerMultiplier.ONE);
                        cell.injectAEPower(1000, Actionable.MODULATE);
                        if (internalPower) {
                            crafter.injectAEPower(1000, Actionable.MODULATE);
                        }
                        var before = crafter.getInternalCurrentPower();
                        var gridBefore = cell.getAECurrentPower();
                        crafter.tickingRequest(crafter.getGridNode(), 1);
                        helper.assertTrue(inventory.stacks.get(iron) == 2 && inventory.stacks.get(gold) == 2
                                && inventory.stacks.get(bucket) == 2,
                                "Powered persisted tick job did not preserve stock and debit one craft: " + factor + " internal=" + internalPower + " working=" + crafter.isWorking() + " stocks=" + inventory.stacks.get(iron) + "/" + inventory.stacks.get(gold) + "/" + inventory.stacks.get(bucket) + " active=" + crafter.getMainNode().isActive() + " work=" + invoke(crafter, "hasCraftWork", new Class<?>[0]));
                        helper.assertTrue(inventory.stacks.get(AEItemKey.of(Items.DIAMOND)) + count(output, new ItemStack(Items.DIAMOND)) == 1
                                && Math.abs(before - crafter.getInternalCurrentPower() - (internalPower ? cost : 0)) < 0.000001,
                                "Powered tick output or internal multiplier cost differs: " + factor);
                        // AAE exports sendList first; its short-circuit defers the three local returns to the next tick.
                        var transferCost = PowerMultiplier.CONFIG.multiply(3.0 / iron.getAmountPerOperation());
                        helper.assertTrue(Math.abs(gridBefore - cell.getAECurrentPower()
                                - transferCost - (internalPower ? 0 : cost)) < 0.000001,
                                "Grid craft/transfer debit differs or transfer cost was multiplied twice: " + factor + " internal=" + internalPower + " actual=" + (gridBefore - cell.getAECurrentPower()) + " expected=" + (transferCost + (internalPower ? 0 : cost)) + " localReturns=" + count(output, namedBucket("returned")));
                        helper.assertTrue(count(output, namedBucket("iron_ingot")) + inventory.stacks.get(AEItemKey.of(namedBucket("iron_ingot"))) == 1
                                && count(output, namedBucket("gold_ingot")) + inventory.stacks.get(AEItemKey.of(namedBucket("gold_ingot"))) == 1
                                && count(output, namedBucket("returned")) + inventory.stacks.get(AEItemKey.of(namedBucket("returned"))) == 1,
                                "Tick crafting lost distinct remainder data");
                        var after = crafter.getInternalCurrentPower();
                        var gridAfter = cell.getAECurrentPower();
                        crafter.tickingRequest(crafter.getGridNode(), 1);
                        helper.assertTrue(inventory.stacks.get(iron) == 2
                                && inventory.stacks.get(gold) == 2 && inventory.stacks.get(bucket) == 2
                                && inventory.stacks.get(AEItemKey.of(Items.DIAMOND)) + count(output, new ItemStack(Items.DIAMOND)) == 1
                                && Math.abs(after - crafter.getInternalCurrentPower()) < 0.000001
                                && Math.abs(gridAfter - cell.getAECurrentPower() - transferCost) < 0.000001
                                && output.isEmpty()
                                && inventory.stacks.get(AEItemKey.of(namedBucket("iron_ingot"))) == 1
                                && inventory.stacks.get(AEItemKey.of(namedBucket("gold_ingot"))) == 1
                                && inventory.stacks.get(AEItemKey.of(namedBucket("returned"))) == 1,
                                "Persisted stock/output limit or deferred remainder transfer/debit differs");
                    }
                    output.clear();
                    inventory.stacks.clear();
                    inventory.stacks.add(iron, 3);
                    inventory.stacks.add(gold, 3);
                    inventory.stacks.add(bucket, 3);
                    inventory.partial = true;
                    crafter.injectAEPower(1000, Actionable.MODULATE);
                    cell.injectAEPower(1000, Actionable.MODULATE);
                    var before = crafter.getInternalCurrentPower();
                    crafter.tickingRequest(crafter.getGridNode(), 1);
                    helper.assertTrue(inventory.stacks.get(iron) == 3 && inventory.stacks.get(gold) == 3
                            && inventory.stacks.get(bucket) == 3 && output.isEmpty()
                            && inventory.stacks.get(AEItemKey.of(Items.DIAMOND)) == 0
                            && Math.abs(before - crafter.getInternalCurrentPower() - cost) < 0.000001,
                            "Failed actual tick extraction changed items or craft-gate debit: " + factor);
                    inventory.partial = false;
                }
            } finally {
                multiplier.set(previousMultiplier);
                export.set(previousExport);
                inventory.partial = false;
                patterns.setItemDirect(0, ItemStack.EMPTY);
                if (crafter.getEnabledPatternSlots().get(0)) {
                    crafter.toggleEnablePattern(0);
                }
                invoke(crafter, "makeCraftingRecipeList", new Class<?>[0]);
            }
        });
    }

    private static long count(AppEngInternalInventory inventory, ItemStack expected) {
        long count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStackInSlot(i);
            if (sameKey(stack, expected)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static boolean sameKey(ItemStack left, ItemStack right) {
        return java.util.Objects.equals(AEItemKey.of(left), AEItemKey.of(right));
    }

    private static ItemStack namedBucket(String name) {
        var stack = new ItemStack(Items.BUCKET);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }

    private static Object field(QuantumCrafterEntity crafter, String name) {
        try {
            var field = QuantumCrafterEntity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(crafter);
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("Quantum fixture field " + name, error);
        }
    }

    private static Object invoke(QuantumCrafterEntity crafter, String name, Class<?>[] parameters, Object... arguments) {
        try {
            var method = QuantumCrafterEntity.class.getDeclaredMethod(name, parameters);
            method.setAccessible(true);
            return method.invoke(crafter, arguments);
        } catch (ReflectiveOperationException error) {
            throw new AssertionError("Quantum fixture method " + name, error);
        }
    }

    private static ExtendedTableCraftingPattern fixturePattern(GameTestHelper helper) {
        var result = new ExtendedTableCraftingPattern[1];
        withFixturePattern(helper, false, false, pattern -> result[0] = pattern);
        return result[0];
    }

    private static void withFixturePattern(GameTestHelper helper, boolean tool, boolean differentData,
            java.util.function.Consumer<ExtendedTableCraftingPattern> check) {
        var ingredients = NonNullList.of(Ingredient.EMPTY, tool ? Ingredient.of(Items.IRON_PICKAXE) : Ingredient.of(Items.IRON_INGOT, Items.EMERALD), Ingredient.of(Items.GOLD_INGOT, Items.LAPIS_LAZULI, Items.EMERALD), Ingredient.of(Items.BUCKET));
        var recipe = new ShapelessRecipe("", CraftingBookCategory.MISC, new ItemStack(Items.DIAMOND), ingredients) {
            @Override
            public ItemStack assemble(CraftingInput input, net.minecraft.core.HolderLookup.Provider registries) {
                boolean emerald = false, lapis = false;
                for (int i = 0; i < input.size(); i++) {
                    emerald |= input.getItem(i).is(Items.EMERALD);
                    lapis |= input.getItem(i).is(Items.LAPIS_LAZULI);
                }
                return new ItemStack(emerald && lapis ? Items.NETHERITE_INGOT : Items.DIAMOND);
            }

            @Override
            public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
                var result = NonNullList.withSize(input.size(), ItemStack.EMPTY);
                for (int i = 0; i < input.size(); i++) {
                    if (input.getItem(i).is(Items.IRON_PICKAXE)) {
                        var remainder = input.getItem(i).copy();
                        remainder.setDamageValue(remainder.getDamageValue() + 1);
                        if (differentData) {
                            setToolData(remainder, "returned");
                        }
                        result.set(i, remainder);
                    } else if (!input.getItem(i).isEmpty()) {
                        result.set(i, namedBucket(input.getItem(i).is(Items.BUCKET) ? "returned" : net.minecraft.core.registries.BuiltInRegistries.ITEM
                                .getKey(input.getItem(i).getItem()).getPath()));
                    }
                }
                return result;
            }
        };
        var holder = new RecipeHolder<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("extendedmolecularassembler", "gametest/quantum"), recipe);
        var manager = helper.getLevel().getRecipeManager();
        var previous = new ArrayList<>(manager.getRecipes());
        var recipes = new ArrayList<>(previous);
        recipes.add(holder);
        try {
            manager.replaceRecipes(recipes);
            var stack = new ItemStack(EMAItems.EXTENDED_CRAFTING_PATTERN.get());
            var inputs = new ItemStack[9];
            Arrays.fill(inputs, ItemStack.EMPTY);
            inputs[0] = tool ? fixtureTool() : new ItemStack(Items.IRON_INGOT);
            inputs[1] = new ItemStack(Items.GOLD_INGOT);
            inputs[2] = namedBucket("input");
            ExtendedTableCraftingPattern.encode(stack, holder, inputs, new ItemStack(Items.DIAMOND), true, false);
            check.accept(new ExtendedTableCraftingPattern(AEItemKey.of(stack), helper.getLevel()));
        } finally {
            manager.replaceRecipes(previous);
        }
    }
    private static final class TestStorage implements MEStorage {
        final KeyCounter stacks = new KeyCounter();
        boolean partial;
        boolean refuseInsert;

        @Override
        public Component getDescription() {
            return Component.literal("Quantum regression storage");
        }

        @Override
        public long insert(AEKey key, long amount, Actionable mode, IActionSource source) {
            if (this.refuseInsert) {
                return 0;
            }
            if (mode == Actionable.MODULATE) {
                this.stacks.add(key, amount);
            }
            return amount;
        }

        @Override
        public long extract(AEKey key, long amount, Actionable mode, IActionSource source) {
            var extracted = Math.min(amount, this.stacks.get(key));
            if (mode == Actionable.MODULATE) {
                if (this.partial) {
                    extracted = Math.max(0, extracted - 1);
                }
                this.stacks.remove(key, extracted);
            }
            return extracted;
        }

        @Override
        public void getAvailableStacks(KeyCounter output) {
            output.addAll(this.stacks);
        }
    }
}
