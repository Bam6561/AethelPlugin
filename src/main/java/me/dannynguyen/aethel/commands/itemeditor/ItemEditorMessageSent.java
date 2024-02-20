package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.plugin.MenuMeta;
import me.dannynguyen.aethel.plugin.PlayerMeta;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Message sent listener for ItemEditor text inputs.
 *
 * @author Danny Nguyen
 * @version 1.11.6
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
    String type = PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.TYPE);
    if (!type.contains("aethel.")) {
      setMinecraftAttribute(type);
    } else {
      setAethelAttribute(type);
    }
    returnToAttributeEditor();
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchant() {
    if (!e.getMessage().equals("0")) {
      try {
        int level = Integer.parseInt(e.getMessage());
        if (level > 0 && level < 32768) {
          NamespacedKey enchant = NamespacedKey.minecraft(PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.TYPE));
          item.addUnsafeEnchantment(Enchantment.getByKey(enchant), level);
          user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(enchant.getKey()) + "]");
        } else {
          user.sendMessage(ChatColor.RED + "Specify a level between 0 - 32767.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid value.");
      }
    } else {
      NamespacedKey enchantment = NamespacedKey.minecraft(PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.TYPE));
      item.removeEnchantment(Enchantment.getByKey(enchantment));
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToEnchantmentEditor);
  }

  /**
   * Sets or removes an item's Aethel tag.
   */
  public void setTag() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.TYPE);
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), "aethel." + tagType);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(tagKey, PersistentDataType.STRING, e.getMessage());
      user.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      dataContainer.remove(tagKey);
      user.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToTagEditor);
  }

  /**
   * Sets a Minecraft attribute.
   *
   * @param type attribute derived from inventory click
   */
  private void setMinecraftAttribute(String type) {
    try {
      Attribute attribute = Attribute.valueOf(type);
      EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.SLOT).toUpperCase());
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
    String equipmentSlot = PluginData.pluginSystem.getPlayerMetadata().get(user).get(PlayerMeta.SLOT);
    String attribute = type + "." + equipmentSlot;
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), attribute);

    // Remove "aethel.attribute."
    attribute = attribute.substring(17);

    try {
      String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
      if (!e.getMessage().equals("0")) {
        setAethelAttributeModifier(type, attribute, attributeKey, attributeValue);
      } else {
        removeAethelAttributeModifier(type, attribute, attributeKey);
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
   * @param attribute      attribute name
   * @param attributeKey   attribute key
   * @param attributeValue attribute value
   */
  private void setAethelAttributeModifier(String type, String attribute, NamespacedKey attributeKey, String attributeValue) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginEnum.Key.ATTRIBUTE_LIST.getNamespacedKey();

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> itemAttributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newAttributes = new StringBuilder();
      for (String itemAttribute : itemAttributes) {
        if (!itemAttribute.equals(attribute)) {
          newAttributes.append(itemAttribute).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newAttributes + attribute);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, attribute);
    }
    dataContainer.set(attributeKey, PersistentDataType.DOUBLE, Double.parseDouble(attributeValue));
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(type.substring(17)) + "]");
  }

  /**
   * Removes an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param type         attribute derived from inventory click
   * @param attribute    attribute name
   * @param attributeKey attribute key
   */
  private void removeAethelAttributeModifier(String type, String attribute, NamespacedKey attributeKey) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginEnum.Key.ATTRIBUTE_LIST.getNamespacedKey();

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> itemAttributes = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newAttributes = new StringBuilder();
      for (String itemAttribute : itemAttributes) {
        if (!itemAttribute.equals(attribute)) {
          newAttributes.append(itemAttribute).append(" ");
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
      for (AttributeModifier attributeModifier : meta.getAttributeModifiers().get(attribute)) {
        if (attributeModifier.getSlot() == equipmentSlot) {
          meta.removeAttributeModifier(attribute, attributeModifier);
        }
      }
    }
  }

  /**
   * Returns to the CosmeticEditor menu.
   */
  private void returnToCosmeticEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new CosmeticEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
    });
  }

  /**
   * Returns to the AttributeEditor.
   */
  private void returnToAttributeEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AttributeEditorMenu(user, AttributeEditorAction.asEnum(playerMeta.get(PlayerMeta.SLOT))).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ATTRIBUTE.getMeta());
    });
  }

  /**
   * Returns to the EnchantmentEditor.
   */
  private void returnToEnchantmentEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new EnchantmentEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
    });
  }

  /**
   * Returns to the TagEditor.
   */
  private void returnToTagEditor() {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new TagEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
    });
  }
}
