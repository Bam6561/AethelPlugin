package me.dannynguyen.aethel.commands.itemeditor;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.KeyHeader;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.enums.PluginKey;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import me.dannynguyen.aethel.rpg.enums.PassiveEffect;
import me.dannynguyen.aethel.rpg.enums.PassiveType;
import me.dannynguyen.aethel.rpg.enums.Trigger;
import me.dannynguyen.aethel.rpg.enums.TriggerCondition;
import me.dannynguyen.aethel.util.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a {@link PluginKey#PASSIVE_LIST passive tag} set or remove operation.
 * <p>
 * Used with {@link ItemEditorMessageSent}.
 *
 * @author Danny Nguyen
 * @version 1.17.18
 * @since 1.15.13
 */
class PassiveTagModifier {
  /**
   * {@link PluginKey#PASSIVE_LIST}
   */
  private static final NamespacedKey listKey = PluginKey.PASSIVE_LIST.getNamespacedKey();

  /**
   * {@link KeyHeader#PASSIVE}
   */
  private static final String passiveHeader = KeyHeader.PASSIVE.getHeader();

  /**
   * User input.
   */
  private final String[] args;

  /**
   * Player who sent the message.
   */
  private final Player user;

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
   * {@link PlayerMeta#SLOT}
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
   * Associates a passive set or remove operation with a message, user, and item.
   *
   * @param message user input
   * @param user    user
   * @param item    interacting item
   */
  PassiveTagModifier(String message, Player user, ItemStack item) {
    this.args = message.split(" ");
    this.user = user;
    this.item = item;
    this.meta = item.getItemMeta();
    this.dataContainer = meta.getPersistentDataContainer();
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    this.slot = playerMeta.get(PlayerMeta.SLOT);
    this.trigger = pluginPlayer.getTrigger().getId();
    this.type = pluginPlayer.getObjectType();
    this.interactingKey = slot + "." + trigger + "." + type;
  }

  /**
   * Determines the type of {@link PluginKey#PASSIVE_LIST ability tag} to be set.
   */
  protected void interpretKeyToBeSet() {
    TriggerCondition condition = Trigger.valueOf(TextFormatter.formatEnum(this.trigger)).getCondition();
    PassiveEffect effect = PassiveType.valueOf(TextFormatter.formatEnum(type)).getEffect();
    switch (condition) {
      case CHANCE_COOLDOWN -> readChanceCooldown(effect);
      case HEALTH_COOLDOWN -> readHpChanceCooldown(effect);
    }
  }

  /**
   * Checks if the input was formatted correctly before setting
   * the {@link PassiveEffect effect's} chance and cooldown.
   *
   * @param effect {@link PassiveEffect}
   */
  private void readChanceCooldown(PassiveEffect effect) {
    switch (effect) {
      case STACK_INSTANCE -> {
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
                      setKeyStringToList(chance + " " + cooldown + " " + self + " " + stacks + " " + ticks);
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
      case CHAIN_DAMAGE -> {
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
                      setKeyStringToList(chance + " " + cooldown + " " + self + " " + damage + " " + distance);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_RADIUS.getMessage());
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
    }
  }

  /**
   * Checks if the input was formatted correctly before setting the
   * {@link PassiveEffect effect's} HP, chance, and cooldown.
   *
   * @param effect {@link PassiveEffect}
   */
  private void readHpChanceCooldown(PassiveEffect effect) {
    switch (effect) {
      case STACK_INSTANCE -> {
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
                      setKeyStringToList(percentHealth + " " + cooldown + " " + self + " " + stacks + " " + ticks);
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
      case CHAIN_DAMAGE -> {
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
                      setKeyStringToList(percentHealth + " " + cooldown + " " + self + " " + damage + " " + distance);
                    } catch (NumberFormatException ex) {
                      user.sendMessage(Message.INVALID_RADIUS.getMessage());
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
   * Sets a key with a String value to a {@link KeyHeader key header's} list of keys.
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
    user.sendMessage(ChatColor.GREEN + "[Set " + TextFormatter.capitalizePhrase(slot) + " " + TextFormatter.capitalizePhrase(trigger) + " " + TextFormatter.capitalizePhrase(type, ".") + "]");
  }

  /**
   * Removes a key from a {@link KeyHeader key header's} list of keys.
   * <p>
   * If the list is empty after the operation, the list is also removed.
   */
  protected void removeKeyFromList() {
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
