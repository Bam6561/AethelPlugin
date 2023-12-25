package me.dannynguyen.aethel.gui;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeGUI is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.0.6
 * @since 1.0.2
 */
public class ForgeGUI implements Listener {
  /**
   * Routes interactions between Forge menus.
   *
   * @param e inventory click event
   */
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    if (player.hasMetadata("menu")) {
      String menuType = player.getMetadata("menu").get(0).asString();
      switch (menuType) {
        case "forge-craft" -> {
          ForgeCraft forgeCraft = new ForgeCraft(player);
          forgeCraft.interpretCraftClick(e);
        }
        case "forge-create" -> {
          if (e.getSlot() == 26) {
            ForgeCreate forgeCreate = new ForgeCreate(player);
            forgeCreate.processSaveClick(e);
          }
        }
        case "forge-editor" -> readForgeEditorClick(e, player);
      }
    }
  }

  /**
   * Removes player metadata pertaining to any open menus.
   *
   * @param e inventory close event
   */
  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    if (player.hasMetadata("menu")) {
      player.removeMetadata("menu", AethelPlugin.getInstance());
    }
  }

  /**
   * Sends either Forge menu:
   * - Create
   * - Modify
   * - Delete
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  private void readForgeEditorClick(InventoryClickEvent e, Player player) {
    switch (e.getSlot()) {
      case 4 -> {
        player.openInventory(createModifyMenu(player));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-modify"));
      }
      case 6 -> {
        player.openInventory(createDeleteMenu(player));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-delete"));
      }
    }
  }

  /**
   * Creates a Forge-Modify menu.
   *
   * @param player interacting player
   * @return Forge-Modify menu
   */
  private Inventory createModifyMenu(Player player) {
    Inventory modifyMenu = Bukkit.createInventory(player, 54, "Forge-Modify");
    modifyMenu.setItem(0, new ItemStack(Material.RED_WOOL));
    modifyMenu.setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(4, new ItemStack(Material.COMPASS));
    modifyMenu.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(8, new ItemStack(Material.GREEN_WOOL));
    return modifyMenu;
  }

  /**
   * Creates a Forge-Delete menu.
   *
   * @param player interacting player
   * @return Forge-Delete menu
   */
  private Inventory createDeleteMenu(Player player) {
    Inventory modifyMenu = Bukkit.createInventory(player, 54, "Forge-Delete");
    modifyMenu.setItem(0, new ItemStack(Material.RED_WOOL));
    modifyMenu.setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(4, new ItemStack(Material.COMPASS));
    modifyMenu.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    modifyMenu.setItem(8, new ItemStack(Material.GREEN_WOOL));
    return modifyMenu;
  }
}
