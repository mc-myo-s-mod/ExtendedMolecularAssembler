package me.myogoo.extendedmolecularassembler.integration.emi;

import appeng.menu.SlotSemantics;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import me.myogoo.extendedmolecularassembler.integration.itemlist.ExtendedPatternRecipeTransfer;
import me.myogoo.extendedmolecularassembler.integration.itemlist.RecipeBridgePathResolver;
import me.myogoo.extendedmolecularassembler.menu.EMASlotSemantics;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ExtendedPatternEmiRecipeHandler
        implements StandardRecipeHandler<ExtendedPatternEncodingTermMenu> {
    @Override
    public List<Slot> getInputSources(ExtendedPatternEncodingTermMenu menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(getCraftingSlots(menu));
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(ExtendedPatternEncodingTermMenu menu) {
        return menu.getSlots(EMASlotSemantics.EXTENDED_PATTERN_CRAFTING_GRID);
    }

    @Override
    public @Nullable Slot getOutputSlot(ExtendedPatternEncodingTermMenu menu) {
        var slots = menu.getSlots(EMASlotSemantics.EXTENDED_PATTERN_CRAFTING_RESULT);
        return slots.isEmpty() ? null : slots.getFirst();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        var holder = findRecipe(recipe, Minecraft.getInstance().level);
        return holder != null && ExtendedPatternRecipeTransfer.canTransfer(holder.value());
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<ExtendedPatternEncodingTermMenu> context) {
        if (context.getType() != EmiCraftContext.Type.FILL_BUTTON) {
            return false;
        }
        return findTransferableRecipe(recipe, context.getScreenHandler().getPlayer().level()) != null;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<ExtendedPatternEncodingTermMenu> context) {
        if (context.getType() != EmiCraftContext.Type.FILL_BUTTON) {
            return false;
        }

        var holder = findTransferableRecipe(recipe, context.getScreenHandler().getPlayer().level());
        if (holder == null) {
            return false;
        }

        ExtendedPatternRecipeTransfer.transfer(context.getScreenHandler(), holder);
        Minecraft.getInstance().setScreen(context.getScreen());
        return true;
    }

    private static @Nullable RecipeHolder<?> findTransferableRecipe(EmiRecipe recipe, Level level) {
        var holder = findRecipe(recipe, level);
        return holder != null && ExtendedPatternRecipeTransfer.canTransfer(holder.value()) ? holder : null;
    }

    private static @Nullable RecipeHolder<?> findRecipe(EmiRecipe recipe, @Nullable Level level) {
        var backingRecipe = recipe.getBackingRecipe();
        if (backingRecipe != null) {
            return backingRecipe;
        }

        var recipeId = recipe.getId();
        if (level == null || recipeId == null) {
            return null;
        }
        var unwrapped = RecipeBridgePathResolver.unwrap(recipeId.getNamespace(), recipeId.getPath());
        var lookupId = ResourceLocation.tryBuild(unwrapped.namespace(), unwrapped.path());
        if (lookupId == null) {
            return null;
        }
        return level.getRecipeManager()
                .byKey(lookupId)
                .orElse(null);
    }
}
