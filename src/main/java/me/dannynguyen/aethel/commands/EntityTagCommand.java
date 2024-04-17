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
 * @version 1.23.9
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
        readRequest(user, args);
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
   */
  private void readRequest(Player user, String[] args) {
    int numberOfParameters = args.length;
    if (numberOfParameters <= 1) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return;
    }

    String action = args[0].toLowerCase();
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

    switch (numberOfParameters) {
      case 2 -> {
        switch (action) {
          case "g", "get" -> getTags(user, entity);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
        }
      }
      case 3 -> {
        switch (action) {
          case "r", "remove" -> removeTag(user, args[2], entity);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> {
        switch (action) {
          case "s", "set" -> setTag(user, args, entity);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
    }
  }

  /**
   * Responds with the entity's {@link Key Aethel tags}.
   *
   * @param user   user
   * @param entity interacting entity
   */
  private void getTags(Player user, Entity entity) {
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
   * @param user   user
   * @param tag    {@link Key Aethel tag} to be removed
   * @param entity interacting entity
   */
  private void removeTag(Player user, String tag, Entity entity) {
    if (new TagModifier(user, entity, tag).removeTag()) {
      user.sendMessage(ChatColor.RED + "[Removed Tag] " + ChatColor.AQUA + tag);
    } else {
      user.sendMessage(ChatColor.RED + "Tag does not exist.");
    }
  }

  /**
   * Sets the {@link Key Aethel tag} to the entity.
   *
   * @param user   user
   * @param args   user provided parameters
   * @param entity interacting entity
   */
  private void setTag(Player user, String[] args, Entity entity) {
    String tag = args[2];
    StringBuilder value = new StringBuilder();
    if (args.length == 4) {
      value = new StringBuilder(args[3]);
    } else {
      for (int i = 3; i < args.length; i++) {
        value.append(args[i]).append(" ");
      }
    }
    new TagModifier(user, entity, tag).setTag(value.toString());
  }

  /**
   * Represents an entity's {@link Key Aethel tag} set or remove operation.
   *
   * @author Danny Nguyen
   * @version 1.22.15
   * @since 1.22.15
   */
  private static class TagModifier {
    /**
     * Interacting player.
     */
    private final Player user;

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
     * @param user   user
     * @param entity interacting entity
     * @param tag    tag to be modified
     */
    TagModifier(@NotNull Player user, @NotNull Entity entity, @NotNull String tag) {
      this.user = Objects.requireNonNull(user, "Null user");
      this.tag = Objects.requireNonNull(tag, "Null tag");
      this.entityTags = Objects.requireNonNull(entity, "Null entity").getPersistentDataContainer();
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
    private void setTag(@NotNull String value) {
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
