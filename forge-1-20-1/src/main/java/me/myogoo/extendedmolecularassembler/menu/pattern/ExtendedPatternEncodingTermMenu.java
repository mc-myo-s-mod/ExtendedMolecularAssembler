package me.myogoo.extendedmolecularassembler.menu.pattern;

import appeng.api.inventories.InternalInventory;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantic;
import appeng.menu.guisync.GuiSync;
import me.myogoo.extendedmolecularassembler.menu.ForgeMenuTypes;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.slot.FakeSlot;
import appeng.menu.slot.DisabledSlot;
import appeng.menu.slot.RestrictedInputSlot;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo.AvaritiaNeoRecipeAdapters;
import me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia.ReAvaritiaRecipeAdapters;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo;
import me.myogoo.extendedmolecularassembler.api.annotation.ExPatternProvider;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.menu.EMASlotSemantics;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.world.SimpleContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ExtendedPatternEncodingTermMenu extends MEStorageMenu {
    private static final String ACTION_ENCODE = "encode";
    private static final String ACTION_CLEAR = "clear";
    private static final String ACTION_SET_SUBSTITUTION = "setSubstitution";
    private static final String ACTION_SET_FLUID_SUBSTITUTION = "setFluidSubstitution";
    private static final String ACTION_CYCLE_RECIPE = "cycleRecipe";
    private static final String ACTION_CYCLE_RECIPE_TABLE = "cycleRecipeTable";
    private static final String ACTION_SELECT_RECIPE = "selectRecipe";
    private static final String ACTION_REMEMBER_RECIPE_TYPE = "rememberRecipeType";
    private static final String ACTION_UPLOAD_TO_MATRIX = "uploadToMatrix";
    private static final String ENCODE_PLAYER_TAG = "encodePlayer";
    private static final int MATRIX_UPLOADER_SYNC_INTERVAL = 20;

    public static final MenuType<ExtendedPatternEncodingTermMenu> TYPE = ForgeMenuTypes.create(
            ExtendedMolecularAssembler.makeId("extended_pattern_encoding_terminal"),
            IExtendedPatternEncodingTerminalHost.class, ExtendedPatternEncodingTermMenu::new);

    private final ExtendedPatternEncodingLogic encodingLogic;
    private final FakeSlot[] craftingGridSlots;
    private final DisabledSlot craftOutputSlot;
    private final RestrictedInputSlot blankPatternSlot;
    private final RestrictedInputSlot encodedPatternSlot;
    private final IExtendedPatternEncodingTerminalHost host;
    private final EnumSet<RecipeProvider> activeRecipeProviders;
    private final List<RecipeDisplay> supportedRecipeDisplays;

    @Nullable
    private ExtendedPatternRecipeMatch currentMatch;
    private List<ExtendedPatternRecipeMatch> currentMatches = List.of();
    private List<Integer> currentRecipeDisplayIndexes = List.of();
    private int currentRecipeIndex;
    @Nullable
    private ResourceLocation selectedRecipeId;
    private long matchedEncodedInputRevision = Long.MIN_VALUE;

    @GuiSync(97)
    public boolean substitute;
    @GuiSync(96)
    public boolean substituteFluids;
    @GuiSync(95)
    public int recipeMatchCount;
    @GuiSync(94)
    public int selectedRecipeProvider;
    @GuiSync(93)
    public int selectedRecipeTableSide;
    @GuiSync(92)
    public boolean matrixUploaderAvailable;
    @GuiSync(91)
    public int selectedRecipeTableTier;
    private int matrixUploaderSyncDelay;

    public ExtendedPatternEncodingTermMenu(int id, Inventory playerInventory,
            IExtendedPatternEncodingTerminalHost host) {
        this(TYPE, id, playerInventory, host);
    }

    public ExtendedPatternEncodingTermMenu(MenuType<?> menuType, int id, Inventory playerInventory,
            IExtendedPatternEncodingTerminalHost host) {
        super(menuType, id, playerInventory, host);
        this.host = host;
        this.encodingLogic = host.getExtendedPatternEncodingLogic();
        this.craftingGridSlots = new FakeSlot[host.getGridSide() * host.getGridSide()];
        var activeRecipeProviders = EnumSet.noneOf(RecipeProvider.class);
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            activeRecipeProviders.add(RecipeProvider.EXTENDED_CRAFTING);
        }
        if (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)) {
            activeRecipeProviders.add(RecipeProvider.AVARITIA_NEO);
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            activeRecipeProviders.add(RecipeProvider.RE_AVARITIA);
        }
        this.activeRecipeProviders = activeRecipeProviders;
        this.supportedRecipeDisplays = createSupportedRecipeDisplays(this.activeRecipeProviders, host.getGridSide());

        var encodedInputs = encodingLogic.getEncodedInputInv().createMenuWrapper();
        for (int i = 0; i < craftingGridSlots.length; i++) {
            var slot = new FakeSlot(encodedInputs, i);
            slot.setHideAmount(true);
            addSlot(craftingGridSlots[i] = slot, EMASlotSemantics.EXTENDED_PATTERN_CRAFTING_GRID);
        }

        addSlot(this.craftOutputSlot = new DisabledSlot(new SimpleContainer(1), 0),
                EMASlotSemantics.EXTENDED_PATTERN_CRAFTING_RESULT);

        addSlot(this.blankPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN,
                encodingLogic.getBlankPatternInv(), 0), appeng.menu.SlotSemantics.BLANK_PATTERN);
        addSlot(this.encodedPatternSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
                encodingLogic.getEncodedPatternInv(), 0), appeng.menu.SlotSemantics.ENCODED_PATTERN);
        this.encodedPatternSlot.setStackLimit(1);

        EMAOptionalIntegrations.addAE2WTLibSingularitySlot(this, host);

        registerClientAction(ACTION_ENCODE, Boolean.class, this::encode);
        registerClientAction(ACTION_CLEAR, this::clear);
        registerClientAction(ACTION_SET_SUBSTITUTION, Boolean.class, encodingLogic::setSubstitution);
        registerClientAction(ACTION_SET_FLUID_SUBSTITUTION, Boolean.class, encodingLogic::setFluidSubstitution);
        registerClientAction(ACTION_CYCLE_RECIPE, this::cycleRecipe);
        registerClientAction(ACTION_CYCLE_RECIPE_TABLE, Boolean.class, this::cycleRecipeTable);
        registerClientAction(ACTION_SELECT_RECIPE, String.class, this::selectRecipeById);
        registerClientAction(ACTION_REMEMBER_RECIPE_TYPE, Boolean.class, this::setRememberRecipeType);
        registerClientAction(ACTION_UPLOAD_TO_MATRIX, this::uploadToMatrix);

        this.substitute = encodingLogic.isSubstitution();
        this.substituteFluids = encodingLogic.isFluidSubstitution();
        if (!loadRememberedRecipeType()) {
            setSelectedRecipeDisplay(null);
        }
        getAndUpdateOutput();
    }

    @Override
    public void setItem(int slotId, int stateId, ItemStack stack) {
        super.setItem(slotId, stateId, stack);
        getAndUpdateOutput();
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, ItemStack carried) {
        super.initializeContents(stateId, items, carried);
        getAndUpdateOutput();
    }

    @Override
    public void onSlotChange(Slot slot) {
        if (slot == this.encodedPatternSlot && isServerSide()) {
            broadcastChanges();
        }
        getAndUpdateOutput();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (isServerSide()) {
            this.substitute = encodingLogic.isSubstitution();
            this.substituteFluids = encodingLogic.isFluidSubstitution();
            if (this.matrixUploaderSyncDelay-- <= 0) {
                this.matrixUploaderSyncDelay = MATRIX_UPLOADER_SYNC_INTERVAL;
                this.matrixUploaderAvailable = EMAOptionalIntegrations.hasEligibleExtendedAEPatternUploader(this);
            }
        }
    }

    @Override
    protected ItemStack transferStackToMenu(ItemStack input) {
        if (blankPatternSlot.mayPlace(input)) {
            input = blankPatternSlot.safeInsert(input);
            if (input.isEmpty()) {
                return input;
            }
        }

        if (encodedPatternSlot.mayPlace(input)) {
            input = encodedPatternSlot.safeInsert(input);
            if (input.isEmpty()) {
                return input;
            }
        }

        return super.transferStackToMenu(input);
    }

    public void encode() {
        encode(false);
    }

    public void encode(boolean skipAutoUpload) {
        if (isClientSide()) {
            sendClientAction(ACTION_ENCODE, skipAutoUpload);
            return;
        }

        var encodedPattern = encodePattern();
        if (encodedPattern == null) {
            clearPattern();
            return;
        }

        var encodeOutput = encodedPatternSlot.getItem();
        if (!encodeOutput.isEmpty()
                && !PatternDetailsHelper.isEncodedPattern(encodeOutput)
                && !AEItems.BLANK_PATTERN.isSameAs(encodeOutput)) {
            return;
        } else if (encodeOutput.isEmpty()) {
            var blankPattern = blankPatternSlot.getItem();
            if (!AEItems.BLANK_PATTERN.isSameAs(blankPattern)) {
                return;
            }

            blankPattern.shrink(1);
            if (blankPattern.isEmpty()) {
                blankPatternSlot.set(ItemStack.EMPTY);
            }
        }

        encodedPatternSlot.set(encodedPattern);
        if (!skipAutoUpload) {
            autoUploadToMatrixIfAvailable();
        }
    }

    private void autoUploadToMatrixIfAvailable() {
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)
                || !EMAOptionalIntegrations.isExtendedAEPlusStyleContentEnabled()) {
            return;
        }
        var player = getPlayerInventory().player;
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        var encodedPattern = encodedPatternSlot.getItem();
        if (EMAOptionalIntegrations.canUploadToExtendedAEAssemblerMatrix(serverPlayer, this, encodedPattern)) {
            uploadToMatrix();
        } else if (EMAOptionalIntegrations.extendedAEAssemblerMatrixContainsPattern(
                serverPlayer,
                this,
                encodedPattern)) {
            serverPlayer.sendSystemMessage(Component.translatable(EMATranslationKey.MESSAGE.MATRIX_UPLOAD_DUPLICATE.key()));
        }
    }

    public void clear() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR);
            return;
        }

        encodingLogic.clearEncodedInputs();
        currentMatch = null;
        currentMatches = List.of();
        currentRecipeDisplayIndexes = List.of();
        currentRecipeIndex = 0;
        selectedRecipeId = null;
        recipeMatchCount = 0;
        setSelectedRecipeDisplay(null);
        craftOutputSlot.set(ItemStack.EMPTY);
        broadcastChanges();
    }

    public void uploadToMatrix() {
        if (isClientSide()) {
            sendClientAction(ACTION_UPLOAD_TO_MATRIX);
            return;
        }

        var player = getPlayerInventory().player;
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            serverPlayer.sendSystemMessage(Component.translatable(EMATranslationKey.MESSAGE.MATRIX_UPLOAD_NO_EXTENDEDAE.key()));
            return;
        }
        if (!EMAOptionalIntegrations.isExtendedAEPlusStyleContentEnabled()) {
            serverPlayer.sendSystemMessage(Component.translatable(EMATranslationKey.MESSAGE.MATRIX_UPLOAD_NO_EXTENDEDAE_PLUS.key()));
            return;
        }

        var stack = encodedPatternSlot.getItem();
        var remainder = EMAOptionalIntegrations.uploadToExtendedAEAssemblerMatrix(serverPlayer, this, stack);
        if (remainder != stack) {
            encodedPatternSlot.set(remainder);
            broadcastChanges();
        }
    }

    public void cycleRecipe() {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_RECIPE);
            return;
        }

        invalidateRecipeMatches();
        getAndUpdateOutput();
        if (currentMatches.size() <= 1) {
            return;
        }

        currentRecipeIndex = nextRecipeIndex(currentRecipeIndex);
        currentMatch = currentMatches.get(currentRecipeIndex);
        selectedRecipeId = currentMatch.recipe().getId();
        setSelectedRecipeDisplay(currentMatch);
        recipeMatchCount = currentMatches.size();
        craftOutputSlot.set(currentMatch.result());
        broadcastChanges();
    }

    public boolean canCycleRecipes() {
        return supportedRecipeDisplays.size() > 1;
    }

    public void cycleRecipeTable() {
        cycleRecipeTable(false);
    }

    public void cycleRecipeTableBackwards() {
        cycleRecipeTable(true);
    }

    private void cycleRecipeTable(boolean backwards) {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_RECIPE_TABLE, backwards);
            return;
        }

        var displays = supportedRecipeDisplays;
        if (displays.size() <= 1) {
            return;
        }

        var current = new RecipeDisplay(getSelectedRecipeProvider(), getSelectedRecipeTableTier(), getSelectedRecipeTableSide());
        var currentPosition = displays.indexOf(current);
        if (currentPosition < 0) {
            currentPosition = 0;
        }

        var direction = backwards ? -1 : 1;
        var next = displays.get(Math.floorMod(currentPosition + direction, displays.size()));
        selectRecipeDisplay(next);
    }

    private void selectRecipeDisplay(RecipeDisplay display) {
        this.selectedRecipeProvider = display.provider().ordinal();
        this.selectedRecipeTableTier = display.tableTier();
        this.selectedRecipeTableSide = display.tableSide();
        this.selectedRecipeId = null;
        saveRememberedRecipeType(display);
        invalidateRecipeMatches();
        getAndUpdateOutput();
        broadcastChanges();
    }

    public boolean isMatrixUploaderAvailable() {
        return matrixUploaderAvailable;
    }

    public void selectTransferredRecipe(ResourceLocation recipeId) {
        if (isClientSide()) {
            sendClientAction(ACTION_SELECT_RECIPE, recipeId.toString());
        } else {
            selectRecipe(recipeId);
        }
    }

    public void selectTransferredRecipe(ResourceLocation recipeId, RecipeProvider provider, int tableTier, int tableSide) {
        if (isClientSide()) {
            sendClientAction(ACTION_SELECT_RECIPE,
                    recipeId + "|" + provider.name() + "|" + tableTier + "|" + tableSide);
        } else {
            selectRecipe(recipeId, provider, tableTier, tableSide);
        }
    }

    private void selectRecipeById(String recipeId) {
        var parts = recipeId.split("\\|", -1);
        var parsed = ResourceLocation.tryParse(parts[0]);
        if (parsed == null) {
            return;
        }
        if (parts.length == 4) {
            var provider = parseRecipeProvider(parts[1]);
            var tableTier = parsePositiveInt(parts[2]);
            var tableSide = parsePositiveInt(parts[3]);
            if (provider != null && tableTier > 0 && tableSide > 0) {
                selectRecipe(parsed, provider, tableTier, tableSide);
                return;
            }
        }
        selectRecipe(parsed);
    }

    private void selectRecipe(ResourceLocation recipeId) {
        selectedRecipeId = recipeId;
        invalidateRecipeMatches();
        getAndUpdateOutput();
        broadcastChanges();
    }

    private void selectRecipe(ResourceLocation recipeId, RecipeProvider provider, int tableTier, int tableSide) {
        if (!supportedRecipeDisplays.contains(new RecipeDisplay(provider, tableTier, tableSide))) {
            return;
        }
        this.selectedRecipeProvider = provider.ordinal();
        this.selectedRecipeTableTier = tableTier;
        this.selectedRecipeTableSide = tableSide;
        this.selectedRecipeId = recipeId;
        saveRememberedRecipeType(new RecipeDisplay(provider, tableTier, tableSide));
        invalidateRecipeMatches();
        getAndUpdateOutput();
        broadcastChanges();
    }

    @Nullable
    private static RecipeProvider parseRecipeProvider(String name) {
        try {
            return RecipeProvider.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static int parsePositiveInt(String value) {
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public RecipeProvider getSelectedRecipeProvider() {
        var providers = RecipeProvider.values();
        if (selectedRecipeProvider >= 0 && selectedRecipeProvider < providers.length) {
            var provider = providers[selectedRecipeProvider];
            if (activeRecipeProviders.contains(provider)) {
                return provider;
            }
        }
        return firstActiveRecipeProvider();
    }

    public int getSelectedRecipeTableSide() {
        return selectedRecipeTableSide <= 0 ? getGridSide() : selectedRecipeTableSide;
    }

    public int getSelectedRecipeTableTier() {
        return selectedRecipeTableTier <= 0 ? (getGridSide() - 1) / 2 : selectedRecipeTableTier;
    }

    private RecipeDisplay getSelectedRecipeDisplay() {
        return new RecipeDisplay(getSelectedRecipeProvider(), getSelectedRecipeTableTier(), getSelectedRecipeTableSide());
    }

    private boolean loadRememberedRecipeType() {
        if (!host.rememberRecipeType()) {
            return false;
        }

        var remembered = host.getRememberedRecipeType();
        if (remembered == null || !activeRecipeProviders.contains(remembered.provider())
                || remembered.tableTier() <= 0 || remembered.tableSide() <= 0) {
            return false;
        }

        var display = new RecipeDisplay(remembered.provider(), remembered.tableTier(), remembered.tableSide());
        if (!supportedRecipeDisplays.contains(display)) {
            return false;
        }

        this.selectedRecipeProvider = remembered.provider().ordinal();
        this.selectedRecipeTableTier = remembered.tableTier();
        this.selectedRecipeTableSide = remembered.tableSide();
        return true;
    }

    public boolean rememberRecipeType() {
        return host.rememberRecipeType();
    }

    public void setRememberRecipeType(boolean remember) {
        if (isClientSide()) {
            sendClientAction(ACTION_REMEMBER_RECIPE_TYPE, remember);
            return;
        }

        host.setRememberRecipeType(remember);
        if (remember) {
            saveRememberedRecipeType(getSelectedRecipeDisplay());
        }
    }

    private void saveRememberedRecipeType(RecipeDisplay display) {
        if (!isClientSide() && host.rememberRecipeType()) {
            host.setRememberedRecipeType(new ExtendedPatternRecipeType(
                    display.provider(),
                    display.tableTier(),
                    display.tableSide()));
        }
    }

    public boolean isSubstituteFluids() {
        return substituteFluids;
    }

    public boolean isSubstitute() {
        return substitute;
    }

    public void setSubstitute(boolean substitute) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_SUBSTITUTION, substitute);
        } else {
            encodingLogic.setSubstitution(substitute);
            this.substitute = substitute;
        }
    }

    public void setSubstituteFluids(boolean substituteFluids) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_FLUID_SUBSTITUTION, substituteFluids);
        } else {
            encodingLogic.setFluidSubstitution(substituteFluids);
            this.substituteFluids = substituteFluids;
        }
    }

    public FakeSlot[] getCraftingGridSlots() {
        return craftingGridSlots;
    }

    public int getGridSide() {
        return host.getGridSide();
    }

    public void addSingularitySlot(InternalInventory singularityInventory, SlotSemantic semantic) {
        addSlot(new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                singularityInventory,
                0), semantic);
    }

    @Nullable
    private ItemStack encodePattern() {
        invalidateRecipeMatches();
        var match = getAndUpdateOutput();
        if (match == null || match.result().isEmpty()) {
            return null;
        }

        var encodedPattern = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), encodingLogic.isSubstitution(), encodingLogic.isFluidSubstitution());
        var playerName = getPlayerInventory().player.getGameProfile().getName();
        encodedPattern.getOrCreateTag().putString(ENCODE_PLAYER_TAG, playerName);
        return encodedPattern;
    }

    @Nullable
    private ExtendedPatternRecipeMatch getAndUpdateOutput() {
        if (isClientSide()) {
            return null;
        }
        var encodedInputRevision = encodingLogic.getEncodedInputRevision();
        if (this.matchedEncodedInputRevision != encodedInputRevision) {
            this.matchedEncodedInputRevision = encodedInputRevision;
            var items = getEncodedGridItems();
            if (items == null) {
                currentMatches = List.of();
                currentRecipeDisplayIndexes = List.of();
            } else {
                currentMatches = ExtendedPatternRecipeFinder.findAll(items, getPlayerInventory().player.level()).stream()
                        .filter(match -> supportedRecipeDisplays.contains(RecipeDisplay.of(match))).toList();
                currentRecipeDisplayIndexes = findRecipeDisplayIndexes(currentMatches);
            }
        }

        if (currentMatches.isEmpty()) {
            currentMatch = null;
            currentRecipeIndex = 0;
        } else {
            var previousRecipe = currentMatch == null ? null : currentMatch.recipe().getId();
            var preferredRecipe = selectedRecipeId != null ? selectedRecipeId : previousRecipe;
            currentRecipeIndex = selectedRecipeId != null
                    ? findRecipeIndex(preferredRecipe)
                    : findRecipeDisplayIndex(getSelectedRecipeDisplay());
            if (currentRecipeIndex < 0) {
                currentRecipeIndex = selectedRecipeId != null && !currentMatches.isEmpty() ? 0 : -1;
                selectedRecipeId = null;
            }
            currentMatch = currentRecipeIndex < 0 || currentMatches.isEmpty() ? null : currentMatches.get(currentRecipeIndex);
        }

        if (currentMatch != null) {
            setSelectedRecipeDisplay(currentMatch);
        }
        recipeMatchCount = currentMatches.size();
        craftOutputSlot.set(currentMatch == null ? ItemStack.EMPTY : currentMatch.result());
        return currentMatch;
    }

    private void invalidateRecipeMatches() {
        this.matchedEncodedInputRevision = Long.MIN_VALUE;
    }

    private int nextRecipeIndex(int currentIndex) {
        if (currentMatches.isEmpty()) {
            return -1;
        }

        if (currentRecipeDisplayIndexes.size() <= 1) {
            return Math.floorMod(currentIndex + 1, currentMatches.size());
        }

        return nextRecipeDisplayIndex(currentIndex);
    }

    private int nextRecipeDisplayIndex(int currentIndex) {
        if (currentRecipeDisplayIndexes.isEmpty()) {
            return -1;
        }

        var currentDisplay = findRecipeDisplayPosition(currentIndex);
        var nextDisplay = Math.floorMod(currentDisplay + 1, currentRecipeDisplayIndexes.size());
        return currentRecipeDisplayIndexes.get(nextDisplay);
    }

    private int findRecipeDisplayPosition(int recipeIndex) {
        for (int i = 0; i < currentRecipeDisplayIndexes.size(); i++) {
            if (currentRecipeDisplayIndexes.get(i) == recipeIndex) {
                return i;
            }
        }
        return 0;
    }

    private static List<Integer> findRecipeDisplayIndexes(List<ExtendedPatternRecipeMatch> matches) {
        if (matches.isEmpty()) {
            return List.of();
        }

        var displayIndexes = new ArrayList<Integer>();
        var displays = new ArrayList<RecipeDisplay>();
        for (int i = 0; i < matches.size(); i++) {
            var display = RecipeDisplay.of(matches.get(i));
            if (!displays.contains(display)) {
                displays.add(display);
                displayIndexes.add(i);
            }
        }
        return List.copyOf(displayIndexes);
    }

    private record RecipeDisplay(RecipeProvider provider, int tableTier, int tableSide) {
        static RecipeDisplay of(ExtendedPatternRecipeMatch match) {
            var provider = RecipeProvider.of(match.recipe());
            var side = (int) Math.sqrt(match.inputs().length);
            return new RecipeDisplay(provider, tierForDisplay(provider, side), side);
        }
    }

    private int findRecipeDisplayIndex(RecipeDisplay display) {
        for (int i = 0; i < currentMatches.size(); i++) {
            if (RecipeDisplay.of(currentMatches.get(i)).equals(display)) {
                return i;
            }
        }
        return -1;
    }

    private int findRecipeIndex(@Nullable ResourceLocation recipeId) {
        if (recipeId == null) {
            return currentMatches.isEmpty() ? -1 : 0;
        }

        for (int i = 0; i < currentMatches.size(); i++) {
            if (currentMatches.get(i).recipe().getId().equals(recipeId)) {
                return i;
            }
        }
        return -1;
    }

    private static List<RecipeDisplay> createSupportedRecipeDisplays(EnumSet<RecipeProvider> activeProviders, int gridSide) {
        if (gridSide > 9) {
            return activeProviders.contains(RecipeProvider.EXTENDED_CRAFTING)
                    ? List.of(new RecipeDisplay(RecipeProvider.EXTENDED_CRAFTING, (gridSide - 1) / 2, gridSide))
                    : List.of();
        }
        var displays = new ArrayList<RecipeDisplay>();
        if (activeProviders.contains(RecipeProvider.EXTENDED_CRAFTING)) {
            displays.add(new RecipeDisplay(RecipeProvider.EXTENDED_CRAFTING, 1, 3));
            displays.add(new RecipeDisplay(RecipeProvider.EXTENDED_CRAFTING, 2, 5));
            displays.add(new RecipeDisplay(RecipeProvider.EXTENDED_CRAFTING, 3, 7));
            displays.add(new RecipeDisplay(RecipeProvider.EXTENDED_CRAFTING, 4, 9));
        }
        if (activeProviders.contains(RecipeProvider.RE_AVARITIA)) {
            displays.add(new RecipeDisplay(RecipeProvider.RE_AVARITIA, 1, 3));
            displays.add(new RecipeDisplay(RecipeProvider.RE_AVARITIA, 2, 5));
            displays.add(new RecipeDisplay(RecipeProvider.RE_AVARITIA, 3, 7));
            displays.add(new RecipeDisplay(RecipeProvider.RE_AVARITIA, 4, 9));
        }
        if (activeProviders.contains(RecipeProvider.AVARITIA_NEO)) {
            displays.add(new RecipeDisplay(RecipeProvider.AVARITIA_NEO, 4, 9));
        }
        return List.copyOf(displays);
    }

    private static int tierForDisplay(RecipeProvider provider, int side) {
        if (provider == RecipeProvider.AVARITIA_NEO) {
            return 4;
        }
        return switch (side) {
            case 3 -> 1;
            case 5 -> 2;
            case 7 -> 3;
            case 11 -> 5;
            case 13 -> 6;
            default -> 4;
        };
    }

    @Nullable
    private List<ItemStack> getEncodedGridItems() {
        var inputInv = encodingLogic.getEncodedInputInv();
        var items = net.minecraft.core.NonNullList.withSize(inputInv.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < inputInv.size(); slot++) {
            var stack = getEncodedCraftingIngredient(slot);
            if (stack == null) {
                return null;
            }
            items.set(slot, stack);
        }
        return items;
    }

    @Nullable
    private ItemStack getEncodedCraftingIngredient(int slot) {
        var what = encodingLogic.getEncodedInputInv().getKey(slot);
        if (what == null) {
            return ItemStack.EMPTY;
        }
        if (what instanceof AEItemKey itemKey) {
            return itemKey.toStack(1);
        }
        return null;
    }

    private void clearPattern() {
        var encodedPattern = encodedPatternSlot.getItem();
        if (PatternDetailsHelper.isEncodedPattern(encodedPattern)) {
            encodedPatternSlot.set(AEItems.BLANK_PATTERN.stack(encodedPattern.getCount()));
        }
    }

    private void setSelectedRecipeDisplay(@Nullable ExtendedPatternRecipeMatch match) {
        var provider = match == null ? firstActiveRecipeProvider() : RecipeProvider.of(match.recipe());
        var side = match == null ? getGridSide() : (int) Math.sqrt(match.inputs().length);
        this.selectedRecipeProvider = provider.ordinal();
        this.selectedRecipeTableTier = tierForDisplay(provider, side);
        this.selectedRecipeTableSide = side;
        if (match != null) {
            saveRememberedRecipeType(getSelectedRecipeDisplay());
        }
    }

    private RecipeProvider firstActiveRecipeProvider() {
        return this.activeRecipeProviders.stream().findFirst().orElse(RecipeProvider.EXTENDED_CRAFTING);
    }

    public enum RecipeProvider {
        EXTENDED_CRAFTING,
        AVARITIA_NEO,
        RE_AVARITIA;

        public boolean isActive() {
            return switch (this) {
                case EXTENDED_CRAFTING -> MyotusAPI.integrations().isLoaded(ExtendedCrafting.class);
                case AVARITIA_NEO -> MyotusAPI.integrations().isLoaded(AvaritiaNeo.class);
                case RE_AVARITIA -> MyotusAPI.integrations().isLoaded(ReAvaritia.class);
            };
        }

        public static RecipeProvider firstActive() {
            if (EXTENDED_CRAFTING.isActive()) {
                return EXTENDED_CRAFTING;
            }
            if (AVARITIA_NEO.isActive()) {
                return AVARITIA_NEO;
            }
            if (RE_AVARITIA.isActive()) {
                return RE_AVARITIA;
            }
            return EXTENDED_CRAFTING;
        }

        public static RecipeProvider of(Recipe<?> recipe) {
            if (AVARITIA_NEO.isActive() && AvaritiaNeoRecipeAdapters.supports(recipe)) {
                return AVARITIA_NEO;
            }
            if (RE_AVARITIA.isActive() && ReAvaritiaRecipeAdapters.supports(recipe)) {
                return RE_AVARITIA;
            }
            return EXTENDED_CRAFTING;
        }
    }
}
