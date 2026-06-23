package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.crafting.pattern.EncodedPatternItem;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.init.EMADataComponents;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ExtendedAssemblerMatrixPatternUploadUtil {
    private ExtendedAssemblerMatrixPatternUploadUtil() {
    }

    public static boolean hasEligibleMatrixUploader(Object menu) {
        var grid = findGrid(menu);
        return grid != null && !findEligiblePatternCoreInventories(grid).isEmpty();
    }

    public static boolean canUploadFromEncodingMenuToMatrix(ServerPlayer player, Object menu, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty() || !isExtendedEncodedPattern(player.level(), stack)) {
            return false;
        }

        var grid = findGrid(menu);
        if (grid == null) {
            return false;
        }

        var targets = findEligiblePatternCoreInventories(grid);
        return !targets.isEmpty() && !matrixContainsPattern(targets, stack) && canFullyInsert(targets, stack);
    }

    public static boolean matrixAlreadyContainsPatternFromEncodingMenu(ServerPlayer player, Object menu, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty() || !isExtendedEncodedPattern(player.level(), stack)) {
            return false;
        }

        var grid = findGrid(menu);
        if (grid == null) {
            return false;
        }

        var targets = findEligiblePatternCoreInventories(grid);
        return !targets.isEmpty() && matrixContainsPattern(targets, stack);
    }

    public static ItemStack uploadFromEncodingMenuToMatrix(ServerPlayer player, Object menu, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            send(player, "message.extendedmolecularassembler.matrix_upload.no_pattern");
            return stack;
        }
        if (!isExtendedEncodedPattern(player.level(), stack)) {
            send(player, "message.extendedmolecularassembler.matrix_upload.invalid_pattern");
            return stack;
        }

        var grid = findGrid(menu);
        if (grid == null) {
            send(player, "message.extendedmolecularassembler.matrix_upload.no_network");
            return stack;
        }

        var targets = findEligiblePatternCoreInventories(grid);
        if (targets.isEmpty()) {
            send(player, "message.extendedmolecularassembler.matrix_upload.no_matrix");
            return stack;
        }

        if (matrixContainsPattern(targets, stack)) {
            send(player, "message.extendedmolecularassembler.matrix_upload.duplicate");
            return stack;
        }

        var remainder = stack.copy();
        for (var inv : targets) {
            if (inv == null) {
                continue;
            }
            remainder = inv.addItems(remainder);
            if (remainder.isEmpty()) {
                send(player, "message.extendedmolecularassembler.matrix_upload.success");
                return ItemStack.EMPTY;
            }
        }

        if (remainder.getCount() < stack.getCount()) {
            send(player, "message.extendedmolecularassembler.matrix_upload.success");
            return remainder;
        }

        send(player, "message.extendedmolecularassembler.matrix_upload.full");
        return stack;
    }

    private static boolean isExtendedEncodedPattern(Level level, ItemStack stack) {
        return level != null
                && !stack.isEmpty()
                && stack.getItem() instanceof EncodedPatternItem<?>
                && PatternDetailsHelper.decodePattern(stack, level) instanceof ExtendedTableCraftingPattern;
    }

    private static IGrid findGrid(Object menu) {
        try {
            if (menu instanceof appeng.menu.AEBaseMenu aeMenu
                    && aeMenu.getTarget() instanceof IActionHost host
                    && host.getActionableNode() != null) {
                return host.getActionableNode().getGrid();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static List<InternalInventory> findEligiblePatternCoreInventories(IGrid grid) {
        var result = new ArrayList<InternalInventory>();
        if (grid == null) {
            return result;
        }

        try {
            Map<ClusterAssemblerMatrix, Boolean> uploaderClusters = new IdentityHashMap<>();
            for (var core : grid.getMachines(ExtendedAssemblerMatrixPatternCoreBlockEntity.class)) {
                if (core == null || !core.isFormed() || !core.getMainNode().isActive()) {
                    continue;
                }

                var cluster = core.getCluster();
                if (cluster == null || !uploaderClusters.computeIfAbsent(cluster,
                        ExtendedAssemblerMatrixPatternUploadUtil::clusterHasUploader)) {
                    continue;
                }

                var inv = core.getExposedInventory();
                if (inv != null) {
                    result.add(inv);
                }
            }
        } catch (Throwable ignored) {
        }
        return result;
    }

    private static boolean clusterHasUploader(ClusterAssemblerMatrix cluster) {
        if (cluster == null) {
            return false;
        }
        try {
            var iterator = cluster.getBlockEntities();
            while (iterator.hasNext()) {
                TileAssemblerMatrixBase blockEntity = iterator.next();
                if (blockEntity instanceof ExtendedAssemblerMatrixPatternUploaderBlockEntity) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static boolean matrixContainsPattern(List<InternalInventory> inventories, ItemStack pattern) {
        for (var inv : inventories) {
            if (inv == null) {
                continue;
            }
            for (int i = 0; i < inv.size(); i++) {
                var stack = inv.getStackInSlot(i);
                if (isSameExtendedPattern(stack, pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean itemHandlerContainsPattern(net.neoforged.neoforge.items.IItemHandler handler,
            ItemStack pattern) {
        if (handler == null || pattern == null || pattern.isEmpty()) {
            return false;
        }
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (isSameExtendedPattern(handler.getStackInSlot(slot), pattern)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameExtendedPattern(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty() || !ItemStack.isSameItem(a, b)) {
            return false;
        }
        var aPattern = a.get(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN);
        var bPattern = b.get(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN);
        if (aPattern != null || bPattern != null) {
            return isSameEncodedPattern(aPattern, bPattern);
        }
        return ItemStack.isSameItemSameComponents(a, b);
    }

    private static boolean isSameEncodedPattern(EncodedExtendedCraftingPattern a,
            EncodedExtendedCraftingPattern b) {
        if (a == null || b == null) {
            return false;
        }
        if (!a.recipeId().equals(b.recipeId())
                || !a.tableType().equals(b.tableType())
                || a.tableTier() != b.tableTier()
                || a.tableSideLength() != b.tableSideLength()
                || a.canSubstitute() != b.canSubstitute()
                || a.canSubstituteFluids() != b.canSubstituteFluids()
                || !isSameStack(a.result(), b.result())
                || a.inputs().size() != b.inputs().size()) {
            return false;
        }

        for (int i = 0; i < a.inputs().size(); i++) {
            if (!isSameStack(a.inputs().get(i), b.inputs().get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSameStack(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return a.isEmpty() && b.isEmpty();
        }
        return a.getCount() == b.getCount() && ItemStack.isSameItemSameComponents(a, b);
    }

    private static boolean canFullyInsert(List<InternalInventory> inventories, ItemStack stack) {
        var remainder = stack.copy();
        for (var inv : inventories) {
            if (inv == null) {
                continue;
            }
            remainder = inv.simulateAdd(remainder);
            if (remainder.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static void send(ServerPlayer player, String key) {
        if (player != null) {
            player.sendSystemMessage(Component.translatable(key));
        }
    }
}
