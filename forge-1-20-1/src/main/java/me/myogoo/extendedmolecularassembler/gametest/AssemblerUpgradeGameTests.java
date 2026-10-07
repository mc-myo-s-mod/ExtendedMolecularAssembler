package me.myogoo.extendedmolecularassembler.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import appeng.api.orientation.IOrientationStrategy;
import appeng.core.definitions.AEItems;
import com.mojang.authlib.GameProfile;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.item.AssemblerUpgradeKitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class AssemblerUpgradeGameTests {
    private AssemblerUpgradeGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void kitRecipesRespectOptionalMods(GameTestHelper helper) {
        boolean extendedAE = EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null;
        var recipes = helper.getLevel().getRecipeManager();
        for (String tier : List.of("epic", "legendary")) {
            boolean tableExists = BuiltInRegistries.ITEM.containsKey(
                    new ResourceLocation("extendedcrafting", tier + "_table"));
            for (String prefix : List.of("", "ex_")) {
                var id = new ResourceLocation(ExtendedMolecularAssembler.MODID,
                        prefix + tier + "_molecular_assembler_upgrade_kit");
                helper.assertTrue(recipes.byKey(id).isPresent() == (tableExists && (prefix.isEmpty() || extendedAE)),
                        "Incorrect recipe condition: " + id);
                helper.assertTrue(BuiltInRegistries.ITEM.containsKey(id) == (prefix.isEmpty() || extendedAE),
                        "Incorrect item registration: " + id);
            }
            var conversionKit = ExtendedMolecularAssembler.makeId(tier + "_molecular_assembler_ex_upgrade_kit");
            helper.assertTrue(recipes.byKey(conversionKit).isPresent() == (tableExists && extendedAE),
                    "Incorrect Ex conversion recipe condition: " + conversionKit);
            helper.assertTrue(BuiltInRegistries.ITEM.containsKey(conversionKit) == extendedAE,
                    "Incorrect Ex conversion kit registration: " + conversionKit);
        }
        var exKit = new ResourceLocation(ExtendedMolecularAssembler.MODID,
                "ex_extended_molecular_assembler_upgrade_kit");
        helper.assertTrue(recipes.byKey(exKit).isPresent() == extendedAE, "Incorrect base Ex kit recipe condition");
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(exKit) == extendedAE,
                "Incorrect base Ex kit registration");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void kitsPreserveIdleAssemblersAcrossAllTransitions(GameTestHelper helper) {
        var player = player(helper);
        var transitions = transitions();
        for (int i = 0; i < transitions.size(); i++) {
            var transition = transitions.get(i);
            var pos = transitionPos(i);
            BlockState orientedState = IOrientationStrategy.get(transition.source().defaultBlockState())
                    .setOrientation(transition.source().defaultBlockState(), Direction.NORTH, 1);
            helper.setBlock(pos, orientedState);
            var oldAssembler = assembler(helper, pos);
            oldAssembler.setName("Upgrade Test");
            oldAssembler.getPatternInventory().setItemDirect(0, pattern("transition_" + i));
            oldAssembler.getUpgrades().setItemDirect(0, new ItemStack(AEItems.SPEED_CARD.asItem()));
            oldAssembler.getMainNode().setOwningPlayerId(42);
        }
        helper.runAfterDelay(2, () -> {
            for (int i = 0; i < transitions.size(); i++) {
                var transition = transitions.get(i);
                var pos = transitionPos(i);
                var oldAssembler = assembler(helper, pos);
                helper.assertTrue(oldAssembler.getMainNode().isReady(), "Source node did not initialize");
                player.getInventory().clearContent();
                var kitStack = new ItemStack(transition.kit(), 2);
                var result = useKit(helper, player, pos, kitStack);
                helper.assertTrue(result == InteractionResult.CONSUME, "Kit did not consume: " + transition.kit());
                helper.assertTrue(kitStack.getCount() == 1, "Survival kit count was not reduced");
                helper.assertTrue(helper.getBlockState(pos).getBlock() == transition.target(), "Wrong upgraded block");
                var upgraded = assembler(helper, pos);
                helper.assertTrue(upgraded.getGridSide() == transition.target().getGridSide(), "Wrong upgraded grid side");
                helper.assertTrue(upgraded.getLaneCount() == (transition.target().isExAssembler() ? 8 : 1),
                        "Wrong upgraded lane count");
                helper.assertTrue(upgraded.getCustomName() != null
                        && "Upgrade Test".equals(upgraded.getCustomName().getString()), "Custom name was lost");
                var expectedPattern = pattern("transition_" + i);
                boolean convertsToEx = !transition.source().isExAssembler() && transition.target().isExAssembler();
                helper.assertTrue(convertsToEx
                        ? upgraded.getPatternInventory().getStackInSlot(0).isEmpty()
                        : ItemStack.matches(upgraded.getPatternInventory().getStackInSlot(0), expectedPattern),
                        "Incorrect target pattern after upgrade");
                helper.assertTrue(patternCount(player, expectedPattern) == (convertsToEx ? 1 : 0),
                        "Pattern was not returned exactly once, or was returned by a nonconversion kit");
                helper.assertTrue(upgraded.getUpgrades().getStackInSlot(0).is(AEItems.SPEED_CARD.asItem()),
                        "Speed card was lost");
                helper.assertTrue(upgraded.getFront() == oldAssembler.getFront()
                        && upgraded.getOrientation().getSpin() == oldAssembler.getOrientation().getSpin(),
                        "Orientation changed");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        new AABB(helper.absolutePos(pos))).isEmpty(), "Upgrade dropped duplicate inventory");
            }
            helper.runAfterDelay(2, () -> {
                for (int i = 0; i < transitions.size(); i++) {
                    var upgraded = assembler(helper, transitionPos(i));
                    helper.assertTrue(upgraded.getMainNode().isReady(), "Upgraded node did not initialize normally");
                    helper.assertTrue(upgraded.getMainNode().getNode().getOwningPlayerId() == 42, "Node owner was lost");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void failedBlockReplacementRestoresAssemblers(GameTestHelper helper) {
        var transitions = transitions();
        for (int i = 0; i < transitions.size(); i++) {
            var state = transitions.get(i).source().defaultBlockState();
            helper.setBlock(transitionPos(i), IOrientationStrategy.get(state).setOrientation(state, Direction.NORTH, 1));
            assembler(helper, transitionPos(i)).getMainNode().setOwningPlayerId(42);
        }
        helper.runAfterDelay(2, () -> {
            for (int i = 0; i < transitions.size(); i++) {
                var transition = transitions.get(i);
                var pos = transitionPos(i);
                var absolutePos = helper.absolutePos(pos);
                var oldState = helper.getBlockState(pos);
                var source = assembler(helper, pos);
                helper.assertTrue(source.getMainNode().isReady(), "Source node did not initialize");
                var expectedPattern = pattern("rollback_" + i);
                source.setName("Rollback Test");
                source.getPatternInventory().setItemDirect(0, expectedPattern.copy());
                source.getUpgrades().setItemDirect(0, new ItemStack(AEItems.SPEED_CARD.asItem()));
                var player = player(helper);
                var kit = new ItemStack(transition.kit(), 2);
                var injectedFailure = new IllegalStateException("Expected upgrade replacement failure");
                var injected = new AtomicBoolean();
                Consumer<BlockEvent.NeighborNotifyEvent> listener = event -> {
                    if (event.getLevel() == helper.getLevel() && event.getPos().equals(absolutePos)
                            && event.getState().is(transition.target()) && injected.compareAndSet(false, true)) {
                        throw injectedFailure;
                    }
                };
                MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, false,
                        BlockEvent.NeighborNotifyEvent.class, listener);
                try {
                    useKit(helper, player, pos, kit);
                    helper.fail("Replacement exception did not propagate");
                } catch (RuntimeException failure) {
                    helper.assertTrue(failure == injectedFailure, "Unexpected upgrade exception: " + failure);
                } finally {
                    MinecraftForge.EVENT_BUS.unregister(listener);
                }
                helper.assertTrue(injected.get(), "Replacement failure was not injected");
                helper.assertTrue(helper.getBlockState(pos).equals(oldState), "Failed upgrade changed block or orientation");
                var restored = assembler(helper, pos);
                helper.assertTrue(ItemStack.matches(restored.getPatternInventory().getStackInSlot(0), expectedPattern),
                        "Failed upgrade lost pattern NBT");
                helper.assertTrue(restored.getUpgrades().getStackInSlot(0).is(AEItems.SPEED_CARD.asItem()),
                        "Failed upgrade lost speed card");
                helper.assertTrue(restored.getCustomName() != null
                        && "Rollback Test".equals(restored.getCustomName().getString()), "Failed upgrade lost name");
                helper.assertTrue(kit.getCount() == 2, "Failed upgrade consumed kit");
                helper.assertTrue(patternCount(player, expectedPattern) == 0, "Failed upgrade returned duplicate pattern");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        new AABB(absolutePos).inflate(2)).isEmpty(), "Failed upgrade dropped inventory");
            }
            helper.runAfterDelay(2, () -> {
                for (int i = 0; i < transitions.size(); i++) {
                    var restored = assembler(helper, transitionPos(i));
                    helper.assertTrue(restored.getMainNode().isReady(), "Restored node did not initialize");
                    helper.assertTrue(restored.getMainNode().getNode().getOwningPlayerId() == 42,
                            "Failed upgrade lost node owner");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void exConversionsDropPatternsAtFullSurvivalPlayer(GameTestHelper helper) {
        checkFullInventoryConversions(helper, false, 2);
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void exConversionsDropPatternsAtFullCreativePlayer(GameTestHelper helper) {
        checkFullInventoryConversions(helper, true, 2);
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void exConversionsReturnPatternIntoConsumedKitSlot(GameTestHelper helper) {
        checkFullInventoryConversions(helper, false, 1);
    }

    private static void checkFullInventoryConversions(GameTestHelper helper, boolean creative, int kitCount) {
        var conversions = conversions();
        for (int i = 0; i < conversions.size(); i++) {
            helper.setBlock(transitionPos(i), conversions.get(i).source());
        }
        helper.runAfterDelay(2, () -> {
            for (int i = 0; i < conversions.size(); i++) {
                var transition = conversions.get(i);
                var pos = transitionPos(i);
                var player = player(helper);
                player.getAbilities().instabuild = creative;
                var playerPos = Vec3.atCenterOf(helper.absolutePos(pos)).add(0, 0, 4);
                player.setPos(playerPos.x, playerPos.y, playerPos.z);
                for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                    player.getInventory().setItem(slot, new ItemStack(Items.STONE, 64));
                }
                var expectedPattern = pattern("full_" + creative + "_" + kitCount + "_" + i);
                var source = assembler(helper, pos);
                helper.assertTrue(source.getMainNode().isReady(), "Source node did not initialize");
                source.getPatternInventory().setItemDirect(0, expectedPattern.copy());
                var kit = new ItemStack(transition.kit(), kitCount);
                helper.assertTrue(useKit(helper, player, pos, kit) == InteractionResult.CONSUME,
                        "Full-inventory conversion failed");
                helper.assertTrue(kit.getCount() == (creative ? kitCount : kitCount - 1),
                        "Incorrect kit consumption");
                helper.assertTrue(player.getAbilities().instabuild == creative, "Creative ability changed");
                helper.assertTrue(helper.getBlockState(pos).getBlock() == transition.target(),
                        "Conversion did not replace block");
                helper.assertTrue(assembler(helper, pos).getPatternInventory().getStackInSlot(0).isEmpty(),
                        "Converted assembler retained pattern");
                boolean freedSlot = !creative && kitCount == 1;
                helper.assertTrue(patternCount(player, expectedPattern) == (freedSlot ? 1 : 0),
                        "Pattern was not returned into the freed kit slot");
                if (freedSlot) {
                    helper.assertTrue(ItemStack.matches(player.getMainHandItem(), expectedPattern),
                            "Consumed kit slot did not receive exact pattern NBT");
                }
                var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        player.getBoundingBox().inflate(2), entity -> entity.getItem().is(Items.PAPER));
                helper.assertTrue(drops.size() == (freedSlot ? 0 : 1),
                        "Full inventory lost or duplicated returned pattern");
                if (!freedSlot) {
                    helper.assertTrue(ItemStack.matches(drops.get(0).getItem(), expectedPattern),
                            "Dropped pattern NBT changed");
                    helper.assertTrue(Math.abs(drops.get(0).getX() - player.getX()) < 0.01
                            && Math.abs(drops.get(0).getZ() - player.getZ()) < 0.01,
                            "Pattern was not dropped at the player");
                    drops.get(0).discard();
                }
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        new AABB(helper.absolutePos(pos))).isEmpty(), "Pattern was dropped at the assembler");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void refusedExConversionsKeepPatternAndEmptyConversionsReturnNothing(GameTestHelper helper) {
        var conversions = conversions();
        for (int i = 0; i < conversions.size(); i++) {
            helper.setBlock(transitionPos(i), conversions.get(i).source());
        }
        helper.runAfterDelay(2, () -> {
            for (int i = 0; i < conversions.size(); i++) {
                var transition = conversions.get(i);
                var pos = transitionPos(i);
                var player = player(helper);
                var playerPos = Vec3.atCenterOf(helper.absolutePos(pos)).add(0, 0, 4);
                player.setPos(playerPos.x, playerPos.y, playerPos.z);
                var source = assembler(helper, pos);
                helper.assertTrue(source.getMainNode().isReady(), "Source node did not initialize");
                var expectedPattern = pattern("busy_" + i);
                source.getPatternInventory().setItemDirect(0, expectedPattern.copy());
                source.getCraftInventory(0).setItemDirect(0, new ItemStack(Items.DIAMOND));
                var kit = new ItemStack(transition.kit(), 2);
                helper.assertTrue(useKit(helper, player, pos, kit) == InteractionResult.FAIL,
                        "Conversion accepted a busy assembler");
                helper.assertTrue(kit.getCount() == 2, "Refused conversion consumed kit");
                helper.assertTrue(assembler(helper, pos) == source, "Refused conversion replaced source");
                helper.assertTrue(ItemStack.matches(source.getPatternInventory().getStackInSlot(0), expectedPattern),
                        "Refused conversion changed pattern NBT");
                helper.assertTrue(patternCount(player, expectedPattern) == 0,
                        "Refused conversion returned pattern before replacing block");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        player.getBoundingBox().inflate(5)).isEmpty(), "Refused conversion dropped pattern");

                source.getCraftInventory(0).setItemDirect(0, ItemStack.EMPTY);
                source.getPatternInventory().setItemDirect(0, ItemStack.EMPTY);
                helper.assertTrue(useKit(helper, player, pos, kit) == InteractionResult.CONSUME,
                        "Empty-pattern conversion failed");
                helper.assertTrue(helper.getBlockState(pos).getBlock() == transition.target(),
                        "Empty-pattern conversion did not replace block");
                helper.assertTrue(assembler(helper, pos).getPatternInventory().getStackInSlot(0).isEmpty(),
                        "Empty-pattern conversion created a pattern");
                helper.assertTrue(player.getInventory().countItem(Items.PAPER) == 0,
                        "Empty-pattern conversion returned a pattern");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        player.getBoundingBox().inflate(5)).isEmpty(), "Empty-pattern conversion dropped an item");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void kitsRefuseWrongTierAndBusyLane(GameTestHelper helper) {
        var player = player(helper);
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        var exPos = new BlockPos(2, 1, 1);
        if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            helper.setBlock(exPos, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
        }
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(assembler(helper, pos).getMainNode().isReady(), "Source node did not initialize");
            var skipKit = new ItemStack(EMAItems.LEGENDARY_ASSEMBLER_UPGRADE_KIT.get());
            helper.assertTrue(useKit(helper, player, pos, skipKit) == InteractionResult.PASS,
                    "Kit skipped a tier");
            helper.assertTrue(skipKit.getCount() == 1, "Wrong-tier kit was consumed");
            helper.assertTrue(helper.getBlockState(pos).getBlock() == EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    "Wrong-tier kit changed the block");

            if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
                var exAssembler = assembler(helper, exPos);
                helper.assertTrue(exAssembler.getMainNode().isReady(), "Ex source node did not initialize");
                exAssembler.getCraftInventory(1).setItemDirect(0, new ItemStack(Items.DIAMOND));
                var exKit = new ItemStack(EMAItems.EX_EPIC_ASSEMBLER_UPGRADE_KIT.get());
                helper.assertTrue(useKit(helper, player, exPos, exKit) == InteractionResult.FAIL,
                        "Kit accepted an occupied nonfirst lane");
                helper.assertTrue(helper.getBlockState(exPos).getBlock() == EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                        "Busy assembler was replaced");
                helper.assertTrue(exKit.getCount() == 1, "Refused kit was consumed");
                helper.assertTrue(exAssembler.getCraftInventory(1).getStackInSlot(0).is(Items.DIAMOND),
                        "Refused upgrade lost an ingredient");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void creativeUpgradeDoesNotConsumeKit(GameTestHelper helper) {
        var player = player(helper);
        player.getAbilities().instabuild = true;
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(assembler(helper, pos).getMainNode().isReady(), "Source node did not initialize");
            var kit = new ItemStack(EMAItems.EPIC_ASSEMBLER_UPGRADE_KIT.get());
            helper.assertTrue(useKit(helper, player, pos, kit) == InteractionResult.CONSUME,
                    "Creative upgrade failed");
            helper.assertTrue(kit.getCount() == 1, "Creative kit was consumed");
            helper.assertTrue(helper.getBlockState(pos).getBlock() == EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(),
                    "Creative upgrade did not replace block");
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(assembler(helper, pos).getMainNode().isReady(),
                        "Creative upgraded node did not initialize");
                helper.succeed();
            });
        });
    }

    private static List<Transition> transitions() {
        var transitions = new ArrayList<Transition>();
        transitions.add(new Transition(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EPIC_ASSEMBLER_UPGRADE_KIT.get(), EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get()));
        transitions.add(new Transition(EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(),
                EMAItems.LEGENDARY_ASSEMBLER_UPGRADE_KIT.get(), EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get()));
        if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            transitions.add(new Transition(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get(), EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));
            transitions.add(new Transition(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EX_EPIC_ASSEMBLER_UPGRADE_KIT.get(), EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get()));
            transitions.add(new Transition(EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EX_LEGENDARY_ASSEMBLER_UPGRADE_KIT.get(), EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get()));
            transitions.add(new Transition(EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EPIC_ASSEMBLER_EX_UPGRADE_KIT.get(), EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get()));
            transitions.add(new Transition(EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.LEGENDARY_ASSEMBLER_EX_UPGRADE_KIT.get(), EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get()));
        }
        return transitions;
    }

    private static BlockPos transitionPos(int index) {
        return new BlockPos(1 + index % 3, 1, 1 + index / 3);
    }

    private static List<Transition> conversions() {
        return transitions().stream()
                .filter(transition -> !transition.source().isExAssembler() && transition.target().isExAssembler())
                .toList();
    }

    private static ItemStack pattern(String marker) {
        var pattern = new ItemStack(Items.PAPER);
        var data = new CompoundTag();
        data.putString("marker", marker);
        data.putIntArray("slots", new int[] { 0, 7, 24 });
        pattern.getOrCreateTag().put("upgrade_test", data);
        return pattern;
    }

    private static int patternCount(FakePlayer player, ItemStack pattern) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            var stack = player.getInventory().getItem(slot);
            if (ItemStack.isSameItemSameTags(stack, pattern)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static ExtendedMolecularAssemblerBlockEntity assembler(GameTestHelper helper, BlockPos pos) {
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof ExtendedMolecularAssemblerBlockEntity, "Assembler BE missing");
        return (ExtendedMolecularAssemblerBlockEntity) blockEntity;
    }

    private static FakePlayer player(GameTestHelper helper) {
        return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_upgrade_test"));
    }

    private static InteractionResult useKit(GameTestHelper helper, FakePlayer player, BlockPos pos, ItemStack kit) {
        player.setItemInHand(InteractionHand.MAIN_HAND, kit);
        var absolutePos = helper.absolutePos(pos);
        var hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        return ((AssemblerUpgradeKitItem) kit.getItem()).onItemUseFirst(kit,
                new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, kit, hit));
    }

    private record Transition(ExtendedMolecularAssemblerBlock source, AssemblerUpgradeKitItem kit,
            ExtendedMolecularAssemblerBlock target) {
    }
}
