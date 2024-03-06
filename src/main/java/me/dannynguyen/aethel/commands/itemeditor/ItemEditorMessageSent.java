package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.utility.ItemDurability;
import me.dannynguyen.aethel.utility.ItemRepairCost;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Message sent listener for ItemEditor text inputs.
 *
 * @author Danny Nguyen
 * @version 1.14.5
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
   * User's UUID.
   */
  private final UUID userUUID;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
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
    this.userUUID = user.getUniqueId();
    this.item = Plugin.getData().getEditedItemCache().getEditedItemMap().get(user.getUniqueId());
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
   * Sets the item's damage or durability.
   */
  public void setDurability() {
    try {
      int value = Integer.parseInt(e.getMessage());
      if (value >= 0) {
        ItemDurability.setDurability(item, value);
        user.sendMessage(ChatColor.GREEN + "[Set Durability] " + ChatColor.WHITE + e.getMessage());
      } else {
        ItemDurability.setDamage(item, Math.abs(value));
        user.sendMessage(ChatColor.GREEN + "[Set Damage] " + ChatColor.WHITE + e.getMessage());
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
    returnToCosmeticEditor();
  }

  /**
   * Sets the item's repair cost.
   */
  public void setRepairCost() {
    try {
      ItemRepairCost.setRepairCost(item, Integer.parseInt(e.getMessage()));
      user.sendMessage(ChatColor.GREEN + "[Set Repair Cost] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
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
   * Sets the potion color.
   */
  public void setPotionColor() {
    String[] input = e.getMessage().split(" ", 3);
    if (input.length == 3) {
      PotionMeta potion = (PotionMeta) meta;
      try {
        int red = Integer.parseInt(input[0]);
        try {
          int green = Integer.parseInt(input[1]);
          try {
            int blue = Integer.parseInt(input[2]);
            potion.setColor(org.bukkit.Color.fromRGB(red, green, blue));
            item.setItemMeta(potion);
            user.sendMessage(ChatColor.GREEN + "[Set Potion Color]");
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid Blue value.");
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid Green value.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid Red value.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Invalid RGB value.");
    }
  }

  /**
   * Sets or removes an item's Minecraft attribute modifier.
   */
  public void setMinecraftAttribute() {
    try {
      String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
      Attribute attribute = Attribute.valueOf(TextFormatter.formatEnum(type));
      String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
      EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(TextFormatter.formatEnum(slot));
      if (!e.getMessage().equals("-")) {
        AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), "attribute", Double.parseDouble(e.getMessage()), AttributeModifier.Operation.ADD_NUMBER, equipmentSlot);
        removeExistingAttributeModifiers(attribute, equipmentSlot);
        meta.addAttributeModifier(attribute, attributeModifier);
        user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
      } else {
        removeExistingAttributeModifiers(attribute, equipmentSlot);
        user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
      }
      item.setItemMeta(meta);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
    returnToAttributeEditor();
  }

  /**
   * Sets or removes an item's Aethel attribute modifier.
   */
  public void setAethelAttribute() {
    String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
    String attribute = type + "." + Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
    NamespacedKey attributeKey = new NamespacedKey(Plugin.getInstance(), "aethel.attribute." + attribute);
    try {
      if (!e.getMessage().equals("-")) {
        String attributeValue = String.valueOf(Double.parseDouble(e.getMessage()));
        setAethelAttributeModifier(type, attribute, attributeKey, attributeValue);
      } else {
        removeAethelAttributeModifier(type, attribute, attributeKey);
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
    returnToAethelAttributeEditor();
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchant() {
    if (!e.getMessage().equals("-")) {
      try {
        int level = Integer.parseInt(e.getMessage());
        if (level > 0 && level < 32768) {
          NamespacedKey enchant = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE));
          item.addUnsafeEnchantment(Enchantment.getByKey(enchant), level);
          user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(enchant.getKey()) + "]");
        } else {
          user.sendMessage(ChatColor.RED + "Specify a level between 1 - 32767.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid value.");
      }
    } else {
      NamespacedKey enchantment = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE));
      item.removeEnchantment(Enchantment.getByKey(enchantment));
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToEnchantmentEditor);
  }

  /**
   * Sets or removes an item's potion effect.
   */
  public void setPotionEffect() {
    PotionMeta potion = (PotionMeta) meta;
    NamespacedKey potionEffectKey = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE));
    PotionEffectType potionEffectType = PotionEffectType.getByKey(potionEffectKey);
    if (!e.getMessage().equals("-")) {
      String[] input = e.getMessage().split(" ", 3);
      if (input.length == 3) {
        try {
          int duration = Integer.parseInt(input[0]);
          try {
            int amplifier = Integer.parseInt(input[1]);
            boolean ambient = Boolean.parseBoolean(input[2]);
            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient);
            potion.addCustomEffect(potionEffect, true);
            user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid amplifier.");
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid duration.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Invalid potion effect.");
      }
    } else {
      potion.removeCustomEffect(potionEffectType);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
    }
    item.setItemMeta(potion);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToPotionEditor);
  }

  /**
   * Sets or removes an item's Aethel tag.
   */
  public void setTag() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
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
   * Sets an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param type           attribute type derived from click
   * @param attribute      attribute name
   * @param attributeKey   attribute key
   * @param attributeValue attribute value
   */
  private void setAethelAttributeModifier(String type, String attribute, NamespacedKey attributeKey, String attributeValue) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
    String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);

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
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type) + "]");
  }

  /**
   * Removes an item's Aethel attribute modifier based on the equipment slot mode.
   *
   * @param type         attribute type derived from click
   * @param attribute    attribute name
   * @param attributeKey attribute key
   */
  private void removeAethelAttributeModifier(String type, String attribute, NamespacedKey attributeKey) {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
    String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);

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
    }
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type) + "]");
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
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
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
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AttributeEditorMenu(user, EquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_MINECRAFT_ATTRIBUTE.getMeta());
    });
  }

  /**
   * Returns to the AethelAttributeEditor.
   */
  private void returnToAethelAttributeEditor() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AethelAttributeEditorMenu(user, RpgEquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_AETHEL_ATTRIBUTE.getMeta());
    });
  }

  /**
   * Returns to the EnchantmentEditor.
   */
  private void returnToEnchantmentEditor() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new EnchantmentEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
    });
  }

  /**
   * Returns to the PotionEditor.
   */
  private void returnToPotionEditor() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new PotionEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_POTION.getMeta());
    });
  }

  /**
   * Returns to the TagEditor.
   */
  private void returnToTagEditor() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new TagEditorMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
    });
  }
}
