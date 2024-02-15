package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a menu that supports categorical pagination of a player's statistics.
 *
 * @author Danny Nguyen
 * @version 1.9.21
 * @since 1.4.7
 */
class PlayerStatMenu {
  /**
   * Stat category names.
   */
  private static final String[] categoryNames = new String[]{
      "Activities", "Containers", "Damage", "Entity Types",
      "General", "Interactions", "Materials", "Movement"};

  /**
   * PlayerStat GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * Owner of the player statistics.
   */
  private final String requestedPlayer;

  /**
   * Associates a new PlayerStat menu with its user.
   *
   * @param user            user
   * @param requestedPlayer requested player's name
   */
  protected PlayerStatMenu(@NotNull Player user, @NotNull String requestedPlayer) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.requestedPlayer = Objects.requireNonNull(requestedPlayer, "Null requested user");
    this.menu = createMenu();
  }

  /**
   * Creates and names a PlayerStat menu with its target player.
   *
   * @return PlayerStat menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "PlayerStat " + ChatColor.DARK_PURPLE + requestedPlayer);
  }

  /**
   * Sets the menu to view stat categories.
   *
   * @return PlayerStat main menu
   */
  @NotNull
  protected Inventory openMainMenu() {
    addCategories();
    addContext(null);
    addStatOwnerHead();
    return menu;
  }

  /**
   * Sets the menu to load stat category page.
   *
   * @param categoryName  category to view
   * @param requestedPage requested page
   * @return PlayerStat category page
   */
  @NotNull
  protected Inventory openCategoryPage(String categoryName, int requestedPage) {
    switch (categoryName) {
      case "Entity Types", "Materials" -> loadSubstatPage(categoryName, requestedPage);
      default -> loadStatsPage(categoryName);
    }
    addContext(categoryName);
    addStatOwnerHead();
    InventoryPages.addBackButton(menu, 5);
    return menu;
  }

  /**
   * Sets the menu to load a substat category page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   */
  private void loadSubstatPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = PluginData.playerStatData.getSubstatCategories().get(requestedCategory);
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    user.setMetadata(PluginEnum.PlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), pageViewed));

    menu.setContents(category.get(pageViewed).getContents());
    InventoryPages.addPageButtons(menu, numberOfPages, pageViewed);
  }

  /**
   * Sets the menu to load a non-stat requestedCategory page.
   *
   * @param requestedCategory requested requestedCategory
   */
  private void loadStatsPage(String requestedCategory) {
    menu.setContents(PluginData.playerStatData.getStatCategories().get(requestedCategory).getContents());
  }

  /**
   * Adds contextual help.
   */
  private void addContext(String categoryName) {
    List<String> lore;
    if (categoryName == null) {
      lore = List.of(ChatColor.WHITE + "Stat Categories");
    } else {
      lore = List.of(
          ChatColor.WHITE + "Shift-click any",
          ChatColor.WHITE + "stat to share it.");
    }
    menu.setItem(3, ItemCreator.createPluginPlayerHead(PluginEnum.PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", lore));
  }

  /**
   * Adds the stat owner's head.
   */
  private void addStatOwnerHead() {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    String statOwner = user.getMetadata(PluginEnum.PlayerMeta.PLAYER.getMeta()).get(0).asString();
    OfflinePlayer requestedPlayer = Bukkit.getOfflinePlayer(statOwner);

    meta.setOwningPlayer(requestedPlayer);
    meta.setDisplayName(ChatColor.DARK_PURPLE + statOwner);
    item.setItemMeta(meta);
    menu.setItem(4, item);
  }

  /**
   * Adds stat categories.
   */
  private void addCategories() {
    int i = 9;
    for (String statCategory : categoryNames) {
      menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + statCategory));
      i++;
    }
  }
}
