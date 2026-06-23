package me.myogoo.extendedmolecularassembler.mixin.advancedae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.menu.implementations.UpgradeableMenu;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.advancedae.EMAAdvancedAEIntegration;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.pedroksl.advanced_ae.gui.QuantumCrafterMenu", remap = false)
public abstract class QuantumCrafterMenuMixin {
    @Shadow
    @Final
    private Slot[] patternSlots;

    @Inject(method = "isValidForSlot", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$allowExtendedCraftingPatterns(Slot slot, ItemStack stack,
            CallbackInfoReturnable<Boolean> cir) {
        if (!this.extendedmolecularassembler$isExtendedQuantumCrafter()) {
            return;
        }
        if (!stack.is(EMAItems.EXTENDED_CRAFTING_PATTERN.get()) || !PatternDetailsHelper.isEncodedPattern(stack)) {
            return;
        }

        for (var patternSlot : this.patternSlots) {
            if (slot == patternSlot) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Unique
    private boolean extendedmolecularassembler$isExtendedQuantumCrafter() {
        if (EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER == null) {
            return false;
        }
        var host = ((UpgradeableMenu<?>) (Object) this).getHost();
        return host instanceof QuantumCrafterEntity quantumCrafter
                && quantumCrafter.getBlockState().is(EMAAdvancedAEIntegration.EXTENDED_QUANTUM_CRAFTER.get());
    }
}
