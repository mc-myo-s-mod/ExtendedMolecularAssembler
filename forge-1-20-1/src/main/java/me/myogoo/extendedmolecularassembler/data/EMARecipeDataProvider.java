package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.shapeless;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.stack;

public final class EMARecipeDataProvider extends JsonRecipeProvider {
    private static final String COMPAT_PROCESSOR = "myotus:compat_processor";

    public EMARecipeDataProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(JsonRecipeOutput output) {
        buildCoreRecipes(output);
        buildIntegrationRecipes(output);
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
        saveShaped(output, "ex_extended_molecular_assembler", myoConditions("expatternprovider"), "redstone",
                new String[]{"CXC", "SMS", "CXC"},
                key('C', COMPAT_PROCESSOR, 'M', "extendedmolecularassembler:extended_molecular_assembler", 'S', "ae2:speed_card", 'X', "expatternprovider:ex_molecular_assembler"),
                "extendedmolecularassembler:ex_extended_molecular_assembler", true);
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

        JsonArray reAvaritia = myoConditions("Re-Avaritia");
        saveShaped(output, "re_avaritia_sculk_me_crafting_provider", reAvaritia, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "ae2:pattern_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_glass_cable", 'T', "avaritia:sculk_crafting_table"),
                "extendedmolecularassembler:re_avaritia_sculk_me_crafting_provider", true);
        saveShaped(output, "re_avaritia_nether_me_crafting_provider", reAvaritia, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:re_avaritia_sculk_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_covered_cable", 'T', "avaritia:nether_crafting_table"),
                "extendedmolecularassembler:re_avaritia_nether_me_crafting_provider", true);
        saveShaped(output, "re_avaritia_end_me_crafting_provider", reAvaritia, "redstone",
                new String[]{" L ", "PCP", " T "},
                key('C', "extendedmolecularassembler:re_avaritia_nether_me_crafting_provider", 'L', COMPAT_PROCESSOR, 'P', "ae2:fluix_smart_cable", 'T', "avaritia:end_crafting_table"),
                "extendedmolecularassembler:re_avaritia_end_me_crafting_provider", true);
        saveShaped(output, "xtreme_me_crafting_provider_from_re_avaritia", reAvaritia, "redstone",
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
        JsonArray expatternprovider = myoConditions("expatternprovider");
        saveAE2WTLibUpgrade(output, "wireless_universal_terminal/upgrade_extended_pattern_encoding",
                "extendedmolecularassembler:wireless_extended_pattern_encoding_terminal",
                "extended_pattern_encoding");
        saveShaped(output, "extended_assembler_matrix_pattern_core", expatternprovider, "redstone",
                new String[]{"BEB", "PMP", "BEB"},
                key('B', "ae2:blank_pattern", 'E', "ae2:engineering_processor", 'M', "expatternprovider:assembler_matrix_pattern", 'P', "expatternprovider:ex_pattern_provider"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core", true);
        saveShaped(output, "extended_assembler_matrix_pattern_uploader", expatternprovider, "redstone",
                new String[]{"HTH", "CUC", "HSH"},
                key('C', "ae2:calculation_processor", 'H', "minecraft:hopper", 'S', "ae2:formation_core", 'T', "ae2:pattern_encoding_terminal", 'U', "expatternprovider:assembler_matrix_pattern"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_uploader", true);
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
        if (showNotification) {
            recipe.addProperty("show_notification", true);
        }
        save(output, path, recipe);
    }

    private static void saveAE2WTLibUpgrade(JsonRecipeOutput output, String path, String terminal,
            String terminalName) {
        JsonObject upgrade = recipe("ae2wtlib:upgrade");
        upgrade.add("terminal", item(terminal));
        upgrade.addProperty("terminalName", terminalName);

        JsonObject entry = new JsonObject();
        entry.add("conditions", myoConditions("ae2wtlib"));
        entry.add("recipe", upgrade);

        JsonArray recipes = new JsonArray();
        recipes.add(entry);

        JsonObject conditional = recipe("forge:conditional");
        conditional.add("recipes", recipes);
        save(output, path, conditional);
    }

    private static JsonArray myoConditions(String... activeMods) {
        JsonArray values = new JsonArray();
        for (String activeMod : activeMods) {
            values.add(myoCondition(activeMod));
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
