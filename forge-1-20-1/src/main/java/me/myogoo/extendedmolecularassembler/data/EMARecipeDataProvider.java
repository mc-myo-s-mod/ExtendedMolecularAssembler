package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.myotus.data.recipe.JsonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.conditions;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.item;
import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.stack;

public final class EMARecipeDataProvider extends JsonRecipeProvider {
    public EMARecipeDataProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(JsonRecipeOutput output) {
        saveShaped(output, "extended_molecular_assembler", null, "redstone",
                new String[]{"CFC", "AMA", "CFC"},
                key('A', "ae2:engineering_processor", 'C', "ae2:calculation_processor", 'F', "ae2:fluix_glass_cable", 'M', "ae2:molecular_assembler"),
                "extendedmolecularassembler:extended_molecular_assembler", true);
        saveShaped(output, "ex_extended_molecular_assembler", null, "redstone",
                new String[]{"EDE", "AMA", "EDE"},
                key('A', "ae2:engineering_processor", 'D', "minecraft:diamond", 'E', "minecraft:ender_eye", 'M', "extendedmolecularassembler:extended_molecular_assembler"),
                "extendedmolecularassembler:ex_extended_molecular_assembler", true);
        saveShaped(output, "extended_crafting_pattern", null, "misc",
                new String[]{"QPQ", "RCR", "QPQ"},
                key('C', "minecraft:crafting_table", 'P', "ae2:blank_pattern", 'Q', "ae2:certus_quartz_crystal", 'R', "minecraft:redstone"),
                "extendedmolecularassembler:extended_crafting_pattern", true);

        JsonArray expatternprovider = conditions("expatternprovider");
        saveShaped(output, "extended_assembler_matrix_pattern_core", expatternprovider, "redstone",
                new String[]{"PPP", "PEP", "PPP"},
                key('P', "ae2:blank_pattern", 'E', "extendedmolecularassembler:ex_extended_molecular_assembler"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_core", false);
        saveShaped(output, "extended_assembler_matrix_pattern_uploader", expatternprovider, "redstone",
                new String[]{" P ", "PEP", " P "},
                key('P', "ae2:blank_pattern", 'E', "extendedmolecularassembler:extended_assembler_matrix_pattern_core"),
                "extendedmolecularassembler:extended_assembler_matrix_pattern_uploader", false);
    }

    @Override
    public @NotNull String getName() {
        return "Extended Molecular Assembler recipes";
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
