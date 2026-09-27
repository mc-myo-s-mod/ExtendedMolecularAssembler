package me.myogoo.extendedmolecularassembler.client.screen;

import appeng.api.config.ActionItems;
import appeng.client.gui.Icon;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.AEConfig;
import appeng.core.localization.ButtonToolTips;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.client.gui.widgets.button.MyoCycleButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class ExtendedPatternEncodingTermScreen extends MEStorageScreen<ExtendedPatternEncodingTermMenu> {
    private final ActionButton encodeBtn;
    private final ActionButton clearBtn;
    private final MyoCycleButton recipeCycleBtn;
    private final ToggleButton substitutionsBtn;
    private final ToggleButton fluidSubstitutionsBtn;

    public ExtendedPatternEncodingTermScreen(ExtendedPatternEncodingTermMenu menu, Inventory playerInventory,
            Component title, ScreenStyle style) {
        this(menu, playerInventory, title, style, true);
    }

    protected ExtendedPatternEncodingTermScreen(ExtendedPatternEncodingTermMenu menu, Inventory playerInventory,
            Component title, ScreenStyle style, boolean useGridSideStyle) {
        super(menu, playerInventory, title, useGridSideStyle && menu.getGridSide() > 9
                ? StyleManager.loadStyleDoc("/screens/extended_molecular_assembler/"
                        + (menu.getGridSide() == 11 ? "epic" : "legendary") + "_pattern_encoding_terminal.json")
                : style);

        this.encodeBtn = new ActionButton(ActionItems.ENCODE, action -> menu.encode(Screen.hasShiftDown()));
        widgets.add("encodePattern", encodeBtn);

        this.clearBtn = new ActionButton(ActionItems.CLOSE, action -> menu.clear());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearPattern", clearBtn);

        this.recipeCycleBtn = new MyoCycleButton(
                () -> Icon.ARROW_RIGHT,
                menu::cycleRecipeTable,
                menu::cycleRecipeTableBackwards,
                this::tableIcon,
                this::recipeCycleTooltip);
        widgets.add("recipeCycle", recipeCycleBtn);

        this.substitutionsBtn = new ToggleButton(
                Icon.SUBSTITUTION_ENABLED,
                Icon.SUBSTITUTION_DISABLED,
                menu::setSubstitute);
        substitutionsBtn.setHalfSize(true);
        substitutionsBtn.setDisableBackground(true);
        substitutionsBtn.setTooltipOn(List.of(
                ButtonToolTips.SubstitutionsOn.text(),
                ButtonToolTips.SubstitutionsDescEnabled.text()));
        substitutionsBtn.setTooltipOff(List.of(
                ButtonToolTips.SubstitutionsOff.text(),
                ButtonToolTips.SubstitutionsDescDisabled.text()));
        widgets.add("substitutions", substitutionsBtn);

        this.fluidSubstitutionsBtn = new ToggleButton(
                Icon.FLUID_SUBSTITUTION_ENABLED,
                Icon.FLUID_SUBSTITUTION_DISABLED,
                menu::setSubstituteFluids);
        fluidSubstitutionsBtn.setHalfSize(true);
        fluidSubstitutionsBtn.setDisableBackground(true);
        fluidSubstitutionsBtn.setTooltipOn(List.of(
                ButtonToolTips.FluidSubstitutions.text(),
                ButtonToolTips.FluidSubstitutionsDescEnabled.text()));
        fluidSubstitutionsBtn.setTooltipOff(List.of(
                ButtonToolTips.FluidSubstitutions.text(),
                ButtonToolTips.FluidSubstitutionsDescDisabled.text()));
        widgets.add("canSubstituteFluids", fluidSubstitutionsBtn);
    }

    @Override
    public void init() {
        super.init();
        if (getMenu().getGridSide() > 9) {
            var slots = getMenu().getCraftingGridSlots();
            int x = slots[0].x;
            int y = slots[0].y;
            int side = getMenu().getGridSide();
            for (int i = 0; i < slots.length; i++) {
                slots[i].x = x + i % side * 18;
                slots[i].y = y + i / side * 18;
            }
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        recipeCycleBtn.setVisibility(true);
        recipeCycleBtn.active = getMenu().canCycleRecipes();
        substitutionsBtn.setState(getMenu().isSubstitute());
        fluidSubstitutionsBtn.setState(getMenu().isSubstituteFluids());
    }

    @Override
    public void onClose() {
        if (AEConfig.instance().isClearGridOnClose()) {
            getMenu().clear();
        }
        super.onClose();
    }

    private Component tableTooltip() {
        return Component.literal("Recipe Table: ")
                .append(tableLabel())
                .append(Component.literal(" "))
                .append(Component.literal(getMenu().getSelectedRecipeTableSide() + "x"
                        + getMenu().getSelectedRecipeTableSide()));
    }

    private List<Component> recipeCycleTooltip() {
        return List.of(
                tableTooltip(),
                Component.literal("Left-click: Next table"),
                Component.literal("Right-click: Previous table"));
    }

    private Component tableLabel() {
        var tier = getMenu().getSelectedRecipeTableTier();
        return switch (getMenu().getSelectedRecipeProvider()) {
            case EXTENDED_CRAFTING -> Component.literal("Extended Crafting");
            case AVARITIA_NEO -> Component.literal("Avaritia Neo Extreme");
            case RE_AVARITIA -> Component.literal("Re:Avaritia ").append(reAvaritiaTableName(tier));
        };
    }

    private Item tableIcon() {
        return switch (getMenu().getSelectedRecipeProvider()) {
            case EXTENDED_CRAFTING -> extendedTableIcon(getMenu().getSelectedRecipeTableSide());
            case RE_AVARITIA -> reAvaritiaTableIcon(getMenu().getSelectedRecipeTableTier());
            case AVARITIA_NEO -> icon("avaritia", "extreme_crafting_table");
        };
    }

    private Component reAvaritiaTableName(int tier) {
        return Component.literal(switch (tier) {
            case 1 -> "Sculk";
            case 2 -> "Nether";
            case 3 -> "End";
            default -> "Extreme";
        });
    }

    private Item reAvaritiaTableIcon(int tier) {
        return icon("avaritia", switch (tier) {
            case 1 -> "sculk_crafting_table";
            case 2 -> "nether_crafting_table";
            case 3 -> "end_crafting_table";
            default -> "extreme_crafting_table";
        });
    }

    private Item extendedTableIcon(int side) {
        return icon("extendedcrafting", switch (side) {
            case 3 -> "basic_table";
            case 5 -> "advanced_table";
            case 7 -> "elite_table";
            case 11 -> "epic_table";
            case 13 -> "legendary_table";
            default -> "ultimate_table";
        });
    }

    private Item icon(String namespace, String path) {
        var item = BuiltInRegistries.ITEM.get(new ResourceLocation(namespace, path));
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }
}
