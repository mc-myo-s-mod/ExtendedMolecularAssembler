package me.myogoo.extendedmolecularassembler.init;

import appeng.api.stacks.AEItemKey;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.core.localization.GuiText;
import appeng.util.InteractionUtil;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.item.AssemblerUpgradeKitItem;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class EMAItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<BlockItem> EXTENDED_MOLECULAR_ASSEMBLER =
            ITEMS.register("extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
    @Nullable
    public static RegistryObject<BlockItem> EX_EXTENDED_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<BlockItem> EX_EPIC_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<BlockItem> EX_LEGENDARY_MOLECULAR_ASSEMBLER;

    public static final RegistryObject<BlockItem> EPIC_MOLECULAR_ASSEMBLER =
            ITEMS.register("epic_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> LEGENDARY_MOLECULAR_ASSEMBLER =
            ITEMS.register("legendary_molecular_assembler",
                    () -> new BlockItem(EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));

    public static final RegistryObject<AssemblerUpgradeKitItem> EPIC_ASSEMBLER_UPGRADE_KIT =
            ITEMS.register("epic_molecular_assembler_upgrade_kit", () -> new AssemblerUpgradeKitItem(
                    new Item.Properties(), EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER, EMABlocks.EPIC_MOLECULAR_ASSEMBLER));
    public static final RegistryObject<AssemblerUpgradeKitItem> LEGENDARY_ASSEMBLER_UPGRADE_KIT =
            ITEMS.register("legendary_molecular_assembler_upgrade_kit", () -> new AssemblerUpgradeKitItem(
                    new Item.Properties(), EMABlocks.EPIC_MOLECULAR_ASSEMBLER, EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER));
    @Nullable
    public static RegistryObject<AssemblerUpgradeKitItem> EX_EXTENDED_ASSEMBLER_UPGRADE_KIT;
    @Nullable
    public static RegistryObject<AssemblerUpgradeKitItem> EX_EPIC_ASSEMBLER_UPGRADE_KIT;
    @Nullable
    public static RegistryObject<AssemblerUpgradeKitItem> EX_LEGENDARY_ASSEMBLER_UPGRADE_KIT;
    @Nullable
    public static RegistryObject<AssemblerUpgradeKitItem> EPIC_ASSEMBLER_EX_UPGRADE_KIT;
    @Nullable
    public static RegistryObject<AssemblerUpgradeKitItem> LEGENDARY_ASSEMBLER_EX_UPGRADE_KIT;

    public static final RegistryObject<BlockItem> BASIC_ME_CRAFTING_PROVIDER =
            ITEMS.register("basic_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ADVANCED_ME_CRAFTING_PROVIDER =
            ITEMS.register("advanced_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ELITE_ME_CRAFTING_PROVIDER =
            ITEMS.register("elite_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ULTIMATE_ME_CRAFTING_PROVIDER =
            ITEMS.register("ultimate_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_sculk_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_nether_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_end_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> XTREME_ME_CRAFTING_PROVIDER =
            ITEMS.register("xtreme_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> EPIC_ME_CRAFTING_PROVIDER =
            ITEMS.register("epic_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.EPIC_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> LEGENDARY_ME_CRAFTING_PROVIDER =
            ITEMS.register("legendary_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.LEGENDARY_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));

    public static final RegistryObject<Item> EXTENDED_CRAFTING_PATTERN =
            ITEMS.register("extended_crafting_pattern", () -> new EncodedPatternItem(new Item.Properties().stacksTo(1)) {
                @Override
                public @Nullable ExtendedTableCraftingPattern decode(ItemStack stack, Level level, boolean tryRecovery) {
                    return decode(AEItemKey.of(stack), level);
                }

                @Override
                public @Nullable ExtendedTableCraftingPattern decode(AEItemKey what, Level level) {
                    if (what == null || what.getItem() != this || level == null) {
                        return null;
                    }
                    try {
                        return new ExtendedTableCraftingPattern(what, level);
                    } catch (Exception invalidPattern) {
                        return null;
                    }
                }

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                        TooltipFlag flag) {
                    if (level != null && EncodedExtendedCraftingPattern.get(stack) != null
                            && decode(stack, level, false) == null) {
                        tooltip.add(GuiText.InvalidPattern.text().withStyle(ChatFormatting.RED));
                    }
                    ExtendedTableCraftingPattern.appendTooltip(stack, tooltip, flag);
                }

                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    var stack = player.getItemInHand(hand);
                    if (!InteractionUtil.isInAlternateUseMode(player)
                            && EncodedExtendedCraftingPattern.get(stack) != null
                            && EMAOptionalIntegrations.openPatternView(player, stack)) {
                        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                    }
                    return super.use(level, player, hand);
                }
            });

    private EMAItems() {
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = ITEMS.register(
                    "ex_extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
            EX_EPIC_MOLECULAR_ASSEMBLER = ITEMS.register(
                    "ex_epic_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
            EX_LEGENDARY_MOLECULAR_ASSEMBLER = ITEMS.register(
                    "ex_legendary_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
            EX_EXTENDED_ASSEMBLER_UPGRADE_KIT = ITEMS.register("ex_extended_molecular_assembler_upgrade_kit",
                    () -> new AssemblerUpgradeKitItem(new Item.Properties(),
                            EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER));
            EX_EPIC_ASSEMBLER_UPGRADE_KIT = ITEMS.register("ex_epic_molecular_assembler_upgrade_kit",
                    () -> new AssemblerUpgradeKitItem(new Item.Properties(),
                            EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER, EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER));
            EX_LEGENDARY_ASSEMBLER_UPGRADE_KIT = ITEMS.register("ex_legendary_molecular_assembler_upgrade_kit",
                    () -> new AssemblerUpgradeKitItem(new Item.Properties(),
                            EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER, EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER));
            EPIC_ASSEMBLER_EX_UPGRADE_KIT = ITEMS.register("epic_molecular_assembler_ex_upgrade_kit",
                    () -> new AssemblerUpgradeKitItem(new Item.Properties(),
                            EMABlocks.EPIC_MOLECULAR_ASSEMBLER, EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER));
            LEGENDARY_ASSEMBLER_EX_UPGRADE_KIT = ITEMS.register("legendary_molecular_assembler_ex_upgrade_kit",
                    () -> new AssemblerUpgradeKitItem(new Item.Properties(),
                            EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER, EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER));
        }
    }

}
