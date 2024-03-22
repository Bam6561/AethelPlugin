package me.dannynguyen.aethel.commands.aetheltag;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PluginKey;
import me.dannynguyen.aethel.rpg.enums.*;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
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
 * Represents an item's {@link PluginKey Aethel tag} set or remove operation.
 * <p>
 * Used with {@link TagCommand}.
 *
 * @author Danny Nguyen
 * @version 1.17.14
 * @since 1.13.9
 */
class TagModifier {
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
  private final PersistentDataContainer dataContainer;

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
    this.dataContainer = meta.getPersistentDataContainer();
  }

  /**
   * Removes the {@link PluginKey Aethel tag} from the item.
   *
   * @return if the tag was removed
   */
  protected boolean removeTag() {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag);
    if (dataContainer.has(tagKey, PersistentDataType.STRING) || dataContainer.has(tagKey, PersistentDataType.DOUBLE)) {
      dataContainer.remove(tagKey);
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
   * Sets the {@link PluginKey Aethel tag} to the item.
   *
   * @param value tag value
   */
  protected void setTag(@NotNull String value) {
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
      dataContainer.set(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag), PersistentDataType.STRING, value);
      item.setItemMeta(meta);
      user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
    }
  }

  /**
   * Removes an item's {@link PluginKey#ATTRIBUTE_LIST attribute} tag.
   */
  private void removeAttributeTag() {
    if (!tag.equals("attribute.list")) {
      NamespacedKey listKey = PluginKey.ATTRIBUTE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        tag = tag.substring(10);
        removeKeyFromList(listKey);
      }
    } else {
      for (NamespacedKey key : dataContainer.getKeys()) {
        if (key.getKey().startsWith(KeyHeader.ATTRIBUTE.getHeader())) {
          dataContainer.remove(key);
        }
      }
    }
  }

  /**
   * Removes an item's {@link PluginKey#PASSIVE_LIST passive} tag.
   */
  private void removePassiveTag() {
    if (!tag.equals("passive.list")) {
      NamespacedKey listKey = PluginKey.PASSIVE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        tag = tag.substring(8);
        removeKeyFromList(listKey);
      }
    } else {
      for (NamespacedKey key : dataContainer.getKeys()) {
        if (key.getKey().startsWith(KeyHeader.PASSIVE.getHeader())) {
          dataContainer.remove(key);
        }
      }
    }
  }

  /**
   * Removes an item's {@link PluginKey#ACTIVE_LIST active} tag.
   */
  private void removeActiveTag() {
    if (!tag.equals("active.list")) {
      NamespacedKey listKey = PluginKey.ACTIVE_LIST.getNamespacedKey();
      if (dataContainer.has(listKey, PersistentDataType.STRING)) {
        tag = tag.substring(7);
        removeKeyFromList(listKey);
      }
    } else {
      for (NamespacedKey key : dataContainer.getKeys()) {
        if (key.getKey().startsWith(KeyHeader.ACTIVE.getHeader())) {
          dataContainer.remove(key);
        }
      }
    }
  }

  /**
   * Checks whether the {@link PluginKey#ATTRIBUTE_LIST attribute}
   * tag was formatted correctly before setting its tag and value.
   *
   * @param value tag value
   */
  private void readAttributeModifier(String value) {
    try {
      double attributeValue = Double.parseDouble(value);
      tag = tag.substring(10);
      String[] tagMeta = tag.split("\\.", 2);
      if (tagMeta.length == 2) {
        try {
          RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
          try {
            AethelAttributeType.valueOf(TextFormatter.formatEnum(tagMeta[1]));
            setAttributeTag(attributeValue);
          } catch (IllegalArgumentException ex) {
            user.sendMessage(ChatColor.RED + "Unrecognized Aethel attribute.");
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
        }
      } else {
        user.sendMessage(ChatColor.RED + "Did not provide equipment slot and attribute.");
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
    }
  }

  /**
   * Checks whether the {@link me.dannynguyen.aethel.rpg.ability.PassiveAbility passive}
   * tag was formatted correctly before setting its tag and value.
   *
   * @param value tag value
   */
  private void readPassive(String value) {
    tag = tag.substring(8);
    String[] tagMeta = tag.split("\\.", 3);
    if (tagMeta.length == 3) {
      try {
        RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
        try {
          TriggerCondition triggerCondition = Trigger.valueOf(TextFormatter.formatEnum(tagMeta[1])).getCondition();
          try {
            PassiveType passiveType = PassiveType.valueOf(TextFormatter.formatEnum(tagMeta[2]));
            switch (passiveType.getEffect()) {
              case STACK_INSTANCE -> readPassiveStackInstance(value, triggerCondition);
              case CHAIN_DAMAGE -> readPassiveChainDamage(value, triggerCondition);
            }
          } catch (IllegalArgumentException ex) {
            user.sendMessage(ChatColor.RED + "Unrecognized passive ability.");
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Unrecognized trigger condition.");
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
      }
    } else {
      user.sendMessage(ChatColor.RED + "Did not provide equipment slot and passive ability.");
    }
  }

  /**
   * Checks whether the {@link me.dannynguyen.aethel.rpg.ability.ActiveAbility active}
   * tag was formatted correctly before setting its tag and value.
   *
   * @param value tag value
   */
  private void readActive(String value) {
    tag = tag.substring(7);
    String[] tagMeta = tag.split("\\.", 2);
    if (tagMeta.length == 2) {
      try {
        RpgEquipmentSlot.valueOf(TextFormatter.formatEnum(tagMeta[0]));
        try {
          ActiveType activeType = ActiveType.valueOf(TextFormatter.formatEnum(tagMeta[1]));
          switch (activeType.getEffect()) {
            case MOVEMENT -> readActiveMovement(value);
            case PROJECTION -> readActiveProjection(value);
            case SHATTER -> readActiveShatter(value);
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Unrecognized active ability.");
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_EQUIPMENT_SLOT.getMessage());
      }
    } else {
      user.sendMessage(ChatColor.RED + "Did not provide equipment slot and active ability.");
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link PassiveEffect#STACK_INSTANCE}.
   *
   * @param value     tag value
   * @param condition {@link TriggerCondition}
   */
  private void readPassiveStackInstance(String value, TriggerCondition condition) {
    String[] args = value.split(" ");
    switch (condition) {
      case CHANCE_COOLDOWN -> {
        if (args.length == 5) {
          try {
            double chance = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              switch (args[2]) {
                case "true", "false" -> {
                  boolean self = Boolean.parseBoolean(args[2]);
                  try {
                    int stacks = Integer.parseInt(args[3]);
                    try {
                      int ticks = Integer.parseInt(args[4]);
                      setPassiveTag(chance + " " + cooldown + " " + self + " " + stacks + " " + ticks);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_TICKS.getMessage());
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(Message.INVALID_STACKS.getMessage());
                  }
                }
                default -> user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_CHANCE.getMessage());
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case HEALTH_COOLDOWN -> {
        if (args.length == 5) {
          try {
            double percentHealth = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              switch (args[2]) {
                case "true", "false" -> {
                  boolean self = Boolean.parseBoolean(args[2]);
                  try {
                    int stacks = Integer.parseInt(args[3]);
                    try {
                      int ticks = Integer.parseInt(args[4]);
                      setPassiveTag(percentHealth + " " + cooldown + " " + self + " " + stacks + " " + ticks);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_TICKS.getMessage());
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(Message.INVALID_STACKS.getMessage());
                  }
                }
                default -> user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_HEALTH.getMessage());
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link PassiveEffect#CHAIN_DAMAGE}.
   *
   * @param value     tag value
   * @param condition {@link TriggerCondition}
   */
  private void readPassiveChainDamage(String value, TriggerCondition condition) {
    String[] args = value.split(" ");
    switch (condition) {
      case CHANCE_COOLDOWN -> {
        if (args.length == 5) {
          try {
            double chance = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              switch (args[2]) {
                case "true", "false" -> {
                  boolean self = Boolean.parseBoolean(args[2]);
                  try {
                    double damage = Integer.parseInt(args[3]);
                    try {
                      double distance = Double.parseDouble(args[4]);
                      setPassiveTag(chance + " " + cooldown + " " + self + " " + damage + " " + distance);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(Message.INVALID_DAMAGE.getMessage());
                  }
                }
                default -> user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_CHANCE.getMessage());
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case HEALTH_COOLDOWN -> {
        if (args.length == 5) {
          try {
            double percentHealth = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              switch (args[2]) {
                case "true", "false" -> {
                  boolean self = Boolean.parseBoolean(args[2]);
                  try {
                    double damage = Integer.parseInt(args[3]);
                    try {
                      double distance = Double.parseDouble(args[4]);
                      setPassiveTag(percentHealth + " " + cooldown + " " + self + " " + damage + " " + distance);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_DISTANCE.getMessage());
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(Message.INVALID_DAMAGE.getMessage());
                  }
                }
                default -> user.sendMessage(Message.INVALID_BOOLEAN.getMessage());
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(Message.INVALID_COOLDOWN.getMessage());
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_HEALTH.getMessage());
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveEffect#MOVEMENT}.
   *
   * @param value tag value
   */
  private void readActiveMovement(String value) {
    String[] args = value.split(" ");
    if (args.length == 2) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double distance = Double.parseDouble(args[1]);
          setActiveTag(cooldown + " " + distance);
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
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveEffect#PROJECTION}.
   *
   * @param value tag value
   */
  private void readActiveProjection(String value) {
    String[] args = value.split(" ");
    if (args.length == 3) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double distance = Double.parseDouble(args[1]);
          try {
            int delay = Integer.parseInt(args[2]);
            setActiveTag(cooldown + " " + distance + " " + delay);
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
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveEffect#SHATTER}.
   *
   * @param value tag value
   */
  private void readActiveShatter(String value) {
    String[] args = value.split(" ");
    if (args.length == 2) {
      try {
        int cooldown = Integer.parseInt(args[0]);
        try {
          double radius = Double.parseDouble(args[1]);
          setActiveTag(cooldown + " " + radius);
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
   * Sets an item's {@link PluginKey#ATTRIBUTE_LIST attribute} tag.
   *
   * @param value tag value
   */
  private void setAttributeTag(Double value) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + tag);
    dataContainer.set(tagKey, PersistentDataType.DOUBLE, value);
    setKeyToList(PluginKey.ATTRIBUTE_LIST.getNamespacedKey());
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
  }

  /**
   * Sets an item's {@link me.dannynguyen.aethel.rpg.ability.PassiveAbility passive} tag.
   *
   * @param value tag value
   */
  private void setPassiveTag(String value) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.PASSIVE.getHeader() + tag);
    dataContainer.set(tagKey, PersistentDataType.STRING, value);
    setKeyToList(PluginKey.PASSIVE_LIST.getNamespacedKey());
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
  }

  /**
   * Sets an item's {@link me.dannynguyen.aethel.rpg.ability.ActiveAbility active} tag.
   *
   * @param value tag value
   */
  private void setActiveTag(String value) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ACTIVE.getHeader() + tag);
    dataContainer.set(tagKey, PersistentDataType.STRING, value);
    setKeyToList(PluginKey.ACTIVE_LIST.getNamespacedKey());
    item.setItemMeta(meta);
    user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + originalTag.toLowerCase() + " " + ChatColor.WHITE + value);
  }

  /**
   * Removes a key from the list of keys.
   *
   * @param listKey list key
   */
  private void removeKeyFromList(NamespacedKey listKey) {
    List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
    StringBuilder newKeys = new StringBuilder();
    for (String key : keys) {
      if (!key.equals(tag)) {
        newKeys.append(key).append(" ");
      }
    }
    if (!newKeys.isEmpty()) {
      dataContainer.set(listKey, PersistentDataType.STRING, newKeys.toString().trim());
    } else {
      dataContainer.remove(listKey);
    }
  }

  /**
   * Sets a key to the list of keys.
   *
   * @param listKey list key
   */
  private void setKeyToList(NamespacedKey listKey) {
    if (dataContainer.has(listKey, PersistentDataType.STRING)) {
      List<String> keys = new ArrayList<>(List.of(dataContainer.get(listKey, PersistentDataType.STRING).split(" ")));
      StringBuilder newKeys = new StringBuilder();
      for (String key : keys) {
        if (!key.equals(tag)) {
          newKeys.append(key).append(" ");
        }
      }
      dataContainer.set(listKey, PersistentDataType.STRING, newKeys + tag);
    } else {
      dataContainer.set(listKey, PersistentDataType.STRING, tag);
    }
  }
}
