package me.myogoo.extendedmolecularassembler.integration.extendedae.menu;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixBridge;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAMatrixPatternCoreUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExtendedAssemblerMatrixPatternCoreMenu extends AEBaseMenu {
    public static final MenuType<ExtendedAssemblerMatrixPatternCoreMenu> TYPE = MenuTypeBuilder
            .create(ExtendedAssemblerMatrixPatternCoreMenu::new, ExtendedAssemblerMatrixPatternCoreBlockEntity.class)
            .buildUnregistered(ExtendedMolecularAssembler.makeId("extended_assembler_matrix_pattern_core"));

    private static final String ACTION_CANCEL = "cancel";
    private static final String ACTION_SET_PATTERN_ACCESS_VISIBLE = "setPatternAccessVisible";
    private static final int RUNNING_THREAD_SYNC_INTERVAL = 5;

    private final ExtendedAssemblerMatrixPatternCoreBlockEntity host;
    private final List<PatternSlotTracker> trackers = new ArrayList<>();
    private final Map<Long, PatternSlotTracker> trackerMap = new HashMap<>();
    private final Map<Long, List<ItemStack>> clientPatternStacks = new LinkedHashMap<>();
    private int runningThreadSyncDelay = 0;

    @GuiSync(8)
    public int runningThreads = 0;
    @GuiSync(9)
    public boolean patternAccessVisible = true;

    public ExtendedAssemblerMatrixPatternCoreMenu(int id, Inventory playerInventory,
            ExtendedAssemblerMatrixPatternCoreBlockEntity host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        registerClientAction(ACTION_CANCEL, this::cancelJobs);
        registerClientAction(ACTION_SET_PATTERN_ACCESS_VISIBLE, Boolean.class, this::setPatternAccessVisible);

        setupPatternTrackers();
        this.createPlayerInventorySlots(playerInventory);
    }

    public ExtendedAssemblerMatrixPatternCoreBlockEntity getHost() {
        return this.host;
    }

    public List<PatternEntry> getPatternEntries() {
        var entries = new ArrayList<PatternEntry>();
        this.clientPatternStacks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(coreEntry -> {
                    var stacks = coreEntry.getValue();
                    for (int i = 0; i < stacks.size(); i++) {
                        entries.add(new PatternEntry(coreEntry.getKey(), i, stacks.get(i)));
                    }
                });
        return entries;
    }

    public void applyPatternCoreUpdate(long coreId, int slotCount, boolean full, Map<Integer, ItemStack> changes) {
        var stacks = this.clientPatternStacks.computeIfAbsent(coreId, ignored -> emptyStackList(slotCount));
        if (full || stacks.size() != slotCount) {
            stacks.clear();
            stacks.addAll(emptyStackList(slotCount));
        }
        for (var entry : changes.entrySet()) {
            var slot = entry.getKey();
            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, entry.getValue().copy());
            }
        }
    }

    private static List<ItemStack> emptyStackList(int size) {
        var list = new ArrayList<ItemStack>(size);
        for (int i = 0; i < size; i++) {
            list.add(ItemStack.EMPTY);
        }
        return list;
    }

    public void cancelJobsFromClient() {
        if (this.isClientSide()) {
            sendClientAction(ACTION_CANCEL);
        }
    }

    public void setPatternAccessVisibleFromClient(boolean visible) {
        this.patternAccessVisible = visible;
        if (this.isClientSide()) {
            sendClientAction(ACTION_SET_PATTERN_ACCESS_VISIBLE, visible);
        } else {
            this.setPatternAccessVisible(visible);
        }
    }

    @Override
    protected int transferStackToMenu(ItemStack input) {
        var target = getAvailablePatternSlot();
        if (target == null) {
            return 0;
        }
        var remaining = target.addItems(input.copy());
        return input.getCount() - remaining.getCount();
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        var tracker = this.trackerMap.get(id);
        if (tracker == null || slot < 0 || slot >= tracker.server.size()) {
            return;
        }

        var slotInv = filteredSlot(tracker.server.getSlotInv(slot));
        var carried = getCarried();
        var current = slotInv.getStackInSlot(0);

        switch (action) {
            case PICKUP_OR_SET_DOWN -> pickupOrSetDown(slotInv, carried, current);
            case SPLIT_OR_PLACE_SINGLE, PLACE_SINGLE -> splitOrPlaceSingle(slotInv, carried, current);
            case PICKUP_SINGLE -> pickupSingle(slotInv, carried, current);
            case SHIFT_CLICK -> shiftClickToPlayer(player, slotInv, current);
            case CREATIVE_DUPLICATE -> creativeDuplicate(player, current);
            default -> {
            }
        }
    }

    private void pickupOrSetDown(FilteredInternalInventory slotInv, ItemStack carried, ItemStack current) {
        if (carried.isEmpty()) {
            setCarried(current.copy());
            slotInv.setItemDirect(0, ItemStack.EMPTY);
            return;
        }

        if (current.isEmpty()) {
            setCarried(slotInv.addItems(carried.copy()));
            return;
        }

        var old = current.copy();
        var incoming = carried.copy();
        slotInv.setItemDirect(0, ItemStack.EMPTY);
        var remainder = slotInv.addItems(incoming);
        if (remainder.isEmpty()) {
            setCarried(old);
        } else {
            slotInv.setItemDirect(0, old);
            setCarried(carried);
        }
    }

    private void splitOrPlaceSingle(FilteredInternalInventory slotInv, ItemStack carried, ItemStack current) {
        if (!carried.isEmpty()) {
            var one = carried.copy();
            one.setCount(1);
            var remainder = slotInv.addItems(one);
            if (remainder.isEmpty()) {
                var newCarried = carried.copy();
                newCarried.shrink(1);
                setCarried(newCarried);
            }
            return;
        }

        if (!current.isEmpty()) {
            var amount = (current.getCount() + 1) / 2;
            setCarried(slotInv.extractItem(0, amount, false));
        }
    }

    private void pickupSingle(FilteredInternalInventory slotInv, ItemStack carried, ItemStack current) {
        if (!carried.isEmpty() || current.isEmpty()) {
            return;
        }
        setCarried(slotInv.extractItem(0, 1, false));
    }

    private void shiftClickToPlayer(ServerPlayer player, FilteredInternalInventory slotInv, ItemStack current) {
        if (current.isEmpty()) {
            return;
        }
        var moving = current.copy();
        var originalCount = moving.getCount();
        if (player.getInventory().add(moving)) {
            slotInv.setItemDirect(0, ItemStack.EMPTY);
        } else if (moving.getCount() != originalCount) {
            slotInv.setItemDirect(0, moving.copy());
        }
    }

    private void creativeDuplicate(ServerPlayer player, ItemStack current) {
        if (!player.getAbilities().instabuild || current.isEmpty()) {
            return;
        }
        setCarried(current.copy());
    }

    private FilteredInternalInventory filteredSlot(InternalInventory inventory) {
        return new FilteredInternalInventory(inventory,
                new ExtendedAssemblerMatrixPatternCoreBlockEntity.ExtendedPatternFilter(this.host::getLevel));
    }

    private InternalInventory getAvailablePatternSlot() {
        for (var patternCore : getClusterPatternCores()) {
            var patternInventory = patternCore.getPatternInventory();
            for (int i = 0; i < patternInventory.size(); i++) {
                if (patternInventory.getStackInSlot(i).isEmpty()) {
                    return filteredSlot(patternInventory.getSlotInv(i));
                }
            }
        }
        return null;
    }

    private void setupPatternTrackers() {
        if (this.isClientSide()) {
            return;
        }
        this.trackers.clear();
        this.trackerMap.clear();
        for (var patternCore : getClusterPatternCores()) {
            var tracker = new PatternSlotTracker(patternCore);
            this.trackers.add(tracker);
            this.trackerMap.put(patternCore.getLocateID(), tracker);
        }
    }

    private List<ExtendedAssemblerMatrixPatternCoreBlockEntity> getClusterPatternCores() {
        var cluster = this.host.getCluster();
        if (cluster == null || cluster.isDestroyed()) {
            return List.of(this.host);
        }

        var patternCores = new ArrayList<ExtendedAssemblerMatrixPatternCoreBlockEntity>();
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity patternCore) {
                patternCores.add(patternCore);
            }
        }

        if (patternCores.isEmpty()) {
            patternCores.add(this.host);
        }
        patternCores.sort(Comparator.comparingLong(ExtendedAssemblerMatrixPatternCoreBlockEntity::getLocateID));
        return patternCores;
    }

    @Override
    public void broadcastChanges() {
        if (!this.isClientSide()) {
            if (this.runningThreadSyncDelay-- <= 0) {
                this.runningThreadSyncDelay = RUNNING_THREAD_SYNC_INTERVAL;
                this.runningThreads = this.getRunningThreads();
            }
            this.patternAccessVisible =
                    this.host.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;
            sendPatternUpdates();
        }
        super.broadcastChanges();
    }

    private void sendPatternUpdates() {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        for (var tracker : this.trackers) {
            var packet = tracker.createPacket();
            if (packet != null) {
                PacketDistributor.sendToPlayer(serverPlayer, packet);
            }
        }
    }

    private int getRunningThreads() {
        var cluster = this.host.getCluster();
        if (cluster == null || cluster.isDestroyed()) {
            return 0;
        }
        return ExtendedAEAssemblerMatrixBridge.getUsedExtendedCraftingSlots(cluster);
    }

    private void cancelJobs() {
        var cluster = this.host.getCluster();
        if (cluster != null && !cluster.isDestroyed()) {
            ExtendedAEAssemblerMatrixBridge.cancelExtendedAssemblerJobs(cluster);
        }
        this.runningThreads = this.getRunningThreads();
        this.runningThreadSyncDelay = 0;
    }

    private void setPatternAccessVisible(Boolean visible) {
        var value = visible ? YesNo.YES : YesNo.NO;
        this.host.getConfigManager().putSetting(Settings.PATTERN_ACCESS_TERMINAL, value);
        this.patternAccessVisible = visible;
        this.host.saveChanges();
        this.host.updatePatterns();
    }

    public record PatternEntry(long coreId, int slot, ItemStack stack) {
    }

    private static class PatternSlotTracker {
        private final ExtendedAssemblerMatrixPatternCoreBlockEntity host;
        private final InternalInventory server;
        private final InternalInventory client;
        private boolean initialized;

        private PatternSlotTracker(ExtendedAssemblerMatrixPatternCoreBlockEntity host) {
            this.host = host;
            this.server = host.getPatternInventory();
            this.client = new AppEngInternalInventory(this.server.size());
        }

        private EMAMatrixPatternCoreUpdatePacket createPacket() {
            var changes = new HashMap<Integer, ItemStack>();
            for (int i = 0; i < this.server.size(); i++) {
                var serverStack = this.server.getStackInSlot(i);
                var clientStack = this.client.getStackInSlot(i);
                if (!this.initialized || !ItemStack.isSameItemSameComponents(serverStack, clientStack)
                        || serverStack.getCount() != clientStack.getCount()) {
                    changes.put(i, serverStack.copy());
                    this.client.setItemDirect(i, serverStack.copy());
                }
            }
            if (this.initialized && changes.isEmpty()) {
                return null;
            }
            var full = !this.initialized;
            this.initialized = true;
            return new EMAMatrixPatternCoreUpdatePacket(this.host.getLocateID(), this.server.size(), full, changes);
        }
    }
}
