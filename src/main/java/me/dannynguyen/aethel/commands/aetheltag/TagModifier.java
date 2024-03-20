package me.dannynguyen.aethel.commands.aetheltag;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PluginNamespacedKey;
import me.dannynguyen.aethel.rpg.enums.*;
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
 * Represents a set or remove operation for an item's {@link PluginNamespacedKey Aethel tag}.
 * <p>
 * Used with {@link AethelTagCommand}.
 *
 * @author Danny Nguyen
 * @version 1.17.7
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
  protected TagModifier(@NotNull Player user, @NotNull ItemStack item, @NotNull String tag) {
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Objects.requireNonNull(item, "Null item");
    this.originalTag = Objects.requireNonNull(tag, "Null tag");
    this.tag = originalTag;
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
  }

  /**
   * Removes the {@link PluginNamespacedKey Aethel tag} from the item.
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
   * Sets the {@link PluginNamespacedKey Aethel tag} to the item.
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
   * Removes an item's {@link PluginNamespacedKey#ATTRIBUTE_LIST attribute} tag.
   */
  private void removeAttributeTag() {
    if (!tag.equals("attribute.list")) {
      NamespacedKey listKey = PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey();
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
   * Removes an item's {@link PluginNamespacedKey#PASSIVE_LIST passive} tag.
   */
  private void removePassiveTag() {
    if (!tag.equals("passive.list")) {
      NamespacedKey listKey = PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey();
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
   * Removes an item's {@link PluginNamespacedKey#ACTIVE_LIST active} tag.
   */
  private void removeActiveTag() {
    if (!tag.equals("active.list")) {
      NamespacedKey listKey = PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey();
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
   * Checks whether the {@link PluginNamespacedKey#ATTRIBUTE_LIST attribute}
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
          RpgEquipmentSlot.valueOf(tagMeta[0].toUpperCase());
          try {
            AethelAttributeType.valueOf(tagMeta[1].toUpperCase());
            setAttributeTag(attributeValue);
          } catch (IllegalArgumentException ex) {
            user.sendMessage(ChatColor.RED + "Aethel attribute does not exist.");
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Equipment slot does not exist.");
        }
      } else {
        user.sendMessage(ChatColor.RED + "Did not provide equipment slot and attribute.");
      }
    } catch (NumberFormatException ex) {
      user.sendMessage(ChatColor.RED + "Invalid value.");
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
        RpgEquipmentSlot.valueOf(tagMeta[0].toUpperCase());
        try {
          TriggerCondition triggerCondition = Trigger.valueOf(tagMeta[1].toUpperCase()).getCondition();
          try {
            PassiveAbilityType passiveType = PassiveAbilityType.valueOf(tagMeta[2].toUpperCase());
            switch (passiveType.getEffect()) {
              case STACK_INSTANCE -> readPassiveStackInstance(value, triggerCondition);
              case CHAIN_DAMAGE -> readPassiveChainDamage(value, triggerCondition);
            }
          } catch (IllegalArgumentException ex) {
            user.sendMessage(ChatColor.RED + "Passive ability does not exist.");
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Trigger condition does not exist.");
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Equipment slot does not exist.");
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
        RpgEquipmentSlot.valueOf(tagMeta[0].toUpperCase());
        try {
          ActiveAbilityType activeType = ActiveAbilityType.valueOf(tagMeta[1].toUpperCase());
          switch (activeType.getEffect()) {
            case MOVEMENT -> readActiveMovement(value);
            case PROJECTION -> readActiveProjection(value);
            case SHATTER -> readActiveShatter(value);
          }
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Active ability does not exist.");
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Equipment slot does not exist.");
      }
    } else {
      user.sendMessage(ChatColor.RED + "Did not provide equipment slot and active ability.");
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link PassiveAbilityEffect#STACK_INSTANCE}.
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
                      user.sendMessage(ChatColor.RED + "Invalid ticks.");
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid stacks.");
                  }
                }
                default -> user.sendMessage(ChatColor.RED + "Invalid true/false.");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid cooldown.");
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid chance.");
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
                      user.sendMessage(ChatColor.RED + "Invalid ticks.");
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid stacks.");
                  }
                }
                default -> user.sendMessage(ChatColor.RED + "Invalid true/false.");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid cooldown.");
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid % health.");
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link PassiveAbilityEffect#CHAIN_DAMAGE}.
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
                      user.sendMessage(ChatColor.RED + "Invalid radius.");
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid damage.");
                  }
                }
                default -> user.sendMessage(ChatColor.RED + "Invalid true/false");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid cooldown.");
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid chance.");
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
                      user.sendMessage(ChatColor.RED + "Invalid radius.");
                    }
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid damage.");
                  }
                }
                default -> user.sendMessage(ChatColor.RED + "Invalid true/false");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid cooldown.");
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid % health.");
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveAbilityEffect#MOVEMENT}.
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
          user.sendMessage(ChatColor.RED + "Invalid distance.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid cooldown.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveAbilityEffect#PROJECTION}.
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
            user.sendMessage(ChatColor.RED + "Invalid delay.");
          }
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid distance.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid cooldown.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Checks if the input was formatted correctly before
   * setting the {@link ActiveAbilityEffect#SHATTER}.
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
          user.sendMessage(ChatColor.RED + "Invalid radius.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid cooldown.");
      }
    } else {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Sets an item's {@link PluginNamespacedKey#ATTRIBUTE_LIST attribute} tag.
   *
   * @param value tag value
   */
  private void setAttributeTag(Double value) {
    NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.ATTRIBUTE.getHeader() + tag);
    dataContainer.set(tagKey, PersistentDataType.DOUBLE, value);
    setKeyToList(PluginNamespacedKey.ATTRIBUTE_LIST.getNamespacedKey());
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
    setKeyToList(PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey());
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
    setKeyToList(PluginNamespacedKey.ACTIVE_LIST.getNamespacedKey());
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
