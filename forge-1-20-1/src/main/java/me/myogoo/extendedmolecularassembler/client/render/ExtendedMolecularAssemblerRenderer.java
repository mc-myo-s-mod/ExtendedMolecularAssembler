package me.myogoo.extendedmolecularassembler.client.render;

import appeng.client.render.crafting.AssemblerAnimationStatus;
import appeng.client.render.effects.ParticleTypes;
import appeng.core.AppEng;
import appeng.core.AppEngClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class ExtendedMolecularAssemblerRenderer
        implements BlockEntityRenderer<ExtendedMolecularAssemblerBlockEntity> {
    public static final ModelResourceLocation LIGHTS_MODEL =
            new ModelResourceLocation(AppEng.makeId("block/molecular_assembler_lights"), "standalone");

    private final RandomSource particleRandom = RandomSource.create();

    public ExtendedMolecularAssemblerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ExtendedMolecularAssemblerBlockEntity assembler, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var status = assembler.getAnimationStatus();
        if (status != null) {
            if (!Minecraft.getInstance().isPaused()) {
                if (status.isExpired()) {
                    assembler.setAnimationStatus(null);
                }

                status.setAccumulatedTicks(status.getAccumulatedTicks() + partialTicks);
                status.setTicksUntilParticles(status.getTicksUntilParticles() - partialTicks);
            }

            renderStatus(assembler, poseStack, buffer, packedLight, status);
        }

        if (assembler.isPowered()) {
            renderPowerLight(poseStack, buffer, packedLight, packedOverlay);
        }
    }

    private void renderPowerLight(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        BakedModel lightsModel = minecraft.getModelManager().getModel(LIGHTS_MODEL);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.tripwire());

        minecraft.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertexConsumer, null,
                lightsModel, 1, 1, 1, packedLight, packedOverlay, ModelData.EMPTY, null);
    }

    private void renderStatus(ExtendedMolecularAssemblerBlockEntity assembler, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, AssemblerAnimationStatus status) {
        double centerX = assembler.getBlockPos().getX() + 0.5;
        double centerY = assembler.getBlockPos().getY() + 0.5;
        double centerZ = assembler.getBlockPos().getZ() + 0.5;

        var minecraft = Minecraft.getInstance();
        if (status.getTicksUntilParticles() <= 0) {
            status.setTicksUntilParticles(4);

            if (AppEngClient.instance().shouldAddParticles(particleRandom)) {
                for (int i = 0; i < (int) Math.ceil(status.getSpeed() / 5.0); i++) {
                    minecraft.particleEngine.createParticle(ParticleTypes.CRAFTING,
                            centerX, centerY, centerZ, 0, 0, 0);
                }
            }
        }

        ItemStack stack = status.getIs();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(0, stack.getItem() instanceof BlockItem ? -0.2f : -0.3f, 0);
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, assembler.getLevel(), 0);
        poseStack.popPose();
    }
}
