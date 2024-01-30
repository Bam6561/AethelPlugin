package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsCategory;
import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsValues;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * PlayerStatsData stores player statistic names in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.8
 */
public class PlayerStatsData {
  private final PlayerStatsCategory[] playerStatsCategories = new PlayerStatsCategory[]{
      new PlayerStatsCategory("Activities", new String[]{
          "ANIMALS_BRED", "ARMOR_CLEANED", "BANNER_CLEANED", "BELL_RING",
          "CAKE_SLICES_EATEN", "CAULDRON_FILLED", "CAULDRON_USED", "CLEAN_SHULKER_BOX",
          "DROP_COUNT", "FISH_CAUGHT", "FLOWER_POTTED", "ITEM_ENCHANTED", "NOTEBLOCK_PLAYED",
          "NOTEBLOCK_TUNED", "RAID_TRIGGER", "RAID_WIN", "RECORD_PLAYED", "SLEEP_IN_BED",
          "TALKED_TO_VILLAGER", "TARGET_HIT", "TRADED_WITH_VILLAGER"}),
      new PlayerStatsCategory("Containers", new String[]{
          "CHEST_OPENED", "DISPENSER_INSPECTED", "DROPPER_INSPECTED", "ENDERCHEST_OPENED",
          "HOPPER_INSPECTED", "OPEN_BARREL", "SHULKER_BOX_OPENED", "TRAPPED_CHEST_TRIGGERED"}),
      new PlayerStatsCategory("Damage", new String[]{
          "DAMAGE_ABSORBED", "DAMAGE_BLOCKED_BY_SHIELD", "DAMAGE_DEALT", "DAMAGE_DEALT_ABSORBED",
          "DAMAGE_DEALT_RESISTED", "DAMAGE_RESISTED", "DAMAGE_TAKEN"}),
      new PlayerStatsCategory("General", new String[]{
          "DEATHS", "LEAVE_GAME", "PLAY_ONE_MINUTE", "TIME_SINCE_DEATH", "TIME_SINCE_REST", "TOTAL_WORLD_TIME"}),
      new PlayerStatsCategory("Movement", new String[]{
          "AVIATE_ONE_CM", "BOAT_ONE_CM", "CLIMB_ONE_CM", "CROUCH_ONE_CM", "FALL_ONE_CM", "FLY_ONE_CM",
          "HORSE_ONE_CM", "JUMP", "MINECART_ONE_CM", "PIG_ONE_CM", "SNEAK_TIME", "SPRINT_ONE_CM",
          "STRIDER_ONE_CM", "SWIM_ONE_CM", "WALK_ON_WATER_ONE_CM", "WALK_ONE_CM", "WALK_UNDER_WATER_ONE_CM"}),
      new PlayerStatsCategory("Interactions", new String[]{
          "BEACON_INTERACTION", "BREWINGSTAND_INTERACTION", "CRAFTING_TABLE_INTERACTION",
          "FURNACE_INTERACTION", "INTERACT_WITH_ANVIL", "INTERACT_WITH_BLAST_FURNACE",
          "INTERACT_WITH_CAMPFIRE", "INTERACT_WITH_CARTOGRAPHY_TABLE", "INTERACT_WITH_GRINDSTONE",
          "INTERACT_WITH_LECTERN", "INTERACT_WITH_LOOM", "INTERACT_WITH_SMITHING_TABLE",
          "INTERACT_WITH_SMOKER", "INTERACT_WITH_STONECUTTER"})};

  private final Map<String, Inventory> statCategoryPages = new HashMap<>();
  private final Map<String, List<Inventory>> substatCategoryPages = new HashMap<>(Map.of(
      "Materials", new ArrayList<>(),
      "Entity Types", new ArrayList<>()));

  private final List<EntityType> sortedEntityTypes = sortEntityTypes();
  private final List<Material> sortedMaterials = sortMaterials();
  private int numberOfMaterialPages = 0;
  private int numberOfEntityTypePages = 0;

