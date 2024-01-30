package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.commands.forge.object.ForgeCraftOperation;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeInventory is an inventory listener for the Forge command.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.0.9
 */
public class ForgeInventoryListener {
  /**
   * Opens a recipe category page.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretMainClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 2, 4 -> { // Help Context
        }
        case 3 -> ForgeAction.openForgeSaveInventory(user);
        default -> {
          String action = user.getMetadata(PluginPlayerMeta.Namespace.FUTURE.namespace).get(0).asString();
          String itemName = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
          user.setMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(), itemName));
          int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

          user.openInventory(ForgeInventory.openForgeCategoryPage(user, action, itemName, pageRequest));
          user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
              new FixedMetadataValue(Plugin.getInstance(), "forge." + action));
        }
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either:
   * - increments or decrements a recipe page
   * - changes the interaction type
   * - opens a ForgeSave inventory
   * - contextualizes the click to expand, edit, or remove recipes
   *
   * @param e      inventory click event
   * @param user   user
   * @param action type of interaction
   */
  public static void interpretCategoryClick(InventoryClickEvent e, Player user, String action) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 0 -> previousRecipePage(user, action);
        case 2 -> { // Help Context
        }
        case 3 -> ForgeAction.openForgeSaveInventory(user);
        case 4 -> {
          if (user.getMetadata(PluginPlayerMeta.Namespace.FUTURE.namespace).get(0).asString().equals("edit")) {
            openForgeEdit(user);
          }
        }
        case 5 -> openForgeRemove(user);
        case 6 -> returnToMainMenu(user);
        case 8 -> nextRecipePage(user, action);
        default -> interpretContextualClick(e, action, user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either crafts a recipe or goes back to the Forge category page with the intent to craft recipes.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretCraftConfirmClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 25 -> new ForgeCraftOperation().craftRecipe(e, user);
        case 26 -> openForgeCraft(user);
      }
    }
    e.setCancelled(true);
  }

  /**
   * Either saves a recipe or goes back to the Forge category page with the intent to edit recipes.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void interpretSaveClick(InventoryClickEvent e, Player user) {
    if (e.getCurrentItem() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      switch (e.getSlot()) {
        case 8 -> e.setCancelled(true);
        case 25 -> ForgeAction.readSaveClick(e, user);
        case 26 -> {
          openForgeEdit(user);
          e.setCancelled(true);
        }
      }
    }
  }

  /**
   * Opens the previous recipe page.
   *
   * @param user   user
   * @param action type of interaction
   */
  private static void previousRecipePage(Player user, String action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(ForgeInventory.openForgeCategoryPage(user, action, categoryName,
        pageRequest - 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "forge." + action));
  }

  /**
   * Opens a ForgeSave menu with the intent to edit recipes.
   * <p>
   * Since the user can either be returning to the main page or
   * a recipe category from here, both scenarios are supported.
   * </p>
   *
   * @param user user
   */
  private static void openForgeEdit(Player user) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    if (categoryName.equals("")) {
      user.openInventory(ForgeInventory.openMainMenu(user, "edit"));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_CATEGORY.inventory));
    } else {
      categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();

      user.openInventory(ForgeInventory.openForgeCategoryPage(user, "edit",
          categoryName, user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt()));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_EDIT.inventory));
    }
  }

  /**
   * Opens a Forge menu with the intent to remove recipes.
   *
   * @param user user
   */
  private static void openForgeRemove(Player user) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();

    user.openInventory(ForgeInventory.openForgeCategoryPage(user, "remove",
        categoryName, user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt()));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_REMOVE.inventory));
  }

  /**
   * Opens a Forge main menu with the future action in mind.
   *
   * @param user user
   */
  private static void returnToMainMenu(Player user) {
    String action = user.getMetadata(PluginPlayerMeta.Namespace.FUTURE.namespace).get(0).asString();
    user.setMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), ""));

    user.openInventory(ForgeInventory.openMainMenu(user, action));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_CATEGORY.inventory));
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens a ForgeCraft menu with the intent to craft recipes.
   *
   * @param user user
   */
  private static void openForgeCraft(Player user) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();

    user.openInventory(ForgeInventory.openForgeCategoryPage(user, "craft", categoryName,
        user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt()));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_CRAFT.inventory));
  }

  /**
   * Opens the next recipe page.
   *
   * @param user   user
   * @param action type of interaction
   */
  private static void nextRecipePage(Player user, String action) {
    String categoryName = user.getMetadata(PluginPlayerMeta.Namespace.CATEGORY.namespace).get(0).asString();
    int pageRequest = user.getMetadata(PluginPlayerMeta.Namespace.PAGE.namespace).get(0).asInt();

    user.openInventory(ForgeInventory.openForgeCategoryPage(user, action, categoryName,
        pageRequest + 1));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), "forge." + action));
  }

  /**
   * Either crafts, edits, or removes a recipe.
   *
   * @param e      inventory click event
   * @param action interaction type
   * @param user   user
   */
  private static void interpretContextualClick(InventoryClickEvent e, String action, Player user) {
    switch (action) {
      case "craft" -> ForgeAction.craftRecipeDetails(e, user);
      case "edit" -> ForgeAction.editRecipeDetails(e, user);
      case "remove" -> ForgeAction.removeRecipe(e, user);
    }
  }
}
