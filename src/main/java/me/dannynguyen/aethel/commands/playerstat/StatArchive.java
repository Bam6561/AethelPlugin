package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.util.InventoryPages;
import me.dannynguyen.aethel.util.ItemCreator;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents player statistic categories.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.4.8
 */
public class StatArchive {
  /**
   * {@link StatisticCategory Stat categories} represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<StatisticCategory, Inventory> statCategories = createStatCategoryPages();

  /**
   * {@link SubstatisticCategory Substat categories} represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<SubstatisticCategory, List<Inventory>> substatCategories = createSubstatCategoryPages();

  /**
   * No parameter constructor.
   */
  public StatArchive() {
  }

  /**
   * Creates pages of {@link StatisticCategory stat categories}.
   *
   * @return pages of {@link StatisticCategory stat categories}
   */
  private Map<StatisticCategory, Inventory> createStatCategoryPages() {
    Map<StatisticCategory, Inventory> statCategories = new HashMap<>();
    for (StatisticCategory statisticCategory : StatisticCategory.values()) {
      int i = 9;
      Inventory inv = Bukkit.createInventory(null, 54);
      for (Statistic stat : statisticCategory.getStatistics()) {
        inv.setItem(i, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + TextFormatter.capitalizePhrase(stat.name())));
        i++;
        statCategories.put(statisticCategory, inv);
      }
    }
    return statCategories;
  }

  /**
   * Creates pages of {@link SubstatisticCategory substat categories}.
   *
   * @return pages of {@link SubstatisticCategory substat categories}
   */
  private Map<SubstatisticCategory, List<Inventory>> createSubstatCategoryPages() {
    Map<SubstatisticCategory, List<Inventory>> substatCategories = new HashMap<>(Map.of(SubstatisticCategory.MATERIALS, new ArrayList<>(), SubstatisticCategory.ENTITY_TYPES, new ArrayList<>()));
    createMaterialPages(substatCategories);
    createEntityTypeStatPages(substatCategories);
    return substatCategories;
  }

  /**
   * Creates pages of {@link SubstatisticCategory#MATERIALS}.
   *
   * @param substatCategories map to add pages to
   */
  private void createMaterialPages(Map<SubstatisticCategory, List<Inventory>> substatCategories) {
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
      substatCategories.get(SubstatisticCategory.MATERIALS).add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfMaterials, endIndex + 45);
    }
  }

  /**
   * Creates pages of {@link SubstatisticCategory#ENTITY_TYPES}.
   *
   * @param substatCategories map to add pages to
   */
  private void createEntityTypeStatPages(Map<SubstatisticCategory, List<Inventory>> substatCategories) {
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
      substatCategories.get(SubstatisticCategory.ENTITY_TYPES).add(inv);

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
   * Gets {@link StatisticCategory stat categories}.
   *
   * @return {@link StatisticCategory stat categories}
   */
  @NotNull
  protected Map<StatisticCategory, Inventory> getStatCategories() {
    return this.statCategories;
  }

  /**
   * Gets {@link SubstatisticCategory substat categories}.
   *
   * @return {@link SubstatisticCategory substat categories}
   */
  @NotNull
  protected Map<SubstatisticCategory, List<Inventory>> getSubstatCategories() {
    return this.substatCategories;
  }
}
