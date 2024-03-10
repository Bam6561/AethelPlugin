package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.*;
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
 * @version 1.15.4
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
    returnToCosmetic();
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
    returnToCosmetic();
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
      user.sendMessage(ChatColor.RED + "Invalid durability/damage.");
    }
    returnToCosmetic();
  }

  /**
   * Sets the item's repair cost.
   */
  public void setRepairCost() {
    try {
      ItemRepairCost.setRepairCost(item, Integer.parseInt(e.getMessage()));
      user.sendMessage(ChatColor.GREEN + "[Set Repair Cost] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid repair cost.");
    }
    returnToCosmetic();
  }

  /**
   * Sets the lore.
   */
  public void setLore() {
    meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', e.getMessage()).split(",, ")));
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Lore]");
    returnToCosmetic();
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
    returnToCosmetic();
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
        user.sendMessage(ChatColor.RED + "Invalid line.");
      } catch (IndexOutOfBoundsException ex) {
        user.sendMessage(ChatColor.RED + "Line does not exist.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
    returnToCosmetic();
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
      user.sendMessage(ChatColor.RED + "Invalid line.");
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(ChatColor.RED + "Line does not exist.");
    }
    returnToCosmetic();
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
            user.sendMessage(ChatColor.RED + "Invalid Blue.");
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid Green.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid Red.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Invalid RGB.");
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
    returnToAttribute();
  }

  /**
   * Sets or removes an item's Aethel attribute modifier.
   */
  public void setAethelAttribute() {
    try {
      if (!e.getMessage().equals("-")) {
        setKeyDoubleToList(KeyHeader.ATTRIBUTE.getHeader(), Double.parseDouble(e.getMessage()), PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey());
      } else {
        removeKeyFromList(KeyHeader.ATTRIBUTE.getHeader(), PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey());
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
    }
    returnToAethelAttribute();
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
        user.sendMessage(ChatColor.RED + "Invalid level.");
      }
    } else {
      NamespacedKey enchantment = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE));
      item.removeEnchantment(Enchantment.getByKey(enchantment));
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToEnchantment);
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
        user.sendMessage(ChatColor.RED + "Invalid effect.");
      }
    } else {
      potion.removeCustomEffect(potionEffectType);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
    }
    item.setItemMeta(potion);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToPotion);
  }

  /**
   * Sets or removes an item's passive ability.
   */
  public void setPassive() {
    if (!e.getMessage().equals("-")) {
      String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE).toUpperCase();
      switch (PassiveAbility.valueOf(type)) {
        case CHILL, DAMPEN, RUPTURE -> readPassiveStackInstance();
        case SPARK -> readPassiveSpark();
      }
    } else {
      removeKeyFromList(KeyHeader.PASSIVE.getHeader(), PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey());
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToPassive);
  }

  /**
   * Sets or removes an item's active ability.
   */
  public void setActive() {
    if (!e.getMessage().equals("-")) {
      String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE).toUpperCase();
      switch (ActiveAbility.valueOf(type)) {
        case BLINK, DASH -> readActiveMovement();
        case PROJECTION -> readActiveProjection();
        case SHATTER -> readActiveShatter();
      }
    } else {
      removeKeyFromList(KeyHeader.ACTIVE.getHeader(), PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey());
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToActive);
  }

  /**
   * Sets or removes an item's Aethel tag.
   */
  public void setTag() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tagType);

    if (!e.getMessage().equals("-")) {
      dataContainer.set(tagKey, PersistentDataType.STRING, e.getMessage());
      user.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      dataContainer.remove(tagKey);
      user.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToTag);
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
   * Checks if the input was formatted correctly before setting the passive stack instance.
   */
  private void readPassiveStackInstance() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        int stacks = Integer.parseInt(args[0]);
        try {
          int ticks = Integer.parseInt(args[1]);
          setKeyStringToList(KeyHeader.PASSIVE.getHeader(), stacks + " " + ticks, PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid ticks.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid stacks.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the passive spark.
   */
  private void readPassiveSpark() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        double damage = Integer.parseInt(args[0]);
        try {
          double distance = Double.parseDouble(args[1]);
          setKeyStringToList(KeyHeader.PASSIVE.getHeader(), damage + " " + distance, PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid radius.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid damage.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the active movement.
   */
  private void readActiveMovement() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        double distance = Double.parseDouble(args[0]);
        try {
          int cooldown = Integer.parseInt(args[1]);
          setKeyStringToList(KeyHeader.ACTIVE.getHeader(), distance + " " + cooldown, PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid cooldown.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid distance.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the active projection.
   */
  private void readActiveProjection() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 3) {
      try {
        double distance = Double.parseDouble(args[0]);
        try {
          int delay = Integer.parseInt(args[1]);
          try {
            int cooldown = Integer.parseInt(args[2]);
            setKeyStringToList(KeyHeader.ACTIVE.getHeader(), distance + " " + delay + " " + cooldown, PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey());
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid cooldown.");
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid delay.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid distance.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the active shatter.
   */
  private void readActiveShatter() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        double radius = Double.parseDouble(args[0]);
        try {
          int cooldown = Integer.parseInt(args[1]);
          setKeyStringToList(KeyHeader.ACTIVE.getHeader(), radius + " " + cooldown, PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid cooldown.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid radius.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Sets a key with a double value to a key header's list of keys.
   *
   * @param keyHeader key header
   * @param keyValue  key value
   * @param listKey   list key
   */
  private void setKeyDoubleToList(String keyHeader, double keyValue, NamespacedKey listKey) {
    String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
    String stringKeyToSet = type + "." + Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
    NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToSet);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newKeys = new StringBuilder();
      for (String key : keys) {
        if (!key.equals(stringKeyToSet)) {
          newKeys.append(key).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newKeys + stringKeyToSet);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, stringKeyToSet);
    }
    dataContainer.set(namespacedKeyToSet, PersistentDataType.DOUBLE, keyValue);
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type) + "]");
  }

  /**
   * Sets a key with a String value to a key header's list of keys.
   *
   * @param keyHeader key header
   * @param keyValue  key value
   * @param listKey   list key
   */
  private void setKeyStringToList(String keyHeader, String keyValue, NamespacedKey listKey) {
    String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
    String stringKeyToSet = type + "." + Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
    NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToSet);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newKeys = new StringBuilder();
      for (String key : keys) {
        if (!key.equals(stringKeyToSet)) {
          newKeys.append(key).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newKeys + stringKeyToSet);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, stringKeyToSet);
    }
    dataContainer.set(namespacedKeyToSet, PersistentDataType.STRING, keyValue);
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type) + "]");
  }

  /**
   * Removes a key from a key header's list of keys.
   * <p>
   * If the list is empty after the operation, the list is also removed.
   * </p>
   *
   * @param keyHeader key header
   * @param listKey   list key
   */
  private void removeKeyFromList(String keyHeader, NamespacedKey listKey) {
    String type = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.TYPE);
    String stringKeyToRemove = type + "." + Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);
    NamespacedKey namespacedKeyToRemove = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToRemove);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String slot = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID).get(PlayerMeta.SLOT);

    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newKeys = new StringBuilder();
      for (String key : keys) {
        if (!key.equals(stringKeyToRemove)) {
          newKeys.append(key).append(" ");
        }
      }
      if (!newKeys.isEmpty()) {
        dataContainer.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
      } else {
        dataContainer.remove(listKey);
      }
      dataContainer.remove(namespacedKeyToRemove);
    }
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type) + "]");
  }

  /**
   * Returns to the Cosmetic menu.
   */
  private void returnToCosmetic() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new CosmeticMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_COSMETIC.getMeta());
    });
  }

  /**
   * Returns to the Attribute menu.
   */
  private void returnToAttribute() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AttributeMenu(user, EquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_MINECRAFT_ATTRIBUTE.getMeta());
    });
  }

  /**
   * Returns to the AethelAttribute menu.
   */
  private void returnToAethelAttribute() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AethelAttributeMenu(user, RpgEquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_AETHEL_ATTRIBUTE.getMeta());
    });
  }

  /**
   * Returns to the Enchantment menu.
   */
  private void returnToEnchantment() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new EnchantmentMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ENCHANTMENT.getMeta());
    });
  }

  /**
   * Returns to the Potion menu.
   */
  private void returnToPotion() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new PotionMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_POTION.getMeta());
    });
  }

  /**
   * Returns to the Passive menu.
   */
  private void returnToPassive() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new PassiveMenu(user, EquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_PASSIVE.getMeta());
    });
  }

  /**
   * Returns to the Active menu.
   */
  private void returnToActive() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new ActiveMenu(user, EquipmentSlot.valueOf(playerMeta.get(PlayerMeta.SLOT).toUpperCase())).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_ACTIVE.getMeta());
    });
  }

  /**
   * Returns to the Tag menu.
   */
  private void returnToTag() {
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    playerMeta.remove(PlayerMeta.MESSAGE);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new TagMenu(user).openMenu());
      playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.ITEMEDITOR_TAG.getMeta());
    });
  }
}
