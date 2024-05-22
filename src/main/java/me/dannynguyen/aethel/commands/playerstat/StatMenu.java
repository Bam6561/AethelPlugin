package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.PlayerHead;
import me.dannynguyen.aethel.interfaces.CategoryMenu;
import me.dannynguyen.aethel.utils.InventoryPages;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.item.ItemCreator;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a menu that supports categorical pagination of a player's statistics.
 * <p>
 * See {@link StatCategory.StatisticType} and {@link StatCategory.SubstatisticType}.
 *
 * @author Danny Nguyen
 * @version 1.25.12
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
    addOwner();
    addContext();
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
      case "Entity Types", "Materials" -> getSubstatPage(category, requestedPage);
      default -> getStatsPage(category);
    }
    addOwner();
    addLeaderboardToggle();
    addShareVisibilityToggle();
    InventoryPages.addBackButton(menu, 7);
    return menu;
  }

  /**
   * Sets the menu to load a {@link StatCategory.SubstatisticType} page.
   *
   * @param requestedCategory requested category
   * @param requestedPage     requested page
   */
  private void getSubstatPage(String requestedCategory, int requestedPage) {
    List<Inventory> category = StatCategory.getSubstatCategories().get(StatCategory.SubstatisticType.valueOf(TextFormatter.formatEnum(requestedCategory)));
    int numberOfPages = category.size();
    int pageViewed = InventoryPages.getPageViewed(numberOfPages, requestedPage);
    Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setPage(pageViewed);

    menu.setContents(category.get(pageViewed).getContents());
    InventoryPages.addPagination(menu, numberOfPages, pageViewed);
  }

  /**
   * Sets the menu to load a {@link StatCategory.StatisticType} page.
   *
   * @param requestedCategory requested category
   */
  private void getStatsPage(String requestedCategory) {
    menu.setContents(StatCategory.getStatCategories().get(StatCategory.StatisticType.valueOf(TextFormatter.formatEnum(requestedCategory))).getContents());
  }

  /**
   * Adds contextual help.
   */
  private void addContext() {
    menu.setItem(1, ItemCreator.createPluginPlayerHead(PlayerHead.QUESTION_MARK_WHITE.getHead(), ChatColor.GREEN + "Help", List.of(ChatColor.WHITE + "Stat Categories")));
  }

  /**
   * Adds the stat owner's head.
   */
  private void addOwner() {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    OfflinePlayer owner = Bukkit.getOfflinePlayer(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getTarget());
    String statOwner = owner.getName();

    meta.setOwningPlayer(owner);
    meta.setDisplayName(ChatColor.DARK_PURPLE + statOwner);
    item.setItemMeta(meta);
    menu.setItem(4, item);
  }

  /**
   * Adds stat sharing leaderboard toggle button.
   */
  private void addLeaderboardToggle() {
    menu.setItem(2, ItemCreator.createItem(Material.IRON_INGOT, ChatColor.AQUA + "Personal"));
  }

  /**
   * Adds stat sharing visibility toggle button.
   */
  private void addShareVisibilityToggle() {
    menu.setItem(3, ItemCreator.createItem(Material.BOOK, ChatColor.AQUA + "View Stat"));
  }

  /**
   * Adds stat categories.
   */
  private void addCategories() {
    int i = 9;
    for (StatCategory.StatisticType category : StatCategory.StatisticType.values()) {
      menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category.getProperName()));
      i++;
    }
    for (StatCategory.SubstatisticType category : StatCategory.SubstatisticType.values()) {
      menu.setItem(i, ItemCreator.createItem(Material.BOOK, ChatColor.WHITE + category.getProperName()));
      i++;
    }
  }

  /**
   * Represents player statistic categories.
   *
   * @author Danny Nguyen
   * @version 1.17.14
   * @since 1.4.8
   */
  private static class StatCategory {
    /**
     * {@link StatisticType Stat categories} represented by groups of inventories.
     * <p>
     * An inventory from any of the groups is also referred to as a page.
     */
    private static final Map<StatisticType, Inventory> statCategories = createStatCategoryPages();

    /**
     * {@link SubstatisticType Substat categories} represented by groups of inventories.
     * <p>
     * An inventory from any of the groups is also referred to as a page.
     */
    private static final Map<SubstatisticType, List<Inventory>> substatCategories = createSubstatCategoryPages();

    /**
     * Utility methods only.
     */
    private StatCategory() {
    }

    /**
     * Creates pages of {@link StatisticType stat categories}.
     *
     * @return pages of {@link StatisticType stat categories}
     */
    private static Map<StatisticType, Inventory> createStatCategoryPages() {
      Map<StatisticType, Inventory> statCategories = new HashMap<>();
      for (StatisticType statisticType : StatisticType.values()) {
        int i = 9;
        Inventory inv = Bukkit.createInventory(null, 54);
        for (Statistic stat : statisticType.getStatistics()) {
          inv.setItem(i, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + TextFormatter.capitalizePhrase(stat.name())));
          i++;
          statCategories.put(statisticType, inv);
        }
      }
      return statCategories;
    }

    /**
     * Creates pages of {@link SubstatisticType substat categories}.
     *
     * @return pages of {@link SubstatisticType substat categories}
     */
    private static Map<SubstatisticType, List<Inventory>> createSubstatCategoryPages() {
      Map<SubstatisticType, List<Inventory>> substatCategories = new HashMap<>(Map.of(SubstatisticType.MATERIALS, new ArrayList<>(), SubstatisticType.ENTITY_TYPES, new ArrayList<>()));
      createMaterialPages(substatCategories);
      createEntityTypePages(substatCategories);
      return substatCategories;
    }

    /**
     * Creates pages of {@link SubstatisticType#MATERIALS}.
     *
     * @param substatCategories map to add pages to
     */
    private static void createMaterialPages(Map<SubstatisticType, List<Inventory>> substatCategories) {
      List<Material> materials = sortMaterials();
      int numberOfMaterials = materials.size();
      int numberOfPages = InventoryPages.getTotalPages(numberOfMaterials);

      int startIndex = 0;
      int endIndex = 45;

      for (int page = 0; page < numberOfPages; page++) {
        Inventory inv = Bukkit.createInventory(null, 54);

        int invSlot = 9;
        for (int statIndex = startIndex; statIndex < endIndex; statIndex++) {
          String material = materials.get(statIndex).name();
          String materialName = TextFormatter.capitalizePhrase(material);
          inv.setItem(invSlot, ItemCreator.createItem(Material.valueOf(material), ChatColor.WHITE + materialName));
          invSlot++;
        }
        substatCategories.get(SubstatisticType.MATERIALS).add(inv);

        // Indices to use for the next page (if it exists)
        startIndex += 45;
        endIndex = Math.min(numberOfMaterials, endIndex + 45);
      }
    }

    /**
     * Creates pages of {@link SubstatisticType#ENTITY_TYPES}.
     *
     * @param substatCategories map to add pages to
     */
    private static void createEntityTypePages(Map<SubstatisticType, List<Inventory>> substatCategories) {
      List<EntityType> entityTypes = sortEntityTypes();
      int numberOfEntityTypes = entityTypes.size();
      int numberOfPages = InventoryPages.getTotalPages(numberOfEntityTypes);

      int startIndex = 0;
      int endIndex = 45;

      for (int page = 0; page < numberOfPages; page++) {
        Inventory inv = Bukkit.createInventory(null, 54);
        // i = stat index
        // j = inventory slot index

        // Stats begin on the second row
        int j = 9;
        for (int i = startIndex; i < endIndex; i++) {
          String entity = TextFormatter.capitalizePhrase(entityTypes.get(i).name());
          inv.setItem(j, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + entity));
          j++;
        }
        substatCategories.get(SubstatisticType.ENTITY_TYPES).add(inv);

        // Indices to use for the next page (if it exists)
        startIndex += 45;
        endIndex = Math.min(numberOfEntityTypes, endIndex + 45);
      }
    }

    /**
     * Sorts materials by name.
     *
     * @return sorted materials
     */
    private static List<Material> sortMaterials() {
      List<Material> materials = new ArrayList<>();
      for (Material material : Material.values()) {
        if (material.isItem() && !material.isAir()) {
          materials.add(material);
        }
      }
      Comparator<Material> materialComparator = Comparator.comparing(Enum::name);
      materials.sort(materialComparator);
      return materials;
    }

    /**
     * Sorts entity types.
     *
     * @return sorted entity types
     */
    private static List<EntityType> sortEntityTypes() {
      List<EntityType> entityTypes = new ArrayList<>(List.of(EntityType.values()));
      Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
      entityTypes.sort(entityTypeComparator);
      return entityTypes;
    }

    /**
     * Gets {@link StatisticType stat categories}.
     *
     * @return {@link StatisticType stat categories}
     */
    @NotNull
    private static Map<StatisticType, Inventory> getStatCategories() {
      return statCategories;
    }

    /**
     * Gets {@link SubstatisticType substat categories}.
     *
     * @return {@link SubstatisticType substat categories}
     */
    @NotNull
    private static Map<SubstatisticType, List<Inventory>> getSubstatCategories() {
      return substatCategories;
    }

    /**
     * Types of player statistics.
     *
     * @author Danny Nguyen
     * @version 1.17.14
     * @since 1.17.14
     */
    private enum StatisticType {
      /**
       * Player activities.
       */
      ACTIVITIES("Activities", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.ANIMALS_BRED, org.bukkit.Statistic.ARMOR_CLEANED, org.bukkit.Statistic.BANNER_CLEANED,
          org.bukkit.Statistic.BELL_RING, org.bukkit.Statistic.CAKE_SLICES_EATEN, org.bukkit.Statistic.CAULDRON_FILLED,
          org.bukkit.Statistic.CAULDRON_USED, org.bukkit.Statistic.CLEAN_SHULKER_BOX, org.bukkit.Statistic.DROP_COUNT,
          org.bukkit.Statistic.FISH_CAUGHT, org.bukkit.Statistic.FLOWER_POTTED, org.bukkit.Statistic.ITEM_ENCHANTED,
          org.bukkit.Statistic.NOTEBLOCK_PLAYED, org.bukkit.Statistic.NOTEBLOCK_TUNED, org.bukkit.Statistic.RAID_TRIGGER,
          org.bukkit.Statistic.RAID_WIN, org.bukkit.Statistic.RECORD_PLAYED, org.bukkit.Statistic.SLEEP_IN_BED,
          org.bukkit.Statistic.TALKED_TO_VILLAGER, org.bukkit.Statistic.TARGET_HIT, org.bukkit.Statistic.TRADED_WITH_VILLAGER}),

      /**
       * Container interactions.
       */
      CONTAINERS("Containers", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.CHEST_OPENED, org.bukkit.Statistic.DISPENSER_INSPECTED, org.bukkit.Statistic.DROPPER_INSPECTED,
          org.bukkit.Statistic.ENDERCHEST_OPENED, org.bukkit.Statistic.HOPPER_INSPECTED, org.bukkit.Statistic.OPEN_BARREL,
          org.bukkit.Statistic.SHULKER_BOX_OPENED, org.bukkit.Statistic.TRAPPED_CHEST_TRIGGERED}),

      /**
       * Damage interactions.
       */
      DAMAGE("Damage", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.DAMAGE_ABSORBED, org.bukkit.Statistic.DAMAGE_BLOCKED_BY_SHIELD, org.bukkit.Statistic.DAMAGE_DEALT,
          org.bukkit.Statistic.DAMAGE_DEALT_ABSORBED, org.bukkit.Statistic.DAMAGE_DEALT_RESISTED,
          org.bukkit.Statistic.DAMAGE_RESISTED, org.bukkit.Statistic.DAMAGE_TAKEN}),

      /**
       * General gameplay.
       */
      GENERAL("General", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.DEATHS, org.bukkit.Statistic.LEAVE_GAME, org.bukkit.Statistic.PLAY_ONE_MINUTE,
          org.bukkit.Statistic.TIME_SINCE_DEATH, org.bukkit.Statistic.TIME_SINCE_REST, org.bukkit.Statistic.TOTAL_WORLD_TIME}),

      /**
       * Block interactions.
       */
      INTERACTIONS("Interactions", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.BEACON_INTERACTION, org.bukkit.Statistic.BREWINGSTAND_INTERACTION,
          org.bukkit.Statistic.CRAFTING_TABLE_INTERACTION, org.bukkit.Statistic.FURNACE_INTERACTION,
          org.bukkit.Statistic.INTERACT_WITH_ANVIL, org.bukkit.Statistic.INTERACT_WITH_BLAST_FURNACE,
          org.bukkit.Statistic.INTERACT_WITH_CAMPFIRE, org.bukkit.Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE,
          org.bukkit.Statistic.INTERACT_WITH_GRINDSTONE, org.bukkit.Statistic.INTERACT_WITH_LECTERN,
          org.bukkit.Statistic.INTERACT_WITH_LOOM, org.bukkit.Statistic.INTERACT_WITH_SMITHING_TABLE,
          org.bukkit.Statistic.INTERACT_WITH_SMOKER, org.bukkit.Statistic.INTERACT_WITH_STONECUTTER}),

      /**
       * Player movement.
       */
      MOVEMENT("Movement", new org.bukkit.Statistic[]{
          org.bukkit.Statistic.AVIATE_ONE_CM, org.bukkit.Statistic.BOAT_ONE_CM, org.bukkit.Statistic.CLIMB_ONE_CM,
          org.bukkit.Statistic.CROUCH_ONE_CM, org.bukkit.Statistic.FALL_ONE_CM, org.bukkit.Statistic.FLY_ONE_CM,
          org.bukkit.Statistic.HORSE_ONE_CM, org.bukkit.Statistic.JUMP, org.bukkit.Statistic.MINECART_ONE_CM,
          org.bukkit.Statistic.PIG_ONE_CM, org.bukkit.Statistic.SNEAK_TIME, org.bukkit.Statistic.SPRINT_ONE_CM,
          org.bukkit.Statistic.STRIDER_ONE_CM, org.bukkit.Statistic.SWIM_ONE_CM, org.bukkit.Statistic.WALK_ON_WATER_ONE_CM,
          org.bukkit.Statistic.WALK_ONE_CM, org.bukkit.Statistic.WALK_UNDER_WATER_ONE_CM});

      /**
       * Type's proper name.
       */
      private final String properName;

      /**
       * Type's statistics.
       */
      private final org.bukkit.Statistic[] statistics;

      /***
       * Associates the type with a proper name.
       * @param properName proper name
       * @param statistics statistics
       */
      StatisticType(String properName, org.bukkit.Statistic[] statistics) {
        this.properName = properName;
        this.statistics = statistics;
      }

      /**
       * Gets the type's proper name.
       *
       * @return type's proper name
       */
      @NotNull
      private String getProperName() {
        return this.properName;
      }

      /**
       * Gets the type's statistics.
       *
       * @return type's statistics
       */
      @NotNull
      private org.bukkit.Statistic[] getStatistics() {
        return this.statistics;
      }
    }

    /**
     * Types of substatistics.
     *
     * @author Danny Nguyen
     * @version 1.17.14
     * @since 1.17.14
     */
    private enum SubstatisticType {
      /**
       * Entity types.
       */
      ENTITY_TYPES("Entity Types"),

      /**
       * Material types.
       */
      MATERIALS("Materials");

      /**
       * Type's proper name.
       */
      private final String properName;

      /***
       * Associates the type with a proper name.
       * @param properName proper name
       */
      SubstatisticType(String properName) {
        this.properName = properName;
      }

      /**
       * Gets the type's proper name.
       *
       * @return type's proper name
       */
      @NotNull
      private String getProperName() {
        return this.properName;
      }
    }
  }
}
