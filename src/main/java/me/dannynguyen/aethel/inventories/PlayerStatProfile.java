package me.dannynguyen.aethel.inventories;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
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
 * PlayerStatProfile is an inventory under the PlayerStat
 * command that contains all of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.4.8
 * @since 1.4.7
 */
public class PlayerStatProfile {
  /**
   * Creates and names a PlayerStatProfile inventory.
   *
   * @param player interacting player
   * @return PlayerStatProfile inventory
   */
  private Inventory createInventory(Player player) {
    Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
        + ChatColor.DARK_PURPLE + player.getDisplayName());
    return inv;
  }

  /**
   * Creates and names a PlayerStatProfile inventory to the requested player.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @return PlayerStatProfile inventory
   */
  private Inventory createInventory(Player player, String requestedPlayerName) {
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(requestedPlayerName);
    if (requestedPlayer.hasPlayedBefore()) {
      Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + "PlayerStat "
          + ChatColor.DARK_PURPLE + requestedPlayer.getName());
      return inv;
    } else {
      player.sendMessage(ChatColor.RED + requestedPlayer.getName() + "has never played on this server.");
      return createInventory(player);
    }
  }

  /**
   * Loads a stat page from memory.
   *
   * @param player              interacting player
   * @param requestedPlayerName requested player's name
   * @param pageRequest         page to view
   * @return PlayerStat inventory with stats
   */
  public Inventory openStatPage(Player player, String requestedPlayerName, int pageRequest) {
    Inventory inv;
    if (requestedPlayerName == null) {
      inv = createInventory(player);
    } else {
      inv = createInventory(player, requestedPlayerName);
    }

    AethelResources resources = AethelPlugin.getInstance().getResources();

    int numberOfPages = resources.getPlayerStatsData().getNumberOfPages();
    int pageViewed;
    if (numberOfPages != 0) {
      pageViewed = new PageCalculator().calculatePageViewed(pageRequest, numberOfPages);
      inv.setContents(resources.getPlayerStatsData().getStatPages().get(pageViewed).getContents());
      addPaginationButtons(inv, pageViewed, numberOfPages);
    } else {
      pageViewed = 0;
    }

    addProfileHelp(inv);
    addPlayerHead(player, inv);

    player.setMetadata("page", new FixedMetadataValue(AethelPlugin.getInstance(), pageViewed));
    return inv;
  }

  /**
   * Adds a help context to the PlayerStatProfile inventory.
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
   * Adds the currently viewing PlayerStatProfile owner's head.
   *
   * @param player interacting player
   * @param inv    interacting inventory
   */
  private void addPlayerHead(Player player, Inventory inv) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getName()));
    meta.setDisplayName(ChatColor.DARK_PURPLE + player.getDisplayName());
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
