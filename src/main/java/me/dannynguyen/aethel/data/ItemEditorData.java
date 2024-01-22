package me.dannynguyen.aethel.data;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static java.util.Comparator.comparing;

/**
 * ItemEditorData stores items currently being edited into memory.
 *
 * @author Danny Nguyen
 * @version 1.7.0
 * @since 1.6.7
 */
public class ItemEditorData {
  private final HashMap<Player, ItemStack> editedItemMap = new HashMap<>();
  private final ArrayList<String> aethelTags = new ArrayList<>(
      Arrays.asList("aitem_cat", "forge_cat", "forge_id"));
  private final List<Attribute> attributes = new ArrayList<>(Arrays.asList(Attribute.values()));
  private final List<Enchantment> enchants = new ArrayList<>(Arrays.asList(Enchantment.values()));

  public void loadSortedData() {
    loadSortedAttributes();
    loadSortedEnchantments();
  }

  private void loadSortedAttributes() {
    Comparator<Attribute> attributeComparator = comparing(e -> e.getKey().getKey());
    attributes.sort(attributeComparator);
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

  public List<Attribute> getAttributes() {
    return this.attributes;
  }

  public List<Enchantment> getEnchants() {
    return this.enchants;
  }
}
