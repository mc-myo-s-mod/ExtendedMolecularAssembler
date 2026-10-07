package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.devCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.shapeless;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.stack;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.tag;

public final class EMARecipeDataProvider extends JsonRecipeProvider {
    private static final String COMPAT_PROCESSOR = "myotus:compat_processor";
    private static final String ENGINEERING_PROCESSOR = "ae2:engineering_processor";
    private static final String ASSEMBLER_MATRIX_FRAME = "expatternprovider:assembler_matrix_wall";
    private static final String EX_EXTENDED_MOLECULAR_ASSEMBLER = "extendedmolecularassembler:ex_extended_molecular_assembler";

    public EMARecipeDataProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(JsonRecipeOutput output) {
        buildCoreRecipes(output);
        buildIntegrationRecipes(output);
        buildGameTestRecipes(output);
    }

    @Override
    public @NotNull String getName() {
        return "Extended Molecular Assembler recipes";
    }

    private static void buildCoreRecipes(JsonRecipeOutput output) {
        saveShaped(output, "extended_molecular_assembler", null, "redstone",
                new String[]{"QFQ", "EME", "QAQ"},
                key('A', "ae2:annihilation_core", 'E', COMPAT_PROCESSOR, 'F', "ae2:formation_core", 'M', "ae2:molecular_assembler", 'Q', "ae2:quartz_vibrant_glass"),
                "extendedmolecularassembler:extended_molecular_assembler", true);
        shapeless("minecraft:crafting_shapeless", EX_EXTENDED_MOLECULAR_ASSEMBLER, 1)
                .conditions(myoConditions("expatternprovider"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item(COMPAT_PROCESSOR))
                .requires(item(ENGINEERING_PROCESSOR))
                .requires(tag("forge:dusts/ender_pearl"))
                .requires(item("ae2:speed_card"))
                .save(output, recipeId("ex_extended_molecular_assembler"));
        shapeless("minecraft:crafting_shapeless", "extendedmolecularassembler:ex_extended_molecular_assembler_upgrade_kit", 1)
                .conditions(myoConditions("expatternprovider"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item("extendedmolecularassembler:extended_molecular_assembler"))
                .requires(item(COMPAT_PROCESSOR))
                .requires(item(ENGINEERING_PROCESSOR))
                .requires(tag("forge:dusts/ender_pearl"))
                .requires(item("ae2:speed_card"))
                .save(output, recipeId("ex_extended_molecular_assembler_upgrade_kit"));
        for (String tier : new String[] { "epic", "legendary" }) {
            String table = "extendedcrafting:" + tier + "_table";
            JsonArray conditions = myoConditions("expatternprovider", "extendedcrafting");
            JsonObject tableExists = new JsonObject();
            tableExists.addProperty("type", "forge:item_exists");
            tableExists.addProperty("item", table);
            conditions.add(tableExists);
            shapeless("minecraft:crafting_shapeless", "extendedmolecularassembler:ex_" + tier + "_molecular_assembler", 1)
                    .conditions(conditions)
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item(COMPAT_PROCESSOR))
                    .requires(item(ENGINEERING_PROCESSOR))
                    .requires(tag("forge:dusts/ender_pearl"))
                    .requires(item("ae2:speed_card"))
                    .save(output, recipeId("ex_" + tier + "_molecular_assembler"));
            shapeless("minecraft:crafting_shapeless",
                    "extendedmolecularassembler:" + tier + "_molecular_assembler_ex_upgrade_kit", 1)
                    .conditions(conditions)
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item("extendedmolecularassembler:" + tier + "_molecular_assembler"))
                    .requires(item(COMPAT_PROCESSOR))
                    .requires(item(ENGINEERING_PROCESSOR))
                    .requires(tag("forge:dusts/ender_pearl"))
                    .requires(item("ae2:speed_card"))
                    .save(output, recipeId(tier + "_molecular_assembler_ex_upgrade_kit"));
        }
        buildExtendedPatternEncodingTerminalRecipes(output);

        for (String tier : new String[] { "epic", "legendary" }) {
            String table = "extendedcrafting:" + tier + "_table";
            JsonArray conditions = myoConditions("extendedcrafting");
            JsonObject tableExists = new JsonObject();
            tableExists.addProperty("type", "forge:item_exists");
            tableExists.addProperty("item", table);
            conditions.add(tableExists);
            String previous = tier.equals("epic") ? "extended" : "epic";
            shapeless("minecraft:crafting_shapeless", "extendedmolecularassembler:" + tier + "_molecular_assembler_upgrade_kit", 1)
                    .conditions(conditions)
                    .requires(item(table))
                    .requires(item(COMPAT_PROCESSOR))
                    .save(output, recipeId(tier + "_molecular_assembler_upgrade_kit"));
            JsonArray exKitConditions = conditions.deepCopy();
            exKitConditions.add(myoCondition("expatternprovider"));
            var exKit = shapeless("minecraft:crafting_shapeless",
                    "extendedmolecularassembler:ex_" + tier + "_molecular_assembler_upgrade_kit", 1)
                    .conditions(exKitConditions);
            for (int i = 0; i < 4; i++) {
                exKit.requires(item(table)).requires(item(COMPAT_PROCESSOR));
            }
            exKit.save(output, recipeId("ex_" + tier + "_molecular_assembler_upgrade_kit"));
            for (String device : new String[] { "molecular_assembler", "pattern_encoding_terminal" }) {
                shapeless("minecraft:crafting_shapeless", "extendedmolecularassembler:" + tier + "_" + device, 1)
                        .conditions(conditions)
                        .requires(item("extendedmolecularassembler:" + previous + "_" + device))
                        .requires(item(table))
                        .requires(item(COMPAT_PROCESSOR))
                        .save(output, recipeId(tier + "_" + device));
            }
            saveShaped(output, tier + "_me_crafting_provider", conditions, "redstone",
                    new String[] { " L ", "PCP", " T " },
                    key('C', "extendedmolecularassembler:" + (tier.equals("epic") ? "ultimate" : "epic")
                                    + "_me_crafting_provider",
                            'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_dense_cable", 'T', table),
                    "extendedmolecularassembler:" + tier + "_me_crafting_provider", true);
            JsonArray matrixConditions = conditions.deepCopy();
            matrixConditions.add(myoCondition("expatternprovider"));
            String assembler = "extendedmolecularassembler:ex_" + tier + "_molecular_assembler";
            saveMatrixCore(output, tier + "_assembler_matrix_crafting_core",
                    "extendedmolecularassembler:" + tier + "_assembler_matrix_crafting_core",
                    matrixConditions, assembler,
                    "ae2:blue_lumen_paint_ball", "ae2:light_blue_lumen_paint_ball", "ae2:cyan_lumen_paint_ball");
        }

        JsonArray extendedCrafting = myoConditions("extendedcrafting");
        saveShaped(output, "basic_me_crafting_provider", extendedCrafting, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "ae2:pattern_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_glass_cable", 'T', "extendedcrafting:basic_table"),
                "extendedmolecularassembler:basic_me_crafting_provider", true);
        saveShaped(output, "advanced_me_crafting_provider", extendedCrafting, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:basic_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_covered_cable", 'T', "extendedcrafting:advanced_table"),
                "extendedmolecularassembler:advanced_me_crafting_provider", true);
        saveShaped(output, "elite_me_crafting_provider", extendedCrafting, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:advanced_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_cable", 'T', "extendedcrafting:elite_table"),
                "extendedmolecularassembler:elite_me_crafting_provider", true);
        saveShaped(output, "ultimate_me_crafting_provider", extendedCrafting, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:elite_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_dense_cable", 'T', "extendedcrafting:ultimate_table"),
                "extendedmolecularassembler:ultimate_me_crafting_provider", true);

        saveShaped(output, "re_avaritia_sculk_me_crafting_provider", myoConditions("Re-Avaritia"), "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "ae2:pattern_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_glass_cable", 'T', "avaritia:sculk_crafting_table"),
                "extendedmolecularassembler:re_avaritia_sculk_me_crafting_provider", true);
        saveShaped(output, "re_avaritia_nether_me_crafting_provider", myoConditions("Re-Avaritia"), "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:re_avaritia_sculk_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_covered_cable", 'T', "avaritia:nether_crafting_table"),
                "extendedmolecularassembler:re_avaritia_nether_me_crafting_provider", true);
        saveShaped(output, "re_avaritia_end_me_crafting_provider", myoConditions("Re-Avaritia"), "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:re_avaritia_nether_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_cable", 'T', "avaritia:end_crafting_table"),
                "extendedmolecularassembler:re_avaritia_end_me_crafting_provider", true);
        saveShaped(output, "xtreme_me_crafting_provider_from_re_avaritia", myoConditions("Re-Avaritia"), "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:re_avaritia_end_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_dense_cable", 'T', "avaritia:extreme_crafting_table"),
                "extendedmolecularassembler:xtreme_me_crafting_provider", true);
        saveShaped(output, "xtreme_me_crafting_provider_from_avaritia_neo", myoConditions("Avaritia"), "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "ae2:pattern_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_dense_cable", 'T', "avaritia:extreme_crafting_table"),
                "extendedmolecularassembler:xtreme_me_crafting_provider", true);

        for (String tier : new String[] { "extended", "epic", "legendary" }) {
            String terminal = (tier.equals("extended") ? "" : tier + "_") + "wireless_extended_pattern_encoding_terminal";
            saveShaped(output, terminal, myoConditions("ae2wtlib"), "redstone",
                    new String[]{"W", "T", "C"},
                    key('C', "ae2:dense_energy_cell", 'T', "extendedmolecularassembler:" + tier + "_pattern_encoding_terminal",
                            'W', "ae2:wireless_receiver"),
                    "extendedmolecularassembler:" + terminal, true);
        }
    }

    private static void buildIntegrationRecipes(JsonRecipeOutput output) {
        JsonArray extendedAEPlus = myoConditions("expatternprovider", "extendedae-plus");
        JsonArray extendedAEPlusContent = extendedAEPlusContentConditions();
        for (String tier : new String[] { "extended", "epic", "legendary" }) {
            String terminal = (tier.equals("extended") ? "" : tier + "_") + "wireless_extended_pattern_encoding_terminal";
            saveAE2WTLibUpgrade(output, "wireless_universal_terminal/upgrade_" + tier + "_pattern_encoding",
                    "extendedmolecularassembler:" + terminal, tier + "_pattern_encoding");
        }

        saveMatrixCore(output,
                "extended_assembler_matrix_crafting_core",
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core",
                myoConditions("expatternprovider"), EX_EXTENDED_MOLECULAR_ASSEMBLER,
                "ae2:blue_lumen_paint_ball",
                "ae2:light_blue_lumen_paint_ball",
                "ae2:cyan_lumen_paint_ball");
        var patternCoreKey = key('A', "ae2:purple_lumen_paint_ball", 'B', "ae2:magenta_lumen_paint_ball",
                'C', "ae2:pink_lumen_paint_ball", 'K', "expatternprovider:assembler_matrix_pattern",
                'P', COMPAT_PROCESSOR);
        patternCoreKey.add("T", tag("expatternprovider:extended_pattern_provider"));
        saveShaped(output, "extended_assembler_matrix_pattern_core", myoConditions("expatternprovider"), "redstone",
                new String[]{"ABC", "KTP", "ABC"}, patternCoreKey,
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core", true);

        shapeless("minecraft:crafting_shapeless",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_uploader", 1)
                .conditions(extendedAEPlus)
                .requires(item("extendedae_plus:assembler_matrix_upload_core"))
                .requires(item(COMPAT_PROCESSOR))
                .save(output, recipeId("extended_assembler_matrix_pattern_uploader"));

        for (String tier : new String[] { "extended", "epic", "legendary" }) {
            String coreId = tier + "_assembler_matrix_crafting_core";
            saveMatrixCorePlus(output, coreId + "_plus", "extendedmolecularassembler:" + coreId,
                    "extendedmolecularassembler:" + coreId + "_plus", extendedAEPlusContent);
        }
        saveMatrixCorePlus(output, "extended_assembler_matrix_pattern_core_plus",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus", extendedAEPlusContent);
    }

    private static void buildExtendedPatternEncodingTerminalRecipes(JsonRecipeOutput output) {
        saveExtendedPatternTerminal(output, "extended_pattern_encoding_terminal",
                conditionsFor(new String[]{}, "Re-Avaritia", "Avaritia", "extendedcrafting"));
        saveExtendedPatternTerminal(output, "reavaritia/extended_pattern_encoding_terminal",
                conditionsFor(new String[]{"Re-Avaritia"}, "Avaritia", "extendedcrafting"),
                "avaritia:sculk_crafting_table",
                "avaritia:nether_crafting_table",
                "avaritia:end_crafting_table",
                "avaritia:extreme_crafting_table");
        saveExtendedPatternTerminal(output, "avaritianeo/extended_pattern_encoding_terminal",
                conditionsFor(new String[]{"Avaritia"}, "Re-Avaritia", "extendedcrafting"),
                "avaritia:extreme_crafting_table");
        saveExtendedPatternTerminal(output, "extendedcrafting/extended_pattern_encoding_terminal",
                conditionsFor(new String[]{"extendedcrafting"}, "Re-Avaritia", "Avaritia"),
                "extendedcrafting:basic_table",
                "extendedcrafting:advanced_table",
                "extendedcrafting:elite_table",
                "extendedcrafting:ultimate_table");
        saveExtendedPatternTerminal(output, "reavaritia_extendedcrafting/extended_pattern_encoding_terminal",
                conditionsFor(new String[]{"Re-Avaritia", "extendedcrafting"}, "Avaritia"),
                "avaritia:sculk_crafting_table",
                "avaritia:nether_crafting_table",
                "avaritia:end_crafting_table",
                "avaritia:extreme_crafting_table",
                "extendedcrafting:basic_table",
                "extendedcrafting:advanced_table",
                "extendedcrafting:elite_table",
                "extendedcrafting:ultimate_table");
        saveExtendedPatternTerminal(output, "avaritianeo_extendedcrafting/extended_pattern_encoding_terminal",
                conditionsFor(new String[]{"Avaritia", "extendedcrafting"}, "Re-Avaritia"),
                "avaritia:extreme_crafting_table",
                "extendedcrafting:basic_table",
                "extendedcrafting:advanced_table",
                "extendedcrafting:elite_table",
                "extendedcrafting:ultimate_table");
        saveExtendedPatternTerminal(output, "extendedterminal/extended_pattern_encoding_terminal",
                myoConditions("extendedterminal"),
                "extendedterminal:united_terminal");
    }

    private static void saveExtendedPatternTerminal(JsonRecipeOutput output, String path, JsonArray conditions,
            String... extraIngredients) {
        JsonObject recipe = recipe("minecraft:crafting_shapeless");
        recipe.add("conditions", conditions);

        JsonArray ingredients = new JsonArray();
        ingredients.add(item("ae2:pattern_encoding_terminal"));
        for (String ingredient : extraIngredients) {
            ingredients.add(item(ingredient));
        }
        recipe.add("ingredients", ingredients);
        recipe.add("result", stack("extendedmolecularassembler:extended_pattern_encoding_terminal", 1));
        save(output, path, recipe);
    }

    private static void saveMatrixCore(JsonRecipeOutput output, String path, String result,
            JsonArray conditions, String assembler, String paintA, String paintB, String paintC) {
        saveShaped(output, path, conditions, "redstone",
                new String[]{"ABC", "FMP", "ABC"},
                key('A', paintA, 'B', paintB, 'C', paintC, 'P', COMPAT_PROCESSOR,
                        'F', ASSEMBLER_MATRIX_FRAME, 'M', assembler),
                result, true);
    }

    private static void saveMatrixCorePlus(JsonRecipeOutput output, String path, String core, String result,
            JsonArray conditions) {
        saveShaped(output, path, conditions, "redstone",
                new String[]{"PCP", "CNC", "PCP"},
                key('C', core, 'N', "minecraft:nether_star", 'P', COMPAT_PROCESSOR),
                result, true);
    }

    private static void buildGameTestRecipes(JsonRecipeOutput output) {
        saveExternalTable(output, "gametest/ec_tier_1", devMyoConditions("extendedcrafting"), "extendedcrafting:shaped_table", 1,
                new String[]{"CIC", "IRI", "CIC"},
                key('C', "minecraft:copper_ingot", 'I', "minecraft:iron_ingot", 'R', "minecraft:redstone"),
                "minecraft:diamond");
        saveExternalTable(output, "gametest/ec_tier_2", devMyoConditions("extendedcrafting"), "extendedcrafting:shaped_table", 2,
                new String[]{"GGGGG", "GLLLG", "GLRLG", "GLLLG", "GGGGG"},
                key('G', "minecraft:gold_ingot", 'L', "minecraft:lapis_lazuli", 'R', "minecraft:redstone"),
                "minecraft:emerald");
        saveExternalTable(output, "gametest/ec_tier_3", devMyoConditions("extendedcrafting"), "extendedcrafting:shaped_table", 3,
                new String[]{"CCCCCCC", "CDDDDDC", "CDGGGDC", "CDGRGDC", "CDGGGDC", "CDDDDDC", "CCCCCCC"},
                key('C', "minecraft:cobblestone", 'D', "minecraft:diamond", 'G', "minecraft:gold_ingot", 'R', "minecraft:redstone"),
                "minecraft:netherite_scrap");
        saveExternalTable(output, "gametest/ec_tier_4", devMyoConditions("extendedcrafting"), "extendedcrafting:shaped_table", 4,
                new String[]{"CCCCCCCCC", "CLLLLLLLC", "CLRRRRRLC", "CLRIIIRLC", "CLRIGIRLC", "CLRIIIRLC", "CLRRRRRLC", "CLLLLLLLC", "CCCCCCCCC"},
                key('C', "minecraft:copper_ingot", 'L', "minecraft:lapis_lazuli", 'R', "minecraft:redstone", 'I', "minecraft:iron_ingot", 'G', "minecraft:gold_ingot"),
                "minecraft:emerald_block");

        for (int tier = 5; tier <= 6; tier++) {
            int side = tier * 2 + 1;
            String[] rows = new String[side];
            java.util.Arrays.fill(rows, "C".repeat(side));
            rows[side / 2] = "C".repeat(side / 2) + "N" + "C".repeat(side / 2);
            JsonArray conditions = devMyoConditions("extendedcrafting");
            JsonObject tableExists = new JsonObject();
            tableExists.addProperty("type", "forge:item_exists");
            tableExists.addProperty("item", "extendedcrafting:" + (tier == 5 ? "epic" : "legendary") + "_table");
            conditions.add(tableExists);
            saveExternalTable(output, "gametest/ec_tier_" + tier, conditions, "extendedcrafting:shaped_table", tier,
                    rows, key('C', "minecraft:copper_ingot", 'N', "minecraft:nether_star"),
                    tier == 5 ? "minecraft:diamond_block" : "minecraft:netherite_block");
        }

        saveExternalTable(output, "gametest/re_tier_1", devMyoConditions("Re-Avaritia"), "avaritia:shaped_table", 1,
                new String[]{"STS", "TBT", "STS"},
                key('S', "minecraft:stone", 'T', "minecraft:stick", 'B', "minecraft:bone"),
                "minecraft:amethyst_shard");
        saveExternalTable(output, "gametest/re_tier_2", devMyoConditions("Re-Avaritia"), "avaritia:shaped_table", 2,
                new String[]{"OOOOO", "ORRRO", "ORERO", "ORRRO", "OOOOO"},
                key('O', "minecraft:obsidian", 'R', "minecraft:redstone", 'E', "minecraft:ender_pearl"),
                "minecraft:quartz");
        saveExternalTable(output, "gametest/re_tier_3", devMyoConditions("Re-Avaritia"), "avaritia:shaped_table", 3,
                new String[]{"BBBBBBB", "BQQQQQB", "BQEEEQB", "BQENEQB", "BQEEEQB", "BQQQQQB", "BBBBBBB"},
                key('B', "minecraft:blackstone", 'Q', "minecraft:quartz", 'E', "minecraft:ender_pearl", 'N', "minecraft:nether_star"),
                "minecraft:diamond_block");
        saveExternalTable(output, "gametest/re_tier_4", devMyoConditions("Re-Avaritia"), "avaritia:shaped_table", 4,
                new String[]{"AAAAAAAAA", "AQQQQQQQA", "AQRRRRRQA", "AQRDDDRQA", "AQRDNDRQA", "AQRDDDRQA", "AQRRRRRQA", "AQQQQQQQA", "AAAAAAAAA"},
                key('A', "minecraft:amethyst_shard", 'Q', "minecraft:quartz", 'R', "minecraft:redstone", 'D', "minecraft:diamond", 'N', "minecraft:nether_star"),
                "minecraft:netherite_ingot");

        String[] xtreme = {"OOOOOOOOO", "OIIIIIIIO", "OIGGGGGIO", "OIGDDDGIO", "OIGDNDGIO", "OIGDDDGIO", "OIGGGGGIO", "OIIIIIIIO", "OOOOOOOOO"};
        JsonObject xtremeKey = key('O', "minecraft:obsidian", 'I', "minecraft:iron_ingot", 'G', "minecraft:gold_ingot", 'D', "minecraft:diamond", 'N', "minecraft:nether_star");
        saveExternalTable(output, "dev/avaritianeo_xtreme_vanilla_test", devMyoConditions("Avaritia"), "avaritia:extreme_shaped", null, xtreme, xtremeKey, "minecraft:netherite_ingot");
        saveExternalTable(output, "dev/reavaritia_xtreme_vanilla_test", devMyoConditions("Re-Avaritia"), "avaritia:shaped_table", 4, xtreme, xtremeKey, "minecraft:netherite_ingot");
        saveExternalTable(output, "dev/extendedcrafting_ultimate_vanilla_test", devMyoConditions("extendedcrafting"), "extendedcrafting:shaped_table", 4,
                new String[]{"DDDDDDDDD", "DEEEEEEDD", "DERRRRRED", "DERGGGRED", "DERGLGRED", "DERGGGRED", "DERRRRRED", "DDEEEEEED", "DDDDDDDDD"},
                key('D', "minecraft:diamond", 'E', "minecraft:emerald", 'R', "minecraft:redstone", 'G', "minecraft:gold_ingot", 'L', "minecraft:lapis_lazuli"),
                "minecraft:emerald_block");
    }

    private static void saveShaped(JsonRecipeOutput output, String path, JsonArray conditions, String category,
            String[] pattern, JsonObject key, String result, boolean showNotification) {
        JsonObject recipe = recipe("minecraft:crafting_shaped");
        if (conditions != null && !conditions.isEmpty()) {
            recipe.add("conditions", conditions);
        }
        recipe.addProperty("category", category);
        recipe.add("pattern", pattern(pattern));
        recipe.add("key", key);
        recipe.add("result", stack(result, 1));
        recipe.addProperty("show_notification", showNotification);
        save(output, path, recipe);
    }

    private static void saveExternalTable(JsonRecipeOutput output, String path, JsonArray conditions, String type,
            Integer tier, String[] pattern, JsonObject key, String result) {
        JsonObject recipe = recipe(type);
        recipe.add("conditions", conditions);
        recipe.add("pattern", pattern(pattern));
        recipe.add("key", key);
        recipe.add("result", stack(result, 1));
        if (tier != null) {
            recipe.addProperty("tier", tier);
        }
        save(output, path, recipe);
    }

    private static void saveAE2WTLibUpgrade(JsonRecipeOutput output, String path, String terminal,
            String terminalName) {
        JsonObject recipe = recipe("ae2wtlib:upgrade");
        recipe.add("conditions", myoConditions("ae2wtlib"));
        recipe.add("terminal", item(terminal));
        recipe.addProperty("terminalName", terminalName);
        save(output, path, recipe);
    }

    private static JsonArray devMyoConditions(String activeMod) {
        JsonArray conditions = new JsonArray();
        conditions.add(devCondition());
        conditions.add(myoCondition(activeMod));
        return conditions;
    }

    private static JsonArray myoConditions(String... activeMods) {
        JsonArray values = new JsonArray();
        for (String activeMod : activeMods) {
            values.add(myoCondition(activeMod));
        }
        return values;
    }

    private static JsonArray conditionsFor(String[] activeMods, String... inactiveMods) {
        JsonArray conditions = myoConditions(activeMods);
        for (String inactiveMod : inactiveMods) {
            conditions.add(notCondition(myoCondition(inactiveMod)));
        }
        return conditions;
    }

    private static JsonObject notCondition(JsonObject value) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "forge:not");
        condition.add("value", value);
        return condition;
    }

    private static JsonArray extendedAEPlusContentConditions() {
        JsonArray conditions = myoConditions("expatternprovider");

        JsonArray alternatives = new JsonArray();
        alternatives.add(myoCondition("extendedae-plus"));
        JsonObject standalone = new JsonObject();
        standalone.addProperty("type", "extendedmolecularassembler:extendedae_plus_standalone");
        alternatives.add(standalone);

        JsonObject anyAvailable = new JsonObject();
        anyAvailable.addProperty("type", "forge:or");
        anyAvailable.add("values", alternatives);
        conditions.add(anyAvailable);
        return conditions;
    }

    private static JsonObject recipe(String type) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", type);
        return recipe;
    }

    private static JsonArray pattern(String[] pattern) {
        JsonArray json = new JsonArray();
        for (String row : pattern) {
            json.add(row);
        }
        return json;
    }

    private static JsonObject key(Object... entries) {
        JsonObject key = new JsonObject();
        for (int i = 0; i < entries.length; i += 2) {
            key.add(String.valueOf(entries[i]), item((String) entries[i + 1]));
        }
        return key;
    }

    private static void save(JsonRecipeOutput output, String path, JsonObject recipe) {
        output.accept(recipeId(path), recipe);
    }

    private static ResourceLocation recipeId(String path) {
        return ExtendedMolecularAssembler.makeId(path);
    }
}
