package me.myogoo.extendedmolecularassembler.gametest;

import appeng.capabilities.Capabilities;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.util.AECableType;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.api.annotation.AdvancedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo;
import me.myogoo.extendedmolecularassembler.api.annotation.ExPatternProvider;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAEPlus;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedTerminal;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.menu.EMASlotSemantics;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerOutputSlot;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerPatternSlot;
import appeng.menu.locator.MenuLocators;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.myogoo.extendedmolecularassembler.integration.advancedae.AdvancedAEGameTestHelper;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.AE2WTLibGameTestHelper;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEGameTestHelper;
import me.myogoo.extendedmolecularassembler.integration.itemlist.ExtendedPatternRecipeTransfer;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeMatch;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class ExtendedPatternGameTests {
    private static final List<PatternCase> EXTENDED_CRAFTING_CASES = List.of(
            new PatternCase("Extended Crafting tier 1", "ec_tier_1", ExtendedPatternTableTypes.extendedCrafting(1), 1,
                    Items.DIAMOND,
                    new String[] {
                            "CIC",
                            "IRI",
                            "CIC"
                    },
                    Map.of('C', Items.COPPER_INGOT, 'I', Items.IRON_INGOT, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 2", "ec_tier_2", ExtendedPatternTableTypes.extendedCrafting(2), 2,
                    Items.EMERALD,
                    new String[] {
                            "GGGGG",
                            "GLLLG",
                            "GLRLG",
                            "GLLLG",
                            "GGGGG"
                    },
                    Map.of('G', Items.GOLD_INGOT, 'L', Items.LAPIS_LAZULI, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 3", "ec_tier_3", ExtendedPatternTableTypes.extendedCrafting(3), 3,
                    Items.NETHERITE_SCRAP,
                    new String[] {
                            "CCCCCCC",
                            "CDDDDDC",
                            "CDGGGDC",
                            "CDGRGDC",
                            "CDGGGDC",
                            "CDDDDDC",
                            "CCCCCCC"
                    },
                    Map.of('C', Items.COBBLESTONE, 'D', Items.DIAMOND, 'G', Items.GOLD_INGOT, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 4", "ec_tier_4", ExtendedPatternTableTypes.extendedCrafting(4), 4,
                    Items.EMERALD_BLOCK,
                    new String[] {
                            "CCCCCCCCC",
                            "CLLLLLLLC",
                            "CLRRRRRLC",
                            "CLRIIIRLC",
                            "CLRIGIRLC",
                            "CLRIIIRLC",
                            "CLRRRRRLC",
                            "CLLLLLLLC",
                            "CCCCCCCCC"
                    },
                    Map.of('C', Items.COPPER_INGOT, 'L', Items.LAPIS_LAZULI, 'R', Items.REDSTONE,
                            'I', Items.IRON_INGOT, 'G', Items.GOLD_INGOT)));

    private static final List<PatternCase> RE_AVARITIA_CASES = List.of(
            new PatternCase("Re:Avaritia tier 1", "re_tier_1", ExtendedPatternTableTypes.reAvaritia(1), 1,
                    Items.AMETHYST_SHARD,
                    new String[] {
                            "STS",
                            "TBT",
                            "STS"
                    },
                    Map.of('S', Items.STONE, 'T', Items.STICK, 'B', Items.BONE)),
            new PatternCase("Re:Avaritia tier 2", "re_tier_2", ExtendedPatternTableTypes.reAvaritia(2), 2,
                    Items.QUARTZ,
                    new String[] {
                            "OOOOO",
                            "ORRRO",
                            "ORERO",
                            "ORRRO",
                            "OOOOO"
                    },
                    Map.of('O', Items.OBSIDIAN, 'R', Items.REDSTONE, 'E', Items.ENDER_PEARL)),
            new PatternCase("Re:Avaritia tier 3", "re_tier_3", ExtendedPatternTableTypes.reAvaritia(3), 3,
                    Items.DIAMOND_BLOCK,
                    new String[] {
                            "BBBBBBB",
                            "BQQQQQB",
                            "BQEEEQB",
                            "BQENEQB",
                            "BQEEEQB",
                            "BQQQQQB",
                            "BBBBBBB"
                    },
                    Map.of('B', Items.BLACKSTONE, 'Q', Items.QUARTZ, 'E', Items.ENDER_PEARL, 'N', Items.NETHER_STAR)),
            new PatternCase("Re:Avaritia tier 4", "re_tier_4", ExtendedPatternTableTypes.reAvaritia(4), 4,
                    Items.NETHERITE_INGOT,
                    new String[] {
                            "AAAAAAAAA",
                            "AQQQQQQQA",
                            "AQRRRRRQA",
                            "AQRDDDRQA",
                            "AQRDNDRQA",
                            "AQRDDDRQA",
                            "AQRRRRRQA",
                            "AQQQQQQQA",
                            "AAAAAAAAA"
                    },
                    Map.of('A', Items.AMETHYST_SHARD, 'Q', Items.QUARTZ, 'R', Items.REDSTONE,
                            'D', Items.DIAMOND, 'N', Items.NETHER_STAR)));

    private static final List<PatternCase> AVARITIA_NEO_CASES = List.of(
            new PatternCase("AvaritiaNeo extreme", "dev/avaritianeo_xtreme_vanilla_test",
                    ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4,
                    Items.NETHERITE_INGOT,
                    new String[] {
                            "OOOOOOOOO",
                            "OIIIIIIIO",
                            "OIGGGGGIO",
                            "OIGDDDGIO",
                            "OIGDNDGIO",
                            "OIGDDDGIO",
                            "OIGGGGGIO",
                            "OIIIIIIIO",
                            "OOOOOOOOO"
                    },
                    Map.of('O', Items.OBSIDIAN, 'I', Items.IRON_INGOT, 'G', Items.GOLD_INGOT,
                            'D', Items.DIAMOND, 'N', Items.NETHER_STAR)));

    private ExtendedPatternGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void recipeTypesRejectNonTableRecipes(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        var shaped = recipes.byKey(new ResourceLocation("minecraft", "crafting_table")).orElseThrow();
        var shapeless = recipes.byKey(new ResourceLocation("minecraft", "oak_planks")).orElseThrow();
        var smelting = recipes.byKey(new ResourceLocation("minecraft", "iron_ingot_from_smelting_iron_ore"))
                .orElseThrow();
        for (var recipe : List.of(shaped, shapeless, smelting)) {
            helper.assertTrue(!TableRecipeAdapters.isExtended(recipe),
                    "Non-table recipe classified as extended: " + recipe.getId());
            helper.assertTrue(!ExtendedPatternRecipeTransfer.canTransfer(recipe),
                    "Non-table recipe accepted for extended transfer: " + recipe.getId());
        }
        assertEqual(helper, 3, TableRecipeAdapters.of(shaped).sideLength(), "Vanilla shaped adapter");
        assertEqual(helper, 3, TableRecipeAdapters.of(shapeless).sideLength(), "Vanilla shapeless adapter");
        assertEqual(helper, ExtendedPatternEncodingTermMenu.RecipeProvider.EXTENDED_CRAFTING,
                ExtendedPatternEncodingTermMenu.RecipeProvider.of(shaped), "Vanilla provider fallback");
        try {
            TableRecipeAdapters.of(smelting);
        } catch (IllegalArgumentException expected) {
            helper.succeed();
            return;
        }
        helper.fail("Smelting recipe must not have a table adapter");
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void optionalRegistrationsFollowMyotusIntegrations(GameTestHelper helper) {
        boolean extendedAE = MyotusAPI.integrations().isLoaded(ExPatternProvider.class);
        boolean extendedAEPlusStyleContent = extendedAE
                && (MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)
                        || EMAConfig.standaloneExtendedAEPlusContent());
        boolean advancedAE = MyotusAPI.integrations().isLoaded(AdvancedAE.class);
        boolean ae2wtlib = MyotusAPI.integrations().isLoaded(AE2WTLib.class);

        var mods = ModList.get();
        assertEqual(helper, mods.isLoaded("expatternprovider"), extendedAE, "ExtendedAE integration detection");
        assertEqual(helper, mods.isLoaded("extendedcrafting"),
                MyotusAPI.integrations().isLoaded(ExtendedCrafting.class), "Extended Crafting integration detection");
        assertEqual(helper, mods.isLoaded("advanced_ae"), advancedAE, "AdvancedAE integration detection");
        assertEqual(helper, mods.isLoaded("ae2wtlib"), ae2wtlib, "AE2WTLib integration detection");

        assertOptionalBlockRegistration(helper, "ex_extended_molecular_assembler", extendedAE);
        assertOptionalBlockRegistration(helper, "extended_assembler_matrix_pattern_core", extendedAE);
        assertOptionalBlockRegistration(helper, "extended_assembler_matrix_crafting_core", extendedAE);
        assertOptionalBlockRegistration(helper, "extended_assembler_matrix_pattern_uploader", extendedAE);
        assertOptionalBlockRegistration(helper, "extended_assembler_matrix_pattern_core_plus", extendedAE);
        assertOptionalBlockRegistration(helper, "extended_assembler_matrix_crafting_core_plus", extendedAE);
        for (var tier : new String[] { "epic", "legendary" }) {
            assertOptionalBlockRegistration(helper, tier + "_assembler_matrix_pattern_core_plus", extendedAE);
            assertOptionalBlockRegistration(helper, tier + "_assembler_matrix_crafting_core_plus", extendedAE);
        }
        assertOptionalBlockRegistration(helper, "extended_quantum_crafter", advancedAE);
        assertRecipeRegistration(helper, "ex_extended_molecular_assembler", extendedAE);
        for (var prefix : new String[] { "", "epic_", "legendary_" }) {
            assertEqual(helper, ae2wtlib,
                    BuiltInRegistries.ITEM.containsKey(ExtendedMolecularAssembler.makeId(
                            prefix + "wireless_extended_pattern_encoding_terminal")),
                    prefix + "AE2WTLib wireless terminal item registration");
        }

        var matrixMenuId = ExtendedMolecularAssembler.makeId("extended_assembler_matrix_pattern_core");
        assertEqual(helper, extendedAE, BuiltInRegistries.MENU.containsKey(matrixMenuId),
                "ExtendedAE matrix pattern core menu registration");
        var patternViewMenuId = ExtendedMolecularAssembler.makeId("extended_crafting_pattern_view");
        assertEqual(helper, extendedAE, BuiltInRegistries.MENU.containsKey(patternViewMenuId),
                "ExtendedAE extended crafting pattern view menu registration");
        assertEqual(helper, extendedAEPlusStyleContent,
                EMAOptionalIntegrations.isExtendedAEPlusStyleContentEnabled(),
                "ExtendedAE Plus-style content availability");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void extendedPatternViewCentersSevenBySevenGrid(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var sevenBySeven = EXTENDED_CRAFTING_CASES.get(2);
        ExtendedAEGameTestHelper.assertExtendedPatternViewLayout(
                helper,
                encodePatternStackForCase(helper, sevenBySeven),
                sevenBySeven.side(),
                sevenBySeven.tableType());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wirelessTerminalRecipesFollowAE2WTLib(GameTestHelper helper) {
        boolean ae2wtlib = MyotusAPI.integrations().isLoaded(AE2WTLib.class);
        for (var tier : new String[] { "extended", "epic", "legendary" }) {
            var prefix = tier.equals("extended") ? "" : tier + "_";
            assertRecipeRegistration(helper, prefix + "wireless_extended_pattern_encoding_terminal", ae2wtlib);
            assertRecipeRegistration(helper,
                    "wireless_universal_terminal/upgrade_" + tier + "_pattern_encoding", ae2wtlib);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wirelessTerminalTiersKeepSeparatePatternData(GameTestHelper helper) {
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            AE2WTLibGameTestHelper.assertTieredPatternStorage(helper);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void extendedPatternTerminalRecipesFollowTableMods(GameTestHelper helper) {
        boolean extendedCrafting = MyotusAPI.integrations().isLoaded(ExtendedCrafting.class);
        boolean reAvaritia = MyotusAPI.integrations().isLoaded(ReAvaritia.class);
        boolean avaritiaNeo = MyotusAPI.integrations().isLoaded(AvaritiaNeo.class);

        assertRecipeRegistration(helper, "extended_pattern_encoding_terminal",
                !extendedCrafting && !reAvaritia && !avaritiaNeo);
        assertRecipeRegistration(helper, "reavaritia/extended_pattern_encoding_terminal",
                reAvaritia && !extendedCrafting);
        assertRecipeRegistration(helper, "avaritianeo/extended_pattern_encoding_terminal",
                avaritiaNeo && !extendedCrafting);
        assertRecipeRegistration(helper, "extendedcrafting/extended_pattern_encoding_terminal",
                extendedCrafting && !reAvaritia && !avaritiaNeo);
        assertRecipeRegistration(helper, "reavaritia_extendedcrafting/extended_pattern_encoding_terminal",
                reAvaritia && extendedCrafting);
        assertRecipeRegistration(helper, "avaritianeo_extendedcrafting/extended_pattern_encoding_terminal",
                avaritiaNeo && extendedCrafting);
        assertRecipeRegistration(helper, "extendedterminal/extended_pattern_encoding_terminal",
                MyotusAPI.integrations().isLoaded(ExtendedTerminal.class));
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void extendedAEPlusRecipesFollowModAndConfig(GameTestHelper helper) {
        boolean extendedAE = MyotusAPI.integrations().isLoaded(ExPatternProvider.class);
        boolean extendedAEPlus = MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class);
        boolean plusContent = extendedAE
                && (extendedAEPlus || EMAConfig.standaloneExtendedAEPlusContent());

        assertRecipeRegistration(helper, "extended_assembler_matrix_pattern_uploader",
                extendedAE && extendedAEPlus);
        assertRecipeRegistration(helper, "extended_assembler_matrix_crafting_core_plus",
                plusContent);
        assertRecipeRegistration(helper, "extended_assembler_matrix_pattern_core_plus",
                plusContent);
        for (var tier : new String[] { "epic", "legendary" }) {
            assertRecipeRegistration(helper, tier + "_assembler_matrix_pattern_core_plus", plusContent);
            assertRecipeRegistration(helper, tier + "_assembler_matrix_crafting_core_plus", plusContent);
        }
        assertRecipeRegistration(helper, "extended_assembler_matrix_pattern_uploader_standalone", false);
        assertRecipeRegistration(helper, "extended_assembler_matrix_crafting_core_plus_standalone", false);
        assertRecipeRegistration(helper, "extended_assembler_matrix_pattern_core_plus_standalone", false);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void tieredMatrixCoreRecipesUseMatchingExAssemblers(GameTestHelper helper) {
        var level = helper.getLevel();
        var recipes = level.getRecipeManager();
        boolean integrations = MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                && MyotusAPI.integrations().isLoaded(ExtendedCrafting.class);
        for (var tier : new String[] { "epic", "legendary" }) {
            boolean available = integrations && BuiltInRegistries.ITEM.containsKey(
                    new ResourceLocation("extendedcrafting", tier + "_table"));
            for (var core : new String[] { "crafting", "pattern" }) {
                var path = tier + "_assembler_matrix_" + core + "_core";
                assertRecipeRegistration(helper, path, available);
                if (!available) {
                    continue;
                }
                var id = ExtendedMolecularAssembler.makeId(path);
                var loaded = recipes.byKey(id).orElseThrow();
                helper.assertTrue(loaded instanceof ShapedRecipe, path + " must be shaped");
                var recipe = (ShapedRecipe) loaded;
                assertEqual(helper, 3, recipe.getWidth(), path + " width");
                assertEqual(helper, 3, recipe.getHeight(), path + " height");
                assertStackMatches(helper, new ItemStack(BuiltInRegistries.ITEM.get(id)),
                        recipe.getResultItem(level.registryAccess()), path + " result");
                var base = recipes.byKey(ExtendedMolecularAssembler.makeId(
                        "extended_assembler_matrix_" + core + "_core")).orElseThrow();
                for (int slot = 0; slot < 9; slot++) {
                    if (slot != 4) {
                        assertEqual(helper, base.getIngredients().get(slot).toJson(),
                                recipe.getIngredients().get(slot).toJson(), path + " ingredient " + slot);
                    }
                }
                var center = recipe.getIngredients().get(4);
                var expected = BuiltInRegistries.ITEM.get(ExtendedMolecularAssembler.makeId(
                        "ex_" + tier + "_molecular_assembler"));
                helper.assertTrue(center.test(new ItemStack(expected)), path + " matching Ex assembler");
                for (var rejected : new String[] { tier, "ex_extended",
                        "ex_" + (tier.equals("epic") ? "legendary" : "epic") }) {
                    var item = BuiltInRegistries.ITEM.get(ExtendedMolecularAssembler.makeId(
                            rejected + "_molecular_assembler"));
                    helper.assertFalse(center.test(new ItemStack(item)), path + " must reject " + rejected);
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void craftConfirmationMenuHasExportModeGate(GameTestHelper helper) {
        helper.assertTrue(CraftConfirmExportPlanGate.class.isAssignableFrom(CraftConfirmMenu.class),
                "AE2 CraftConfirmMenu is missing EMA's export-mode server gate");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void destroyedCraftingCpuNodeIgnoresLateSideUpdate(GameTestHelper helper) {
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, AEBlocks.CRAFTING_UNIT.block());
        helper.runAfterDelay(1, () -> {
            var blockEntity = helper.getBlockEntity(pos);
            helper.assertTrue(blockEntity instanceof CraftingBlockEntity,
                    "AE2 crafting unit did not create a CraftingBlockEntity");

            var craftingBlock = (CraftingBlockEntity) blockEntity;
            helper.assertTrue(craftingBlock.getMainNode().isReady(),
                    "AE2 crafting unit node was not ready before regression check");
            craftingBlock.getMainNode().destroy();
            craftingBlock.updateSubType(true);
            helper.assertFalse(craftingBlock.getMainNode().isReady(),
                    "destroyed AE2 crafting unit node unexpectedly became ready again");
            helper.destroyBlock(pos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void assemblerConnectionCapabilitiesFollowAe2(GameTestHelper helper) {
        assertAssemblerConnection(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Extended Molecular Assembler");
        if (MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            assertAssemblerConnection(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(2, 1, 1),
                    "Ex Extended Molecular Assembler");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void patternPreviewPreservesServerSlotSync(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_preview_test"));
        var host = new ExtendedPatternEncodingTerminalPart(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL.asItem()) {
            @Override
            public net.minecraft.world.level.Level getLevel() {
                return helper.getLevel();
            }

            @Override
            public void markForSave() {
            }
        };
        var menu = new ExtendedPatternEncodingTermMenu(0, player.getInventory(), host) {
            @Override
            public boolean isClientSide() {
                return true;
            }
        };
        var output = menu.getSlots(EMASlotSemantics.EXTENDED_PATTERN_CRAFTING_RESULT).get(0);
        var selectedOutput = new ItemStack(Items.DIAMOND, 2);
        menu.setItem(output.index, 1, selectedOutput);
        assertStackMatches(helper, selectedOutput, output.getItem(), "incremental server output sync");
        menu.onSlotChange(output);
        assertStackMatches(helper, selectedOutput, output.getItem(), "client slot notification output");

        var contents = NonNullList.withSize(menu.slots.size(), ItemStack.EMPTY);
        var initialOutput = new ItemStack(Items.EMERALD, 3);
        contents.set(output.index, initialOutput);
        menu.initializeContents(2, contents, ItemStack.EMPTY);
        assertStackMatches(helper, initialOutput, output.getItem(), "initial server output sync");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void encodedPatternNbtRoundTripPreservesMetadata(GameTestHelper helper) {
        var inputs = NonNullList.withSize(9, ItemStack.EMPTY);
        var namedInput = new ItemStack(Items.DIAMOND, 2);
        namedInput.setHoverName(net.minecraft.network.chat.Component.literal("NBT input"));
        inputs.set(4, namedInput);
        var encoded = new EncodedExtendedCraftingPattern(inputs, new ItemStack(Items.EMERALD, 3),
                ExtendedMolecularAssembler.makeId("gametest/nbt_round_trip"),
                ExtendedPatternTableTypes.RE_AVARITIA_SCULK, 1, 3, true, false);
        var stack = new ItemStack(EMAItems.EXTENDED_CRAFTING_PATTERN.get());
        EncodedExtendedCraftingPattern.set(stack, encoded);
        var savedStack = ItemStack.of(stack.save(new CompoundTag()));
        var decoded = EncodedExtendedCraftingPattern.get(savedStack);
        helper.assertTrue(decoded != null, "encoded pattern did not survive ItemStack NBT round trip");
        assertEqual(helper, encoded.recipeId(), decoded.recipeId(), "NBT recipe id");
        assertEqual(helper, encoded.tableType(), decoded.tableType(), "NBT table type");
        assertEqual(helper, encoded.tableTier(), decoded.tableTier(), "NBT table tier");
        assertEqual(helper, encoded.tableSideLength(), decoded.tableSideLength(), "NBT table side");
        assertEqual(helper, encoded.canSubstitute(), decoded.canSubstitute(), "NBT item substitution flag");
        assertEqual(helper, encoded.canSubstituteFluids(), decoded.canSubstituteFluids(), "NBT fluid substitution flag");
        assertStackMatches(helper, encoded.result(), decoded.result(), "NBT result stack");
        assertEqual(helper, encoded.inputs().size(), decoded.inputs().size(), "NBT sparse input count");
        for (int slot = 0; slot < inputs.size(); slot++) {
            assertStackMatches(helper, inputs.get(slot), decoded.inputs().get(slot), "NBT input slot " + slot);
        }

        savedStack.getOrCreateTag().getCompound("extendedmolecularassembler:encoded_extended_crafting_pattern")
                .getList("inputs", Tag.TAG_COMPOUND).getCompound(4)
                .putString("id", "extendedmolecularassembler:missing_test_item");
        helper.assertTrue(EncodedExtendedCraftingPattern.get(savedStack) == null,
                "missing registry input must invalidate the encoded pattern");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void encodedPatternRetainsAe2ClearAction(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_pattern_test"));
        var stack = new ItemStack(EMAItems.EXTENDED_CRAFTING_PATTERN.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        helper.assertTrue(appeng.api.crafting.PatternDetailsHelper.isEncodedPattern(stack),
                "AE2 did not recognize EMA's encoded pattern item");
        player.setShiftKeyDown(true);
        stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        assertStackMatches(helper, appeng.core.definitions.AEItems.BLANK_PATTERN.stack(),
                player.getMainHandItem(), "AE2 alternate-use pattern clearing");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void assemblerBlockUseOpensRegisteredMenu(GameTestHelper helper) {
        assertAssemblerMenu(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1), 1);
        if (MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            assertAssemblerMenu(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(2, 1, 1),
                    ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT);
        }
        helper.succeed();
    }

    private static void assertAssemblerMenu(GameTestHelper helper, Block block, BlockPos pos, int laneCount) {
        var assembler = placeAssembler(helper, block, pos, "assembler menu host");
        var level = helper.getLevel();
        var absolutePos = helper.absolutePos(pos);
        var player = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "ema_menu_test"));
        player.setPos(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 1.5);
        var hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.NORTH, absolutePos, false);
        try {
            for (var heldItem : List.of(ItemStack.EMPTY, new ItemStack(Items.STICK))) {
                player.setItemInHand(InteractionHand.MAIN_HAND, heldItem);
                var result = level.getBlockState(absolutePos).use(level, player, InteractionHand.MAIN_HAND, hit);
                helper.assertTrue(result.consumesAction(), "assembler block use did not handle the interaction");
                helper.assertTrue(player.containerMenu instanceof ExtendedMolecularAssemblerMenu,
                        "assembler block use did not open its server menu");
                var menu = (ExtendedMolecularAssemblerMenu) player.containerMenu;
                assertEqual(helper, ExtendedMolecularAssembler.makeId("extended_molecular_assembler"),
                        BuiltInRegistries.MENU.getKey(menu.getType()), "assembler menu registry namespace");
                helper.assertTrue(menu.getHost() == assembler, "assembler menu resolved another host");
                assertEqual(helper, laneCount, menu.getPageCount(), "assembler menu page count");
                for (int page = 0; page < laneCount; page++) {
                    menu.selectPage(page);
                    int gridBackgrounds = 0;
                    int outputs = 0;
                    for (var slot : menu.slots) {
                        if (slot instanceof ExtendedMolecularAssemblerPatternSlot input) {
                            helper.assertTrue(input.isRenderDisabled() == (input.getLaneIndex() == page),
                                    "only the selected lane must draw input backgrounds, including disabled cells");
                            if (input.isRenderDisabled()) {
                                gridBackgrounds++;
                            }
                        } else if (slot instanceof ExtendedMolecularAssemblerOutputSlot && slot.isActive()) {
                            outputs++;
                        }
                    }
                    assertEqual(helper, 81, gridBackgrounds, "assembler visible input backgrounds");
                    assertEqual(helper, 1, outputs, "assembler visible output slot");
                }
                menu.selectPage(0);

                var buffer = new FriendlyByteBuf(Unpooled.buffer());
                try {
                    MenuLocators.writeToPacket(buffer, menu.getLocator());
                    buffer.writeBoolean(false);
                    var decoded = ExtendedMolecularAssemblerMenu.TYPE.create(menu.containerId, player.getInventory(),
                            buffer);
                    helper.assertTrue(decoded.getHost() == assembler, "Forge menu payload resolved another host");
                    assertEqual(helper, menu.slots.size(), decoded.slots.size(), "Forge menu payload slot layout");
                    assertEqual(helper, 0, buffer.readableBytes(), "Forge menu payload left unread bytes");
                } finally {
                    buffer.release();
                }
                player.doCloseContainer();
            }
        } finally {
            player.doCloseContainer();
        }
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void poweredAssemblerCompletesPushedCraft(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }
        assertPoweredAssemblerCrafts(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get());
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void poweredExAssemblerCompletesAllLanes(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }
        assertPoweredAssemblerCrafts(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void poweredExEpicAssemblerCompletesAllLanes(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }
        assertPoweredAssemblerCrafts(helper, EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get());
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void poweredExLegendaryAssemblerCompletesAllLanes(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }
        assertPoweredAssemblerCrafts(helper, EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get());
    }

    private static void assertPoweredAssemblerCrafts(GameTestHelper helper, Block block) {
        var testCase = EXTENDED_CRAFTING_CASES.get(0);
        var pattern = decodePatternForCase(helper, testCase);
        var assembler = placeAssembler(helper, block, new BlockPos(1, 1, 1), "powered assembler");
        helper.setBlock(new BlockPos(1, 1, 2), AEBlocks.CREATIVE_ENERGY_CELL.block());
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(assembler.getMainNode().getNode() != null, "assembler grid node did not initialize");
            for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
                helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                        "powered assembler rejected lane " + lane);
            }
            helper.succeedWhen(() -> {
                helper.assertTrue(assembler.isPowered(), "assembler did not receive AE power");
                for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
                    var inventory = assembler.getCraftInventory(lane);
                    assertStackMatches(helper, testCase.outputStack(),
                            inventory.getStackInSlot(assembler.getOutputSlot()),
                            "powered assembler lane " + lane + " crafted output");
                    for (int slot = 0; slot < assembler.getGridSize(); slot++) {
                        helper.assertTrue(inventory.getStackInSlot(slot).isEmpty(),
                                "powered assembler retained consumed ingredients in lane " + lane);
                    }
                }
            });
        });
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void exportMECraftingProvidersExposeConfiguredIdlePower(GameTestHelper helper) {
        var tiers = ExportMECraftingProviderTier.values();
        for (int i = 0; i < tiers.length; i++) {
            helper.setBlock(providerTestPosition(i), providerBlock(tiers[i]));
        }

        helper.runAfterDelay(1, () -> {
            for (int i = 0; i < tiers.length; i++) {
                var tier = tiers[i];
                var configPath = List.of("blocks", tier.blockId(), "idlePowerUsage");
                var configValue = EMAConfig.COMMON.getValues().get(configPath);
                helper.assertTrue(configValue instanceof ForgeConfigSpec.DoubleValue,
                        tier + " is missing its idlePowerUsage config");
                assertEqual(helper, 1.0, ((ForgeConfigSpec.DoubleValue) configValue).getDefault(),
                        tier + " provider default idle AE/t");

                var blockEntity = helper.getBlockEntity(providerTestPosition(i));
                helper.assertTrue(blockEntity instanceof ExportMECraftingProviderBlockEntity,
                        tier + " did not create an ExportMECraftingProviderBlockEntity");

                var provider = (ExportMECraftingProviderBlockEntity) blockEntity;
                assertEqual(helper, tier, provider.getTier(), tier + " provider tier");

                var node = provider.getMainNode().getNode();
                helper.assertTrue(node != null, tier + " provider ME node was not created");
                assertEqual(helper, EMAConfig.exportMECraftingProviderIdlePowerUsage(tier),
                        node.getIdlePowerUsage(), tier + " provider idle AE/t");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void extendedAssemblerAcceptsOnePushedJob(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var testCase = EXTENDED_CRAFTING_CASES.get(0);
        var pattern = decodePatternForCase(helper, testCase);
        var assembler = placeAssembler(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Extended Molecular Assembler");

        helper.assertTrue(assembler.acceptsPlans(), "fresh single-lane assembler should accept plans");
        helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "single-lane assembler did not accept a supported extended pattern");
        assertLaneGrid(helper, assembler, 0, testCase, "single-lane assembler pushed job");
        helper.assertFalse(assembler.acceptsPlans(), "busy single-lane assembler should not accept another plan");
        helper.assertFalse(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "busy single-lane assembler accepted a second job");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void exAssemblerAcceptsOneJobPerLane(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var testCase = EXTENDED_CRAFTING_CASES.get(0);
        var pattern = decodePatternForCase(helper, testCase);
        var assembler = placeAssembler(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Ex Extended Molecular Assembler");

        assertEqual(helper, ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT, assembler.getLaneCount(),
                "Ex assembler lane count");
        for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
            helper.assertTrue(assembler.acceptsPlans(), "Ex assembler should accept lane " + lane);
            helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                    "Ex assembler did not accept job for lane " + lane);
            assertLaneGrid(helper, assembler, lane, testCase, "Ex assembler lane " + lane + " pushed job");
        }

        helper.assertFalse(assembler.acceptsPlans(), "full Ex assembler should not advertise free lanes");
        helper.assertFalse(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "full Ex assembler accepted a ninth job");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void advancedAEQuantumCrafterMenuRejectsExtendedPattern(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(AdvancedAE.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        AdvancedAEGameTestHelper.assertAdvancedQuantumCrafterRejectsExtendedPattern(helper, patternStack);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedQuantumCrafterMenuAcceptsExtendedPattern(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(AdvancedAE.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        AdvancedAEGameTestHelper.assertExtendedQuantumCrafterAcceptsExtendedPattern(helper, patternStack);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEPatternCoreAcceptsOnlyExtendedEncodedPatterns(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        ExtendedAEGameTestHelper.assertPatternCoreAcceptsOnlyExtendedEncodedPatterns(helper, patternStack);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixPatternCoresRouteByGridSide(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            helper.succeed();
            return;
        }

        ExtendedAEGameTestHelper.assertPatternCoreSideRouting(helper);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixCraftingCoresExposeTierGridSides(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            helper.succeed();
            return;
        }

        ExtendedAEGameTestHelper.assertMatrixCraftingCoreVariantGridSides(helper);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEPatternUploaderUploadsIntoExtendedPatternCore(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        ExtendedAEGameTestHelper.assertPatternUploaderUploadsIntoExtendedPatternCore(helper, patternStack);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixCraftingCoreTracksAndCancelsExtendedJobs(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var pattern = decodePatternForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        ExtendedAEGameTestHelper.assertMatrixCraftingCoreTracksAndCancelsExtendedJobs(helper, pattern);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void extendedAEPlusCoresAndUploaderRespectTiers(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        ExtendedAEGameTestHelper.assertPlusCoreAndUploader(helper,
                decodePatternForCase(helper, EXTENDED_CRAFTING_CASES.get(3)), "extended");
        for (int tier = 5; tier <= 6; tier++) {
            var variant = tier == 5 ? "epic" : "legendary";
            if (!BuiltInRegistries.ITEM.containsKey(new ResourceLocation("extendedcrafting", variant + "_table"))) {
                continue;
            }
            var pattern = decodeLargePatternForTier(helper, tier,
                    ExtendedMolecularAssembler.makeId("gametest/ec_tier_" + tier),
                    tier == 5 ? Items.DIAMOND_BLOCK : Items.NETHERITE_BLOCK);
            helper.assertTrue(pattern != null, variant + " matrix test pattern was not decoded");
            ExtendedAEGameTestHelper.assertPlusCoreAndUploader(helper, pattern, variant);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixClusterDispatchesExtendedJob(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var pattern = decodePatternForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        ExtendedAEGameTestHelper.assertClusterDispatchesExtendedJob(helper, pattern);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixClusterRoutesLargePatternsToMatchingCores(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var level = helper.getLevel();
        for (int tier = 5; tier <= 6; tier++) {
            var tableId = new ResourceLocation("extendedcrafting", tier == 5 ? "epic_table" : "legendary_table");
            var recipeId = ExtendedMolecularAssembler.makeId("gametest/ec_tier_" + tier);
            var tableAvailable = BuiltInRegistries.ITEM.containsKey(tableId);
            var fixtureRegistered = level.getRecipeManager().byKey(recipeId).isPresent();
            helper.assertTrue(tableAvailable == fixtureRegistered,
                    "Tier " + tier + " fixture registration did not match table availability");
            if (!tableAvailable) {
                continue;
            }

            var expectedOutput = tier == 5 ? Items.DIAMOND_BLOCK : Items.NETHERITE_BLOCK;
            var pattern = decodeLargePatternForTier(helper, tier, recipeId, expectedOutput);
            if (pattern != null) {
                ExtendedAEGameTestHelper.assertClusterDispatchesExtendedJob(
                        helper,
                        pattern,
                        pattern.tableSideLength());
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixCraftingCoreDropsActiveExtendedJobInputs(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var pattern = decodePatternForCase(helper, EXTENDED_CRAFTING_CASES.get(0));
        ExtendedAEGameTestHelper.assertMatrixCraftingCoreDropsActiveExtendedJobInputs(helper, pattern);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void extendedCraftingTiersEncodeAndCraft(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        validateCases(helper, EXTENDED_CRAFTING_CASES);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void reAvaritiaTiersEncodeAndCraft(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            helper.succeed();
            return;
        }

        validateCases(helper, RE_AVARITIA_CASES);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void avaritiaNeoExtremeEncodeAndCraft(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)) {
            helper.succeed();
            return;
        }

        validateCases(helper, AVARITIA_NEO_CASES);
        helper.succeed();
    }

    private static void assertAssemblerConnection(GameTestHelper helper, Block block, BlockPos pos, String name) {
        helper.setBlock(pos, block);
        var level = helper.getLevel();
        var absolutePos = helper.absolutePos(pos);
        var blockEntity = level.getBlockEntity(absolutePos);
        var nodeHost = blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).orElse(null);
        helper.assertTrue(nodeHost != null, name + " does not expose AE2 grid-node host capability");
        helper.assertTrue(nodeHost.getCableConnectionType(Direction.NORTH) == AECableType.COVERED,
                name + " must expose covered cable connection type like AE2 Molecular Assembler");
        helper.assertTrue(ICraftingMachine.of(level, absolutePos, Direction.NORTH, blockEntity) != null,
                name + " does not expose AE2 crafting-machine capability");
    }

    private static BlockPos providerTestPosition(int index) {
        return new BlockPos(index % 3, 1, index / 3);
    }

    private static Block providerBlock(ExportMECraftingProviderTier tier) {
        return switch (tier) {
            case BASIC -> EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get();
            case ADVANCED -> EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get();
            case ELITE -> EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get();
            case ULTIMATE -> EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get();
            case RE_AVARITIA_SCULK -> EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get();
            case RE_AVARITIA_NETHER -> EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get();
            case RE_AVARITIA_END -> EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get();
            case XTREME -> EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get();
            case EPIC -> EMABlocks.EPIC_ME_CRAFTING_PROVIDER.get();
            case LEGENDARY -> EMABlocks.LEGENDARY_ME_CRAFTING_PROVIDER.get();
        };
    }

    private static ExtendedMolecularAssemblerBlockEntity placeAssembler(GameTestHelper helper, Block block, BlockPos pos,
            String name) {
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof ExtendedMolecularAssemblerBlockEntity,
                name + " did not create an ExtendedMolecularAssemblerBlockEntity");
        return (ExtendedMolecularAssemblerBlockEntity) blockEntity;
    }

    private static void assertOptionalBlockRegistration(GameTestHelper helper, String path, boolean expected) {
        var id = ExtendedMolecularAssembler.makeId(path);
        assertEqual(helper, expected, BuiltInRegistries.BLOCK.containsKey(id), path + " block registration");
        assertEqual(helper, expected, BuiltInRegistries.ITEM.containsKey(id), path + " item registration");
        assertEqual(helper, expected, BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(id),
                path + " block entity registration");
    }

    private static void assertRecipeRegistration(GameTestHelper helper, String path, boolean expected) {
        var id = ExtendedMolecularAssembler.makeId(path);
        assertEqual(helper, expected, helper.getLevel().getRecipeManager().byKey(id).isPresent(),
                path + " recipe registration");
    }

    private static void validateCases(GameTestHelper helper, List<PatternCase> cases) {
        for (var testCase : cases) {
            validateCase(helper, testCase);
        }
    }

    private static void validateCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var expectedRecipe = testCase.recipeId();
        helper.assertTrue(level.getRecipeManager().byKey(expectedRecipe).isPresent(),
                "Missing test recipe " + expectedRecipe);

        var machineGrid = testCase.machineGrid();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(machineGrid, level), testCase);
        assertEqual(helper, expectedRecipe, match.recipe().getId(), testCase.label() + " recipe lookup");
        helper.assertTrue(TableRecipeAdapters.isExtended(match.recipe()),
                testCase.label() + " was not recognized as an extended recipe");
        helper.assertTrue(ExtendedPatternRecipeTransfer.canTransfer(match.recipe()),
                testCase.label() + " was rejected for extended recipe transfer");
        var provider = switch (testCase.tableType().getNamespace()) {
            case "extendedcrafting" -> ExtendedPatternEncodingTermMenu.RecipeProvider.EXTENDED_CRAFTING;
            case "reavaritia" -> ExtendedPatternEncodingTermMenu.RecipeProvider.RE_AVARITIA;
            case "avaritianeo" -> ExtendedPatternEncodingTermMenu.RecipeProvider.AVARITIA_NEO;
            default -> throw new IllegalArgumentException("Unexpected test table: " + testCase.tableType());
        };
        assertEqual(helper, provider, ExtendedPatternEncodingTermMenu.RecipeProvider.of(match.recipe()),
                testCase.label() + " recipe provider");
        assertStackMatches(helper, testCase.outputStack(), match.result(), testCase.label() + " lookup output");
        assertSparseInputs(helper, testCase, match.inputs(), testCase.label() + " lookup inputs");

        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
        var encoded = EncodedExtendedCraftingPattern.get(patternStack);
        helper.assertTrue(encoded != null, testCase.label() + " did not write encoded pattern data");
        assertEncoded(helper, testCase, encoded);

        var decoded = appeng.api.crafting.PatternDetailsHelper.decodePattern(patternStack, level);
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern,
                testCase.label() + " did not decode to ExtendedTableCraftingPattern");
        var pattern = (ExtendedTableCraftingPattern) decoded;
        assertPattern(helper, testCase, pattern);

        assertStackMatches(helper, testCase.outputStack(), pattern.assembleFromMachineGrid(machineGrid::get, level),
                testCase.label() + " assembly from source grid");
        assertFillCraftingGrid(helper, testCase, pattern);
    }

    private static ExtendedTableCraftingPattern decodePatternForCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(testCase.machineGrid(), level), testCase);
        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
        var decoded = appeng.api.crafting.PatternDetailsHelper.decodePattern(patternStack, level);
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern,
                testCase.label() + " did not decode to ExtendedTableCraftingPattern");
        return (ExtendedTableCraftingPattern) decoded;
    }

    private static ExtendedTableCraftingPattern decodeLargePatternForTier(
            GameTestHelper helper,
            int tier,
            ResourceLocation expectedRecipeId,
            Item expectedOutput) {
        var side = tier * 2 + 1;
        var machineGrid = NonNullList.withSize(side * side, ItemStack.EMPTY);
        for (int slot = 0; slot < machineGrid.size(); slot++) {
            machineGrid.set(slot, new ItemStack(Items.COPPER_INGOT));
        }
        machineGrid.set((side / 2) * side + side / 2, new ItemStack(Items.NETHER_STAR));

        var match = ExtendedPatternRecipeFinder.find(machineGrid, helper.getLevel()).orElse(null);
        helper.assertTrue(match != null, "Tier " + tier + " Extended Crafting fixture did not match its grid");
        if (match == null) {
            return null;
        }
        assertEqual(helper, expectedRecipeId, match.recipe().getId(),
                "Tier " + tier + " Extended Crafting fixture recipe");
        assertStackMatches(helper, new ItemStack(expectedOutput), match.result(),
                "Tier " + tier + " Extended Crafting fixture result");

        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(
                match.recipe(), match.inputs(), match.result(), false, true);
        var decoded = appeng.api.crafting.PatternDetailsHelper.decodePattern(patternStack, helper.getLevel());
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern,
                "Tier " + tier + " fixture did not decode to an ExtendedTableCraftingPattern");
        if (!(decoded instanceof ExtendedTableCraftingPattern pattern)) {
            return null;
        }
        assertEqual(helper, tier, pattern.tableTier(), "Tier " + tier + " decoded table tier");
        assertEqual(helper, side, pattern.tableSideLength(), "Tier " + tier + " decoded pattern side length");
        return pattern;
    }

    private static ItemStack encodePatternStackForCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(testCase.machineGrid(), level), testCase);
        return ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
    }

    private static KeyCounter[] countersForPattern(ExtendedTableCraftingPattern pattern) {
        var counters = new KeyCounter[pattern.getInputs().length];
        IPatternDetails.IInput[] inputs = pattern.getInputs();
        for (int i = 0; i < inputs.length; i++) {
            counters[i] = new KeyCounter();
            var primaryInput = inputs[i].getPossibleInputs()[0];
            counters[i].add(primaryInput.what(), primaryInput.amount() * inputs[i].getMultiplier());
        }
        return counters;
    }

    private static void assertLaneGrid(GameTestHelper helper, ExtendedMolecularAssemblerBlockEntity assembler,
            int laneIndex, PatternCase testCase, String name) {
        var expectedGrid = testCase.machineGrid();
        var laneInventory = assembler.getCraftInventory(laneIndex);
        for (int slot = 0; slot < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; slot++) {
            assertStackMatches(helper, expectedGrid.get(slot), laneInventory.getStackInSlot(slot),
                    name + " slot " + slot);
        }
        assertStackMatches(helper, ItemStack.EMPTY,
                laneInventory.getStackInSlot(ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT),
                name + " output slot should stay empty before ticking");
    }

    private static ExtendedPatternRecipeMatch requireMatch(GameTestHelper helper,
            Optional<ExtendedPatternRecipeMatch> match, PatternCase testCase) {
        if (match.isEmpty()) {
            helper.fail(testCase.label() + " was not found from a centered 9x9 machine grid");
        }
        return match.orElseThrow();
    }

    private static void assertEncoded(GameTestHelper helper, PatternCase testCase,
            EncodedExtendedCraftingPattern encoded) {
        assertEqual(helper, testCase.recipeId(), encoded.recipeId(), testCase.label() + " encoded recipe id");
        assertEqual(helper, testCase.tableType(), encoded.tableType(), testCase.label() + " encoded table type");
        assertEqual(helper, testCase.tier(), encoded.tableTier(), testCase.label() + " encoded table tier");
        assertEqual(helper, testCase.side(), encoded.tableSideLength(), testCase.label() + " encoded table side");
        helper.assertFalse(encoded.canSubstitute(), testCase.label() + " unexpectedly allows substitutions");
        helper.assertTrue(encoded.canSubstituteFluids(), testCase.label() + " did not allow fluid substitutions");
        assertStackMatches(helper, testCase.outputStack(), encoded.result(), testCase.label() + " encoded output");
        assertEqual(helper, testCase.side() * testCase.side(), encoded.inputs().size(),
                testCase.label() + " encoded input size");
        assertSparseInputs(helper, testCase, encoded.inputs().toArray(ItemStack[]::new),
                testCase.label() + " encoded inputs");
    }

    private static void assertPattern(GameTestHelper helper, PatternCase testCase, ExtendedTableCraftingPattern pattern) {
        assertEqual(helper, testCase.tableType(), pattern.tableType(), testCase.label() + " decoded table type");
        assertEqual(helper, testCase.tier(), pattern.tableTier(), testCase.label() + " decoded table tier");
        assertEqual(helper, testCase.side(), pattern.tableSideLength(), testCase.label() + " decoded table side");
        var output = pattern.getSparseOutputs()[0];
        helper.assertTrue(output.what() instanceof AEItemKey, testCase.label() + " decoded output is not an item");
        assertStackMatches(helper, testCase.outputStack(),
                ((AEItemKey) output.what()).toStack(Math.toIntExact(output.amount())),
                testCase.label() + " decoded output");
    }

    private static void assertFillCraftingGrid(GameTestHelper helper, PatternCase testCase,
            ExtendedTableCraftingPattern pattern) {
        var counters = countersForPattern(pattern);

        var filledGrid = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        pattern.fillCraftingGrid(counters, filledGrid::set);
        assertCountersEmpty(helper, counters, testCase.label() + " fillCraftingGrid");
        assertStackMatches(helper, testCase.outputStack(),
                pattern.assembleFromMachineGrid(filledGrid::get, helper.getLevel()),
                testCase.label() + " assembly from filled AE counters");

        var expectedGrid = testCase.machineGrid();
        for (int slot = 0; slot < expectedGrid.size(); slot++) {
            assertStackMatches(helper, expectedGrid.get(slot), filledGrid.get(slot),
                    testCase.label() + " filled machine slot " + slot);
        }
    }

    private static void assertCountersEmpty(GameTestHelper helper, KeyCounter[] counters, String name) {
        for (int i = 0; i < counters.length; i++) {
            counters[i].removeZeros();
            if (!counters[i].isEmpty()) {
                helper.fail(name + " left over AE input counter " + i + ": " + counters[i].iterator().next());
            }
        }
    }

    private static void assertSparseInputs(GameTestHelper helper, PatternCase testCase, ItemStack[] actual,
            String name) {
        var expected = testCase.sparseInputs();
        assertEqual(helper, expected.length, actual.length, name + " length");
        for (int slot = 0; slot < expected.length; slot++) {
            assertStackMatches(helper, expected[slot], actual[slot], name + " slot " + slot);
        }
    }

    private static void assertStackMatches(GameTestHelper helper, ItemStack expected, ItemStack actual, String name) {
        if (!ItemStack.matches(expected, actual)) {
            helper.fail(name + ": expected " + describe(expected) + ", got " + describe(actual));
        }
    }

    private static void assertEqual(GameTestHelper helper, Object expected, Object actual, String name) {
        if (!Objects.equals(expected, actual)) {
            helper.fail(name + ": expected " + expected + ", got " + actual);
        }
    }

    private static String describe(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }
        return stack.getCount() + "x" + BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private record PatternCase(String label, String recipePath, ResourceLocation tableType, int tier, Item output,
            String[] pattern, Map<Character, Item> keys) {
        private ResourceLocation recipeId() {
            return ExtendedMolecularAssembler.makeId(recipePath.contains("/") ? recipePath : "gametest/" + recipePath);
        }

        private int side() {
            return tier * 2 + 1;
        }

        private ItemStack outputStack() {
            return new ItemStack(output);
        }

        private NonNullList<ItemStack> machineGrid() {
            var result = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
            var offset = Math.floorDiv(ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - side(), 2);
            for (int y = 0; y < side(); y++) {
                for (int x = 0; x < side(); x++) {
                    result.set(x + offset + (y + offset) * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE,
                            stackFor(pattern[y].charAt(x)));
                }
            }
            return result;
        }

        private ItemStack[] sparseInputs() {
            var result = new ItemStack[side() * side()];
            for (int y = 0; y < side(); y++) {
                for (int x = 0; x < side(); x++) {
                    result[x + y * side()] = stackFor(pattern[y].charAt(x));
                }
            }
            return result;
        }

        private ItemStack stackFor(char symbol) {
            if (symbol == ' ') {
                return ItemStack.EMPTY;
            }
            var item = keys.get(symbol);
            if (item == null) {
                throw new IllegalArgumentException("No key for symbol " + symbol + " in " + label);
            }
            return new ItemStack(item);
        }
    }
}
