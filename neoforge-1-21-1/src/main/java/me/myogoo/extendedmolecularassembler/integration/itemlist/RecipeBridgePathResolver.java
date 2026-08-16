package me.myogoo.extendedmolecularassembler.integration.itemlist;

import java.util.Set;

public final class RecipeBridgePathResolver {
    private static final Set<String> BRIDGE_NAMESPACES = Set.of("jei", "toomanyrecipeviewers");

    private RecipeBridgePathResolver() {
    }

    public static RecipeId unwrap(String namespace, String path) {
        if (!BRIDGE_NAMESPACES.contains(namespace) || !path.startsWith("/")) {
            return new RecipeId(namespace, path);
        }

        var namespaceEnd = path.indexOf('/', 1);
        if (namespaceEnd <= 1 || namespaceEnd >= path.length() - 1) {
            return new RecipeId(namespace, path);
        }

        return new RecipeId(
                path.substring(1, namespaceEnd),
                path.substring(namespaceEnd + 1));
    }

    public record RecipeId(String namespace, String path) {
    }
}
