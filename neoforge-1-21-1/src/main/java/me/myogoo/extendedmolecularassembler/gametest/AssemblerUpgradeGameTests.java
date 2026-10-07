package me.myogoo.extendedmolecularassembler.gametest;

import java.util.UUID;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.stacks.KeyCounter;
import appeng.core.definitions.AEItems;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class AssemblerUpgradeGameTests {
    private AssemblerUpgradeGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void exUpgradeKitRegistrationFollowsExtendedAE(GameTestHelper helper) {
        boolean extendedAE = MyotusAPI.integrations().isLoaded(ExtendedAE.class);
        var id = ExtendedMolecularAssembler.makeId("ex_extended_molecular_assembler_upgrade_kit");
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(id) == extendedAE,
                "Ex assembler upgrade kit item registration does not match ExtendedAE availability");
        helper.assertTrue(helper.getLevel().getRecipeManager().byKey(id).isPresent() == extendedAE,
                "Ex assembler upgrade kit recipe registration does not match ExtendedAE availability");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void exUpgradeReturnsPatternToPlayer(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedAE.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var level = (ServerLevel) helper.getLevel();
        var pos = new BlockPos(1, 1, 1);
        BlockState sourceState = IOrientationStrategy.get(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get().defaultBlockState())
                .setOrientation(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get().defaultBlockState(), Direction.NORTH, 1);
        helper.setBlock(pos, sourceState);
        var source = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(pos);
        source.setName("Upgrade Test");
        source.getUpgrades().setItemDirect(0, new ItemStack(AEItems.SPEED_CARD.asItem()));
        source.getMainNode().setOwningPlayerId(42);
        var encoded = encodeTierOnePattern(helper);
        source.getPatternInventory().setItemDirect(0, encoded.copy());

        helper.runAfterDelay(2, () -> {
            helper.assertTrue(source.getMainNode().isReady(), "Source assembler node did not initialize");
            helper.assertTrue(source.getCurrentPattern(0) != null, "Source pattern did not decode");

            var player = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ema_ex_upgrade_test"));
            var kit = new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get(), 2);
            helper.assertTrue(useKit(helper, player, pos, kit) == InteractionResult.CONSUME,
                    "EMA to Ex EMA upgrade failed");
            helper.assertTrue(kit.getCount() == 1, "Survival upgrade kit was not consumed exactly once");
            helper.assertTrue(helper.getBlockState(pos).getBlock() == EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    "Upgrade did not replace EMA with Ex EMA");

            var upgraded = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(pos);
            helper.assertTrue(upgraded.getLaneCount() == ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT,
                    "Upgrade did not initialize all Ex lanes");
            helper.assertTrue(upgraded.getCustomName() != null
                    && "Upgrade Test".equals(upgraded.getCustomName().getString()), "Custom name was lost");
            helper.assertTrue(upgraded.getFront() == source.getFront()
                    && upgraded.getOrientation().getSpin() == source.getOrientation().getSpin(),
                    "Assembler orientation changed");
            helper.assertTrue(upgraded.getUpgrades().getStackInSlot(0).is(AEItems.SPEED_CARD.asItem()),
                    "Speed card was lost");
            var preservedPattern = upgraded.getPatternInventory().getStackInSlot(0);
            helper.assertTrue(preservedPattern.isEmpty(), "Ex assembler retained the converted pattern");
            helper.assertTrue(upgraded.getCurrentPatternStack(0).isEmpty(), "Ex lane zero retained the converted pattern");
            for (int lane = 1; lane < upgraded.getLaneCount(); lane++) {
                helper.assertTrue(upgraded.getCurrentPatternStack(lane).isEmpty(),
                        "Upgrade copied lane zero's pattern into Ex lane " + lane);
            }
            assertPatternStackInInventory(helper, player.getInventory(), encoded);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                    new AABB(helper.absolutePos(pos))).isEmpty(), "Upgrade dropped duplicate inventory");

            var inventory = player.getInventory();
            var clientHost = new ExtendedMolecularAssemblerBlockEntity(upgraded.getType(),
                    upgraded.getBlockPos(), upgraded.getBlockState()) {
                @Override
                public ExtendedTableCraftingPattern getCurrentPattern(int laneIndex) {
                    throw new AssertionError("Client menu bypassed its synchronized pattern cache");
                }
            };
            clientHost.setLevel(level);
            var clientMenu = new ExtendedMolecularAssemblerMenu(1, inventory, clientHost) {
                @Override
                public boolean isClientSide() {
                    return true;
                }
            };
            clientMenu.lanePatterns = roundTrip(helper, ExtendedMolecularAssemblerMenu.LanePatternSync.from(upgraded));
            helper.assertTrue(clientMenu.getCurrentPattern(0) == null,
                    "Ex menu received a pattern after it was returned to the player");
            clientMenu.page = 1;
            clientMenu.showPage();
            clientMenu.page = 0;
            clientMenu.showPage();
            helper.assertTrue(clientMenu.getCurrentPattern(0) == null,
                    "Ex page cycling restored a pattern that had been returned to the player");

            helper.runAfterDelay(2, () -> {
                helper.assertTrue(upgraded.getMainNode().isReady(), "Upgraded Ex node did not initialize");
                helper.assertTrue(upgraded.getMainNode().getNode().getOwningPlayerId() == 42,
                        "Network node owner was lost");

                var saved = new CompoundTag();
                upgraded.saveAdditional(saved, level.registryAccess());
                var restored = (ExtendedMolecularAssemblerBlockEntity) EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER
                        .get().newBlockEntity(pos, upgraded.getBlockState());
                restored.setLevel(level);
                restored.loadTag(saved, level.registryAccess());
                helper.assertTrue(restored.getPatternInventory().isEmpty()
                        && restored.getCurrentPatternStack(0).isEmpty(),
                        "Returned pattern reappeared after Ex save/load");
                assertPatternStackInInventory(helper, player.getInventory(), encoded);
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", timeoutTicks = 50)
    public static void fullInventoryUpgradeDropsPatternAtPlayerForSurvivalAndCreative(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedAE.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var level = (ServerLevel) helper.getLevel();
        var survival = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ema_ex_upgrade_full_test"));
        var creative = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ema_ex_upgrade_creative_full_test"));
        creative.getAbilities().instabuild = true;
        var survivalPos = new BlockPos(1, 1, 1);
        var creativePos = new BlockPos(5, 1, 1);
        helper.setBlock(survivalPos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        helper.setBlock(creativePos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        var survivalAssembler = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(survivalPos);
        var creativeAssembler = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(creativePos);
        var encoded = encodeTierOnePattern(helper);
        survivalAssembler.getPatternInventory().setItemDirect(0, encoded.copy());
        creativeAssembler.getPatternInventory().setItemDirect(0, encoded.copy());
        var survivalKit = fillInventoryWithKit(survival.getInventory());
        var creativeKit = fillInventoryWithKit(creative.getInventory());
        var survivalPlayerPos = helper.absolutePos(survivalPos);
        survival.setPos(survivalPlayerPos.getX() + 0.5, survivalPlayerPos.getY() + 1,
                survivalPlayerPos.getZ() + 4.5);
        var creativePlayerPos = helper.absolutePos(creativePos);
        creative.setPos(creativePlayerPos.getX() + 0.5, creativePlayerPos.getY() + 1,
                creativePlayerPos.getZ() + 4.5);

        helper.runAfterDelay(2, () -> {
            helper.assertTrue(survivalAssembler.getMainNode().isReady()
                    && creativeAssembler.getMainNode().isReady(), "Full-inventory source node did not initialize");
            helper.assertTrue(useKit(helper, survival, survivalPos, survivalKit) == InteractionResult.CONSUME,
                    "Full-inventory survival upgrade failed");
            helper.assertTrue(survivalKit.getCount() == 1, "Survival kit was not consumed before pattern overflow");
            helper.assertTrue(useKit(helper, creative, creativePos, creativeKit) == InteractionResult.CONSUME,
                    "Full-inventory creative upgrade failed");
            helper.assertTrue(creativeKit.getCount() == 2, "Creative full-inventory upgrade consumed the kit");

            var survivalEx = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(survivalPos);
            var creativeEx = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(creativePos);
            helper.assertTrue(survivalEx.getPatternInventory().isEmpty()
                    && creativeEx.getPatternInventory().isEmpty(), "Full-inventory upgrade retained an internal pattern");
            assertNoPatternInInventory(helper, survival.getInventory());
            assertNoPatternInInventory(helper, creative.getInventory());
            assertPatternDropAtPlayer(helper, survival, encoded);
            assertPatternDropAtPlayer(helper, creative, encoded);
            assertNoPatternDropAtAssembler(helper, survivalPos);
            assertNoPatternDropAtAssembler(helper, creativePos);
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(((ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(survivalPos))
                        .getMainNode().isReady(), "Survival upgraded Ex node did not initialize");
                helper.assertTrue(((ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(creativePos))
                        .getMainNode().isReady(), "Creative upgraded Ex node did not initialize");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void exUpgradeKitRefusesWrongBlockAndBusyAssembler(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedAE.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var level = (ServerLevel) helper.getLevel();
        var player = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ema_ex_upgrade_busy_test"));
        var wrongPos = new BlockPos(1, 1, 1);
        helper.setBlock(wrongPos, Blocks.STONE);
        var wrongBlockKit = new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get());
        helper.assertTrue(useKit(helper, player, wrongPos, wrongBlockKit) == InteractionResult.PASS,
                "Upgrade kit claimed a non-assembler block");
        helper.assertTrue(wrongBlockKit.getCount() == 1, "Wrong-block use consumed the kit");

        var missingEntityPos = new BlockPos(2, 1, 1);
        helper.setBlock(missingEntityPos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        helper.getLevel().removeBlockEntity(helper.absolutePos(missingEntityPos));
        var missingEntityKit = new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get());
        helper.assertTrue(useKit(helper, player, missingEntityPos, missingEntityKit) == InteractionResult.FAIL,
                "Upgrade accepted an assembler block without its block entity");
        helper.assertTrue(missingEntityKit.getCount() == 1, "Missing-entity use consumed the kit");

        var pos = new BlockPos(3, 1, 1);
        helper.setBlock(pos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        var source = (ExtendedMolecularAssemblerBlockEntity) helper.getBlockEntity(pos);
        var encoded = encodeTierOnePattern(helper);
        source.getPatternInventory().setItemDirect(0, encoded.copy());
        helper.assertFalse(source.getMainNode().isReady(), "Fresh assembler node initialized before its first tick");
        var uninitializedKit = new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get());
        helper.assertTrue(useKit(helper, player, pos, uninitializedKit) == InteractionResult.FAIL,
                "Upgrade accepted an uninitialized assembler node");
        helper.assertTrue(uninitializedKit.getCount() == 1, "Uninitialized-node use consumed the kit");

        helper.runAfterDelay(2, () -> {
            helper.assertTrue(source.getMainNode().isReady(), "Assembler node did not initialize");
            var busyKit = new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get());
            source.getCraftInventory(0).setItemDirect(0, new ItemStack(Items.DIAMOND));
            helper.assertTrue(useKit(helper, player, pos, busyKit) == InteractionResult.FAIL,
                    "Upgrade accepted an occupied crafting input");
            helper.assertTrue(ItemStack.matches(encoded, source.getPatternInventory().getStackInSlot(0)),
                    "Failed upgrade changed the installed pattern");
            source.getCraftInventory(0).setItemDirect(0, ItemStack.EMPTY);
            source.getCraftInventory(0).setItemDirect(ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT,
                    new ItemStack(Items.DIAMOND));
            helper.assertTrue(useKit(helper, player, pos, busyKit) == InteractionResult.FAIL,
                    "Upgrade accepted an occupied output slot");
            source.getCraftInventory(0).setItemDirect(ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT,
                    ItemStack.EMPTY);
            helper.assertTrue(ItemStack.matches(encoded, source.getPatternInventory().getStackInSlot(0)),
                    "Failed output-slot upgrade changed the installed pattern");
            source.getPatternInventory().setItemDirect(0, ItemStack.EMPTY);

            var pattern = decodedTierOnePattern(helper);
            helper.assertTrue(source.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                    "Could not create pushed crafting job");
            for (int slot = 0; slot <= ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT; slot++) {
                source.getCraftInventory(0).setItemDirect(slot, ItemStack.EMPTY);
            }
            helper.assertTrue(source.getCraftInventory(0).isEmpty(), "Could not clear the pushed job's grid");
            helper.assertFalse(source.canUpgrade(), "Empty grid unexpectedly cleared the active pushed job");
            helper.assertTrue(useKit(helper, player, pos, busyKit) == InteractionResult.FAIL,
                    "Upgrade accepted an assembler with a pushed crafting job");
            helper.assertTrue(busyKit.getCount() == 1, "Busy-assembler use consumed the kit");
            helper.assertTrue(helper.getBlockState(pos).getBlock() == EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    "Busy assembler was replaced");
            helper.succeed();
        });
    }

    private static ItemStack encodeTierOnePattern(GameTestHelper helper) {
        var level = helper.getLevel();
        var match = ExtendedPatternRecipeFinder.find(tierOneGrid(), level).orElseThrow();
        return ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(
                match.recipe(), match.inputs(), match.result(), false, true);
    }

    private static ExtendedTableCraftingPattern decodedTierOnePattern(GameTestHelper helper) {
        var decoded = PatternDetailsHelper.decodePattern(encodeTierOnePattern(helper), helper.getLevel());
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern, "Pattern did not decode as Extended Crafting");
        return (ExtendedTableCraftingPattern) decoded;
    }

    private static NonNullList<ItemStack> tierOneGrid() {
        var grid = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        ItemStack[] inputs = {
                new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.COPPER_INGOT),
                new ItemStack(Items.IRON_INGOT), new ItemStack(Items.REDSTONE), new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.COPPER_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.COPPER_INGOT)
        };
        int offset = (ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - 3) / 2;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                grid.set(offset + x + (offset + y) * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE,
                        inputs[x + y * 3]);
            }
        }
        return grid;
    }

    private static KeyCounter[] countersForPattern(ExtendedTableCraftingPattern pattern) {
        var inputs = pattern.getInputs();
        var counters = new KeyCounter[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            counters[i] = new KeyCounter();
            var primary = inputs[i].getPossibleInputs()[0];
            counters[i].add(primary.what(), primary.amount() * inputs[i].getMultiplier());
        }
        return counters;
    }

    private static ItemStack fillInventoryWithKit(Inventory inventory) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            inventory.setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
        inventory.setItem(0, new ItemStack(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get(), 2));
        return inventory.getItem(0);
    }

    private static void assertPatternStackInInventory(GameTestHelper helper, Inventory inventory,
            ItemStack expected) {
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            var stack = inventory.getItem(slot);
            if (stack.is(EMAItems.EXTENDED_CRAFTING_PATTERN.get())) {
                helper.assertTrue(ItemStack.matches(expected, stack), "Player received a changed pattern stack");
                count += stack.getCount();
            }
        }
        helper.assertTrue(count == 1, "Player inventory did not receive the pattern exactly once");
    }

    private static void assertNoPatternInInventory(GameTestHelper helper, Inventory inventory) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            helper.assertFalse(inventory.getItem(slot).is(EMAItems.EXTENDED_CRAFTING_PATTERN.get()),
                    "Full inventory unexpectedly accepted the pattern");
        }
    }

    private static void assertPatternDropAtPlayer(GameTestHelper helper, FakePlayer player, ItemStack expected) {
        int count = 0;
        for (var drop : helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(1))) {
            if (drop.getItem().is(EMAItems.EXTENDED_CRAFTING_PATTERN.get())) {
                helper.assertTrue(ItemStack.matches(expected, drop.getItem()), "Dropped pattern stack changed");
                count += drop.getItem().getCount();
            }
        }
        helper.assertTrue(count == 1, "Full inventory did not drop the pattern exactly once at the player");
    }

    private static void assertNoPatternDropAtAssembler(GameTestHelper helper, BlockPos pos) {
        for (var drop : helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                new AABB(helper.absolutePos(pos)).inflate(1))) {
            helper.assertFalse(drop.getItem().is(EMAItems.EXTENDED_CRAFTING_PATTERN.get()),
                    "Pattern overflow dropped at the assembler instead of the player");
        }
    }

    private static InteractionResult useKit(GameTestHelper helper, ServerPlayer player,
            BlockPos pos, ItemStack kit) {
        var absolutePos = helper.absolutePos(pos);
        var hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        return EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get().onItemUseFirst(kit,
                new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, kit, hit));
    }

    private static ExtendedMolecularAssemblerMenu.LanePatternSync roundTrip(GameTestHelper helper,
            ExtendedMolecularAssemblerMenu.LanePatternSync source) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            source.writeToPacket(buffer);
            return new ExtendedMolecularAssemblerMenu.LanePatternSync(buffer);
        } finally {
            buffer.release();
        }
    }
}
