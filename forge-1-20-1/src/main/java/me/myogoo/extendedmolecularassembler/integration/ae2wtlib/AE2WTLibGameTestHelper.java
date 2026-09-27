package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.WTDefinition;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedmolecularassembler.item.WirelessExtendedPatternEncodingTerminalItem;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import me.myogoo.myotus.api.wt.AddTerminalEvent;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class AE2WTLibGameTestHelper {
    private static final String LOGIC_TAG = "extendedmolecularassembler:extendedPatternEncoding";
    private static final List<Tier> TIERS = List.of(
            new Tier(EMAAE2WTLibIntegration.TERMINAL_NAME, 9, 4, Items.DIAMOND, LOGIC_TAG),
            new Tier(EMAAE2WTLibIntegration.EPIC_TERMINAL_NAME, 11, 5, Items.EMERALD,
                    "extendedmolecularassembler:epicPatternEncoding"),
            new Tier(EMAAE2WTLibIntegration.LEGENDARY_TERMINAL_NAME, 13, 6, Items.GOLD_INGOT,
                    "extendedmolecularassembler:legendaryPatternEncoding"));

    private AE2WTLibGameTestHelper() {
    }

    public static void assertTieredPatternStorage(GameTestHelper helper) {
        helper.assertTrue(AddTerminalEvent.didRun(), "Myotus wireless registration hook did not run");
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema-wut-test"));
        var definitions = new ArrayList<WTDefinition>();

        for (var tier : TIERS) {
            var definition = WUTHandler.wirelessTerminals.get(tier.terminalName());
            helper.assertTrue(definition != null, tier.terminalName() + " missing from WUT registration");
            if (definition == null || !(definition.item() instanceof WirelessExtendedPatternEncodingTerminalItem item)) {
                helper.fail(tier.terminalName() + " is not registered with the EMA wireless terminal item");
                return;
            }
            definitions.add(definition);

            var directHost = item.getMenuHost(player, 0, new ItemStack(item), null);
            if (!(directHost instanceof IExtendedPatternEncodingTerminalHost patternHost)) {
                helper.fail(tier.terminalName() + " direct item did not create an EMA pattern host");
                return;
            }
            assertCapacity(helper, player, patternHost, tier);
        }

        var universalItem = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(AE2wtlib.MOD_NAME, "wireless_universal_terminal"));
        helper.assertTrue(universalItem != null, "AE2WTLib universal terminal item is missing");
        if (universalItem == null) {
            return;
        }

        var universal = new ItemStack(universalItem);
        for (var tier : TIERS) {
            universal.getOrCreateTag().putBoolean(tier.terminalName(), true);
        }

        for (int index = 0; index < TIERS.size(); index++) {
            var tier = TIERS.get(index);
            var definition = definitions.get(index);
            universal.getOrCreateTag().putString("currentTerminal", tier.terminalName());
            WTMenuHost wirelessHost = definition.wTMenuHostFactory().create(
                    player, null, universal, (returningPlayer, subMenu) -> { });
            if (!(wirelessHost instanceof IExtendedPatternEncodingTerminalHost patternHost)) {
                helper.fail(tier.terminalName() + " WUT factory did not create an EMA pattern host");
                return;
            }
            assertCapacity(helper, player, patternHost, tier);

            var inputs = patternHost.getExtendedPatternEncodingLogic().getEncodedInputInv();
            inputs.setStack(inputs.size() - 1, new GenericStack(AEItemKey.of(tier.marker()), 1));
            patternHost.setRememberedRecipeType(rememberedType(tier));
            patternHost.markForSave();

            universal.getOrCreateTag().putString("currentTerminal",
                    TIERS.get((index + 1) % TIERS.size()).terminalName());
            helper.assertTrue(patternHost.getGridSide() == tier.gridSide()
                            && inputs.size() == tier.gridSide() * tier.gridSide(),
                    tier.terminalName() + " host changed grid size when WUT selection changed");
        }

        var restoredStack = ItemStack.of(universal.save(new CompoundTag()));
        for (int index = 0; index < TIERS.size(); index++) {
            var tier = TIERS.get(index);
            var restoredHost = definitions.get(index).wTMenuHostFactory().create(
                    player, null, restoredStack, (returningPlayer, subMenu) -> { });
            if (!(restoredHost instanceof IExtendedPatternEncodingTerminalHost patternHost)) {
                helper.fail(tier.terminalName() + " restored WUT factory did not create an EMA pattern host");
                return;
            }

            var inputs = patternHost.getExtendedPatternEncodingLogic().getEncodedInputInv();
            helper.assertTrue(inputs.size() == tier.gridSide() * tier.gridSide()
                            && AEItemKey.of(tier.marker()).equals(inputs.getKey(inputs.size() - 1)),
                    tier.terminalName() + " highest input slot did not survive its WUT NBT roundtrip");
            helper.assertTrue(rememberedType(tier).equals(patternHost.getRememberedRecipeType()),
                    tier.terminalName() + " remembered table selection did not survive its WUT NBT roundtrip");
            helper.assertTrue(restoredStack.getOrCreateTag().contains(tier.logicTag(), Tag.TAG_COMPOUND),
                    tier.terminalName() + " did not use its tier-specific WUT data tag");
        }
    }

    private static void assertCapacity(
            GameTestHelper helper,
            Player player,
            IExtendedPatternEncodingTerminalHost host,
            Tier tier) {
        var expectedSize = tier.gridSide() * tier.gridSide();
        helper.assertTrue(host.getGridSide() == tier.gridSide()
                        && host.getExtendedPatternEncodingLogic().getEncodedInputInv().size() == expectedSize,
                tier.terminalName() + " host has the wrong grid capacity");
        var menu = new ExtendedPatternEncodingTermMenu(0, player.getInventory(), host);
        helper.assertTrue(menu.getGridSide() == tier.gridSide()
                        && menu.getCraftingGridSlots().length == expectedSize,
                tier.terminalName() + " menu has the wrong grid capacity");
    }

    private static ExtendedPatternRecipeType rememberedType(Tier tier) {
        return new ExtendedPatternRecipeType(
                ExtendedPatternEncodingTermMenu.RecipeProvider.EXTENDED_CRAFTING,
                tier.tableTier(),
                tier.gridSide());
    }

    private record Tier(String terminalName, int gridSide, int tableTier, Item marker, String logicTag) {
    }
}
