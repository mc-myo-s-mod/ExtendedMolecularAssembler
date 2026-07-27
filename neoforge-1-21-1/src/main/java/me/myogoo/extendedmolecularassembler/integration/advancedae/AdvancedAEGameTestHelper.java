package me.myogoo.extendedmolecularassembler.integration.advancedae;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;

import java.util.Objects;

public final class AdvancedAEGameTestHelper {
    private AdvancedAEGameTestHelper() {
    }

    public static void assertAdvancedQuantumCrafterRejectsExtendedPattern(
            GameTestHelper helper,
            ItemStack patternStack) {
        var menu = createQuantumCrafterMenu(
                helper,
                ResourceLocation.fromNamespaceAndPath("advanced_ae", "quantum_crafter"),
                "AdvancedAE Quantum Crafter");
        var patternSlot = getPatternSlot(helper, menu);

        helper.assertFalse(menu.isValidForSlot(patternSlot, patternStack),
                "AdvancedAE Quantum Crafter menu accepted an EMA extended encoded pattern");
        helper.assertFalse(patternSlot.mayPlace(patternStack),
                "AdvancedAE Quantum Crafter pattern slot accepted an EMA extended encoded pattern");
    }

    public static void assertExtendedQuantumCrafterAcceptsExtendedPattern(
            GameTestHelper helper,
            ItemStack patternStack) {
        var menu = createQuantumCrafterMenu(
                helper,
                ExtendedMolecularAssembler.makeId("extended_quantum_crafter"),
                "EMA Extended Quantum Crafter");
        var patternSlot = getPatternSlot(helper, menu);

        helper.assertTrue(menu.isValidForSlot(patternSlot, patternStack),
                "EMA Extended Quantum Crafter menu rejected an EMA extended encoded pattern");
        helper.assertTrue(patternSlot.mayPlace(patternStack),
                "EMA Extended Quantum Crafter pattern slot rejected an EMA extended encoded pattern");
    }

    private static AEBaseMenu createQuantumCrafterMenu(
            GameTestHelper helper,
            ResourceLocation blockId,
            String name) {
        var block = BuiltInRegistries.BLOCK.get(blockId);
        helper.assertTrue(block != Blocks.AIR, "Missing " + name + " block " + blockId);

        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof QuantumCrafterEntity,
                name + " block " + blockId + " did not create a QuantumCrafterEntity");

        var player = FakePlayerFactory.getMinecraft((ServerLevel) helper.getLevel());
        return new QuantumCrafterMenu(1, player.getInventory(), (QuantumCrafterEntity) blockEntity);
    }

    private static Slot getPatternSlot(GameTestHelper helper, AEBaseMenu menu) {
        var patternSlots = menu.getSlots(SlotSemantics.MACHINE_INPUT);
        assertEqual(helper, 9, patternSlots.size(), "AdvancedAE Quantum Crafter pattern slot count");
        return patternSlots.getFirst();
    }

    private static void assertEqual(GameTestHelper helper, Object expected, Object actual, String name) {
        if (!Objects.equals(expected, actual)) {
            helper.fail(name + ": expected " + expected + ", got " + actual);
        }
    }
}
