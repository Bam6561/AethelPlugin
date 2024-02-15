package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for ItemEditor text inputs.
 *
 * @author Danny Nguyen
 * @version 1.9.20
 * @since 1.7.0
 */
public class ItemEditorMessageSent {
  /**
   * Message sent event.
   */
  private final AsyncPlayerChatEvent e;

  /**
   * Player who sent the message.
   */
  private final Player user;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * ItemStack meta.
   */
  private final ItemMeta meta;

  /**
   * Associates a message sent event with its user and current
   * editing item in the context of using an ItemEditor menu.
   *
   * @param e message sent event
   */
  public ItemEditorMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.item = PluginData.editedItemCache.getEditedItemMap().get(user);
    this.meta = item.getItemMeta();
  }

  /**
   * Sets the item's display name.
   */
  public void setDisplayName() {
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Named Item] " + ChatColor.WHITE + e.getMessage());
    returnToCosmeticEditor();
  }

  /**
   * Sets the item's custom model data.
   */
  public void setCustomModelData() {
    try {
      meta.setCustomModelData(Integer.parseInt(e.getMessage()));
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Custom Model Data] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid custom model data.");
    }
    returnToCosmeticEditor();
  }

  /**
   * Sets the lore.
   */
  public void setLore() {
    meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', e.getMessage()).split(",, ")));
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Lore]");
    returnToCosmeticEditor();
  }

  /**
   * Adds a line of lore.
   */
  public void addLore() {
    if (meta.hasLore()) {
      List<String> lore = meta.getLore();
      lore.add(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
      meta.setLore(lore);
    } else {
      meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', e.getMessage())));
    }
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Added Lore]");
    returnToCosmeticEditor();
  }

  /**
   * Edits a line of lore.
   */
  public void editLore() {
    String[] input = e.getMessage().split(" ", 2);
    if (input.length == 2) {
      try {
        List<String> lore = meta.getLore();
        lore.set(Integer.parseInt(input[0]) - 1, ChatColor.translateAlternateColorCodes('&', input[1]));
        meta.setLore(lore);
        item.setItemMeta(meta);
        user.sendMessage(ChatColor.GREEN + "[Edited Lore]");
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid line number.");
      } catch (IndexOutOfBoundsException ex) {
        user.sendMessage(ChatColor.RED + "Line does not exist.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Invalid input.");
    }
    returnToCosmeticEditor();
  }

  /**
   * Removes a line of lore.
   */
  public void removeLore() {
    try {
      List<String> lore = meta.getLore();
      lore.remove(Integer.parseInt(e.getMessage()) - 1);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Removed Lore]");
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid line number.");
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(ChatColor.RED + "Line does not exist.");
    }
    returnToCosmeticEditor();
  }

  /**
   * Sets or removes an item's attribute modifier.
   */
  public void setAttribute() {
    String type = user.getMetadata(PluginPlayerMeta.TYPE.getMeta()).get(0).asString();
    if (!type.contains("aethel.")) {
      setMinecraftAttribute(type);
    } else {
      setAethelAttribute(type);
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> returnToAttributeEditor());
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchant() {
    if (!e.getMessage().equals("0")) {
      try {
        int level = Integer.parseInt(e.getMessage());
        if (level > 0 && level < 32768) {
          NamespacedKey enchant = NamespacedKey.minecraft(user.getMetadata(PluginPlayerMeta.TYPE.getMeta()).get(0).asString());
          item.addUnsafeEnchantment(Enchantment.getByKey(enchant), level);
          user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(enchant.getKey()) + "]");
        } else {
          user.sendMessage(ChatColor.RED + "Specify a level between 0 - 32767.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid value.");
      }
    } else {
      NamespacedKey enchantment = NamespacedKey.minecraft(user.getMetadata(PluginPlayerMeta.TYPE.getMeta()).get(0).asString());
      item.removeEnchantment(Enchantment.getByKey(enchantment));
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> returnToEnchantmentEditor());
  }

  /**
   * Sets or removes an item's Aethel tag.
   */
  public void setTag() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = user.getMetadata(PluginPlayerMeta.TYPE.getMeta()).get(0).asString();
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tagType);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(tagKey, PersistentDataType.STRING, e.getMessage());
      user.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      dataContainer.remove(tagKey);
      user.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> returnToTagEditor());
  }

  /**
   * Sets a Minecraft attribute.
   *
   * @param type attribute derived from inventory click
   */
  private void setMinecraftAttribute(String type) {
    try {
      Attribute attribute = Attribute.valueOf(type);
      EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(user.getMetadata(PluginPlayerMeta.SLOT.getMeta()).get(0).asString().toUpperCase());
      if (!e.getMessage().equals("0")) {
        AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "attribute", Double.parseDouble(e.getMessage()), AttributeModifier.Operation.ADD_NUMBER, equipmentSlot);
        removeExistingAttributeModifiers(attribute, equipmentSlot);
        meta.addAttributeModifier(attribute, attributeModifier);
        user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(attribute.getKey().getKey(), ".").substring(8) + "]");
      } else {
        removeExistingAttributeModifiers(attribute, equipmentSlot);
        user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(attribute.getKey().getKey(), ".").substring(8) + "]");
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
  }

  /**
   * Sets an Aethel attribute.
   *
   * @param type attribute derived from inventory click
   */
  private void setAethelAttribute(String type) {
    String equipmentSlot = user.getMetadata(PluginPlayerMeta.SLOT.getMeta()).get(0).asString();
    String attributeName = type + "." + equipmentSlot;
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), attributeName);

    // Remove "aethel.attribute."
    attributeName = attributeName.substring(17);

    try {
      String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
      if (!e.getMessage().equals("0")) {
        setAethelAttributeModifier(type, attributeName, attributeKey, attributeValue);
      } else {
        removeAethelAttributeModifier(type, attributeName, attributeKey);
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
  }

  /**
   * Sets an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param type           attribute derived from inventory click
   * @param attributeName  attribute name
   * @param attributeKey   attribute key
   * @param attributeValue attribute value
   */
  private void setAethelAttributeModifier(String type, String attributeName, NamespacedKey attributeKey, String attributeValue) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newAttributes = new StringBuilder();
      for (String attribute : attributes) {
        if (!attribute.equals(attributeName)) {
          newAttributes.append(attribute).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newAttributes + attributeName);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, attributeName);
    }
    dataContainer.set(attributeKey, PersistentDataType.DOUBLE, Double.parseDouble(attributeValue));
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(type.substring(17)) + "]");
  }

  /**
   * Removes an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param type          attribute derived from inventory click
   * @param attributeName attribute name
   * @param attributeKey  attribute key
   */
  private void removeAethelAttributeModifier(String type, String attributeName, NamespacedKey attributeKey) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginNamespacedKey.AETHEL_ATTRIBUTE_LIST.getNamespacedKey();

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> attributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newAttributes = new StringBuilder();
      for (String attribute : attributes) {
        if (!attribute.equals(attributeName)) {
          newAttributes.append(attribute).append(" ");
        }
      }

      if (!newAttributes.isEmpty()) {
        dataContainer.set(listKey, PersistentDataType.STRING, newAttributes.toString().trim());
      } else {
        dataContainer.remove(listKey);
      }
      dataContainer.remove(attributeKey);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(type.substring(17)) + "]");
    }
  }


  /**
   * Removes existing attribute modifiers in the same slot.
   *
   * @param attribute     attribute
   * @param equipmentSlot equipment slot
   */
  private void removeExistingAttributeModifiers(Attribute attribute, EquipmentSlot equipmentSlot) {
    if (meta.getAttributeModifiers() != null) {
      for (AttributeModifier existingAttributeModifier : meta.getAttributeModifiers().get(attribute)) {
        if (existingAttributeModifier.getSlot().equals(equipmentSlot)) {
          meta.removeAttributeModifier(attribute, existingAttributeModifier);
        }
      }
    }
  }

  /**
   * Returns to the CosmeticEditor menu.
   */
  private void returnToCosmeticEditor() {
    user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new CosmeticEditorMenu(user).openMenu());
      user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_COSMETICS.menu));
    });
  }

  /**
   * Returns to the AttributeEditor.
   */
  private void returnToAttributeEditor() {
    user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.asEnum(user.getMetadata(PluginPlayerMeta.SLOT.getMeta()).get(0).asString())).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ATTRIBUTES.menu));
  }

  /**
   * Returns to the EnchantmentEditor.
   */
  private void returnToEnchantmentEditor() {
    user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    user.openInventory(new EnchantmentEditorMenu(user).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_ENCHANTMENTS.menu));
  }

  /**
   * Returns to the TagEditor.
   */
  private void returnToTagEditor() {
    user.removeMetadata(PluginPlayerMeta.MESSAGE.getMeta(), Plugin.getInstance());
    user.openInventory(new TagEditorMenu(user).openMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.ITEMEDITOR_TAGS.menu));
  }
}
