package me.myogoo.extendedmolecularassembler.integration.itemlist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecipeBridgeIdResolverTest {
    @Test
    void unwrapsTooManyRecipeViewersRecipeId() {
        assertEquals(
                new RecipeBridgePathResolver.RecipeId("extendedcrafting", "table/ultimate_component"),
                RecipeBridgePathResolver.unwrap(
                        "toomanyrecipeviewers",
                        "/extendedcrafting/table/ultimate_component"));
    }

    @Test
    void unwrapsJemiRecipeId() {
        assertEquals(
                new RecipeBridgePathResolver.RecipeId("avaritia", "extreme/infinity_catalyst"),
                RecipeBridgePathResolver.unwrap("jei", "/avaritia/extreme/infinity_catalyst"));
    }

    @Test
    void preservesNativeAndMalformedIds() {
        assertEquals(
                new RecipeBridgePathResolver.RecipeId("extendedcrafting", "table/basic_component"),
                RecipeBridgePathResolver.unwrap("extendedcrafting", "table/basic_component"));
        assertEquals(
                new RecipeBridgePathResolver.RecipeId("toomanyrecipeviewers", "/extendedcrafting"),
                RecipeBridgePathResolver.unwrap("toomanyrecipeviewers", "/extendedcrafting"));
    }
}
