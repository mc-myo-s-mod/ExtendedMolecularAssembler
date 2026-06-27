package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.client.gui.GuiAssemblerMatrix;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import me.myogoo.extendedmolecularassembler.client.widget.EMAIconButton;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.AssemblerMatrixNavigationContext;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAOpenExtendedAEAssemblerMatrixScreenPacket;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMARequestMatrixCraftingStatusPacket;
import me.myogoo.myotus.client.gui.MyoIcon;
import me.myogoo.myotus.client.gui.widgets.button.CustomImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiAssemblerMatrix.class, remap = false)
public abstract class GuiAssemblerMatrixMixin extends AEBaseScreen<ContainerAssemblerMatrix> {
    protected GuiAssemblerMatrixMixin(ContainerAssemblerMatrix menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void extendedmolecularassembler$addExtendedPatternButton(ContainerAssemblerMatrix menu,
            Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {
        var button = new CustomImageButton(MyoIcon.EMA_CONFIG, btn -> {
            var matrixPos = menu.getHost().getBlockPos();
            AssemblerMatrixNavigationContext.rememberMatrixPos(matrixPos);
            PacketDistributor.sendToServer(new EMAOpenExtendedAEAssemblerMatrixScreenPacket(
                    matrixPos,
                    EMAOpenExtendedAEAssemblerMatrixScreenPacket.Target.PATTERN_CORE));
        });
        button.setMessage(Component.translatable("gui.extendedmolecularassembler.matrix.openExtendedPatterns"));
        this.addToLeftToolbar(button);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void extendedmolecularassembler$addCraftingStatusButton(CallbackInfo ci) {
        var button = new EMAIconButton(Icon.CRAFT_HAMMER,
                Component.translatable("gui.extendedmolecularassembler.matrix.craftingStatus"),
                btn -> PacketDistributor.sendToServer(new EMARequestMatrixCraftingStatusPacket(
                        this.menu.getHost().getBlockPos())));
        button.setX(this.leftPos + this.imageWidth - 22);
        button.setY(this.topPos + 4);
        this.addRenderableWidget(button);
    }
}
