package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.commands.playerstats.objects.PlayerStatsCategory;
import me.dannynguyen.aethel.commands.playerstats.objects.PlayerStatsValues;
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
 * @version 1.7.8
 * @since 1.4.8
 */
public class PlayerStatsData {
  private final ArrayList<String> statCategoryNames = new ArrayList<>(Arrays.asList(
      "Activities", "Containers", "Damage", "Entity Types",
      "General", "Interactions", "Materials", "Movement"));

  private final HashMap<String, Inventory> statCategoryPages = new HashMap<>();
  private final HashMap<String, ArrayList<Inventory>> substatCategoryPages = new HashMap<>() {{
    put("Materials", new ArrayList<>());
    put("Entity Types", new ArrayList<>());
  }};

  private int numberOfMaterialPages = 0;
  private int numberOfEntityTypePages = 0;

  private final ArrayList<PlayerStatsValues> pastStatsValues = new ArrayList<>();

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
    for (PlayerStatsCategory playerStatsCategory : getPlayerStatsCategories()) {
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
    ArrayList<Material> materials = new ArrayList<>();
    for (Material material : Material.values()) {
      if (material.isItem() && !material.isAir()) {
        materials.add(material);
      }
    }
    Comparator<Material> materialComparator = Comparator.comparing(Enum::name);
    materials.sort(materialComparator);

    int numberOfMaterials = materials.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfMaterials);
    setNumberOfMaterialPages(numberOfPages);

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
    HashMap<String, ArrayList<Inventory>> substatCategoryPages = getSubstatCategoryPages();

    List<EntityType> entityTypes = Arrays.asList(EntityType.values());
    Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
    entityTypes.sort(entityTypeComparator);

    int numberOfEntityTypes = entityTypes.size();
    int numberOfPages = InventoryPages.calculateNumberOfPages(numberOfEntityTypes);
    setNumberOfEntityTypePages(numberOfPages);

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
   * Categorizes player stats.
   *
   * @return player stat categories
   */
  private ArrayList<PlayerStatsCategory> getPlayerStatsCategories() {
    return new ArrayList<>(Arrays.asList(
        new PlayerStatsCategory("Activities",
            new ArrayList<>(Arrays.asList("ANIMALS_BRED", "ARMOR_CLEANED", "BANNER_CLEANED",
                "BELL_RING", "CAKE_SLICES_EATEN", "CAULDRON_FILLED", "CAULDRON_USED",
                "CLEAN_SHULKER_BOX", "DROP_COUNT", "FISH_CAUGHT", "FLOWER_POTTED", "ITEM_ENCHANTED",
                "NOTEBLOCK_PLAYED", "NOTEBLOCK_TUNED", "RAID_TRIGGER", "RAID_WIN", "RECORD_PLAYED",
                "SLEEP_IN_BED", "TALKED_TO_VILLAGER", "TARGET_HIT", "TRADED_WITH_VILLAGER"))),
        new PlayerStatsCategory("Containers",
            new ArrayList<>(Arrays.asList("CHEST_OPENED", "DISPENSER_INSPECTED",
                "DROPPER_INSPECTED", "ENDERCHEST_OPENED", "HOPPER_INSPECTED",
                "OPEN_BARREL", "SHULKER_BOX_OPENED", "TRAPPED_CHEST_TRIGGERED"))),
        new PlayerStatsCategory("Damage",
            new ArrayList<>(Arrays.asList("DAMAGE_ABSORBED", "DAMAGE_BLOCKED_BY_SHIELD", "DAMAGE_DEALT",
                "DAMAGE_DEALT_ABSORBED", "DAMAGE_DEALT_RESISTED", "DAMAGE_RESISTED", "DAMAGE_TAKEN"))),
        new PlayerStatsCategory("General",
            new ArrayList<>(Arrays.asList("DEATHS", "LEAVE_GAME", "PLAY_ONE_MINUTE",
                "TIME_SINCE_DEATH", "TIME_SINCE_REST", "TOTAL_WORLD_TIME"))),
        new PlayerStatsCategory("Movement",
            new ArrayList<>(Arrays.asList("AVIATE_ONE_CM", "BOAT_ONE_CM", "CLIMB_ONE_CM", "CROUCH_ONE_CM",
                "FALL_ONE_CM", "FLY_ONE_CM", "HORSE_ONE_CM", "JUMP", "MINECART_ONE_CM", "PIG_ONE_CM",
                "SNEAK_TIME", "SPRINT_ONE_CM", "STRIDER_ONE_CM", "SWIM_ONE_CM", "WALK_ON_WATER_ONE_CM",
                "WALK_ONE_CM", "WALK_UNDER_WATER_ONE_CM"))),
        new PlayerStatsCategory("Interactions",
            new ArrayList<>(Arrays.asList("BEACON_INTERACTION", "BREWINGSTAND_INTERACTION",
                "CRAFTING_TABLE_INTERACTION", "FURNACE_INTERACTION", "INTERACT_WITH_ANVIL",
                "INTERACT_WITH_BLAST_FURNACE", "INTERACT_WITH_CAMPFIRE", "INTERACT_WITH_CARTOGRAPHY_TABLE",
                "INTERACT_WITH_GRINDSTONE", "INTERACT_WITH_LECTERN", "INTERACT_WITH_LOOM",
                "INTERACT_WITH_SMITHING_TABLE", "INTERACT_WITH_SMOKER", "INTERACT_WITH_STONECUTTER")))));
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

  public ArrayList<String> getStatCategoryNames() {
    return this.statCategoryNames;
  }

  public HashMap<String, Inventory> getStatCategoryPages() {
    return this.statCategoryPages;
  }

  public HashMap<String, ArrayList<Inventory>> getSubstatCategoryPages() {
    return this.substatCategoryPages;
  }

  public int getNumberOfMaterialPages() {
    return this.numberOfMaterialPages;
  }

  public int getNumberOfEntityTypePages() {
    return this.numberOfEntityTypePages;
  }

  public ArrayList<PlayerStatsValues> getPastStatsValues() {
    return this.pastStatsValues;
  }

  public void setNumberOfMaterialPages(int numberOfMaterialPages) {
    this.numberOfMaterialPages = numberOfMaterialPages;
  }

  public void setNumberOfEntityTypePages(int numberOfEntityTypePages) {
    this.numberOfEntityTypePages = numberOfEntityTypePages;
  }
}
