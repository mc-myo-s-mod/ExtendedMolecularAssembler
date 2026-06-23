package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.conditions;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.devCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.stack;

public final class EMARecipeDataProvider extends JsonRecipeProvider {
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
                new String[]{"CFC", "AMA", "CFC"},
                key('A', "ae2:engineering_processor", 'C', "ae2:calculation_processor", 'F', "ae2:fluix_glass_cable", 'M', "ae2:molecular_assembler"),
                "extendedmolecularassembler:extended_molecular_assembler", true);
        saveShaped(output, "ex_extended_molecular_assembler", conditions("extendedae"), "redstone",
                new String[]{"EDE", "AMA", "EDE"},
                key('A', "ae2:engineering_processor", 'D', "minecraft:diamond", 'E', "minecraft:ender_eye", 'M', "extendedmolecularassembler:extended_molecular_assembler"),
                "extendedmolecularassembler:ex_extended_molecular_assembler", true);
        saveShaped(output, "extended_crafting_pattern", null, "misc",
                new String[]{"QPQ", "RCR", "QPQ"},
                key('C', "minecraft:crafting_table", 'P', "ae2:blank_pattern", 'Q', "ae2:certus_quartz_crystal", 'R', "minecraft:redstone"),
                "extendedmolecularassembler:extended_crafting_pattern", true);
    }

    private static void buildIntegrationRecipes(JsonRecipeOutput output) {
        saveShaped(output, "extended_quantum_crafter", conditions("advanced_ae"), "redstone",
                new String[]{"EPE", "QMQ", "EPE"},
                key('E', "extendedmolecularassembler:extended_crafting_pattern", 'M', "advanced_ae:quantum_crafter", 'P', "ae2:engineering_processor", 'Q', "ae2:quantum_link"),
                "extendedmolecularassembler:extended_quantum_crafter", true);

        JsonArray extendedAE = conditions("extendedae");
        JsonArray extendedAEPlus = modLoadedConditions("extendedae", "extendedae_plus");
        saveShaped(output, "extended_assembler_matrix_crafting_core", extendedAE, "redstone",
                new String[]{"EPE", "CMC", "EPE"},
                key('C', "ae2:logic_processor", 'E', "extendedmolecularassembler:ex_extended_molecular_assembler", 'M', "extendedae:assembler_matrix_crafter", 'P', "ae2:engineering_processor"),
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core", true);
        saveShaped(output, "extended_assembler_matrix_pattern_core", extendedAE, "redstone",
                new String[]{"EPE", "CMC", "EPE"},
                key('C', "ae2:calculation_processor", 'E', "extendedmolecularassembler:extended_crafting_pattern", 'M', "extendedae:assembler_matrix_pattern", 'P', "ae2:engineering_processor"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core", true);
        saveShaped(output, "extended_assembler_matrix_pattern_uploader", extendedAEPlus, "redstone",
                new String[]{" P ", "HCH", " M "},
                key('C', "ae2:calculation_processor", 'H', "minecraft:hopper", 'M', "extendedae:assembler_matrix_pattern", 'P', "extendedmolecularassembler:extended_crafting_pattern"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_uploader", true);
        saveShaped(output, "extended_assembler_matrix_crafting_core_plus", extendedAEPlus, "redstone",
                new String[]{" P ", "PBP", " P "},
                key('B', "extendedmolecularassembler:extended_assembler_matrix_crafting_core", 'P', "extendedae_plus:assembler_matrix_crafter_plus"),
                "extendedmolecularassembler:extended_assembler_matrix_crafting_core_plus", true);
        saveShaped(output, "extended_assembler_matrix_pattern_core_plus", extendedAEPlus, "redstone",
                new String[]{" P ", "PBP", " P "},
                key('B', "extendedmolecularassembler:extended_assembler_matrix_pattern_core", 'P', "extendedae_plus:assembler_matrix_pattern_plus"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core_plus", true);
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

    private static JsonArray devMyoConditions(String activeMod) {
        JsonArray conditions = new JsonArray();
        conditions.add(devCondition());
        conditions.add(myoCondition(activeMod));
        return conditions;
    }

    private static JsonArray modLoadedConditions(String... modIds) {
        JsonArray values = new JsonArray();
        for (String modId : modIds) {
            conditions(modId).forEach(values::add);
        }
        return values;
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
