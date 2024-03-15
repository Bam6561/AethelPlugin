package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.KeyHeader;
import me.dannynguyen.aethel.systems.plugin.Message;
import me.dannynguyen.aethel.systems.plugin.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.PluginNamespacedKey;
import me.dannynguyen.aethel.systems.rpg.PassiveAbilityType;
import me.dannynguyen.aethel.systems.rpg.PassiveAbilityEffect;
import me.dannynguyen.aethel.systems.rpg.Trigger;
import me.dannynguyen.aethel.systems.rpg.TriggerCondition;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a passive ability tag set or remove operation.
 *
 * @author Danny Nguyen
 * @version 1.15.15
 * @since 1.15.13
 */
class PassiveAbilityTag {
  /**
   * Passive list key.
   */
  private final NamespacedKey listKey = PluginNamespacedKey.PASSIVE_LIST.getNamespacedKey();

  /**
   * Passive header.
   */
  private final String passiveHeader = KeyHeader.PASSIVE.getHeader();

  /**
   * User input.
   */
  private final String[] args;

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
   * ItemStack's persistent tags.
   */
  private final PersistentDataContainer dataContainer;

  /**
   * User's slot metadata.
   */
  private final String slot;

  /**
   * User's condition metadata.
   */
  private final String condition;

  /**
   * User's type metadata.
   */
  private final String type;

  /**
   * Interacting key.
   */
  private final String interactingKey;

  /**
   * Associates a passive set or remove operation with a message, user, and item.
   *
   * @param message user input
   * @param user    user
   * @param item    interacting item
   */
  PassiveAbilityTag(@NotNull String message, @NotNull Player user, @NotNull ItemStack item) {
    this.args = Objects.requireNonNull(message, "Null message").split(" ");
    this.user = Objects.requireNonNull(user, "Null user");
    this.item = Objects.requireNonNull(item, "Null item");
    this.userUUID = user.getUniqueId();
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(userUUID);
    this.slot = playerMeta.get(PlayerMeta.SLOT);
    this.condition = playerMeta.get(PlayerMeta.CONDITION);
    this.type = playerMeta.get(PlayerMeta.TYPE);
    this.interactingKey = slot + "." + condition + "." + type;
  }

  /**
   * Determines the type of ability tag to be set.
   */
  public void interpretKeyToBeSet() {
    TriggerCondition triggerCondition = Trigger.valueOf(condition.toUpperCase()).getCondition();
    PassiveAbilityEffect abilityEffect = PassiveAbilityType.valueOf(type.toUpperCase()).getEffect();
    switch (triggerCondition) {
      case CHANCE_COOLDOWN -> readChanceCooldown(abilityEffect);
      case HP_CHANCE_COOLDOWN -> readHpChanceCooldown(abilityEffect);
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the effect's chance and cooldown.
   *
   * @param abilityEffect ability effect
   */
  private void readChanceCooldown(PassiveAbilityEffect abilityEffect) {
    switch (abilityEffect) {
      case STACK_INSTANCE -> {
        if (args.length == 4) {
          try {
            double chance = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              try {
                int stacks = Integer.parseInt(args[2]);
                try {
                  int ticks = Integer.parseInt(args[3]);
                  setKeyStringToList(chance + " " + cooldown + " " + stacks + " " + ticks);
                } catch (NumberFormatException ex) {
                  user.sendMessage(ChatColor.RED + "Invalid ticks.");
                }
              } catch (NumberFormatException ex) {
                user.sendMessage(ChatColor.RED + "Invalid stacks.");
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
      case CHAIN -> {
        if (args.length == 4) {
          try {
            double chance = Double.parseDouble(args[0]);
            try {
              int cooldown = Integer.parseInt(args[1]);
              try {
                double damage = Integer.parseInt(args[2]);
                try {
                  double distance = Double.parseDouble(args[3]);
                  setKeyStringToList(chance + " " + cooldown + " " + damage + " " + distance);
                } catch (NumberFormatException ex) {
                  user.sendMessage(ChatColor.RED + "Invalid radius.");
                }
              } catch (NumberFormatException ex) {
                user.sendMessage(ChatColor.RED + "Invalid damage.");
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
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the effect's HP, chance, and cooldown.
   *
   * @param abilityEffect ability effect
   */
  private void readHpChanceCooldown(PassiveAbilityEffect abilityEffect) {
    switch (abilityEffect) {
      case STACK_INSTANCE -> {
        if (args.length == 5) {
          try {
            double percentHealth = Double.parseDouble(args[0]);
            try {
              double chance = Double.parseDouble(args[1]);
              try {
                int cooldown = Integer.parseInt(args[2]);
                try {
                  int stacks = Integer.parseInt(args[3]);
                  try {
                    int ticks = Integer.parseInt(args[4]);
                    setKeyStringToList(percentHealth + " " + chance + " " + cooldown + " " + stacks + " " + ticks);
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid ticks.");
                  }
                } catch (NumberFormatException ex) {
                  user.sendMessage(ChatColor.RED + "Invalid stacks.");
                }
              } catch (NumberFormatException ex) {
                user.sendMessage(ChatColor.RED + "Invalid cooldown.");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid chance.");
            }
          } catch (NumberFormatException ex) {
            user.sendMessage(ChatColor.RED + "Invalid % health.");
          }
        } else {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case CHAIN -> {
        if (args.length == 5) {
          try {
            double percentHealth = Double.parseDouble(args[0]);
            try {
              double chance = Double.parseDouble(args[1]);
              try {
                int cooldown = Integer.parseInt(args[2]);
                try {
                  double damage = Integer.parseInt(args[3]);
                  try {
                    double distance = Double.parseDouble(args[4]);
                    setKeyStringToList(percentHealth + " " + chance + " " + cooldown + " " + damage + " " + distance);
                  } catch (NumberFormatException ex) {
                    user.sendMessage(ChatColor.RED + "Invalid radius.");
                  }
                } catch (NumberFormatException ex) {
                  user.sendMessage(ChatColor.RED + "Invalid damage.");
                }
              } catch (NumberFormatException ex) {
                user.sendMessage(ChatColor.RED + "Invalid cooldown.");
              }
            } catch (NumberFormatException ex) {
              user.sendMessage(ChatColor.RED + "Invalid chance.");
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
   * Sets a key with a String value to a key header's list of keys.
   *
   * @param keyValue key value
   */
  private void setKeyStringToList(String keyValue) {
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
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(condition) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
  }

  /**
   * Removes a key from a key header's list of keys.
   * <p>
   * If the list is empty after the operation, the list is also removed.
   * </p>
   */
  public void removeKeyFromList() {
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
    user.sendMessage(ChatColor.RED + "[Removed " + TextFormatter.capitalizePhrase(condition) + " " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
  }
}
