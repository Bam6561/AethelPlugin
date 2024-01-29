package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsCategory;
import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsValues;
import me.dannynguyen.aethel.enums.PluginConstant;
import me.dannynguyen.aethel.utility.InventoryPages;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlayerStatsData stores player statistic names in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.2
 * @since 1.4.8
 */
public class PlayerStatsData {
  private final Map<String, Inventory> statCategoryPages = new HashMap<>();
  private final Map<String, List<Inventory>> substatCategoryPages = new HashMap<>(Map.of(
      "Materials", new ArrayList<>(),
      "Entity Types", new ArrayList<>()));

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
    for (PlayerStatsCategory playerStatsCategory : PluginConstant.playerStatsCategories) {
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
    List<Material> materials = PluginConstant.sortedMaterials;
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
    List<EntityType> entityTypes = PluginConstant.sortedEntityTypes;
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

  public void setNumberOfMaterialPages(int numberOfMaterialPages) {
    this.numberOfMaterialPages = numberOfMaterialPages;
  }

  public void setNumberOfEntityTypePages(int numberOfEntityTypePages) {
    this.numberOfEntityTypePages = numberOfEntityTypePages;
  }
}
