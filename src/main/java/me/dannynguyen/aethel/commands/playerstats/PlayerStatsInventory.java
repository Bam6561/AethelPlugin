package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

/**
 * PlayerStatsMain is a shared inventory under the PlayerStats command
 * that supports categorical pagination of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.7.3
 * @since 1.4.7
 */
public class PlayerStatsInventory {
  /**
   * Creates a PlayerStatsMain page containing stat categories.
   *
   * @param user                user
   * @param requestedPlayerName requested player's name
   * @return PlayerStatsMain inventory with stat categories
   */
  public static Inventory openMainMenu(Player user, String requestedPlayerName) {
    Inventory inv = createInventory(user, requestedPlayerName);
    addStatCategories(inv);
    addStatContext("categories", inv);
    addOwnerHead(user, inv);
    return inv;
  }

  /**
   * Creates and names a PlayerStatsMain inventory to the requested player.
   *
   * @param user                user
   * @param requestedPlayerName requested player's name
   * @return PlayerStatsMain inventory
   */
  private static Inventory createInventory(Player user, String requestedPlayerName) {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "PlayerStats "
        + ChatColor.DARK_PURPLE + requestedPlayerName);
  }

  /**
   * Adds stat categories.
   *
   * @param inv interacting inventory
   */
  private static void addStatCategories(Inventory inv) {
    int i = 9;
    for (String statCategory : PluginData.playerStatsData.getStatCategoryNames()) {
      inv.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + statCategory));
      i++;
    }
  }

  /**
   * Loads a stat category page from memory.
   *
   * @param user                user
   * @param requestedPlayerName requested player's name
   * @param categoryName        category to view
   * @param pageRequest         page to view
   * @return PlayerStatsMain inventory with stat values
   */
  public static Inventory openPlayerStatsCategoryPage(Player user, String requestedPlayerName,
                                                      String categoryName, int pageRequest) {
    PlayerStatsData playerStatsData = PluginData.playerStatsData;

    Inventory inv = createInventory(user, requestedPlayerName);
    switch (categoryName) {
      case "Entity Types",
          "Materials" -> loadPlayerSubstatPage(user, categoryName, pageRequest, playerStatsData, inv);
      default -> loadPlayerStatsPage(categoryName, playerStatsData, inv);
    }

    addStatContext(categoryName, inv);
    addOwnerHead(user, inv);
    InventoryPages.addBackButton(inv, 5);
    return inv;
  }

  /**
   * Loads a substat category page from memory.
   *
   * @param user            user
   * @param categoryName    category to view
   * @param pageRequest     page to view
   * @param playerStatsData player stat data
   * @param inv             interacting inventory
   */
  private static void loadPlayerSubstatPage(Player user, String categoryName, int pageRequest,
                                            PlayerStatsData playerStatsData, Inventory inv) {
    int numberOfPages;
    if (categoryName.equals("Entity Types")) {
      numberOfPages = playerStatsData.getNumberOfEntityTypePages();
    } else {
      numberOfPages = playerStatsData.getNumberOfMaterialPages();
    }
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, pageRequest);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace, new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(playerStatsData.getSubstatCategoryPages().get(categoryName).get(pageViewed).getContents());
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
  }

  /**
   * Loads a non-substat category page from memory.
   *
   * @param categoryName    category to view
   * @param playerStatsData player stat data
   * @param inv             interacting inventory
   */
  private static void loadPlayerStatsPage(String categoryName,
                                          PlayerStatsData playerStatsData, Inventory inv) {
    inv.setContents(playerStatsData.getStatCategoryPages().get(categoryName).getContents());
  }

  /**
   * Adds a help context to the PlayerStatsMain inventory.
   *
   * @param inv interacting inventory
   */
  private static void addStatContext(String categoryName, Inventory inv) {
    List<String> helpLore;
    if (categoryName.equals("categories")) {
      helpLore = List.of(ChatColor.WHITE + "Stat Categories");
    } else {
      helpLore = Arrays.asList(
          ChatColor.WHITE + categoryName,
          ChatColor.WHITE + "Shift-click any",
          ChatColor.WHITE + "stat to share it.");
    }

    inv.setItem(3, ItemCreator.createPluginPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds the currently viewing stat owner's head.
   *
   * @param user user
   * @param inv  interacting inventory
   */
  private static void addOwnerHead(Player user, Inventory inv) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    String statOwner = user.getMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace).get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    meta.setOwningPlayer(requestedPlayer);
    meta.setDisplayName(ChatColor.DARK_PURPLE + statOwner);
    item.setItemMeta(meta);

    inv.setItem(4, item);
  }
}
