package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link ItemCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.23.7
 * @since 1.4.0
 */
public class ItemMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open {@link ItemCommand} menu.
   *
   * @param e inventory click event
   */
  public ItemMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.slot = e.getSlot();
  }

  /**
   * Either saves a {@link ItemRegistry.Item item} or gets an {@link ItemRegistry.Item item} category page.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 3 -> e.setCancelled(false); // Save Item Slot
      case 4 -> saveItem();
      default -> {
        if (slot > 8) {
          getCategoryPage();
        }
      }
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements an {@link ItemRegistry.Item item} category page
   *  <li>saves a {@link ItemRegistry.Item item}
   *  <li>changes the {@link ItemMenu.Action interaction}
   *  <li>contextualizes the click to get or remove {@link ItemRegistry.Item items}
   * </ul>
   *
   * @param action type of {@link ItemMenu.Action} interaction
   */
  public void interpretCategoryClick(@NotNull ItemMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> previousPage(action);
      case 2 -> { // Context
      }
      case 3 -> e.setCancelled(false); // Save Item Slot
      case 4 -> saveItem();
      case 5 -> toggleAction(action);
      case 6 -> returnToMainMenu();
      case 8 -> nextPage(action);
      default -> {
        if (slot > 8) {
          interpretContextualClick(action);
        }
      }
    }
  }

  /**
   * Checks if there is an item in the designated save slot
   * before saving the {@link ItemRegistry.Item item}.
   */
  private void saveItem() {
    ItemStack item = e.getClickedInventory().getItem(3);
    if (ItemReader.isNullOrAir(item)) {
      user.sendMessage(ChatColor.RED + "No item to save.");
      return;
    }
    String encodedItem = ItemCreator.encodeItem(item);
    if (encodedItem == null) {
      user.sendMessage(ChatColor.RED + "Failed to save item.");
      return;
    }

    try {
      FileWriter fw = new FileWriter(Directory.AETHELITEM.getFile().getPath() + "/" + nameItemFile(item) + "_itm.txt");
      fw.write(encodedItem);
      fw.close();
      user.sendMessage(ChatColor.GREEN + "[Saved Aethel Item] " + ChatColor.WHITE + ItemReader.readName(item));
    } catch (IOException ex) {
      user.sendMessage(ChatColor.RED + "Failed to write item to file.");
    }
  }

  /**
   * Gets an {@link ItemRegistry.Item item} category page.
   */
  private void getCategoryPage() {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String category = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
    int pageRequest = menuInput.getPage();

    menuInput.setCategory(category);
    user.openInventory(new ItemMenu(user, ItemMenu.Action.GET).getCategoryPage(category, pageRequest));
    menuInput.setMenu(MenuListener.Menu.AETHELITEM_GET);
  }

  /**
   * Gets the previous {@link ItemRegistry.Item item} category page.
   *
   * @param action type of interaction
   */
  private void previousPage(ItemMenu.Action action) {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String category = menuInput.getCategory();
    int pageRequest = menuInput.getPage();

    user.openInventory(new ItemMenu(user, action).getCategoryPage(category, pageRequest - 1));
    menuInput.setMenu(MenuListener.Menu.valueOf("AETHELITEM_" + action.name()));
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param action type of interaction
   */
  private void toggleAction(ItemMenu.Action action) {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String category = menuInput.getCategory();
    int pageRequest = menuInput.getPage();

    switch (action) {
      case GET -> {
        user.openInventory(new ItemMenu(user, ItemMenu.Action.REMOVE).getCategoryPage(category, pageRequest));
        menuInput.setMenu(MenuListener.Menu.AETHELITEM_REMOVE);
      }
      case REMOVE -> {
        user.openInventory(new ItemMenu(user, ItemMenu.Action.GET).getCategoryPage(category, pageRequest));
        menuInput.setMenu(MenuListener.Menu.AETHELITEM_GET);
      }
    }
  }

  /**
   * Returns to the {@link ItemMenu}.
   */
  private void returnToMainMenu() {
    user.openInventory(new ItemMenu(user, ItemMenu.Action.VIEW).getMainMenu());
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    menuInput.setMenu(MenuListener.Menu.AETHELITEM_CATEGORY);
    menuInput.setPage(0);
  }

  /**
   * Gets the next {@link ItemRegistry.Item item} category page.
   *
   * @param action type of interaction
   */
  private void nextPage(ItemMenu.Action action) {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String category = menuInput.getCategory();
    int pageRequest = menuInput.getPage();

    user.openInventory(new ItemMenu(user, action).getCategoryPage(category, pageRequest + 1));
    menuInput.setMenu(MenuListener.Menu.valueOf("AETHELITEM_" + action.name()));
  }

  /**
   * Either gets or remove an {@link ItemRegistry.Item item}.
   *
   * @param action type of interaction
   */
  private void interpretContextualClick(ItemMenu.Action action) {
    ItemStack clicked = e.getCurrentItem();
    switch (action) {
      case GET -> {
        ItemStack item = Plugin.getData().getItemRegistry().getItems().get(ItemReader.readName(clicked)).getItem();
        if (user.getInventory().firstEmpty() != -1) {
          user.getInventory().addItem(item);
        } else {
          user.getWorld().dropItem(user.getLocation(), item);
        }
      }
      case REMOVE -> {
        ItemRegistry.Item pItem = Plugin.getData().getItemRegistry().getItems().get(ItemReader.readName(clicked));
        pItem.delete();
        user.sendMessage(ChatColor.RED + "[Removed Aethel Item] " + ChatColor.WHITE + pItem.getName());
      }
    }
  }

  /**
   * Names an {@link ItemRegistry.Item item} file by either its display name or material.
   *
   * @param item interacting item
   * @return {@link ItemRegistry.Item item} file name
   */
  private String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return TextFormatter.formatId(ChatColor.stripColor(meta.getDisplayName()));
    } else {
      return TextFormatter.formatId(item.getType().name());
    }
  }
}
