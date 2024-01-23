package me.dannynguyen.aethel.data;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.7.1
 * @since 1.6.7
 */
public class ItemEditorData {
  private final HashMap<Player, ItemStack> editedItemMap = new HashMap<>();
  private final ArrayList<String> aethelTags = new ArrayList<>(
      Arrays.asList("aitem_cat", "forge_cat", "forge_id"));
  private final HashMap<String, ArrayList<String>> attributesMap = new HashMap<>();
  private final List<Enchantment> enchants = new ArrayList<>(Arrays.asList(Enchantment.values()));

  public void loadAttributesEnchants() {
    loadSortedAttributes();
    loadSortedEnchantments();
  }

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

  private void loadSortedEnchantments() {
    Comparator<Enchantment> enchantmentComparator = comparing(e -> e.getKey().getKey());
    enchants.sort(enchantmentComparator);
  }

  public HashMap<Player, ItemStack> getEditedItemMap() {
    return this.editedItemMap;
  }

  public ArrayList<String> getAethelTags() {
    return this.aethelTags;
  }

  public HashMap<String, ArrayList<String>> getAttributesMap() {
    return this.attributesMap;
  }

  public List<Enchantment> getEnchants() {
    return this.enchants;
  }
}
