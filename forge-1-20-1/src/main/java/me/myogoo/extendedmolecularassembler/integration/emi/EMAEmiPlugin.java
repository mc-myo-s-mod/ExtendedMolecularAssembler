package me.myogoo.extendedmolecularassembler.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;

@EmiEntrypoint
@me.myogoo.myotus.api.annotation.itemList.emi.EMI
public final class EMAEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(
                ExtendedPatternEncodingTermMenu.TYPE,
                new ExtendedPatternEmiRecipeHandler());
    }
}
