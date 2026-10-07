package me.myogoo.extendedmolecularassembler.gametest;

import java.util.Map;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.CraftingPlan;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.crafting.ExportCraftingPlanGuard;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanHighlightsPacket;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class CraftingPlanHighlightGameTests {
    @GameTest(template = "empty", timeoutTicks = 20)
    public static void tableTiersKeepCraftingPlanHighlights(GameTestHelper helper) {
        helper.assertTrue(ExportMECraftingProviderTier.EPIC.colorRgb() == 0x483E48,
                "Epic highlight must match the provider texture");
        helper.assertTrue(ExportMECraftingProviderTier.LEGENDARY.colorRgb() == 0xBE0202,
                "Legendary highlight must match the provider texture");
        helper.assertTrue(ExportMECraftingProviderTier.requiredFor(ExtendedPatternTableTypes.VANILLA_CRAFTING, 1)
                == ExportMECraftingProviderTier.BASIC, "Vanilla recipes must retain the Basic table mapping");
        for (int tier = 1; tier <= 4; tier++) {
            var provider = ExportMECraftingProviderTier.requiredFor(ExtendedPatternTableTypes.reAvaritia(tier), tier);
            helper.assertTrue(provider.tier() == tier && provider != ExportMECraftingProviderTier.byTier(tier),
                    "Re:Avaritia must retain its own table colors for tier " + tier);
        }
        helper.assertTrue(ExportMECraftingProviderTier.requiredFor(ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4)
                == ExportMECraftingProviderTier.XTREME, "AvaritiaNeo must retain the Extreme table mapping");

        int checkedRecipes = 0;
        for (int tier = 1; tier <= 6; tier++) {
            var provider = ExportMECraftingProviderTier.byTier(tier);
            helper.assertTrue(ExportMECraftingProviderTier.requiredFor(ExtendedPatternTableTypes.extendedCrafting(tier), tier)
                    == provider, "Wrong Extended Crafting provider for tier " + tier);
            var recipe = helper.getLevel().getRecipeManager()
                    .byKey(ExtendedMolecularAssembler.makeId("gametest/ec_tier_" + tier)).orElse(null);
            if (recipe == null) {
                continue;
            }
            var inputs = TableRecipeAdapters.of(recipe).slotIngredients().stream()
                    .map(ingredient -> ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0].copy())
                    .toArray(ItemStack[]::new);
            var encoded = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(recipe, inputs,
                    recipe.getResultItem(helper.getLevel().registryAccess()), false, false);
            var pattern = PatternDetailsHelper.decodePattern(encoded, helper.getLevel());
            helper.assertTrue(pattern != null, "Failed to decode tier " + tier + " highlight fixture");
            var plan = new CraftingPlan(pattern.getPrimaryOutput(), 1, false, false,
                    new KeyCounter(), new KeyCounter(), new KeyCounter(), Map.of(pattern, 1L));
            var required = ExportCraftingPlanGuard.getRequiredProviders(plan);
            var output = pattern.getPrimaryOutput().what();
            helper.assertTrue(required.size() == 1 && required.get(output) == provider,
                    "Crafting plan lost the table tier for " + tier);

            for (boolean exportMode : new boolean[] { false, true }) {
                for (int mask : new int[] { 0, 1 << provider.ordinal() }) {
                    var highlights = ExportCraftingPlanGuard.getEntryHighlights(required, mask, exportMode);
                    var highlight = highlights.get(output);
                    boolean missing = exportMode && mask == 0;
                    helper.assertTrue(highlight != null && highlight.provider() == provider
                            && highlight.missingProvider() == missing, "Wrong highlight state for tier " + tier);
                    helper.assertTrue(provider.planHighlightColor(missing)
                            == ((missing ? 0x66 : 0x22) << 24 | provider.colorRgb()),
                            "Wrong highlight color/opacity for tier " + tier);
                    var packet = new EMACraftConfirmPlanHighlightsPacket(17, highlights);
                    var buffer = new FriendlyByteBuf(Unpooled.buffer());
                    try {
                        packet.write(buffer);
                        helper.assertTrue(packet.equals(EMACraftConfirmPlanHighlightsPacket.decode(buffer))
                                && !buffer.isReadable(), "Highlight packet changed tier " + tier);
                    } finally {
                        buffer.release();
                    }
                }
            }
            checkedRecipes++;
        }
        ExtendedMolecularAssembler.LOGGER.info("EMA crafting-plan highlight checks passed: {} recipe tiers", checkedRecipes);
        helper.succeed();
    }
}
