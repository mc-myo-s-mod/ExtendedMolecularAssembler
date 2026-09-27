package me.myogoo.extendedmolecularassembler.menu;

import appeng.menu.AEBaseMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.locator.MenuLocators;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;

/** Forge menu registration using AE2's host locators without registering into AE2's namespace. */
public final class ForgeMenuTypes {
    private ForgeMenuTypes() {
    }

    public static <M extends AEBaseMenu, H> MenuType<M> create(ResourceLocation id, Class<H> hostType,
            MenuTypeBuilder.MenuFactory<M, H> factory) {
        MenuType<M> type = IForgeMenuType.create((containerId, inventory, buffer) -> {
            var locator = MenuLocators.readFromPacket(buffer);
            var host = locator.locate(inventory.player, hostType);
            if (host == null) {
                throw new IllegalStateException("Missing host for " + id + " at " + locator);
            }
            var menu = factory.create(containerId, inventory, host);
            menu.setReturnedFromSubScreen(buffer.readBoolean());
            return menu;
        });
        MenuOpener.addOpener(type, (player, locator, fromSubMenu) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return false;
            }
            var host = locator.locate(player, hostType);
            if (host == null) {
                return false;
            }
            var title = host instanceof Nameable nameable && nameable.hasCustomName()
                    ? nameable.getCustomName() : Component.empty();
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, inventory, ignored) -> {
                var menu = factory.create(containerId, inventory, host);
                menu.setLocator(locator);
                return menu;
            }, title), buffer -> {
                MenuLocators.writeToPacket(buffer, locator);
                buffer.writeBoolean(fromSubMenu);
            });
            return true;
        });
        return type;
    }
}
