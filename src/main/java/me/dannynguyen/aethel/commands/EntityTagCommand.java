package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Key;
import me.dannynguyen.aethel.enums.plugin.KeyHeader;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Command invocation that allows the user to retrieve,
 * set, or remove {@link Key Aethel tags} to entities.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": reads the entity's {@link Key tags}
 *  <li>"set", "s": sets the entity's {@link Key tag}
 *  <li>"remove", "r": removes the entity's {@link Key tag}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.23.12
 * @since 1.22.15
 */
public class EntityTagCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public EntityTagCommand() {
  }

  /**
   * Executes the AethelEntityTag command.
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
      if (user.hasPermission("aethel.aethelentitytag")) {
        new Request(user, args).readRequest();
      } else {
        user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(Message.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Represents an EntityTag command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.12
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before interpreting its usage.
     */
    private void readRequest() {
      int numberOfParameters = args.length;
      if (numberOfParameters <= 1) {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        return;
      }

      Entity entity;

      UUID uuid;
      String target = args[1];
      if (Bukkit.getPlayer(target) != null) {
        entity = Bukkit.getPlayer(target);
      } else {
        try {
          uuid = UUID.fromString(target);
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Invalid UUID.");
          return;
        }
        entity = Bukkit.getEntity(uuid);
        if (!(entity instanceof LivingEntity)) {
          user.sendMessage(ChatColor.RED + "Not a living entity.");
          return;
        }
      }

      String action = args[0].toLowerCase();
      switch (numberOfParameters) {
        case 2 -> {
          switch (action) {
            case "g", "get" -> getTags(entity);
            default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
          }
        }
        case 3 -> {
          switch (action) {
            case "r", "remove" -> removeTag(entity);
            default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
          }
        }
        default -> {
          switch (action) {
            case "s", "set" -> setTag(entity);
            default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
          }
        }
      }
    }

    /**
     * Responds with the entity's {@link Key Aethel tags}.
     *
     * @param entity interacting entity
     */
    private void getTags(Entity entity) {
      PersistentDataContainer entityTags = Objects.requireNonNull(entity, "Null entity").getPersistentDataContainer();
      StringBuilder aethelTags = new StringBuilder();
      for (NamespacedKey key : entityTags.getKeys()) {
        String keyName = key.getKey();
        if (keyName.startsWith(KeyHeader.AETHEL.getHeader())) {
          keyName = keyName.substring(7);
          if (keyName.startsWith("attribute.") || keyName.startsWith("rpg.health")) {
            aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.DOUBLE)).append(" ");
          } else if (keyName.startsWith("enchantment.")) {
            aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.INTEGER)).append(" ");
          } else {
            aethelTags.append(ChatColor.AQUA).append(keyName).append(" ").append(ChatColor.WHITE).append(entityTags.get(key, PersistentDataType.STRING)).append(" ");
          }
        }
      }

      if (!aethelTags.isEmpty()) {
        user.sendMessage(ChatColor.GREEN + "[Get Tags] " + aethelTags);
      } else {
        user.sendMessage(ChatColor.RED + "No tags found.");
      }
    }

    /**
     * Removes the {@link Key Aethel tag} from the entity.
     *
     * @param entity interacting entity
     */
    private void removeTag(Entity entity) {
      String tag = args[2];
      if (new TagModifier(entity, tag).removeTag()) {
        user.sendMessage(ChatColor.RED + "[Removed Tag] " + ChatColor.AQUA + tag);
      } else {
        user.sendMessage(ChatColor.RED + "Tag does not exist.");
      }
    }

    /**
     * Sets the {@link Key Aethel tag} to the entity.
     *
     * @param entity interacting entity
     */
    private void setTag(Entity entity) {
      String tag = args[2];
      StringBuilder value = new StringBuilder();
      if (args.length == 4) {
        value = new StringBuilder(args[3]);
      } else {
        for (int i = 3; i < args.length; i++) {
          value.append(args[i]).append(" ");
        }
      }
      new TagModifier(entity, tag).setTag(value.toString());
    }

    /**
     * Represents an entity's {@link Key Aethel tag} set or remove operation.
     *
     * @author Danny Nguyen
     * @version 1.23.12
     * @since 1.22.15
     */
    private class TagModifier {
      /**
       * Entity's persistent data tags.
       */
      private final PersistentDataContainer entityTags;

      /**
       * Tag to be modified.
       */
      private final String tag;

      /**
       * Associates an entity with its tag to be modified.
       *
       * @param entity interacting entity
       * @param tag    tag to be modified
       */
      TagModifier(Entity entity, String tag) {
        this.tag = tag;
        this.entityTags = entity.getPersistentDataContainer();
      }

      /**
       * Removes the {@link Key Aethel tag} from the entity.
       *
       * @return if the tag was removed
       */
      private boolean removeTag() {
        NamespacedKey tagKey = new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag);
        if (entityTags.has(tagKey, PersistentDataType.DOUBLE) || entityTags.has(tagKey, PersistentDataType.INTEGER) || entityTags.has(tagKey, PersistentDataType.STRING)) {
          entityTags.remove(tagKey);
          return true;
        }
        return false;
      }

      /**
       * Sets the {@link Key Aethel tag} to the entity.
       *
       * @param value tag value
       */
      private void setTag(String value) {
        Objects.requireNonNull(value, "Null value");
        if (tag.startsWith("attribute.")) {
          try {
            AethelAttribute.valueOf(TextFormatter.formatEnum(tag.substring(10)));
          } catch (IllegalArgumentException ex) {
            user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
            return;
          }
          double attributeValue;
          try {
            attributeValue = Double.parseDouble(value);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_VALUE.getMessage());
            return;
          }
          entityTags.set(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag), PersistentDataType.DOUBLE, attributeValue);
        } else if (tag.startsWith("enchantment.")) {
          switch (tag.substring(12)) {
            case "protection", "blast_protection", "fire_protection", "projectile_protection", "feather_falling" -> {
            }
            default -> {
              user.sendMessage(Message.INVALID_ATTRIBUTE.getMessage());
              return;
            }
          }
          int enchantmentValue;
          try {
            enchantmentValue = Integer.parseInt(value);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_VALUE.getMessage());
            return;
          }
          entityTags.set(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag), PersistentDataType.INTEGER, enchantmentValue);
        } else {
          entityTags.set(new NamespacedKey(Plugin.getInstance(), KeyHeader.AETHEL.getHeader() + tag), PersistentDataType.STRING, value);
        }
        user.sendMessage(ChatColor.GREEN + "[Set Tag] " + ChatColor.AQUA + tag.toLowerCase() + " " + ChatColor.WHITE + value);
      }
    }
  }
}
