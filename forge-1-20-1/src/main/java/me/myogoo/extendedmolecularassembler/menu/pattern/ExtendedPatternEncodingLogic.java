package me.myogoo.extendedmolecularassembler.menu.pattern;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.AEItemDefinitionFilter;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ExtendedPatternEncodingLogic implements InternalInventoryHost {
    private final IExtendedPatternEncodingTerminalHost host;
    private final ConfigInventory encodedInputInv;
    private final AppEngInternalInventory blankPatternInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory encodedPatternInv = new AppEngInternalInventory(this, 1);

    private boolean substitute;
    private boolean substituteFluids = true;
    private boolean loading;
    private long encodedInputRevision;

    public ExtendedPatternEncodingLogic(IExtendedPatternEncodingTerminalHost host) {
        this.host = host;
        this.encodedInputInv = ConfigInventory.configStacks(null,
                host.getGridSide() * host.getGridSide(), this::onEncodedInputChanged, true);
        this.blankPatternInv.setFilter(new AEItemDefinitionFilter(AEItems.BLANK_PATTERN));
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        if (inv == this.encodedPatternInv) {
            loadEncodedPattern(encodedPatternInv.getStackInSlot(0));
        }
        saveChanges();
    }

    @Override
    public boolean isClientSide() {
        return host.getLevel().isClientSide();
    }

    public ConfigInventory getEncodedInputInv() {
        return encodedInputInv;
    }

    public long getEncodedInputRevision() {
        return encodedInputRevision;
    }

    public InternalInventory getBlankPatternInv() {
        return blankPatternInv;
    }

    public InternalInventory getEncodedPatternInv() {
        return encodedPatternInv;
    }

    public boolean isFluidSubstitution() {
        return substituteFluids;
    }

    public boolean isSubstitution() {
        return substitute;
    }

    public void setSubstitution(boolean substitute) {
        this.substitute = substitute;
        saveChanges();
    }

    public void setFluidSubstitution(boolean substituteFluids) {
        this.substituteFluids = substituteFluids;
        saveChanges();
    }

    public void clearEncodedInputs() {
        encodedInputInv.clear();
        saveChanges();
    }

    public void clearAll() {
        encodedInputInv.clear();
        blankPatternInv.clear();
        encodedPatternInv.clear();
        saveChanges();
    }

    public void readFromNBT(CompoundTag data) {
        loading = true;
        try {
            this.substitute = data.getBoolean("substitute");
            this.substituteFluids = !data.contains("substituteFluids") || data.getBoolean("substituteFluids");
            blankPatternInv.readFromNBT(data, "blankPattern");
            encodedPatternInv.readFromNBT(data, "encodedPattern");
            encodedInputInv.readFromChildTag(data, "encodedInputs");
        } finally {
            loading = false;
        }
    }

    public void writeToNBT(CompoundTag data) {
        data.putBoolean("substitute", substitute);
        data.putBoolean("substituteFluids", substituteFluids);
        blankPatternInv.writeToNBT(data, "blankPattern");
        encodedPatternInv.writeToNBT(data, "encodedPattern");
        encodedInputInv.writeToChildTag(data, "encodedInputs");
    }

    private void onEncodedInputChanged() {
        fixCraftingContainers();
        encodedInputRevision++;
        saveChanges();
    }

    @Override
    public void saveChanges() {
        if (!loading) {
            host.markForSave();
        }
    }

    private void loadEncodedPattern(ItemStack pattern) {
        if (pattern.isEmpty() || host.getLevel() == null) {
            return;
        }

        var details = PatternDetailsHelper.decodePattern(pattern, host.getLevel());
        if (!(details instanceof ExtendedTableCraftingPattern tablePattern)
                || tablePattern.sideLength() > host.getGridSide()) {
            return;
        }

        loading = true;
        try {
            this.substitute = tablePattern.canSubstitute();
            this.substituteFluids = tablePattern.canSubstituteFluids();
            encodedInputInv.clear();

            var side = tablePattern.sideLength();
            var offset = Math.floorDiv(host.getGridSide() - side, 2);
            var sparseInputs = tablePattern.getSparseInputs();
            for (int patternSlot = 0; patternSlot < sparseInputs.size(); patternSlot++) {
                var input = sparseInputs.get(patternSlot);
                if (input == null) {
                    continue;
                }

                var x = patternSlot % side + offset;
                var y = patternSlot / side + offset;
                encodedInputInv.setStack(x + y * host.getGridSide(), input);
            }
        } finally {
            loading = false;
        }
    }

    private void fixCraftingContainers() {
        if (host.getLevel() == null || host.getLevel().isClientSide()) {
            return;
        }

        for (int slot = 0; slot < encodedInputInv.size(); slot++) {
            var stack = encodedInputInv.getStack(slot);
            if (stack == null) {
                continue;
            }

            if (!AEItemKey.is(stack.what())) {
                encodedInputInv.setStack(slot, null);
                continue;
            }

            if (stack.amount() != 1) {
                encodedInputInv.setStack(slot, new GenericStack(stack.what(), 1));
            }
        }
    }
}
