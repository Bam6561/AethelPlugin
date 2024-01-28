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
 * @version 1.8.0
 * @since 1.7.13
 */
public class PluginConstant {
  public static final ArrayList<NamespacedKey> aethelTags = new ArrayList<>(List.of(
      PluginNamespacedKey.AETHELITEM_CATEGORY.namespacedKey,
      PluginNamespacedKey.FORGE_CATEGORY.namespacedKey,
      PluginNamespacedKey.FORGE_ID.namespacedKey));
  public static final HashMap<String, ArrayList<String>> attributesMap = new HashMap<>() {{
    put("offense", new ArrayList<>(PluginList.AETHEL_ATTRIBUTE_OFFENSE.list));
    put("defense", new ArrayList<>(PluginList.AETHEL_ATTRIBUTE_DEFENSE.list));
    put("other", new ArrayList<>(PluginList.AETHEL_ATTRIBUTE_OTHER.list));
  }};
  public static final HashSet<String> minecraftAttributes = new HashSet<>(loadMinecraftAttributes());
  public static final List<Enchantment> sortedEnchantments = new ArrayList<>(sortEnchantments());
  public static final ArrayList<EntityType> sortedEntityTypes = new ArrayList<>(sortEntityTypes());
  public static final ArrayList<Material> sortedMaterials = new ArrayList<>(sortMaterials());
  public static final ArrayList<PlayerStatsCategory> playerStatsCategories = new ArrayList<>(List.of(
      new PlayerStatsCategory("Activities",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_ACTIVITIES.list)),
      new PlayerStatsCategory("Containers",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_CONTAINERS.list)),
      new PlayerStatsCategory("Damage",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_DAMAGE.list)),
      new PlayerStatsCategory("General",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_GENERAL.list)),
      new PlayerStatsCategory("Movement",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_MOVEMENT.list)),
      new PlayerStatsCategory("Interactions",
          new ArrayList<>(PluginList.PLAYERSTAT_CATEGORY_INTERACTIONS.list))));

  /**
   * Adds minecraft attributes to a HashSet.
   *
   * @return minecraft attributes
   */
  private static HashSet<String> loadMinecraftAttributes() {
    HashSet<String> minecraftAttributes = new HashSet<>();
    for (String attribute : PluginList.MINECRAFT_ATTRIBUTES.list) {
      minecraftAttributes.add(attribute);
    }
    return minecraftAttributes;
  }

  /**
   * Sorts enchantments by name.
   *
   * @return sorted enchantments
   */
  private static List<Enchantment> sortEnchantments() {
    List<Enchantment> enchantments = new ArrayList<>(List.of(Enchantment.values()));
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
    ArrayList<Material> materials = new ArrayList<>();
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
    ArrayList<EntityType> entityTypes = new ArrayList<>(List.of(EntityType.values()));
    Comparator<EntityType> entityTypeComparator = Comparator.comparing(Enum::name);
    entityTypes.sort(entityTypeComparator);
    return entityTypes;
  }
}
