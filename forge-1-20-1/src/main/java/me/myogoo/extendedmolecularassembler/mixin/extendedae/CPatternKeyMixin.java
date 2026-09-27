package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.api.crafting.PatternDetailsHelper;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CPatternKey.class, remap = false)
public abstract class CPatternKeyMixin {
    @Shadow
    private ItemStack pattern;

    @Inject(method = "onMessage(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void ema$openPatternView(Player player, CallbackInfo ci) {
        if (this.pattern.is(EMAItems.EXTENDED_CRAFTING_PATTERN.get())
                && PatternDetailsHelper.decodePattern(this.pattern, player.level())
                        instanceof ExtendedTableCraftingPattern) {
            EMAExtendedAEIntegration.openPatternView(player, this.pattern);
            ci.cancel();
        }
    }
}
