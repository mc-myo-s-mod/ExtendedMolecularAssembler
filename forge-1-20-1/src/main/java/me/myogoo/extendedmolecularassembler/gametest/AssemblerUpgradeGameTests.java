package me.myogoo.extendedmolecularassembler.gametest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import net.minecraftforge.common.util.FakePlayer;
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
            oldAssembler.getPatternInventory().setItemDirect(0, new ItemStack(Items.PAPER));
            oldAssembler.getUpgrades().setItemDirect(0, new ItemStack(AEItems.SPEED_CARD.asItem()));
            oldAssembler.getMainNode().setOwningPlayerId(42);
        }
        helper.runAfterDelay(2, () -> {
            for (int i = 0; i < transitions.size(); i++) {
                var transition = transitions.get(i);
                var pos = transitionPos(i);
                var oldAssembler = assembler(helper, pos);
                helper.assertTrue(oldAssembler.getMainNode().isReady(), "Source node did not initialize");
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
                helper.assertTrue(upgraded.getPatternInventory().getStackInSlot(0).is(Items.PAPER), "Pattern was lost");
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

    private static ExtendedMolecularAssemblerBlockEntity assembler(GameTestHelper helper, BlockPos pos) {
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof ExtendedMolecularAssemblerBlockEntity, "Assembler BE missing");
        return (ExtendedMolecularAssemblerBlockEntity) blockEntity;
    }

    private static FakePlayer player(GameTestHelper helper) {
        return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_upgrade_test"));
    }

    private static InteractionResult useKit(GameTestHelper helper, FakePlayer player, BlockPos pos, ItemStack kit) {
        var absolutePos = helper.absolutePos(pos);
        var hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        return ((AssemblerUpgradeKitItem) kit.getItem()).onItemUseFirst(kit,
                new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, kit, hit));
    }

    private record Transition(ExtendedMolecularAssemblerBlock source, AssemblerUpgradeKitItem kit,
            ExtendedMolecularAssemblerBlock target) {
    }
}
