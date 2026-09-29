package me.myogoo.extendedmolecularassembler.client.render;

import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.particles.ParticleTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Objects;
public class ExtendedMolecularAssemblerRenderer
        implements BlockEntityRenderer<ExtendedMolecularAssemblerBlockEntity,
                ExtendedMolecularAssemblerRenderer.RenderState> {
    private static final Identifier LIGHTS_MODEL_ID = AppEng.makeId("block/molecular_assembler_lights");
    private static final StandaloneModelKey<BlockStateModel> LIGHTS_MODEL =
            new StandaloneModelKey<>(LIGHTS_MODEL_ID::toString);

    private final ItemModelResolver itemModelResolver;
    private final BlockStateModel lightsModel;

    public ExtendedMolecularAssemblerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.lightsModel = Objects.requireNonNull(
                Minecraft.getInstance().getModelManager().getStandaloneModel(LIGHTS_MODEL));
    }

    public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        event.register(LIGHTS_MODEL, SimpleUnbakedStandaloneModel.blockStateModel(LIGHTS_MODEL_ID));
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ExtendedMolecularAssemblerBlockEntity assembler, RenderState state,
            float partialTicks, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(assembler, state, partialTicks, cameraPos, crumblingOverlay);

        state.item.clear();
        state.lights.clear();

        var status = assembler.getAnimationStatus();
        if (status != null) {
            if (!Minecraft.getInstance().isPaused()) {
                if (status.isExpired()) {
                    assembler.setAnimationStatus(null);
                }

                status.setAccumulatedTicks(status.getAccumulatedTicks() + partialTicks);
                status.setTicksUntilParticles(status.getTicksUntilParticles() - partialTicks);
            }

            if (AEConfig.instance().isEnableEffects() && status.getTicksUntilParticles() <= 0) {
                status.setTicksUntilParticles(4);
                var level = assembler.getLevel();
                if (level != null) {
                    var centerX = assembler.getBlockPos().getX() + 0.5;
                    var centerY = assembler.getBlockPos().getY() + 0.5;
                    var centerZ = assembler.getBlockPos().getZ() + 0.5;
                    var stack = status.getIs();
                    for (int i = 0; i < (int) Math.ceil(status.getSpeed() / 5.0); i++) {
                        level.addParticle(
                                new ItemParticleOption(
                                        ParticleTypes.CRAFTING,
                                        ItemStackTemplate.fromNonEmptyStack(stack)),
                                centerX, centerY, centerZ, 0, 0, 0);
                    }
                }
            }

            var stack = status.getIs();
            this.itemModelResolver.updateForTopItem(
                    state.item,
                    stack,
                    ItemDisplayContext.FIXED,
                    assembler.getLevel(),
                    null,
                    (int) assembler.getBlockPos().asLong());
            state.blockItem = stack.getItem() instanceof BlockItem;
        }

        if (assembler.isPowered() && assembler.getLevel() != null) {
            var level = (BlockAndTintGetter) assembler.getLevel();
            var parts = state.lights.setupModel(
                    new Matrix4f(),
                    this.lightsModel.hasMaterialFlag(
                            level,
                            assembler.getBlockPos(),
                            assembler.getBlockState(),
                            BakedQuad.FLAG_TRANSLUCENT));
            this.lightsModel.collectParts(
                    level,
                    assembler.getBlockPos(),
                    assembler.getBlockState(),
                    state.lights.scratchRandomSource(42L),
                    parts);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector nodes,
            CameraRenderState cameraRenderState) {
        if (!state.lights.isEmpty()) {
            state.lights.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }

        if (state.item.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(0, state.blockItem ? -0.2f : -0.3f, 0);
        state.item.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        private final net.minecraft.client.renderer.item.ItemStackRenderState item =
                new net.minecraft.client.renderer.item.ItemStackRenderState();
        private final BlockModelRenderState lights = new BlockModelRenderState();
        private boolean blockItem;
    }
}
