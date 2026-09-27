package me.myogoo.extendedmolecularassembler.gametest;

import java.util.UUID;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.mojang.authlib.GameProfile;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class LargeTerminalGameTests {
    private LargeTerminalGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void terminalGridSizesSurviveSaveAndMenuCreation(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_large_terminal"));
        for (int side : new int[] { 9, 11, 13 }) {
            var item = switch (side) {
                case 11 -> EMAParts.EPIC_PATTERN_ENCODING_TERMINAL.asItem();
                case 13 -> EMAParts.LEGENDARY_PATTERN_ENCODING_TERMINAL.asItem();
                default -> EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL.asItem();
            };
            helper.assertTrue(((ExtendedPatternEncodingTerminalPart) item.createPart()).getGridSide() == side,
                    "Registered terminal has the wrong grid size: " + side);
            var host = new ExtendedPatternEncodingTerminalPart(item, side) {
                @Override
                public Level getLevel() {
                    return helper.getLevel();
                }

                @Override
                public void markForSave() {
                }
            };
            var logic = host.getExtendedPatternEncodingLogic();
            var inputs = logic.getEncodedInputInv();
            helper.assertTrue(inputs.size() == side * side, "Incorrect terminal inventory size");
            inputs.setStack(inputs.size() - 1, new GenericStack(AEItemKey.of(Items.DIAMOND), 1));
            var saved = new CompoundTag();
            logic.writeToNBT(saved);
            logic.clearEncodedInputs();
            logic.readFromNBT(saved);
            helper.assertTrue(AEItemKey.of(Items.DIAMOND).equals(inputs.getKey(inputs.size() - 1)),
                    "Last grid slot was lost on save/load: " + side);
            var menu = new ExtendedPatternEncodingTermMenu(0, player.getInventory(), host) {
                @Override
                public boolean isClientSide() {
                    return true;
                }
            };
            helper.assertTrue(menu.getCraftingGridSlots().length == side * side,
                    "Terminal menu slot count differs from its inventory");
            helper.assertTrue(menu.getGridSide() == side, "Menu grid side differs from its host");
        }
        helper.succeed();
    }
}
