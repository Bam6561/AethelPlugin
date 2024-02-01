package me.dannynguyen.aethel.systems;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.object.RpgCharacter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * RpgData stores RPG characters in memory.
 *
 * @author Danny Nguyen
 * @version 1.8.10
 * @since 1.8.10
 */
public class RpgData {
  private Map<Player, RpgCharacter> rpgCharacters = new HashMap<>();

  /**
   * Loads an RPG character into memory.
   *
   * @param player interacting player
   */
  public void loadRpgCharacter(Player player) {
    PlayerInventory inv = player.getInventory();

    if (!player.getInventory().isEmpty()) {
      Map<String, ItemStack> equipment = new HashMap<>();
      Map<String, Double> aethelAttributes = new HashMap<>();

      loadEquipment(inv, equipment);
      loadAethelAttributes(equipment, aethelAttributes);
    }
  }

  /**
   * Loads the player's equipment into memory.
   *
   * @param inv       interacting player's inventory
   * @param equipment interacting player's equipment
   */
  private void loadEquipment(PlayerInventory inv, Map<String, ItemStack> equipment) {
    equipment.put(Slot.HAND.slot, inv.getItemInMainHand());
    equipment.put(Slot.OFF_HAND.slot, inv.getItemInOffHand());
    equipment.put(Slot.HEAD.slot, inv.getHelmet());
    equipment.put(Slot.CHEST.slot, inv.getChestplate());
    equipment.put(Slot.LEGS.slot, inv.getLeggings());
    equipment.put(Slot.FEET.slot, inv.getBoots());
  }

  /**
   * Loads the player's Aethel attribute values into memory.
   *
   * @param equipment        interacting player's equipment
   * @param aethelAttributes interacting player's Aethel attribute values
   */
  private void loadAethelAttributes(Map<String, ItemStack> equipment,
                                    Map<String, Double> aethelAttributes) {
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.namespacedKey;

    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.HAND.slot);
    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.OFF_HAND.slot);
    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.HEAD.slot);
    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.CHEST.slot);
    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.LEGS.slot);
    readAethelAttributes(equipment, aethelAttributes, listKey, Slot.FEET.slot);
  }

  /**
   * Checks if the item has Aethel attributes in the matching slot type before calculating values.
   *
   * @param attributeValues interacting player's Aethel attributes
   * @param listKey         Aethel attributes list key
   * @param slot            equipment slot
   */
  private void readAethelAttributes(Map<String, ItemStack> equipment,
                                    Map<String, Double> attributeValues,
                                    NamespacedKey listKey, String slot) {
    ItemStack item = equipment.get(slot);
    if (item.getType() != Material.AIR) {

      PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {

        String[] attributes = dataContainer.get(listKey, PersistentDataType.STRING).split(" ");
        for (String attribute : attributes) {

          String attributeSlot = attribute.substring(attribute.indexOf(".") + 1);
          if (attributeSlot.equals(slot)) {
            updateAethelAttributes(attributeValues, dataContainer, attribute);
          }
        }
      }
    }
  }

  /**
   * Updates the player's Aethel attribute values based on the item's statistics.
   *
   * @param attributeValues interacting player's Aethel attributes
   * @param dataContainer   item's persistent tags
   * @param attribute       Aethel attribute type
   */
  private void updateAethelAttributes(Map<String, Double> attributeValues,
                                      PersistentDataContainer dataContainer,
                                      String attribute) {
    String attributeType = attribute.substring(0, attribute.indexOf("."));
    NamespacedKey key = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);

    if (attributeValues.containsKey(attributeType)) {
      attributeValues.put(attributeType,
          (attributeValues.get(attributeType) + dataContainer.get(key, PersistentDataType.DOUBLE)));
    } else {
      attributeValues.put(attributeType, (dataContainer.get(key, PersistentDataType.DOUBLE)));
    }
  }

  public Map<Player, RpgCharacter> getRpgCharacters() {
    return this.rpgCharacters;
  }

  private enum Slot {
    HAND("hand"),
    OFF_HAND("off_hand"),
    HEAD("head"),
    CHEST("chest"),
    LEGS("legs"),
    FEET("feet");

    public final String slot;

    Slot(String slot) {
      this.slot = slot;
    }
  }
}
