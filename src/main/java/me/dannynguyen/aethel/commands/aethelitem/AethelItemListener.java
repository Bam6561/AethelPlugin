package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Inventory click event listener for AethelItem menus.
 *
 * @author Danny Nguyen
 * @version 1.9.8
 * @since 1.4.0
 */
public record AethelItemListener(@NotNull InventoryClickEvent e, @NotNull Player user) {
  /**
   * Associates an inventory click event with its user in the context of an open AethelItem menu.
   *
   * @param e    inventory click event
   * @param user user
   */
  public AethelItemListener(@NotNull InventoryClickEvent e, @NotNull Player user) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = Objects.requireNonNull(user, "Null user");
  }

  /**
   * Either saves an item or opens an item category page.
   */
  public void interpretMainMenuClick() {
    if (e.getSlot() == 4) {
      new AethelItemOperation(e, user).saveItem();
    } else if (e.getSlot() > 8) {
      String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

      user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), itemName));
      user.openInventory(new AethelItemMenu(user, AethelItemAction.GET).openCategoryPage(itemName, pageRequest));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEMS_GET.menu));
    }
  }

  /**
   * Either:
   * - increments or decrements an item category page
   * - saves an item
   * - changes the interaction type
   * - contextualizes the click to get or remove items
   *
   * @param action type of interaction
   */
  public void interpretCategoryClick(@NotNull AethelItemAction action) {
    int slotClicked = e.getSlot();
    switch (slotClicked) {
      case 0 -> previousPage(action);
      case 2 -> { // Help Context
      }
      case 4 -> new AethelItemOperation(e, user).saveItem();
      case 5 -> toggleAction(action);
      case 6 -> returnToMainMenu();
      case 8 -> nextPage(action);
      default -> interpretContextualClick(action);
    }
  }

  /**
   * Opens the previous item category page.
   *
   * @param action type of interaction
   */
  private void previousPage(AethelItemAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new AethelItemMenu(user, action).openCategoryPage(categoryName, pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "aethelitems." + AethelItemAction.asString(action)));
  }

  /**
   * Toggles between get and remove actions.
   *
   * @param action type of interaction
   */
  private void toggleAction(AethelItemAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    if (action.equals(AethelItemAction.GET)) {
      user.openInventory(new AethelItemMenu(user, AethelItemAction.REMOVE).openCategoryPage(categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEMS_REMOVE.menu));
    } else {
      user.openInventory(new AethelItemMenu(user, AethelItemAction.GET).openCategoryPage(categoryName, pageRequest));
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEMS_GET.menu));
    }
  }

  /**
   * Returns to the AethelItem main menu.
   */
  private void returnToMainMenu() {
    user.openInventory(new AethelItemMenu(user, AethelItemAction.VIEW).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.AETHELITEM_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the next item category page.
   *
   * @param action type of interaction
   */
  private void nextPage(AethelItemAction action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.CATEGORY.getMeta()).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.PAGE.getMeta()).get(0).asInt();

    user.openInventory(new AethelItemMenu(user, action).openCategoryPage(categoryName, pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "aethelitems." + AethelItemAction.asString(action)));
  }

  /**
   * Either gets or remove an item.
   *
   * @param action interacting action
   */
  private void interpretContextualClick(AethelItemAction action) {
    switch (action) {
      case GET -> new AethelItemOperation(e, user).getItem();
      case REMOVE -> new AethelItemOperation(e, user).removeItem();
    }
  }
}
