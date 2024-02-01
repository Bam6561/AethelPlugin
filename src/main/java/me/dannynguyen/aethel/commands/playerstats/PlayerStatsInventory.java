package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
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

import java.util.List;

/**
 * PlayerStatsInventory is a shared inventory that
 * supports categorical pagination of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.7
 */
public class PlayerStatsInventory {
  /**
   * Creates a PlayerStats main menu containing stat categories.
   *
   * @param user                user
   * @param requestedPlayerName requested player's name
   * @return PlayerStats main menu
   */
  public static Inventory openMainMenu(Player user, String requestedPlayerName) {
    Inventory inv = createInventory(user, requestedPlayerName);
    addCategories(inv);
    addContext("categories", inv);
    addOwnerHead(user, inv);
    return inv;
  }

  /**
   * Creates and names a PlayerStats inventory to the requested player.
   *
   * @param user                user
   * @param requestedPlayerName requested player's name
   * @return PlayerStats inventory
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
  private static void addCategories(Inventory inv) {
    int i = 9;
    for (String statCategory : Array.CATEGORY_NAMES.array) {
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
   * @return PlayerStats category page
   */
  public static Inventory openCategoryPage(Player user, String requestedPlayerName,
                                           String categoryName, int pageRequest) {
    PlayerStatsData playerStatsData = PluginData.playerStatsData;

    Inventory inv = createInventory(user, requestedPlayerName);
    switch (categoryName) {
      case "Entity Types",
          "Materials" -> loadSubstatPage(user, categoryName, pageRequest, playerStatsData, inv);
      default -> loadStatsPage(categoryName, playerStatsData, inv);
    }

    addContext(categoryName, inv);
    addOwnerHead(user, inv);
    InventoryPages.addBackButton(inv, 5);
    return inv;
  }

  /**
   * Loads a substat category page from memory.
   *
   * @param user            user
   * @param category        requested category
   * @param pageRequest     page to view
   * @param playerStatsData player stat data
   * @param inv             interacting inventory
   */
  private static void loadSubstatPage(Player user, String category, int pageRequest,
                                      PlayerStatsData playerStatsData, Inventory inv) {
    int numberOfPages;
    if (category.equals("Entity Types")) {
      numberOfPages = playerStatsData.getNumberOfEntityTypePages();
    } else {
      numberOfPages = playerStatsData.getNumberOfMaterialPages();
    }
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, pageRequest);
    user.setMetadata(PluginPlayerMeta.Namespace.PAGE.namespace,
        new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    inv.setContents(playerStatsData.getSubstatCategoryPages().get(category).get(pageViewed).getContents());
    InventoryPages.addPageButtons(inv, numberOfPages, pageViewed);
  }

  /**
   * Loads a non-substat category page from memory.
   *
   * @param category        requested category
   * @param playerStatsData player stat data
   * @param inv             interacting inventory
   */
  private static void loadStatsPage(String category,
                                    PlayerStatsData playerStatsData, Inventory inv) {
    inv.setContents(playerStatsData.getStatCategoryPages().get(category).getContents());
  }

  /**
   * Adds a help context to the PlayerStats inventory.
   *
   * @param inv interacting inventory
   */
  private static void addContext(String categoryName, Inventory inv) {
    List<String> helpLore;
    if (categoryName.equals("categories")) {
      helpLore = Context.CATEGORIES.context;
    } else {
      helpLore = Context.SHARE_STAT.context;
    }

    inv.setItem(3, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
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

  private enum Array {
    CATEGORY_NAMES(new String[]{
        "Activities", "Containers", "Damage", "Entity Types",
        "General", "Interactions", "Materials", "Movement"});

    public final String[] array;

    Array(String[] array) {
      this.array = array;
    }
  }

  private enum Context {
    CATEGORIES(List.of(ChatColor.WHITE + "Stat Categories")),
    SHARE_STAT(List.of(
        ChatColor.WHITE + "Shift-click any",
        ChatColor.WHITE + "stat to share it."));

    public final List<String> context;

    Context(List<String> context) {
      this.context = context;
    }
  }
}
