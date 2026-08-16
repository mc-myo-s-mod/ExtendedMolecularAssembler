package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.myotus.data.builder.extendedae.MyoExtendedAECrystalAssemblerRecipeBuilder;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.devCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.shapeless;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.stack;

public final class EMARecipeDataProvider extends JsonRecipeProvider {
    private static final String COMPAT_PROCESSOR = "myotus:compat_processor";
    private static final String CONCURRENT_PROCESSOR = "extendedae:concurrent_processor";
    private static final String ASSEMBLER_MATRIX_FRAME = "extendedae:assembler_matrix_wall";
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
        MyoExtendedAECrystalAssemblerRecipeBuilder
                .create(recipeId("ex_extended_molecular_assembler"))
                .conditions(myoConditions("extendedae"))
                .inputItem("extendedmolecularassembler:extended_molecular_assembler", 4)
                .inputItem(CONCURRENT_PROCESSOR, 4)
                .inputItem(COMPAT_PROCESSOR, 4)
                .inputTag("c:dusts/ender_pearl", 4)
                .inputItem("ae2:speed_card", 4)
                .output(EX_EXTENDED_MOLECULAR_ASSEMBLER, 1)
                .save(output);
        shapeless("minecraft:crafting_shapeless",
                "extendedmolecularassembler:extended_pattern_encoding_terminal", 1)
                .requires(item(COMPAT_PROCESSOR))
                .requires(item("ae2:pattern_encoding_terminal"))
                .requires(item("ae2:annihilation_core"))
                .requires(item("ae2:formation_core"))
                .save(output, recipeId("extended_pattern_encoding_terminal"));

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

        saveShaped(output, "wireless_extended_pattern_encoding_terminal", myoConditions("ae2wtlib"), "redstone",
                new String[]{"W", "T", "C"},
                key('C', "ae2:dense_energy_cell", 'T', "extendedmolecularassembler:extended_pattern_encoding_terminal", 'W', "ae2:wireless_receiver"),
                "extendedmolecularassembler:wireless_extended_pattern_encoding_terminal", true);
    }

    private static void buildIntegrationRecipes(JsonRecipeOutput output) {
        JsonArray extendedAEPlus = myoConditions("extendedae", "extendedae_plus");
        JsonArray extendedAEPlusContent = extendedAEPlusContentConditions();
        saveAE2WTLibUpgrade(output, "wireless_universal_terminal/upgrade_extended_pattern_encoding",
                "extendedmolecularassembler:wireless_extended_pattern_encoding_terminal",
                "extended_pattern_encoding");

        saveMatrixCoreCrystalAssembler(output,
                "extended_assembler_matrix_crafting_core",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core",
                "ae2:blue_lumen_paint_ball",
                "ae2:light_blue_lumen_paint_ball",
                "ae2:cyan_lumen_paint_ball");
        saveMatrixCoreCrystalAssembler(output,
                "extended_assembler_matrix_pattern_core",
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core",
                "ae2:purple_lumen_paint_ball",
                "ae2:magenta_lumen_paint_ball",
                "ae2:pink_lumen_paint_ball");

        shapeless("minecraft:crafting_shapeless",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_uploader", 1)
                .conditions(extendedAEPlus)
                .requires(item("extendedae_plus:assembler_matrix_upload_core"))
                .requires(item(COMPAT_PROCESSOR))
                .save(output, recipeId("extended_assembler_matrix_pattern_uploader"));

        saveMatrixCorePlus(output, "extended_assembler_matrix_crafting_core_plus",
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core",
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus", extendedAEPlusContent);
        saveMatrixCorePlus(output, "extended_assembler_matrix_pattern_core_plus",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core",
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus", extendedAEPlusContent);
    }

    private static void saveMatrixCoreCrystalAssembler(JsonRecipeOutput output, String path, String result,
            String paintA, String paintB, String paintC) {
        MyoExtendedAECrystalAssemblerRecipeBuilder
                .create(recipeId(path))
                .conditions(myoConditions("extendedae"))
                .inputItem(ASSEMBLER_MATRIX_FRAME, 1)
                .inputItem(EX_EXTENDED_MOLECULAR_ASSEMBLER, 1)
                .inputItem(COMPAT_PROCESSOR, 1)
                .inputItem(CONCURRENT_PROCESSOR, 1)
                .inputItem(paintA, 2)
                .inputItem(paintB, 2)
                .inputItem(paintC, 2)
                .output(result, 1)
                .save(output);
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
            recipe.add("neoforge:conditions", conditions);
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
        recipe.add("neoforge:conditions", conditions);
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
        recipe.add("neoforge:conditions", myoConditions("ae2wtlib"));
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

    private static JsonArray extendedAEPlusContentConditions() {
        JsonArray conditions = myoConditions("extendedae");

        JsonArray alternatives = new JsonArray();
        alternatives.add(myoCondition("extendedae_plus"));
        JsonObject standalone = new JsonObject();
        standalone.addProperty("type", "extendedmolecularassembler:extendedae_plus_standalone");
        alternatives.add(standalone);

        JsonObject anyAvailable = new JsonObject();
        anyAvailable.addProperty("type", "neoforge:or");
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
