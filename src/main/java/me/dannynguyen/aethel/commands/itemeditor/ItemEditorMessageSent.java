package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.listeners.MessageEvent;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveEffect;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveType;
import me.dannynguyen.aethel.util.ItemDurability;
import me.dannynguyen.aethel.util.ItemRepairCost;
import me.dannynguyen.aethel.util.TextFormatter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for ItemEditor text inputs.
 * <p>
 * Called with {@link MessageEvent}.
 *
 * @author Danny Nguyen
 * @version 1.17.19
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
  private final UUID uuid;

  /**
   * ItemStack being edited.
   */
  private final ItemStack item;

  /**
   * ItemStack's meta.
   */
  private final ItemMeta meta;

  /**
   * Associates a message sent event with its user and current editing
   * item in the context of using an {@link ItemEditorCommand} menu.
   *
   * @param e message sent event
   */
  public ItemEditorMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
    this.item = Plugin.getData().getEditedItemCache().getEditedItems().get(user.getUniqueId());
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
      user.sendMessage(Message.INVALID_VALUE.getMessage());
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
      user.sendMessage(Message.INVALID_VALUE.getMessage());
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
      user.sendMessage(Message.INVALID_VALUE.getMessage());
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
        user.sendMessage(Message.INVALID_LINE.getMessage());
      } catch (IndexOutOfBoundsException ex) {
        user.sendMessage(Message.LINE_DOES_NOT_EXIST.getMessage());
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
      user.sendMessage(Message.INVALID_LINE.getMessage());
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(Message.LINE_DOES_NOT_EXIST.getMessage());
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
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
      String type = pluginPlayer.getObjectType();
      Attribute attribute = Attribute.valueOf(TextFormatter.formatEnum(type));
      String slot = pluginPlayer.getSlot().getId();
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
      user.sendMessage(Message.INVALID_VALUE.getMessage());
    }
    returnToAttribute();
  }

  /**
   * Sets or removes an item's {@link Key#ATTRIBUTE_LIST Aethel attribute} modifier.
   */
  public void setAethelAttribute() {
    try {
      if (!e.getMessage().equals("-")) {
        setKeyDoubleToList(KeyHeader.ATTRIBUTE.getHeader(), Double.parseDouble(e.getMessage()), Key.ATTRIBUTE_LIST.getNamespacedKey());
      } else {
        removeKeyFromList(KeyHeader.ATTRIBUTE.getHeader(), Key.ATTRIBUTE_LIST.getNamespacedKey());
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
    }
    returnToAethelAttribute();
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchantment() {
    NamespacedKey enchantment = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getObjectType());

    if (!e.getMessage().equals("-")) {
      try {
        int level = Integer.parseInt(e.getMessage());
        if (level > 0 && level < 32768) {
          item.addUnsafeEnchantment(Enchantment.getByKey(enchantment), level);
          user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
        } else {
          user.sendMessage(ChatColor.RED + "Specify a level between 1 - 32767.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid level.");
      }
    } else {
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
    NamespacedKey potionEffectKey = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getObjectType());
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
          user.sendMessage(ChatColor.RED + "Invalid duration");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Invalid effect");
      }
    } else {
      potion.removeCustomEffect(potionEffectType);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
    }
    item.setItemMeta(potion);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToPotion);
  }

  /**
   * Sets or removes an item's {@link Key#PASSIVE_LIST passive ability}.
   */
  public void setPassive() {
    if (!e.getMessage().equals("-")) {
      new PassiveTagModifier(e.getMessage(), user, item).interpretKeyToBeSet();
    } else {
      new PassiveTagModifier(e.getMessage(), user, item).removeKeyFromList();
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToPassive);
  }

  /**
   * Sets or removes an item's {@link Key#ACTIVE_LIST active ability}.
   */
  public void setActive() {
    if (!e.getMessage().equals("-")) {
      switch (ActiveType.valueOf(TextFormatter.formatEnum(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getObjectType())).getEffect()) {
        case MOVEMENT -> readActiveMovement();
        case PROJECTION -> readActiveProjection();
        case SHATTER -> readActiveShatter();
      }
    } else {
      removeKeyFromList(KeyHeader.ACTIVE.getHeader(), Key.ACTIVE_LIST.getNamespacedKey());
    }
    Bukkit.getScheduler().runTask(Plugin.getInstance(), this::returnToActive);
  }

  /**
   * Sets or removes an item's {@link Key Aethel tag}.
   */
  public void setTag() {
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
    String tagType = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getObjectType();
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
   * Checks if the input was formatted correctly before setting the
   * {@link ActiveEffect#MOVEMENT}.
   */
  private void readActiveMovement() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double distance = Double.parseDouble(args[1]);
          setKeyStringToList(KeyHeader.ACTIVE.getHeader(), cooldown + " " + distance, Key.ACTIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the
   * {@link ActiveEffect#PROJECTION}.
   */
  private void readActiveProjection() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 3) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double distance = Double.parseDouble(args[1]);
          try {
            int delay = Integer.parseInt(args[2]);
            setKeyStringToList(KeyHeader.ACTIVE.getHeader(), cooldown + " " + distance + " " + delay, Key.ACTIVE_LIST.getNamespacedKey());
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_DELAY.getMessage());
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(Message.INVALID_DISTANCE.getMessage());
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the
   * {@link ActiveEffect#SHATTER}.
   */
  private void readActiveShatter() {
    String[] args = e.getMessage().split(" ");
    if (args.length == 2) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double radius = Double.parseDouble(args[1]);
          setKeyStringToList(KeyHeader.ACTIVE.getHeader(), cooldown + " " + radius, Key.ACTIVE_LIST.getNamespacedKey());
        } catch (NumberFormatException ex) {
          user.sendMessage(Message.INVALID_RADIUS.getMessage());
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Sets a key with a double value to a {@link KeyHeader key header's} list of keys.
   *
   * @param keyHeader {@link KeyHeader}
   * @param keyValue  key value
   * @param listKey   {@link Key list key}
   */
  private void setKeyDoubleToList(String keyHeader, double keyValue, NamespacedKey listKey) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String slot = pluginPlayer.getSlot().getId();
    String type = pluginPlayer.getObjectType();
    String stringKeyToSet = slot + "." + type;
    NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToSet);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

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
   * Sets a key with a String value to a {@link KeyHeader key header's} list of keys.
   *
   * @param keyHeader {@link KeyHeader}
   * @param keyValue  key value
   * @param listKey   {@link Key list key}
   */
  private void setKeyStringToList(String keyHeader, String keyValue, NamespacedKey listKey) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String slot = pluginPlayer.getSlot().getId();
    String type = pluginPlayer.getObjectType();
    String stringKeyToSet = slot + "." + type;
    NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToSet);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

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
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
  }

  /**
   * Removes a key from a {@link KeyHeader key header's} list of keys.
   * <p>
   * If the list is empty after the operation, the list is also removed.
   *
   * @param keyHeader {@link KeyHeader}
   * @param listKey   {@link Key list key}
   */
  private void removeKeyFromList(String keyHeader, NamespacedKey listKey) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String slot = pluginPlayer.getSlot().getId();
    String type = pluginPlayer.getObjectType();
    String stringKeyToRemove = slot + "." + type;
    NamespacedKey namespacedKeyToRemove = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToRemove);
    PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

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
    user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
  }

  /**
   * Returns to the {@link CosmeticMenu}.
   */
  private void returnToCosmetic() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new CosmeticMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_COSMETIC);
    });
  }

  /**
   * Returns to the {@link AttributeMenu}.
   */
  private void returnToAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AttributeMenu(user, EquipmentSlot.valueOf(pluginPlayer.getSlot().name())).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
    });
  }

  /**
   * Returns to the {@link AethelAttributeMenu}.
   */
  private void returnToAethelAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new AethelAttributeMenu(user, pluginPlayer.getSlot()).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
    });
  }

  /**
   * Returns to the {@link EnchantmentMenu}.
   */
  private void returnToEnchantment() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new EnchantmentMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_ENCHANTMENT);
    });
  }

  /**
   * Returns to the {@link PotionMenu}.
   */
  private void returnToPotion() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new PotionMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_POTION);
    });
  }

  /**
   * Returns to the {@link PassiveMenu}.
   */
  private void returnToPassive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new PassiveMenu(user, pluginPlayer.getSlot(), pluginPlayer.getTrigger()).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_PASSIVE);
    });
  }

  /**
   * Returns to the {@link ActiveMenu}.
   */
  private void returnToActive() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new ActiveMenu(user, pluginPlayer.getSlot()).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_ACTIVE);
    });
  }

  /**
   * Returns to the {@link TagMenu}.
   */
  private void returnToTag() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      user.openInventory(new TagMenu(user).getMainMenu());
      pluginPlayer.setMenu(MenuEvent.Menu.ITEMEDITOR_TAG);
    });
  }
}
