package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.enums.rpg.RpgEquipmentSlot;
import me.dannynguyen.aethel.enums.rpg.abilities.ActiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveAbilityType;
import me.dannynguyen.aethel.enums.rpg.abilities.PassiveTriggerType;
import me.dannynguyen.aethel.rpg.abilities.ActiveAbility;
import me.dannynguyen.aethel.rpg.abilities.PassiveAbility;
import me.dannynguyen.aethel.utils.TextFormatter;
import me.dannynguyen.aethel.utils.abilities.ActiveAbilityInput;
import me.dannynguyen.aethel.utils.abilities.PassiveAbilityInput;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Command invocation that allows the user to retrieve, set,
 * or remove {@link Key Aethel tags} to their main hand item.
 * <p>
 * Registered through {@link me.dannynguyen.aethel.Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": reads the item's {@link Key tags}
 *  <li>"set", "s": sets the item's {@link Key tag}
 *  <li>"remove", "r": removes the item's {@link Key tag}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.23.9
 * @since 1.2.6
 */
public class ItemTagCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ItemTagCommand() {
  }

  /**
   * Executes the AethelItemTag command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command parameters
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.aethelitemtag")) {
        ItemStack item = user.getInventory().getItemInMainHand();
        if (ItemReader.isNotNullOrAir(item)) {
          readRequest(user, args, item);
        } else {
          user.sendMessage(Message.NO_MAIN_HAND_ITEM.getMessage());
        }
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   * @param item main hand item
   */
  private void readRequest(Player user, String[] args, ItemStack item) {
    int numberOfParameters = args.length;
    if (numberOfParameters == 0) {
      user.sendMessage(Message.NO_PARAMETERS.getMessage());
      return;
    }

    String action = args[0].toLowerCase();
    switch (numberOfParameters) {
      case 1 -> {
        switch (action) {
          case "g", "get" -> getTags(user, item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
        }
      }
      case 2 -> {
        switch (action) {
          case "r", "remove" -> removeTag(user, args[1], item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> {
        switch (action) {
          case "s", "set" -> setTag(user, args, item);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Responds with the item's {@link Key Aethel tags}.
   *
   * @param user user
   * @param item main hand item
   */
  private void getTags(Player user, ItemStack item) {
    PersistentDataContainer itemTags = Objects.requireNonNull(item, "Null item").getItemMeta().getPersistentDataContainer();
    StringBuilder aethelTags = new StringBuilder();
    for (NamespacedKey key : itemTags.getKeys()) {
      String keyName = key.getKey();
      if (!keyName.startsWith(KeyHeader.AETHEL.getHeader())) {
        continue;
      }

      keyName = keyName.substring(7);
      if (keyName.startsWith("attribute.")) {
        if (keyName.matches("attribute.list")) {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(itemTags.get(key, PersistentDataType.STRING)).append(" ");
        } else {
          aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(itemTags.get(key, PersistentDataType.DOUBLE)).append(" ");
        }
      } else if (keyName.startsWith("rpg.")) {
        aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(itemTags.get(key, PersistentDataType.INTEGER)).append(" ");
      } else {
        aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(itemTags.get(key, PersistentDataType.STRING)).append(" ");
      }
    }

    if (!aethelTags.isEmpty()) {
      user.sendMessage(ChatColor.GREEN + "[Get Tags] " + aethelTags);
    } else {
      user.sendMessage(ChatColor.RED + "No tags found.");
    }
  }

  /**
   * Removes the {@link Key Aethel tag} from the item.
   *
   * @param user user
   * @param tag  {@link Key Aethel tag} to be removed
   * @param item main hand item
   */
  private void removeTag(Player user, String tag, ItemStack item) {
    if (new TagModifier(user, item, tag).removeTag()) {
      user.sendMessage(ChatColor.RED + "[Removed Tag] " + ChatColor.AQUA + tag);
    } else {
      user.sendMessage(ChatColor.RED + "Tag does not exist.");
    }
  }

  /**
   * Sets the {@link Key Aethel tag} to the item.
   *
   * @param user user
   * @param args user provided parameters
   * @param item main hand item
   */
  private void setTag(Player user, String[] args, ItemStack item) {
    String tag = args[1];
    StringBuilder value = new StringBuilder();
    if (args.length == 3) {
      value = new StringBuilder(args[2]);
    } else {
      for (int i = 2; i < args.length; i++) {
        value.append(args[i]).append(" ");
      }
    }
    new TagModifier(user, item, tag).setTag(value.toString());
  }

  /**
   * Represents an item's {@link Key Aethel tag} set or remove operation.
   *
   * @author Danny Nguyen
   * @version 1.20.7
   * @since 1.13.9
   */
  private static class TagModifier {
    /**
     * Interacting player.
     */
    private final Player user;

    /**
     * ItemStack being modified.
     */
    private final ItemStack item;

    /**
     * ItemStack's meta.
     */
    private final ItemMeta meta;

    /**
     * ItemStack's persistent data tags.
     */
    private final PersistentDataContainer itemTags;

    /**
     * Tag to be modified.
     */
    private final String originalTag;

    /**
     * Modified tag.
     */
    private String tag;

    /**
     * Associates an item with its tag to be modified.
     *
     * @param user user
     * @param item interacting item
     * @param tag  tag to be modified
     */
    TagModifier(@NotNull Player user, @NotNull ItemStack item, @NotNull String tag) {
      this.user = Objects.requireNonNull(user, "Null user");
      this.item = Objects.requireNonNull(item, "Null item");
      this.originalTag = Objects.requireNonNull(tag, "Null tag");
      this.tag = originalTag;
      this.meta = item.getItemMeta();
      this.itemTags = meta.getPersistentDataContainer();
    }

    /**
     * Removes the {@link Key Aethel tag} from the item.
     *
     * @return if the tag was removed
     */
    private boolean removeTag() {
      NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag);
      if (itemTags.has(tagKey, PersistentDataType.STRING) || itemTags.has(tagKey, PersistentDataType.DOUBLE)) {
        itemTags.remove(tagKey);
        if (tag.startsWith("attribute.")) {
          removeAttributeTag();
        } else if (tag.startsWith("passive.")) {
          removePassiveTag();
        } else if (tag.startsWith("active.")) {
          removeActiveTag();
        }
        item.setItemMeta(meta);
        return true;
      }
      return false;
    }

    /**
     * Sets the {@link Key Aethel tag} to the item.
     *
     * @param value tag value
     */
    private void setTag(@NotNull String value) {
      Objects.requireNonNull(value, "Null value");
      if (tag.startsWith("attribute.")) {
        if (!tag.equals("attribute.list")) {
          readAttributeModifier(value);
        } else {
          user.sendMessage(ChatColor.RED + "Cannot set attribute.list directly.");
        }
      } else if (tag.startsWith("passive.")) {
        if (!tag.equals("passive.list")) {
          readPassive(value);
        } else {
          user.sendMessage(ChatColor.RED + "Cannot set passive.list directly.");
        }
      } else if (tag.startsWith("active.")) {
        if (!tag.equals("active.list")) {
          readActive(value);
        } else {
          user.sendMessage(ChatColor.RED + "Cannot set active.list directly.");
        }
      } else {
        itemTags.set(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
      }
    }

    /**
     * Removes an item's {@link Key#ATTRIBUTE_LIST attribute} tag.
     */
    private void removeAttributeTag() {
      if (!tag.equals("attribute.list")) {
        NamespacedKey listKey = Key.ATTRIBUTE_LIST.getNamespacedKey();
        if (itemTags.has(listKey, PersistentDataType.STRING)) {
          tag = tag.substring(10);
          removeKeyFromList(listKey);
        }
      } else {
        for (NamespacedKey key : itemTags.getKeys()) {
          if (key.getKey().startsWith(KeyHeader.ATTRIBUTE.getHeader())) {
            itemTags.remove(key);
          }
        }
      }
    }

    /**
     * Removes an item's {@link Key#PASSIVE_LIST passive} tag.
     */
    private void removePassiveTag() {
      if (!tag.equals("passive.list")) {
        NamespacedKey listKey = Key.PASSIVE_LIST.getNamespacedKey();
        if (itemTags.has(listKey, PersistentDataType.STRING)) {
          tag = tag.substring(8);
          removeKeyFromList(listKey);
        }
      } else {
        for (NamespacedKey key : itemTags.getKeys()) {
          if (key.getKey().startsWith(KeyHeader.PASSIVE.getHeader())) {
            itemTags.remove(key);
          }
        }
      }
    }

    /**
     * Removes an item's {@link Key#ACTIVE_LIST active} tag.
     */
    private void removeActiveTag() {
      if (!tag.equals("active.list")) {
        NamespacedKey listKey = Key.ACTIVE_LIST.getNamespacedKey();
        if (itemTags.has(listKey, PersistentDataType.STRING)) {
          tag = tag.substring(7);
          removeKeyFromList(listKey);
        }
      } else {
        for (NamespacedKey key : itemTags.getKeys()) {
          if (key.getKey().startsWith(KeyHeader.ACTIVE.getHeader())) {
            itemTags.remove(key);
          }
        }
      }
    }

    /**
     * Checks whether the {@link Key#ATTRIBUTE_LIST attribute}
     * tag was formatted correctly before setting its tag and value.
     *
     * @param value tag value
     */
    private void readAttributeModifier(String value) {
      double attributeValue;
      try {
        attributeValue = Double.parseDouble(value);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_VALUE.getMessage());
        return;
      }
      tag = tag.substring(10);
      String[] tagMeta = tag.split("\\.", 2);
      if (tagMeta.length != 2) {
        user.sendMessage(ChatColor.RED + "Did not provide equipment slot and attribute.");
        return;
      }
      try {
        RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
        return;
      }
      try {
        AethelAttribute.valueOf(TextFormatter.formatEnum(tagMeta[1]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Unrecognized Aethel attribute.");
        return;
      }
      setAttributeTag(attributeValue);
    }

    /**
     * Checks whether the {@link PassiveAbility passive}
     * tag was formatted correctly before setting its tag and value.
     *
     * @param value tag value
     */
    private void readPassive(String value) {
      tag = tag.substring(8);
      String[] tagMeta = tag.split("\\.", 3);
      if (tagMeta.length != 3) {
        user.sendMessage(ChatColor.RED + "Did not provide equipment slot and passive ability.");
        return;
      }
      try {
        RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
        return;
      }
      PassiveTriggerType trigger;
      try {
        trigger = PassiveTriggerType.valueOf(TextFormatter.formatEnum(tagMeta[1]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Unrecognized trigger condition.");
        return;
      }
      PassiveAbilityType passiveAbilityType;
      try {
        passiveAbilityType = PassiveAbilityType.valueOf(TextFormatter.formatEnum(tagMeta[2]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Unrecognized passive ability.");
        return;
      }

      switch (passiveAbilityType.getEffect()) {
        case STACK_INSTANCE -> readPassiveStackInstance(value, trigger);
        case CHAIN_DAMAGE -> readPassiveChainDamage(value, trigger);
        case POTION_EFFECT -> readPassivePotionEffect(value, trigger);
      }
    }

    /**
     * Checks whether the {@link ActiveAbility active}
     * tag was formatted correctly before setting its tag and value.
     *
     * @param value tag value
     */
    private void readActive(String value) {
      tag = tag.substring(7);
      String[] tagMeta = tag.split("\\.", 2);
      if (tagMeta.length != 2) {
        user.sendMessage(ChatColor.RED + "Did not provide equipment slot and active ability.");
        return;
      }
      try {
        RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
        return;
      }
      ActiveAbilityType activeAbilityType;
      try {
        activeAbilityType = ActiveAbilityType.valueOf(TextFormatter.formatEnum(tagMeta[1]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Unrecognized active ability.");
        return;
      }

      String[] args = value.split(" ");
      switch (activeAbilityType.getEffect()) {
        case CLEAR_STATUS -> setActiveTag(ActiveAbilityInput.clearStatus(user, args));
        case DISTANCE_DAMAGE -> setActiveTag(ActiveAbilityInput.distanceDamage(user, args));
        case MOVEMENT -> setActiveTag(ActiveAbilityInput.movement(user, args));
        case POTION_EFFECT -> setActiveTag(ActiveAbilityInput.potionEffect(user, args));
        case PROJECTION -> setActiveTag(ActiveAbilityInput.projection(user, args));
        case SHATTER -> setActiveTag(ActiveAbilityInput.shatter(user, args));
        case TELEPORT -> setActiveTag(ActiveAbilityInput.teleport(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before
     * setting the {@link PassiveAbilityType.Effect#CHAIN_DAMAGE}.
     *
     * @param value   tag value
     * @param trigger {@link PassiveTriggerType}
     */
    private void readPassiveChainDamage(String value, PassiveTriggerType trigger) {
      String[] args = value.split(" ");
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setPassiveTag(PassiveAbilityInput.chanceCooldownChainDamage(user, args));
        case HEALTH_COOLDOWN -> setPassiveTag(PassiveAbilityInput.healthCooldownChainDamage(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before
     * setting the {@link PassiveAbilityType.Effect#POTION_EFFECT}.
     *
     * @param value   tag value
     * @param trigger {@link PassiveTriggerType}
     */
    private void readPassivePotionEffect(String value, PassiveTriggerType trigger) {
      String[] args = value.split(" ");
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setPassiveTag(PassiveAbilityInput.chanceCooldownPotionEffect(user, args, trigger));
        case HEALTH_COOLDOWN -> setPassiveTag(PassiveAbilityInput.healthCooldownPotionEffect(user, args));
      }
    }

    /**
     * Checks if the input was formatted correctly before
     * setting the {@link PassiveAbilityType.Effect#STACK_INSTANCE}.
     *
     * @param value   tag value
     * @param trigger {@link PassiveTriggerType}
     */
    private void readPassiveStackInstance(String value, PassiveTriggerType trigger) {
      String[] args = value.split(" ");
      switch (trigger.getCondition()) {
        case CHANCE_COOLDOWN -> setPassiveTag(PassiveAbilityInput.chanceCooldownStackInstance(user, args, trigger));
        case HEALTH_COOLDOWN -> setPassiveTag(PassiveAbilityInput.healthCooldownStackInstance(user, args));
      }
    }

    /**
     * Sets an item's {@link Key#ATTRIBUTE_LIST attribute} tag.
     *
     * @param value tag value
     */
    private void setAttributeTag(double value) {
      NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + tag);
      itemTags.set(tagKey, PersistentDataType.DOUBLE, value);
      setKeyToList(Key.ATTRIBUTE_LIST.getNamespacedKey());
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
    }

    /**
     * Sets an item's {@link PassiveAbility passive} tag.
     *
     * @param value tag value
     */
    private void setPassiveTag(String value) {
      if (value == null) {
        return;
      }

      NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + tag);
      itemTags.set(tagKey, PersistentDataType.STRING, value);
      setKeyToList(Key.PASSIVE_LIST.getNamespacedKey());
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
    }

    /**
     * Sets an item's {@link ActiveAbility active} tag.
     *
     * @param value tag value
     */
    private void setActiveTag(String value) {
      if (value == null) {
        return;
      }

      NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + tag);
      itemTags.set(tagKey, PersistentDataType.STRING, value);
      setKeyToList(Key.ACTIVE_LIST.getNamespacedKey());
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
    }

    /**
     * Removes a key from the list of keys.
     *
     * @param listKey list key
     */
    private void removeKeyFromList(NamespacedKey listKey) {
      List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newKeys = new StringBuilder();
      for (String key : keys) {
        if (!key.equals(tag)) {
          newKeys.append(key).append(" ");
        }
      }
      if (!newKeys.isEmpty()) {
        itemTags.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
      } else {
        itemTags.remove(listKey);
      }
    }

    /**
     * Sets a key to the list of keys.
     *
     * @param listKey list key
     */
    private void setKeyToList(NamespacedKey listKey) {
      if (itemTags.has(listKey, PersistentDataType.STRING)) {
        List<String> keys = new ArrayList<>(List.of(itemTags.get(listKey, PersistentDataType.STRING).split(" ")));
        StringBuilder newKeys = new StringBuilder();
        for (String key : keys) {
          if (!key.equals(tag)) {
            newKeys.append(key).append(" ");
          }
        }
        itemTags.set(listKey, PersistentDataType.STRING, newKeys + tag);
      } else {
        itemTags.set(listKey, PersistentDataType.STRING, tag);
      }
    }
  }
}
