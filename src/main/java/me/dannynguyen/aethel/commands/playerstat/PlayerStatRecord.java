package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents player statistic categories.
 *
 * @author Danny Nguyen
 * @version 1.12.4
 * @since 1.4.8
 */
public class PlayerStatRecord {
  /**
   * Player stat categories.
   */
  private final Map<String, String[]> playerStatCategories = new HashMap<>(Map.of(
      "Activities", new String[]{
          "ANIMALS_BRED", "ARMOR_CLEANED", "BANNER_CLEANED", "BELL_RING",
          "CAKE_SLICES_EATEN", "CAULDRON_FILLED", "CAULDRON_USED", "CLEAN_SHULKER_BOX",
          "DROP_COUNT", "FISH_CAUGHT", "FLOWER_POTTED", "ITEM_ENCHANTED", "NOTEBLOCK_PLAYED",
          "NOTEBLOCK_TUNED", "RAID_TRIGGER", "RAID_WIN", "RECORD_PLAYED", "SLEEP_IN_BED",
          "TALKED_TO_VILLAGER", "TARGET_HIT", "TRADED_WITH_VILLAGER"},
      "Containers", new String[]{
          "CHEST_OPENED", "DISPENSER_INSPECTED", "DROPPER_INSPECTED", "ENDERCHEST_OPENED",
          "HOPPER_INSPECTED", "OPEN_BARREL", "SHULKER_BOX_OPENED", "TRAPPED_CHEST_TRIGGERED"},
      "Damage", new String[]{
          "DAMAGE_ABSORBED", "DAMAGE_BLOCKED_BY_SHIELD", "DAMAGE_DEALT", "DAMAGE_DEALT_ABSORBED",
          "DAMAGE_DEALT_RESISTED", "DAMAGE_RESISTED", "DAMAGE_TAKEN"},
      "General", new String[]{
          "DEATHS", "LEAVE_GAME", "PLAY_ONE_MINUTE", "TIME_SINCE_DEATH", "TIME_SINCE_REST", "TOTAL_WORLD_TIME"},
      "Movement", new String[]{
          "AVIATE_ONE_CM", "BOAT_ONE_CM", "CLIMB_ONE_CM", "CROUCH_ONE_CM", "FALL_ONE_CM", "FLY_ONE_CM",
          "HORSE_ONE_CM", "JUMP", "MINECART_ONE_CM", "PIG_ONE_CM", "SNEAK_TIME", "SPRINT_ONE_CM",
          "STRIDER_ONE_CM", "SWIM_ONE_CM", "WALK_ON_WATER_ONE_CM", "WALK_ONE_CM", "WALK_UNDER_WATER_ONE_CM"},
      "Interactions", new String[]{
          "BEACON_INTERACTION", "BREWINGSTAND_INTERACTION", "CRAFTING_TABLE_INTERACTION",
          "FURNACE_INTERACTION", "INTERACT_WITH_ANVIL", "INTERACT_WITH_BLAST_FURNACE",
          "INTERACT_WITH_CAMPFIRE", "INTERACT_WITH_CARTOGRAPHY_TABLE", "INTERACT_WITH_GRINDSTONE",
          "INTERACT_WITH_LECTERN", "INTERACT_WITH_LOOM", "INTERACT_WITH_SMITHING_TABLE",
          "INTERACT_WITH_SMOKER", "INTERACT_WITH_STONECUTTER"}));

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
   * Creates pages of non-substats by category.
   *
   * @return pages of non-substats
   */
  private Map<String, Inventory> createStatCategoryPages() {
    Map<String, Inventory> statCategories = new HashMap<>();
    for (String category : playerStatCategories.keySet()) {
      int i = 9;
      Inventory inv = Bukkit.createInventory(null, 54);
      for (String stat : playerStatCategories.get(category)) {
        inv.setItem(i, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + TextFormatter.capitalizePhrase(stat)));
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
  @NotNull
  protected Map<String, Inventory> getStatCategories() {
    return this.statCategories;
  }

  /**
   * Gets player substatistic categories.
   *
   * @return player substatistic categories
   */
  @NotNull
  protected Map<String, List<Inventory>> getSubstatCategories() {
    return this.substatCategories;
  }
}