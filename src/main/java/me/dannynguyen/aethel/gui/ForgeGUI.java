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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

/**
 * ForgeGUI is an inventory listener for the Forge command invocation.
 *
 * @author Danny Nguyen
 * @version 1.0.4
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
        case "forge-craft" -> e.setCancelled(true);
        case "forge-editor" -> readForgeEditorClick(e, player);
        case "forge-create" -> readForgeCreateClick(e, player);
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
   * @param player player
   */
  private void readForgeEditorClick(InventoryClickEvent e, Player player) {
    switch (e.getSlot()) {
      case 2 -> {
        player.openInventory(createCreateMenu(player));
        player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-create"));
      }
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
   * Handles a Save Recipe interaction within Forge-Create.
   *
   * @param e inventory click event
   */
  private void readForgeCreateClick(InventoryClickEvent e, Player player) {
    if (e.getSlot() == 26) {
      ArrayList<ItemStack> forgeCreatePage = new ArrayList<ItemStack>();
      Collections.addAll(forgeCreatePage, e.getInventory().getContents());

      writeRecipeToFile(nameRecipe(forgeCreatePage), createRecipe(forgeCreatePage), e, player);
    }
  }

  /**
   * Names a Forge Recipe by the first item in the results row.
   *
   * @param forgeCreatePage items in the forge-create page
   * @return name of the forge recipe
   */
  private String nameRecipe(ArrayList<ItemStack> forgeCreatePage) {
    String recipeName;
    ItemStack firstResult = forgeCreatePage.get(1);
    if (firstResult.getItemMeta().hasDisplayName()) {
      recipeName = firstResult.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      recipeName = firstResult.getType().name().toLowerCase();
    }
    return recipeName;
  }

  /**
   * Serializes the Forge-Create page by its results and components.
   *
   * @param forgeCreatePage items in the forge-create page
   * @return serialized forge recipe
   */
  private String createRecipe(ArrayList<ItemStack> forgeCreatePage) {
    StringBuilder recipe = new StringBuilder("Results\n");
    for (int i = 1; i < 9; i++) {
      ItemStack item = forgeCreatePage.get(i);
      if (!(item == null)) {
        recipe.append(encodeItem(forgeCreatePage.get(i)) + "\n");
      }
    }
    recipe.append("Components\n");
    for (int i = 10; i < 18; i++) {
      ItemStack item = forgeCreatePage.get(i);
      if (!(item == null)) {
        recipe.append(encodeItem(forgeCreatePage.get(i)) + "\n");
      }
    }
    for (int i = 19; i < 26; i++) {
      ItemStack item = forgeCreatePage.get(i);
      if (!(item == null)) {
        recipe.append(encodeItem(forgeCreatePage.get(i)) + "\n");
      }
    }
    return recipe.toString();
  }

  /**
   * Creates a Forge-Create menu.
   *
   * @param player player
   * @return Forge-Create menu
   */
  private Inventory createCreateMenu(Player player) {
    Inventory craftMenu = Bukkit.createInventory(player, 27, "Forge-Create");
    craftMenu.setItem(0, createMenuItem(Material.CRAFTING_TABLE, "Result"));
    craftMenu.setItem(9, createMenuItem(Material.PAPER, "Recipe"));
    craftMenu.setItem(18, createMenuItem(Material.PAPER, "Recipe"));
    craftMenu.setItem(26, createMenuItem(Material.GREEN_CONCRETE, "Save Recipe"));
    return craftMenu;
  }

  /**
   * Creates a Forge-Modify menu.
   *
   * @param player player
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
   * @param player player
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

  /**
   * Creates a named item in a menu.
   *
   * @param material    item material
   * @param displayName item name
   * @return named item
   */
  private ItemStack createMenuItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Serializes an item into bytes.
   *
   * @param item item to serialize
   * @return serialized item string
   */
  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();

      byte[] serializedItem = baos.toByteArray();

      return Base64.getEncoder().encodeToString(serializedItem);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Writes a Forge recipe to storage.
   *
   * @param itemName    item name
   * @param encodedItem serialized item string
   */
  private void writeRecipeToFile(String itemName, String encodedItem, InventoryClickEvent e, Player player) {
    try {
      String fileName = itemName;
      File file = new File("./plugins/Aethel/" + fileName + ".txt");
      FileWriter fw = new FileWriter(file);
      fw.write(encodedItem);
      fw.close();
      player.sendMessage("Forge Recipe saved as " + fileName);
      e.setCancelled(true);
    } catch (IOException error) {
      error.printStackTrace();
    }
  }
}
