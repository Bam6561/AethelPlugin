package me.dannynguyen.aethel.data;

import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.7.8
 * @since 1.6.7
 */
public class ItemEditorData {
  private final HashMap<Player, ItemStack> editedItemMap = new HashMap<>();
  private final ArrayList<NamespacedKey> aethelTags = new ArrayList<>(
      Arrays.asList(PluginNamespacedKey.AETHELITEM_CATEGORY.namespacedKey,
          PluginNamespacedKey.FORGE_CATEGORY.namespacedKey,
          PluginNamespacedKey.FORGE_ID.namespacedKey));
  private final HashMap<String, ArrayList<String>> attributesMap = new HashMap<>();
  private final List<Enchantment> enchants = new ArrayList<>(Arrays.asList(Enchantment.values()));

  /**
   * Loads sorted attributes and enchants into memory.
   */
  public void loadAttributesEnchants() {
    loadSortedAttributes();
    loadSortedEnchantments();
  }

  /**
   * Loads sorted attributes into memory.
   */
  private void loadSortedAttributes() {
    attributesMap.put("offense", new ArrayList<>(Arrays.asList(
        "Attack Damage", "Attack Speed",
        "Critical Chance", "Critical Damage")));
    attributesMap.put("defense", new ArrayList<>(Arrays.asList(
        "Max Health", "Armor", "Armor Toughness",
        "Movement Speed", "Block", "Parry", "Dodge")));
    attributesMap.put("other", new ArrayList<>(Arrays.asList(
        "Ability Damage", "Ability Cooldown",
        "Apply Status", "Knockback Resistance", "Luck")));
  }

  /**
   * Loads sorted enchants into memory.
   */
  private void loadSortedEnchantments() {
    Comparator<Enchantment> enchantmentComparator = comparing(e -> e.getKey().getKey());
    enchants.sort(enchantmentComparator);
  }

  public HashMap<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }

  public ArrayList<NamespacedKey> getAethelTags() {
    return this.aethelTags;
  }

  public HashMap<String, ArrayList<String>> getAttributesMap() {
    return this.attributesMap;
  }

  public List<Enchantment> getEnchants() {
    return this.enchants;
  }
}