  private final List<PlayerStatsValues> pastStatsValues = new ArrayList<>();

  /**
   * Loads player stat pages into memory.
   */
  public void loadStats() {
    createStatCategoryPages();
    createMaterialPages();
    createEntityTypeStatPages();
  }

  /**
   * Creates pages of non-substats by category.
   */
  private void createStatCategoryPages() {
    for (PlayerStatsCategory playerStatsCategory : playerStatsCategories) {
      int i = 9;
      Inventory inv = Bukkit.createInventory(null, 54, "PlayerStats Category Page");
      for (String statName : playerStatsCategory.getStats()) {
        inv.setItem(i, ItemCreator.createItem(Material.PAPER,
            ChatColor.WHITE + TextFormatter.capitalizePhrase(statName)));
        i++;
      }
      statCategoryPages.put(playerStatsCategory.getName(), inv);
    }
  }

  /**
   * Creates pages of materials.
   */
  private void createMaterialPages() {
    List<Material> materials = sortedMaterials;
    int numberOfMaterials = materials.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfMaterials);
    numberOfMaterialPages = numberOfPages;

    int startIndex = 0;
    int endIndex = 45;

    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Player Statistic Material Page");

      int invSlot = 9;
      for (int statIndex = startIndex; statIndex < endIndex; statIndex++) {
        String materialName = materials.get(statIndex).name();
        String materialDisplayName = TextFormatter.capitalizePhrase(materialName);
        inv.setItem(invSlot, ItemCreator.createItem(
            Material.valueOf(materialName), ChatColor.WHITE + materialDisplayName));
        invSlot++;
      }
      substatCategoryPages.get("Materials").add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfMaterials, endIndex + 45);
    }
  }

  /**
   * Creates pages of entities.
   */
  private void createEntityTypeStatPages() {
    List<EntityType> entityTypes = sortedEntityTypes;
    int numberOfEntityTypes = entityTypes.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfEntityTypes);
    numberOfEntityTypePages = numberOfPages;

    int startIndex = 0;
    int endIndex = 45;

    for (int page = 0; page < numberOfPages; page++) {
      Inventory inv = Bukkit.createInventory(null, 54, "Player Statistic Entity Types Page");
      // i = stat index
      // j = inventory slot index

      // Stats begin on the second row
      int j = 9;
      for (int i = startIndex; i < endIndex; i++) {
        String entityName = TextFormatter.capitalizePhrase(entityTypes.get(i).name());
        inv.setItem(j, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + entityName));
        j++;
      }
      substatCategoryPages.get("Entity Types").add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfEntityTypes, endIndex + 45);
    }
  }

  /**
   * Ensures the number of past stats values never exceeds 9 (the PlayerStatsPast inventory size).
   *
   * @param statName stat name
   * @param stats    associated statistics
   */
  public void addToPastStats(String statName, List<String> stats) {
    if (pastStatsValues.size() == 9) {
      pastStatsValues.remove(0);
    }
    pastStatsValues.add(new PlayerStatsValues(statName, stats));
  }

  /**
   * Sorts entity types.
   *
   * @return sorted entity types
   */
  private List<EntityType> sortEntityTypes() {
    List<EntityType> entityTypes = Arrays.asList(EntityType.values());
    Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
    entityTypes.sort(entityTypeComparator);
    return entityTypes;
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

  public Map<String, Inventory> getStatCategoryPages() {
    return this.statCategoryPages;
  }

  public Map<String, List<Inventory>> getSubstatCategoryPages() {
    return this.substatCategoryPages;
  }

  public int getNumberOfMaterialPages() {
    return this.numberOfMaterialPages;
  }

  public int getNumberOfEntityTypePages() {
    return this.numberOfEntityTypePages;
  }

  public List<PlayerStatsValues> getPastStatsValues() {
    return this.pastStatsValues;
  }
}
