package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ExtendedAssemblerMatrixPatternCoreBlockEntity extends TileAssemblerMatrixFunction
        implements InternalInventoryHost {
    public static final int INV_SIZE = 36;

    private final AppEngInternalInventory patternInventory = new AppEngInternalInventory(this, INV_SIZE, 1);
    private final LazyOptional<IItemHandler> patternHandler = LazyOptional.of(() -> this.patternInventory.toItemHandler());

    public ExtendedAssemblerMatrixPatternCoreBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState blockState) {
        super(type, pos, blockState);
        this.patternInventory.setFilter(new ExtendedPatternFilter(this::getLevel));
    }

    public AppEngInternalInventory getPatternInventory() {
        return this.patternInventory;
    }

    @Nullable
    public IItemHandler getPatternInv(Direction ignored) {
        return this.patternInventory.toItemHandler();
    }

    @Override
    public void add(ClusterAssemblerMatrix cluster) {
        // Function block only. The Forge 1.20.1 port currently stores matrix extended patterns.
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.patternInventory.writeToNBT(data, "pattern");
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.patternInventory.readFromNBT(data, "pattern");
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, java.util.List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var pattern : this.patternInventory) {
            if (!pattern.isEmpty()) {
                drops.add(pattern);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.patternInventory.clear();
    }

    @Override
    public void saveChanges() {
        super.saveChanges();
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.saveChanges();
    }

    @Override
    public boolean isClientSide() {
        return this.level != null && this.level.isClientSide();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, this.patternHandler);
        }
        return super.getCapability(capability, facing);
    }

    public record ExtendedPatternFilter(Supplier<Level> world) implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return stack.getItem() instanceof EncodedPatternItem
                    && PatternDetailsHelper.decodePattern(stack, world.get()) instanceof ExtendedTableCraftingPattern;
        }
    }
}
