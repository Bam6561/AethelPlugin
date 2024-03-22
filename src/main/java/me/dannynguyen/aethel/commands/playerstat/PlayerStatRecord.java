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
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents player statistic categories.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.4.8
 */
public class PlayerStatRecord {
  /**
   * {@link StatCategory Stat categories} represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<StatCategory, Inventory> statCategories = createStatCategoryPages();

  /**
   * {@link SubstatCategory Substat categories} represented by groups of inventories.
   * <p>
   * An inventory from any of the groups is also referred to as a page.
   */
  private final Map<SubstatCategory, List<Inventory>> substatCategories = createSubstatCategoryPages();

  /**
   * No parameter constructor.
   */
  public PlayerStatRecord() {
  }

  /**
   * Creates pages of {@link StatCategory stat categories}.
   *
   * @return pages of {@link StatCategory stat categories}
   */
  private Map<StatCategory, Inventory> createStatCategoryPages() {
    Map<StatCategory, Inventory> statCategories = new HashMap<>();
    for (StatCategory statCategory : StatCategory.values()) {
      int i = 9;
      Inventory inv = Bukkit.createInventory(null, 54);
      for (Statistic stat : statCategory.getStatistics()) {
        inv.setItem(i, ItemCreator.createItem(Material.PAPER, ChatColor.WHITE + TextFormatter.capitalizePhrase(stat.name())));
        i++;
        statCategories.put(statCategory, inv);
      }
    }
    return statCategories;
  }

  /**
   * Creates pages of {@link SubstatCategory substat categories}.
   *
   * @return pages of {@link SubstatCategory substat categories}
   */
  private Map<SubstatCategory, List<Inventory>> createSubstatCategoryPages() {
    Map<SubstatCategory, List<Inventory>> substatCategories = new HashMap<>(Map.of(SubstatCategory.MATERIALS, new ArrayList<>(), SubstatCategory.ENTITY_TYPES, new ArrayList<>()));
    createMaterialPages(substatCategories);
    createEntityTypeStatPages(substatCategories);
    return substatCategories;
  }

  /**
   * Creates pages of {@link SubstatCategory#MATERIALS}.
   *
   * @param substatCategories map to add pages to
   */
  private void createMaterialPages(Map<SubstatCategory, List<Inventory>> substatCategories) {
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
      substatCategories.get(SubstatCategory.MATERIALS).add(inv);

      // Indices to use for the next page (if it exists)
      startIndex += 45;
      endIndex = Math.min(numberOfMaterials, endIndex + 45);
    }
  }

  /**
   * Creates pages of {@link SubstatCategory#ENTITY_TYPES}.
   *
   * @param substatCategories map to add pages to
   */
  private void createEntityTypeStatPages(Map<SubstatCategory, List<Inventory>> substatCategories) {
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
      substatCategories.get(SubstatCategory.ENTITY_TYPES).add(inv);

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
   * Gets {@link StatCategory stat categories}.
   *
   * @return {@link StatCategory stat categories}
   */
  @NotNull
  protected Map<StatCategory, Inventory> getStatCategories() {
    return this.statCategories;
  }

  /**
   * Gets {@link SubstatCategory substat categories}.
   *
   * @return {@link SubstatCategory substat categories}
   */
  @NotNull
  protected Map<SubstatCategory, List<Inventory>> getSubstatCategories() {
    return this.substatCategories;
  }
}
