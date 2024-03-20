package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.TextFormatter;
import me.dannynguyen.aethel.util.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * Represents player statistic categories.
 *
 * @author Danny Nguyen
 * @version 1.13.6
 * @since 1.4.8
 */
public class PlayerStatRecord {
  /**
   * Player stat categories.
   */
  private static final Map<String, Statistic[]> playerStatCategories = new HashMap<>(Map.of(
      "Activities", new Statistic[]{
          Statistic.ANIMALS_BRED, Statistic.ARMOR_CLEANED, Statistic.BANNER_CLEANED,
          Statistic.BELL_RING, Statistic.CAKE_SLICES_EATEN, Statistic.CAULDRON_FILLED,
          Statistic.CAULDRON_USED, Statistic.CLEAN_SHULKER_BOX, Statistic.DROP_COUNT,
          Statistic.FISH_CAUGHT, Statistic.FLOWER_POTTED, Statistic.ITEM_ENCHANTED,
          Statistic.NOTEBLOCK_PLAYED, Statistic.NOTEBLOCK_TUNED, Statistic.RAID_TRIGGER,
          Statistic.RAID_WIN, Statistic.RECORD_PLAYED, Statistic.SLEEP_IN_BED,
          Statistic.TALKED_TO_VILLAGER, Statistic.TARGET_HIT, Statistic.TRADED_WITH_VILLAGER},
      "Containers", new Statistic[]{
          Statistic.CHEST_OPENED, Statistic.DISPENSER_INSPECTED, Statistic.DROPPER_INSPECTED,
          Statistic.ENDERCHEST_OPENED, Statistic.HOPPER_INSPECTED, Statistic.OPEN_BARREL,
          Statistic.SHULKER_BOX_OPENED, Statistic.TRAPPED_CHEST_TRIGGERED},
      "Damage", new Statistic[]{
          Statistic.DAMAGE_ABSORBED, Statistic.DAMAGE_BLOCKED_BY_SHIELD, Statistic.DAMAGE_DEALT,
          Statistic.DAMAGE_DEALT_ABSORBED, Statistic.DAMAGE_DEALT_RESISTED,
          Statistic.DAMAGE_RESISTED, Statistic.DAMAGE_TAKEN},
      "General", new Statistic[]{
          Statistic.DEATHS, Statistic.LEAVE_GAME, Statistic.PLAY_ONE_MINUTE,
          Statistic.TIME_SINCE_DEATH, Statistic.TIME_SINCE_REST, Statistic.TOTAL_WORLD_TIME},
      "Movement", new Statistic[]{
          Statistic.AVIATE_ONE_CM, Statistic.BOAT_ONE_CM, Statistic.CLIMB_ONE_CM,
          Statistic.CROUCH_ONE_CM, Statistic.FALL_ONE_CM, Statistic.FLY_ONE_CM,
          Statistic.HORSE_ONE_CM, Statistic.JUMP, Statistic.MINECART_ONE_CM,
          Statistic.PIG_ONE_CM, Statistic.SNEAK_TIME, Statistic.SPRINT_ONE_CM,
          Statistic.STRIDER_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.WALK_ON_WATER_ONE_CM,
          Statistic.WALK_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM},
      "Interactions", new Statistic[]{
          Statistic.BEACON_INTERACTION, Statistic.BREWINGSTAND_INTERACTION,
          Statistic.CRAFTING_TABLE_INTERACTION, Statistic.FURNACE_INTERACTION,
          Statistic.INTERACT_WITH_ANVIL, Statistic.INTERACT_WITH_BLAST_FURNACE,
          Statistic.INTERACT_WITH_CAMPFIRE, Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE,
          Statistic.INTERACT_WITH_GRINDSTONE, Statistic.INTERACT_WITH_LECTERN,
          Statistic.INTERACT_WITH_LOOM, Statistic.INTERACT_WITH_SMITHING_TABLE,
          Statistic.INTERACT_WITH_SMOKER, Statistic.INTERACT_WITH_STONECUTTER}));

  /**
   * Player statistic categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   * </p>
   */
  private final Map<String, Inventory> statCategories = createStatCategoryPages();

  /**
   * Player substatistic categories represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   * </p>
   */
  private final Map<String, List<Inventory>> substatCategories = createSubstatCategoryPages();

  /**
   * No parameter constructor.
   */
  public PlayerStatRecord() {
  }

  /**
   * Creates pages of non-substats by category.
   *
   * @return pages of non-substats
   */
  private Map<String, Inventory> createStatCategoryPages() {
    Map<String, Inventory> statCategories = new HashMap<>();
    for (String category : playerStatCategories.keySet()) {
      int i = 9;
      Inventory inv = Bukkit.createInventory(null, 54);
      for (Statistic stat : playerStatCategories.get(category)) {
        inv.setItem(i, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + TextFormatter.capitalizePhrase(stat.name())));
        i++;
      }
      statCategories.put(category, inv);
    }
    return statCategories;
  }

  /**
   * Creates pages of substats by category.
   *
   * @return pages of substats
   */
  private Map<String, List<Inventory>> createSubstatCategoryPages() {
    Map<String, List<Inventory>> substatCategories = new HashMap<>(Map.of("Materials", new ArrayList<>(), "Entity Types", new ArrayList<>()));
    createMaterialPages(substatCategories);
    createEntityTypeStatPages(substatCategories);
    return substatCategories;
  }

  /**
   * Creates pages of materials.
   *
   * @param substatCategories map to add pages to
   */
  private void createMaterialPages(Map<String, List<Inventory>> substatCategories) {
    List<Material> materials = sortMaterials();
    int numberOfMaterials = materials.size();
    int numberOfPages = InventoryPages.calculateTotalPages(numberOfMaterials);

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
      substatCategories.get("Materials").add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfMaterials, endIndex + 45);
    }
  }

  /**
   * Creates pages of entities.
   *
   * @param substatCategories map to add pages to
   */
  private void createEntityTypeStatPages(Map<String, List<Inventory>> substatCategories) {
    List<EntityType> entityTypes = sortEntityTypes();
    int numberOfEntityTypes = entityTypes.size();
    int numberOfPages = InventoryPages.calculateTotalPages(numberOfEntityTypes);

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
      substatCategories.get("Entity Types").add(inv);

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
  private List<Material> sortMaterials() {
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
  private List<EntityType> sortEntityTypes() {
    List<EntityType> entityTypes = new ArrayList<>(List.of(EntityType.values()));
    Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
    entityTypes.sort(entityTypeComparator);
    return entityTypes;
  }

  /**
   * Gets player statistic categories.
   *
   * @return player statistic categories
   */
  protected Map<String, Inventory> getStatCategories() {
    return this.statCategories;
  }

  /**
   * Gets player substatistic categories.
   *
   * @return player substatistic categories
   */
  protected Map<String, List<Inventory>> getSubstatCategories() {
    return this.substatCategories;
  }
}
