package me.dannynguyen.aethel.enums;

import me.dannynguyen.aethel.commands.playerstats.object.PlayerStatsCategory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * PluginConstant is an enum-like storage containing various constants.
 *
 * @author Danny Nguyen
 * @version 1.8.1
 * @since 1.7.13
 */
public class PluginConstant {
  public static final NamespacedKey[] aethelTags = {
      PluginNamespacedKey.AETHELITEM_CATEGORY.namespacedKey,
      PluginNamespacedKey.FORGE_CATEGORY.namespacedKey,
      PluginNamespacedKey.FORGE_ID.namespacedKey};

  public static final Map<String, List<String>> aethelAttributesMap = Map.of(
      "offense", PluginList.AETHEL_ATTRIBUTE_OFFENSE.list,
      "defense", PluginList.AETHEL_ATTRIBUTE_DEFENSE.list,
      "other", PluginList.AETHEL_ATTRIBUTE_OTHER.list);

  public static final Set<String> minecraftAttributes = new HashSet<>(PluginList.MINECRAFT_ATTRIBUTES.list);

  public static final List<Enchantment> sortedEnchantments = sortEnchantments();
  public static final List<EntityType> sortedEntityTypes = sortEntityTypes();
  public static final List<Material> sortedMaterials = sortMaterials();

  public static final List<PlayerStatsCategory> playerStatsCategories = List.of(
      new PlayerStatsCategory("Activities", PluginList.PLAYERSTAT_CATEGORY_ACTIVITIES.list),
      new PlayerStatsCategory("Containers", PluginList.PLAYERSTAT_CATEGORY_CONTAINERS.list),
      new PlayerStatsCategory("Damage", PluginList.PLAYERSTAT_CATEGORY_DAMAGE.list),
      new PlayerStatsCategory("General", PluginList.PLAYERSTAT_CATEGORY_GENERAL.list),
      new PlayerStatsCategory("Movement", PluginList.PLAYERSTAT_CATEGORY_MOVEMENT.list),
      new PlayerStatsCategory("Interactions", PluginList.PLAYERSTAT_CATEGORY_INTERACTIONS.list));

  /**
   * Sorts enchantments by name.
   *
   * @return sorted enchantments
   */
  private static List<Enchantment> sortEnchantments() {
    List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
    Comparator<Enchantment> enchantmentComparator = comparing(e -> e.getKey().getKey());
    enchantments.sort(enchantmentComparator);
    return enchantments;
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
   * Sorts entity types
   *
   * @return sorted entity types
   */
  private static List<EntityType> sortEntityTypes() {
    List<EntityType> entityTypes = Arrays.asList(EntityType.values());
    Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
    entityTypes.sort(entityTypeComparator);
    return entityTypes;
  }
}
