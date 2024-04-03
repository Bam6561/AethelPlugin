package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.listeners.MessageEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.abilities.ActiveAbilityInput;
import me.dannynguyen.aethel.utils.abilities.PassiveAbilityInput;
import me.dannynguyen.aethel.utils.item.ItemDurability;
import me.dannynguyen.aethel.utils.item.ItemRepairCost;
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
 * @version 1.20.2
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
    int value;
    try {
      value = Integer.parseInt(e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      returnToCosmetic();
      return;
    }

    if (value >= 0) {
      ItemDurability.setDurability(item, value);
      user.sendMessage(ChatColor.GREEN + "[Set Durability] " + ChatColor.WHITE + e.getMessage());
    } else {
      ItemDurability.setDamage(item, Math.abs(value));
      user.sendMessage(ChatColor.GREEN + "[Set Damage] " + ChatColor.WHITE + e.getMessage());
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
    if (input.length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      returnToCosmetic();
      return;
    }
    int line;
    try {
      line = Integer.parseInt(input[0]) - 1;
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_LINE.getMessage());
      returnToCosmetic();
      return;
    }

    try {
      List<String> lore = meta.getLore();
      lore.set(line, ChatColor.translateAlternateColorCodes('&', input[1]));
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Edited Lore]");
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(Message.LINE_DOES_NOT_EXIST.getMessage());
    }
    returnToCosmetic();
  }

  /**
   * Removes a line of lore.
   */
  public void removeLore() {
    int line;
    try {
      line = Integer.parseInt(e.getMessage()) - 1;
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_LINE.getMessage());
      returnToCosmetic();
      return;
    }

    try {
      List<String> lore = meta.getLore();
      lore.remove(line);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Removed Lore]");
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
    if (input.length != 3) {
      user.sendMessage(ChatColor.RED + "Invalid RGB.");
      return;
    }

    PotionMeta potion = (PotionMeta) meta;
    int red;
    try {
      red = Integer.parseInt(input[0]);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid Red.");
      returnToPotion();
      return;
    }
    int green;
    try {
      green = Integer.parseInt(input[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid Green.");
      returnToPotion();
      return;
    }
    int blue;
    try {
      blue = Integer.parseInt(input[2]);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid Blue.");
      returnToPotion();
      return;
    }

    potion.setColor(org.bukkit.Color.fromRGB(red, green, blue));
    item.setItemMeta(potion);
    user.sendMessage(ChatColor.GREEN + "[Set Potion Color]");
    returnToPotion();
  }

  /**
   * Sets or removes an item's Minecraft attribute modifier.
   */
  public void setMinecraftAttribute() {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid);
    String type = pluginPlayer.getObjectType();
    Attribute attribute = Attribute.valueOf(TextFormatter.formatEnum(type));
    String slot = pluginPlayer.getSlot().getId();
    EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(TextFormatter.formatEnum(slot));

    if (!e.getMessage().equals("-")) {
      double attributeValue;
      try {
        attributeValue = Double.parseDouble(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        returnToAttribute();
        return;
      }
      removeExistingAttributeModifiers(attribute, equipmentSlot);
      meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), "attribute", attributeValue, AttributeModifier.Operation.ADD_NUMBER, equipmentSlot));
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
    } else {
      removeExistingAttributeModifiers(attribute, equipmentSlot);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
    }
    item.setItemMeta(meta);
    returnToAttribute();
  }

  /**
   * Sets or removes an item's {@link Key#ATTRIBUTE_LIST Aethel attribute} modifier.
   */
  public void setAethelAttribute() {
    double attributeValue;
    try {
      attributeValue = Double.parseDouble(e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      returnToAethelAttribute();
      return;
    }

    if (!e.getMessage().equals("-")) {
      setKeyDoubleToList(KeyHeader.ATTRIBUTE.getHeader(), attributeValue, Key.ATTRIBUTE_LIST.getNamespacedKey());
    } else {
      removeKeyFromList(KeyHeader.ATTRIBUTE.getHeader(), Key.ATTRIBUTE_LIST.getNamespacedKey());
    }
    returnToAethelAttribute();
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchantment() {
    NamespacedKey enchantment = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getObjectType());

    if (!e.getMessage().equals("-")) {
      int level;
      try {
        level = Integer.parseInt(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid level.");
        returnToEnchantment();
        return;
      }

      if (level > 0 && level < 32768) {
        item.addUnsafeEnchantment(Enchantment.getByKey(enchantment), level);
        user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
      } else {
        user.sendMessage(ChatColor.RED + "Specify a level between 1 - 32767.");
      }
    } else {
      item.removeEnchantment(Enchantment.getByKey(enchantment));
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(enchantment.getKey()) + "]");
    }
    returnToEnchantment();
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
      if (input.length != 3) {
        user.sendMessage("Invalid effect.");
        returnToPotion();
        return;
      }
      int duration;
      try {
        duration = Integer.parseInt(input[0]);
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid duration");
        returnToPotion();
        return;
      }
      int amplifier;
      try {
        amplifier = Integer.parseInt(input[1]);
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid amplifier.");
        returnToPotion();
        return;
      }

      switch (input[2]) {
        case "true", "false" -> {
          boolean ambient = Boolean.parseBoolean(input[2]);
          PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient);
          potion.addCustomEffect(potionEffect, true);
          user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
        }
        default -> {
          user.sendMessage("Invalid true/false.");
          returnToPotion();
          return;
        }
      }
    } else {
      potion.removeCustomEffect(potionEffectType);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
    }
    item.setItemMeta(potion);
    returnToPotion();
  }

  /**
   * Sets or removes an item's {@link Key#PASSIVE_LIST passive ability}.
   */
  public void setPassive() {
    if (!e.getMessage().equals("-")) {
      new PassiveTagModification().interpretKeyToBeSet();
    } else {
      new PassiveTagModification().removeKeyFromList();
    }
    returnToPassive();
  }

  /**
   * Sets or removes an item's {@link Key#ACTIVE_LIST active ability}.
   */
  public void setActive() {
    if (!e.getMessage().equals("-")) {
      new ActiveTagSetter().interpretKeyToBeSet();
    } else {
      removeKeyFromList(KeyHeader.ACTIVE.getHeader(), Key.ACTIVE_LIST.getNamespacedKey());
    }
    returnToActive();
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
    returnToTag();
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

  /**
   * Represents a {@link Key#PASSIVE_LIST passive tag} set or remove operation.
   *
   * @author Danny Nguyen
   * @version 1.20.11
   * @since 1.15.13
   */
  private class PassiveTagModification {
    /**
     * {@link Key#PASSIVE_LIST}
     */
    private static final NamespacedKey listKey = Key.PASSIVE_LIST.getNamespacedKey();

    /**
     * {@link KeyHeader#PASSIVE}
     */
    private static final String passiveHeader = KeyHeader.PASSIVE.getHeader();

    /**
     * User input.
     */
    private final String[] args = e.getMessage().split(" ");

    /**
     * ItemStack's persistent tags.
     */
    private final PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

    /**
     * {@link PluginPlayer#getSlot()}
     */
    private final String slot;

    /**
     * {@link PluginPlayer#getTrigger()}
     */
    private final String trigger;

    /**
     * {@link PluginPlayer#getObjectType()}
     */
    private final String type;

    /**
     * Interacting key.
     */
    private final String interactingKey;

    /**
     * No parameter constructor.
     */
    private PassiveTagModification() {
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
      this.slot = pluginPlayer.getSlot().getId();
      this.trigger = pluginPlayer.getTrigger().getId();
      this.type = pluginPlayer.getObjectType();
      this.interactingKey = slot + "." + trigger + "." + type;
    }

    /**
     * Determines the type of {@link Key#PASSIVE_LIST ability tag} to be set.
     */
    private void interpretKeyToBeSet() {
      PassiveAbilityType.Effect effect = PassiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();
      PassiveTriggerType triggerType = PassiveTriggerType.valueOf(TextFormatter.formatEnum(trigger));
      switch (effect) {
        case BUFF -> readBuff(triggerType);
        case CHAIN_DAMAGE -> readChainDamage(triggerType);
        case STACK_INSTANCE -> readStackInstance(triggerType);
        case POTION_EFFECT -> readPotionEffect(triggerType);
      }
    }

    /**
     * Checks if the input was formatted correctly before setting
     * the {@link PassiveAbilityType.Effect#BUFF}.
     *
     * @param trigger {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType}
     */
    private void readBuff(PassiveTriggerType trigger) {
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.chanceCooldownBuff(user, args));
        case HEALTH_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.healthCooldownBuff(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before setting
     * the {@link PassiveAbilityType.Effect#CHAIN_DAMAGE}.
     *
     * @param trigger {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType}
     */
    private void readChainDamage(PassiveTriggerType trigger) {
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.chanceCooldownChainDamage(user, args));
        case HEALTH_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.healthCooldownChainDamage(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before setting
     * the {@link PassiveAbilityType.Effect#POTION_EFFECT}.
     *
     * @param trigger {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType}
     */
    private void readPotionEffect(PassiveTriggerType trigger) {
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.chanceCooldownPotionEffect(user, args, trigger));
        case HEALTH_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.healthCooldownPotionEffect(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before setting
     * the {@link PassiveAbilityType.Effect#STACK_INSTANCE}.
     *
     * @param trigger {@link me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType}
     */
    private void readStackInstance(PassiveTriggerType trigger) {
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.chanceCooldownStackInstance(user, args, trigger));
        case HEALTH_COOLDOWN -> setKeyStringToList(PassiveAbilityInput.healthCooldownStackInstance(user, args));
      }
    }

    /**
     * Sets a key with a String value to a {@link KeyHeader key header's} list of keys.
     *
     * @param keyValue key value
     */
    private void setKeyStringToList(String keyValue) {
      if (keyValue == null) {
        return;
      }

      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        dataContainer.set(listKey, PersistentDataType.STRING, newKeys + interactingKey);
      } else {
        dataContainer.set(listKey, PersistentDataType.STRING, interactingKey);
      }
      dataContainer.set(new NamespacedKey(Plugin.getInstance(), passiveHeader + interactingKey), PersistentDataType.STRING, keyValue);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(trigger) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }

    /**
     * Removes a key from a {@link KeyHeader key header's} list of keys.
     * <p>
     * If the list is empty after the operation, the list is also removed.
     */
    private void removeKeyFromList() {
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        if (!newKeys.isEmpty()) {
          dataContainer.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
        } else {
          dataContainer.remove(listKey);
        }
        dataContainer.remove(new NamespacedKey(Plugin.getInstance(), passiveHeader + interactingKey));
      }
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(trigger) + " " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }
  }

  /**
   * Represents a {@link Key#ACTIVE_LIST active tag} set operation.
   *
   * @author Danny Nguyen
   * @version 1.20.10
   * @since 1.19.4
   */
  private class ActiveTagSetter {
    /**
     * {@link Key#ACTIVE_LIST}
     */
    private static final NamespacedKey listKey = Key.ACTIVE_LIST.getNamespacedKey();

    /**
     * {@link KeyHeader#ACTIVE}
     */
    private static final String activeHeader = KeyHeader.ACTIVE.getHeader();

    /**
     * User input.
     */
    private final String[] args = e.getMessage().split(" ");

    /**
     * {@link PluginPlayer#getSlot()}
     */
    private final String slot;

    /**
     * {@link PluginPlayer#getObjectType()}
     */
    private final String type;

    /**
     * Interacting key.
     */
    private final String interactingKey;

    /**
     * No parameter constructor.
     */
    private ActiveTagSetter() {
      PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
      this.slot = pluginPlayer.getSlot().getId();
      this.type = pluginPlayer.getObjectType();
      this.interactingKey = slot + "." + type;
    }

    /**
     * Determines the type of {@link Key#ACTIVE_LIST ability tag} to be set.
     */
    private void interpretKeyToBeSet() {
      ActiveAbilityType.Effect effect = ActiveAbilityType.valueOf(TextFormatter.formatEnum(type)).getEffect();
      switch (effect) {
        case BUFF -> setKeyStringToList(ActiveAbilityInput.buff(user, args));
        case CLEAR_STATUS -> setKeyStringToList(ActiveAbilityInput.clearStatus(user, args));
        case DISTANCE_DAMAGE -> setKeyStringToList(ActiveAbilityInput.distanceDamage(user, args));
        case MOVEMENT -> setKeyStringToList(ActiveAbilityInput.movement(user, args));
        case POTION_EFFECT -> setKeyStringToList(ActiveAbilityInput.potionEffect(user, args));
        case PROJECTION -> setKeyStringToList(ActiveAbilityInput.projection(user, args));
        case SHATTER -> setKeyStringToList(ActiveAbilityInput.shatter(user, args));
        case TELEPORT -> setKeyStringToList(ActiveAbilityInput.teleport(user, args));
      }
    }

    /**
     * Sets a key with a String value to a {@link KeyHeader key header's} list of keys.
     *
     * @param keyValue key value
     */
    private void setKeyStringToList(String keyValue) {
      if (keyValue == null) {
        return;
      }

      NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), activeHeader + interactingKey);
      PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        dataContainer.set(listKey, PersistentDataType.STRING, newKeys + interactingKey);
      } else {
        dataContainer.set(listKey, PersistentDataType.STRING, interactingKey);
      }
      dataContainer.set(namespacedKeyToSet, PersistentDataType.STRING, keyValue);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }
  }
}
