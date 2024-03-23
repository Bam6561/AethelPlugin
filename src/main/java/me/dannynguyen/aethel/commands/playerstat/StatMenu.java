package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.PlayerHead;
import me.dannynguyen.aethel.plugin.interfaces.CategoryMenu;
import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.ItemCreator;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a menu that supports categorical pagination of a player's statistics.
 * <p>
 * See {@link StatisticCategory} and {@link SubstatisticCategory}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
 * @since 1.4.7
 */
public class StatMenu implements CategoryMenu {
  /**
   * GUI.
   */
  private final Inventory menu;

  /**
   * GUI user.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Owner of the player statistics.
   */
  private final String owner;

  /**
   * Associates a new PlayerStat menu with its user and target player.
   *
   * @param user  user
   * @param owner requested player's name
   */
  public StatMenu(@NotNull Player user, @NotNull String owner) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.owner = Objects.requireNonNull(owner, "Null owner");
    this.uuid = user.getUniqueId();
    this.menu = createMenu();
  }

  /**
   * Creates and names a PlayerStat menu with its target player.
   *
   * @return PlayerStat menu
   */
  private Inventory createMenu() {
    return Bukkit.createInventory(user, 54, ChatColor.DARK_GRAY + "PlayerStat " + ChatColor.DARK_PURPLE + owner);
  }

  /**
   * Sets the menu to view stat categories.
   *
   * @return PlayerStat main menu
   */
  @NotNull
  public Inventory getMainMenu() {
    addCategories();
    addContext(null);
    addOwner();
    return menu;
  }

  /**
   * Sets the menu to load stat category page.
   *
   * @param category      category to view
   * @param requestedPage requested page
   * @return PlayerStat category page
   */
  @NotNull
  public Inventory getCategoryPage(String category, int requestedPage) {
    switch (category) {
      case "Entity Types", "Materials" -> loadSubstatPage(category, requestedPage);
      default -> loadStatsPage(category);
    }
    addContext(category);
    addOwner();
    InventoryPages.addBackButton(menu, 5);
    return menu;
  }

  /**
   * Sets the menu to load a {@link SubstatisticCategory} page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   */
  private void loadSubstatPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = Plugin.getData().getPlayerStatRecord().getSubstatCategories().get(SubstatisticCategory.valueOf(TextFormatter.formatEnum(requestedCategory)));
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.calculatePageViewed(numberOfPages, requestedPage);
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).setPage(pageViewed);

    menu.setContents(category.get(pageViewed).getContents());
    InventoryPages.addPageButtons(menu, numberOfPages, pageViewed);
  }

  /**
   * Sets the menu to load a {@link StatisticCategory} page.
   *
   * @param requestedCategory requested category
   */
  private void loadStatsPage(String requestedCategory) {
    menu.setContents(Plugin.getData().getPlayerStatRecord().getStatCategories().get(StatisticCategory.valueOf(TextFormatter.formatEnum(requestedCategory))).getContents());
  }

  /**
   * Adds contextual help.
   *
   * @param category category name
   */
  private void addContext(String category) {
    List<String> lore;
    if (category == null) {
      lore = List.of(ChatColor.WHITE + "Stat Categories");
    } else {
      lore = List.of(
          ChatColor.WHITE + "Shift-click any",
          ChatColor.WHITE + "stat to share it.");
    }
    menu.setItem(3, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", lore));
  }

  /**
   * Adds the stat owner's head.
   */
  private void addOwner() {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    OfflinePlayer owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getTarget());
    String statOwner = owner.getName();

    meta.setOwningPlayer(owner);
    meta.setDisplayName(ChatColor.DARK_PURPLE + statOwner);
    item.setItemMeta(meta);
    menu.setItem(4, item);
  }

  /**
   * Adds stat categories.
   */
  private void addCategories() {
    int i = 9;
    for (StatisticCategory category : StatisticCategory.values()) {
      menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category.getProperName()));
      i++;
    }
    for (SubstatisticCategory category : SubstatisticCategory.values()) {
      menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category.getProperName()));
      i++;
    }
  }
}
