package me.dannynguyen.aethel.inventories.playerstat;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.PlayerStatData;
import me.dannynguyen.aethel.inventories.Pagination;
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
 * PlayerStatMain is a shared inventory under the PlayerStat command
 * that supports categorical pagination of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.4.7
 */
public class PlayerStatMain {
  /**
   * Creates a PlayerStatMain page containing stat categories.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatMain inventory with stat categories
   */
  public static Inventory openPlayerStatMainPage(Player player, String requestedPlayerName) {
    Inventory inv = createInventory(player, requestedPlayerName);
    addStatCategories(inv);
    addStatContext("categories", inv);
    addOwnerHead(player, inv);
    return inv;
  }

  /**
   * Creates and names a PlayerStatMain inventory to the requested player.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatMain inventory
   */
  private static Inventory createInventory(Player player, String requestedPlayerName) {
    return Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
        + ChatColor.DARK_PURPLE + requestedPlayerName);
  }

  /**
   * Adds stat categories.
   *
   * @param inv interacting inventory
   */
  private static void addStatCategories(Inventory inv) {
    int i = 9;
    for (String statCategory : AethelResources.playerStatData.getStatCategoryNames()) {
      inv.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + statCategory));
      i++;
    }
  }

  /**
   * Loads a stat category page from memory.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @param categoryName        category to view
   * @param pageRequest         page to view
   * @return PlayerStatMain inventory with stat values
   */
  public static Inventory openPlayerStatCategoryPage(Player player, String requestedPlayerName,
                                                     String categoryName, int pageRequest) {
    PlayerStatData playerStatData = AethelResources.playerStatData;

    Inventory inv = createInventory(player, requestedPlayerName);
    switch (categoryName) {
      case "Entity Types",
          "Materials" -> loadPlayerSubstatPage(player, categoryName, pageRequest, playerStatData, inv);
      default -> loadPlayerStatPage(categoryName, playerStatData, inv);
    }

    addStatContext(categoryName, inv);
    addOwnerHead(player, inv);
    addBackButton(inv);
    return inv;
  }

  /**
   * Loads a substat category page from memory.
   *
   * @param player         interacting player
   * @param categoryName   category to view
   * @param pageRequest    page to view
   * @param playerStatData player stat data
   * @param inv            interacting inventory
   */
  private static void loadPlayerSubstatPage(Player player, String categoryName, int pageRequest,
                                            PlayerStatData playerStatData, Inventory inv) {
    int numberOfPages;
    if (categoryName.equals("Entity Types")) {
      numberOfPages = playerStatData.getNumberOfEntityTypePages();
    } else {
      numberOfPages = playerStatData.getNumberOfMaterialPages();
    }
    int pageViewed = Pagination.calculatePageViewed(numberOfPages, pageRequest);
    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));

    inv.setContents(playerStatData.getSubstatCategoryPages().get(categoryName).get(pageViewed).getContents());
    addPageButtons(inv, pageViewed, numberOfPages);
  }

  /**
   * Loads a non-substat category page from memory.
   *
   * @param categoryName   category to view
   * @param playerStatData player stat data
   * @param inv            interacting inventory
   */
  private static void loadPlayerStatPage(String categoryName,
                                         PlayerStatData playerStatData, Inventory inv) {
    inv.setContents(playerStatData.getStatCategoryPages().get(categoryName).getContents());
  }

  /**
   * Adds a help context to the PlayerStatMain inventory.
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

    inv.setItem(3, ItemCreator.createPlayerHead("WHITE_QUESTION_MARK",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds the currently viewing stat owner's head.
   *
   * @param player interacting player
   * @param inv    interacting inventory
   */
  private static void addOwnerHead(Player player, Inventory inv) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    String statOwner = player.getMetadata("stat-owner").get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    meta.setOwningPlayer(requestedPlayer);
    meta.setDisplayName(ChatColor.DARK_PURPLE + statOwner);
    item.setItemMeta(meta);

    inv.setItem(4, item);
  }

  /**
   * Returns the player to the stat categories page.
   *
   * @param inv interacting inventory
   */
  private static void addBackButton(Inventory inv) {
    inv.setItem(5, ItemCreator.createPlayerHead("CHISELED_BOOKSHELF",
        ChatColor.AQUA + "Back"));
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private static void addPageButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, ItemCreator.
          createPlayerHead("RED_BACKWARD", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, ItemCreator.
          createPlayerHead("LIME_FORWARD", ChatColor.AQUA + "Next Page"));
    }
  }
}
