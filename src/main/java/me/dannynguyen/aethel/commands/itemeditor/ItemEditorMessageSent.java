package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.abilities.ActiveAbilityInput;
import me.dannynguyen.aethel.utils.abilities.PassiveAbilityInput;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
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
 * Called with {@link MessageListener}.
 *
 * @author Danny Nguyen
 * @version 1.23.11
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
    new MenuChange().returnToCosmetic();
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
    new MenuChange().returnToCosmetic();
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
      new MenuChange().returnToCosmetic();
      return;
    }

    Damageable durability = (Damageable) meta;
    if (value >= 0) {
      if (value > item.getType().getMaxDurability()) {
        durability.setDamage(0);
      } else {
        durability.setDamage(Math.abs(value - item.getType().getMaxDurability()));
      }
      user.sendMessage(ChatColor.GREEN + "[Set Durability] " + ChatColor.WHITE + e.getMessage());
    } else {
      durability.setDamage(Math.min(Math.abs(value), item.getType().getMaxDurability()));
      user.sendMessage(ChatColor.GREEN + "[Set Damage] " + ChatColor.WHITE + e.getMessage());
    }
    item.setItemMeta(durability);
    new MenuChange().returnToCosmetic();
  }

  /**
   * Sets the item's durability reinforcement.
   */
  public void setDurabilityReinforcement() {
    int value;
    try {
      value = Integer.parseInt(e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      new MenuChange().returnToCosmetic();
      return;
    }
    meta.getPersistentDataContainer().set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, value);
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Durability Reinforcement] " + ChatColor.WHITE + e.getMessage());
    new MenuChange().returnToCosmetic();
  }

  /**
   * Sets the item's max durability reinforcement.
   */
  public void setMaxDurabilityReinforcement() {
    PersistentDataContainer itemTags = meta.getPersistentDataContainer();
    if (!e.getMessage().equals("-")) {
      int value;
      try {
        value = Integer.parseInt(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        new MenuChange().returnToCosmetic();
        return;
      }
      itemTags.set(Key.RPG_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, value);
      itemTags.set(Key.RPG_MAX_DURABILITY.getNamespacedKey(), PersistentDataType.INTEGER, value);
      user.sendMessage(ChatColor.GREEN + "[Set Max Durability Reinforcement] " + ChatColor.WHITE + e.getMessage());
    } else {
      itemTags.remove(Key.RPG_DURABILITY.getNamespacedKey());
      itemTags.remove(Key.RPG_MAX_DURABILITY.getNamespacedKey());
      user.sendMessage(ChatColor.RED + "[Removed Durability Reinforcement]");
    }
    item.setItemMeta(meta);
    new MenuChange().returnToCosmetic();
  }

  /**
   * Sets the item's repair cost.
   */
  public void setRepairCost() {
    try {
      Repairable repair = (Repairable) meta;
      repair.setRepairCost(Integer.parseInt(e.getMessage()));
      item.setItemMeta(repair);
      user.sendMessage(ChatColor.GREEN + "[Set Repair Cost] " + ChatColor.WHITE + e.getMessage());
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
    }
    new MenuChange().returnToCosmetic();
  }

  /**
   * Sets the lore.
   */
  public void setLore() {
    meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', e.getMessage()).split(",, ")));
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Lore]");
    new MenuChange().returnToCosmetic();
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
    new MenuChange().returnToCosmetic();
  }

  /**
   * Edits a line of lore.
   */
  public void editLore() {
    String[] input = e.getMessage().split(" ", 2);
    if (input.length != 2) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      new MenuChange().returnToCosmetic();
      return;
    }
    int line;
    try {
      line = Integer.parseInt(input[0]) - 1;
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_LINE.getMessage());
      new MenuChange().returnToCosmetic();
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
    new MenuChange().returnToCosmetic();
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
      new MenuChange().returnToCosmetic();
      return;
    }

    try {
      List<String> lore = meta.getLore();
      lore.remove(line);
      meta.setLore(lore);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.RED + "[Removed Lore]");
    } catch (IndexOutOfBoundsException ex) {
      user.sendMessage(Message.LINE_DOES_NOT_EXIST.getMessage());
    }
    new MenuChange().returnToCosmetic();
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
      new MenuChange().returnToPotion();
      return;
    }
    int green;
    try {
      green = Integer.parseInt(input[1]);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid Green.");
      new MenuChange().returnToPotion();
      return;
    }
    int blue;
    try {
      blue = Integer.parseInt(input[2]);
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid Blue.");
      new MenuChange().returnToPotion();
      return;
    }

    potion.setColor(org.bukkit.Color.fromRGB(red, green, blue));
    item.setItemMeta(potion);
    user.sendMessage(ChatColor.GREEN + "[Set Potion Color]");
    new MenuChange().returnToPotion();
  }

  /**
   * Sets or removes an item's Minecraft attribute modifier.
   */
  public void setMinecraftAttribute() {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String type = menuInput.getObjectType();
    Attribute attribute = Attribute.valueOf(TextFormatter.formatEnum(type));
    String slot = menuInput.getSlot().getId();
    EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(TextFormatter.formatEnum(slot));

    if (!e.getMessage().equals("-")) {
      double attributeValue;
      try {
        attributeValue = Double.parseDouble(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        new MenuChange().returnToAttribute();
        return;
      }
      new AttributeRemove().removeExistingAttributeModifiers(attribute, equipmentSlot);
      meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), "attribute", attributeValue, AttributeModifier.Operation.ADD_NUMBER, equipmentSlot));
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
    } else {
      new AttributeRemove().removeExistingAttributeModifiers(attribute, equipmentSlot);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + type + "]");
    }
    item.setItemMeta(meta);
    new MenuChange().returnToAttribute();
  }

  /**
   * Sets or removes an item's {@link Key#ATTRIBUTE_LIST Aethel attribute} modifier.
   */
  public void setAethelAttribute() {
    if (!e.getMessage().equals("-")) {
      double attributeValue;
      try {
        attributeValue = Double.parseDouble(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        new MenuChange().returnToAethelAttribute();
        return;
      }
      new KeyChange().setKeyDoubleToList(KeyHeader.ATTRIBUTE.getHeader(), attributeValue, Key.ATTRIBUTE_LIST.getNamespacedKey());
    } else {
      new KeyChange().removeKeyFromList(KeyHeader.ATTRIBUTE.getHeader(), Key.ATTRIBUTE_LIST.getNamespacedKey());
    }
    new MenuChange().returnToAethelAttribute();
  }

  /**
   * Sets or removes an item's enchantment.
   */
  public void setEnchantment() {
    NamespacedKey enchantment = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getObjectType());

    if (!e.getMessage().equals("-")) {
      int level;
      try {
        level = Integer.parseInt(e.getMessage());
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid level.");
        new MenuChange().returnToEnchantment();
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
    new MenuChange().returnToEnchantment();
  }

  /**
   * Sets or removes an item's potion effect.
   */
  public void setPotionEffect() {
    PotionMeta potion = (PotionMeta) meta;
    NamespacedKey potionEffectKey = NamespacedKey.minecraft(Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getObjectType());
    PotionEffectType potionEffectType = PotionEffectType.getByKey(potionEffectKey);

    if (!e.getMessage().equals("-")) {
      String[] input = e.getMessage().split(" ", 3);
      if (input.length != 3) {
        user.sendMessage("Invalid effect.");
        new MenuChange().returnToPotion();
        return;
      }
      int duration;
      try {
        duration = Integer.parseInt(input[0]);
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid duration");
        new MenuChange().returnToPotion();
        return;
      }
      int amplifier;
      try {
        amplifier = Integer.parseInt(input[1]);
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid amplifier.");
        new MenuChange().returnToPotion();
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
          new MenuChange().returnToPotion();
          return;
        }
      }
    } else {
      potion.removeCustomEffect(potionEffectType);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(potionEffectKey.getKey()) + "]");
    }
    item.setItemMeta(potion);
    new MenuChange().returnToPotion();
  }

  /**
   * Sets or removes an item's {@link Key#PASSIVE_LIST passive ability}.
   */
  public void setPassive() {
    if (!e.getMessage().equals("-")) {
      new PassiveChange().interpretKeyToBeSet();
    } else {
      new PassiveChange().removeKeyFromList();
    }
    new MenuChange().returnToPassive();
  }

  /**
   * Sets or removes an item's {@link Key#ACTIVE_LIST active ability}.
   */
  public void setActive() {
    if (!e.getMessage().equals("-")) {
      new ActiveChange().interpretKeyToBeSet();
    } else {
      new KeyChange().removeKeyFromList(KeyHeader.ACTIVE.getHeader(), Key.ACTIVE_LIST.getNamespacedKey());
    }
    new MenuChange().returnToActive();
  }

  /**
   * Sets or removes an item's {@link Key Aethel tag}.
   */
  public void setTag() {
    PersistentDataContainer itemTags = meta.getPersistentDataContainer();
    String tagType = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().getObjectType();
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tagType);

    if (!e.getMessage().equals("-")) {
      itemTags.set(tagKey, PersistentDataType.STRING, e.getMessage());
      user.sendMessage(ChatColor.GREEN + "[Set " + tagType + "]");
    } else {
      itemTags.remove(tagKey);
      user.sendMessage(ChatColor.RED + "[Removed " + tagType + "]");
    }
    item.setItemMeta(meta);
    new MenuChange().returnToTag();
  }

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Returns to the {@link CosmeticMenu}.
     */
    private void returnToCosmetic() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new CosmeticMenu(user).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_COSMETIC);
      });
    }

    /**
     * Returns to the {@link AttributeMenu}.
     */
    private void returnToAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new AttributeMenu(user, EquipmentSlot.valueOf(menuInput.getSlot().name())).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_MINECRAFT_ATTRIBUTE);
      });
    }

    /**
     * Returns to the {@link AethelAttributeMenu}.
     */
    private void returnToAethelAttribute() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new AethelAttributeMenu(user, menuInput.getSlot()).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_AETHEL_ATTRIBUTE);
      });
    }

    /**
     * Returns to the {@link EnchantmentMenu}.
     */
    private void returnToEnchantment() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new EnchantmentMenu(user).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ENCHANTMENT);
      });
    }

    /**
     * Returns to the {@link PotionMenu}.
     */
    private void returnToPotion() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new PotionMenu(user).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_POTION);
      });
    }

    /**
     * Returns to the {@link PassiveMenu}.
     */
    private void returnToPassive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new PassiveMenu(user, menuInput.getSlot(), menuInput.getTrigger()).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_PASSIVE);
      });
    }

    /**
     * Returns to the {@link ActiveMenu}.
     */
    private void returnToActive() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new ActiveMenu(user, menuInput.getSlot()).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_ACTIVE);
      });
    }

    /**
     * Returns to the {@link TagMenu}.
     */
    private void returnToTag() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
        user.openInventory(new TagMenu(user).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.ITEMEDITOR_TAG);
      });
    }
  }

  /**
   * Represents an item's attribute removal operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class AttributeRemove {
    /**
     * No parameter constructor.
     */
    AttributeRemove() {
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
  }

  /**
   * Represents an item key change operation.
   *
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private class KeyChange {
    /**
     * Sets a key with a double value to a {@link KeyHeader key header's} list of keys.
     *
     * @param keyHeader {@link KeyHeader}
     * @param keyValue  key value
     * @param listKey   {@link Key list key}
     */
    private void setKeyDoubleToList(String keyHeader, double keyValue, NamespacedKey listKey) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String slot = menuInput.getSlot().getId();
      String type = menuInput.getObjectType();
      String stringKeyToSet = slot + "." + type;
      NamespacedKey namespacedKeyToSet = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToSet);
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();

      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(stringKeyToSet)) {
            newKeys.append(key).append(" ");
          }
        }
        itemTags.set(listKey, PersistentDataType.STRING, newKeys + stringKeyToSet);
      } else {
        itemTags.set(listKey, PersistentDataType.STRING, stringKeyToSet);
      }
      itemTags.set(namespacedKeyToSet, PersistentDataType.DOUBLE, keyValue);
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
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String slot = menuInput.getSlot().getId();
      String type = menuInput.getObjectType();
      String stringKeyToRemove = slot + "." + type;
      NamespacedKey namespacedKeyToRemove = new NamespacedKey(Plugin.getInstance(), keyHeader + stringKeyToRemove);
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();

      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(stringKeyToRemove)) {
            newKeys.append(key).append(" ");
          }
        }
        if (!newKeys.isEmpty()) {
          itemTags.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
        } else {
          itemTags.remove(listKey);
        }
        itemTags.remove(namespacedKeyToRemove);
      }
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }
  }

  /**
   * Represents a {@link Key#PASSIVE_LIST passive tag} set or remove operation.
   *
   * @author Danny Nguyen
   * @version 1.20.11
   * @since 1.15.13
   */
  private class PassiveChange {
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
    private final PersistentDataContainer itemTags = meta.getPersistentDataContainer();

    /**
     * {@link MenuInput#getSlot()}
     */
    private final String slot;

    /**
     * {@link MenuInput#getTrigger()}
     */
    private final String trigger;

    /**
     * {@link MenuInput#getObjectType()}
     */
    private final String type;

    /**
     * Interacting key.
     */
    private final String interactingKey;

    /**
     * No parameter constructor.
     */
    private PassiveChange() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      this.slot = menuInput.getSlot().getId();
      this.trigger = menuInput.getTrigger().getId();
      this.type = menuInput.getObjectType();
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

      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        itemTags.set(listKey, PersistentDataType.STRING, newKeys + interactingKey);
      } else {
        itemTags.set(listKey, PersistentDataType.STRING, interactingKey);
      }
      itemTags.set(new NamespacedKey(Plugin.getInstance(), passiveHeader + interactingKey), PersistentDataType.STRING, keyValue);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(trigger) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }

    /**
     * Removes a key from a {@link KeyHeader key header's} list of keys.
     * <p>
     * If the list is empty after the operation, the list is also removed.
     */
    private void removeKeyFromList() {
      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        if (!newKeys.isEmpty()) {
          itemTags.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
        } else {
          itemTags.remove(listKey);
        }
        itemTags.remove(new NamespacedKey(Plugin.getInstance(), passiveHeader + interactingKey));
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
  private class ActiveChange {
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
     * {@link MenuInput#getSlot()}
     */
    private final String slot;

    /**
     * {@link MenuInput#getObjectType()}
     */
    private final String type;

    /**
     * Interacting key.
     */
    private final String interactingKey;

    /**
     * No parameter constructor.
     */
    private ActiveChange() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      this.slot = menuInput.getSlot().getId();
      this.type = menuInput.getObjectType();
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
      PersistentDataContainer itemTags = meta.getPersistentDataContainer();

      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(interactingKey)) {
            newKeys.append(key).append(" ");
          }
        }
        itemTags.set(listKey, PersistentDataType.STRING, newKeys + interactingKey);
      } else {
        itemTags.set(listKey, PersistentDataType.STRING, interactingKey);
      }
      itemTags.set(namespacedKeyToSet, PersistentDataType.STRING, keyValue);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
    }
  }
}
