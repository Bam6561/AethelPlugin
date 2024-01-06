package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.data.PlayerStatData;
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
 * PlayerStatMain is a shared inventory under the PlayerStat
 * command that contains all of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.9
 * @since 1.4.7
 */
public class PlayerStatMain {
  /**
   * Creates and names a PlayerStatMain inventory to the requested player.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatMain inventory
   */
  private Inventory createInventory(Player player, String requestedPlayerName) {
    Inventory inv;
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
          + ChatColor.DARK_PURPLE + requestedPlayerName);
    } else {
      player.sendMessage(ChatColor.RED + requestedPlayerName + "has never played on this server.");
      inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
          + ChatColor.DARK_PURPLE + player.getName());
    }
    return inv;
  }

  /**
   * Opens a PlayerStatMain page.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatMain inventory with stat categories
   */
  public Inventory openPlayerStatMainPage(Player player, String requestedPlayerName) {
    Inventory inv = createInventory(player, requestedPlayerName);
    addStatCategories(inv);
    addProfileHelp(inv);
    addPlayerHead(player, requestedPlayerName, inv);
    return inv;
  }

  /**
   * Loads a PlayerStat category page from memory.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @param categoryName        stat category
   * @param pageRequest         page to view
   * @return PlayerStatMain inventory with stats
   */
  public Inventory openPlayerStatCategoryPage(Player player, String requestedPlayerName,
                                              String categoryName, int pageRequest) {
    Inventory inv = createInventory(player, requestedPlayerName);

    PlayerStatData playerStatData = AethelPlugin.getInstance().getResources().getPlayerStatData();
    switch (categoryName) {
      case "Entity Types", "Materials" -> {
        int numberOfPages;
        int pageViewed = 0;
        if (categoryName.equals("Entity Types")) {
          numberOfPages = playerStatData.getNumberOfEntityTypePages();
        } else {
          numberOfPages = playerStatData.getNumberOfMaterialPages();
        }
        pageViewed = new PageCalculator().calculatePageViewed(pageRequest, numberOfPages);
        inv.setContents(playerStatData.getSubstatCategoryPages().get(categoryName).get(pageViewed).getContents());
        addPaginationButtons(inv, pageViewed, numberOfPages);
        player.setMetadata("stat-category",
            new FixedMetadataValue(AethelPlugin.getInstance(), categoryName));
        player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));
      }
      default -> {
        inv.setContents(playerStatData.getStatCategoryPages().get(categoryName).getContents());
        player.setMetadata("stat-category",
            new FixedMetadataValue(AethelPlugin.getInstance(), categoryName));
      }
    }

    addProfileHelp(inv);
    addPlayerHead(player, requestedPlayerName, inv);

    return inv;
  }

  /**
   * Adds stat categories.
   *
   * @param inv interacting inventory
   */
  private void addStatCategories(Inventory inv) {
    int i = 9;
    ItemCreator itemCreator = new ItemCreator();
    for (String statCategory : AethelPlugin.getInstance().getResources().
        getPlayerStatData().getStatCategoryNames()) {
      inv.setItem(i, itemCreator.createItem(Material.BOOKSHELF, ChatColor.WHITE + statCategory));
      i++;
    }
  }

  /**
   * Adds a help context to the PlayerStatMain inventory.
   *
   * @param inv interacting inventory
   */
  private void addProfileHelp(Inventory inv) {
    List<String> helpLore = Arrays.asList(
        ChatColor.WHITE + "Placeholder text");

    inv.setItem(3, new ItemCreator().createPlayerHead("White Question Mark",
        ChatColor.GREEN + "Help", helpLore));
  }

  /**
   * Adds the currently viewing PlayerStat owner's head.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @param inv                 interacting inventory
   */
  private void addPlayerHead(Player player, String requestedPlayerName, Inventory inv) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      meta.setOwningPlayer(requestedPlayer);
      meta.setDisplayName(ChatColor.DARK_PURPLE + requestedPlayerName);
    } else {
      meta.setOwningPlayer(player);
      meta.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
    }
    item.setItemMeta(meta);
    inv.setItem(4, item);
  }

  /**
   * Adds previous and next page buttons based on the page number.
   *
   * @param inv           interacting inventory
   * @param pageViewed    page viewed
   * @param numberOfPages number of recipe pages
   */
  private void addPaginationButtons(Inventory inv, int pageViewed, int numberOfPages) {
    if (pageViewed > 0) {
      inv.setItem(0, new ItemCreator().
          createPlayerHead("Red Backward", ChatColor.AQUA + "Previous Page"));
    }
    if (numberOfPages - 1 > pageViewed) {
      inv.setItem(8, new ItemCreator().
          createPlayerHead("Lime Forward", ChatColor.AQUA + "Next Page"));
    }
  }
}
