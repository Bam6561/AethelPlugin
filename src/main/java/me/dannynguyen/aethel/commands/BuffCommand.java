package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.rpg.Buffs;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Command invocation that allows the user to retrieve or
 * give {@link Buffs} from entities.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": reads the entity's {@link Buffs}
 *  <li>"add", "a": adds a {@link Buffs buff} to the entity
 *  <li>"remove", "r": removes all {@link Buffs} from the entity
 * </ul>
 * <p>
 * For multiple targets, use the "D:," target radius selector.
 * The self user is included by default, unless "D:!s," is specified.
 *
 * @author Danny Nguyen
 * @version 1.21.8.1
 * @since 1.21.5
 */
public class BuffCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public BuffCommand() {
  }

  /**
   * Executes the Buff command.
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
      if (user.hasPermission("aethel.buff")) {
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
    String action = null;
    if (numberOfParameters > 0) {
      action = args[0].toLowerCase();
    }
    switch (numberOfParameters) {
      case 0 -> user.sendMessage(Message.NO_PARAMETERS.getMessage());
      case 2 -> {
        switch (action) {
          case "g", "get" -> readEntityTarget(user, Action.GET, args);
          case "r", "remove" -> readEntityTarget(user, Action.REMOVE_ALL, args);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case 5 -> {
        switch (action) {
          case "a", "add" -> readEntityTarget(user, Action.ADD, args);
          default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Determines which target(s) the user is referring to.
   *
   * @param user   user
   * @param action type of interaction
   * @param args   user provided parameters
   */
  private void readEntityTarget(Player user, Action action, String[] args) {
    if (!args[1].startsWith("D:")) {
      UUID uuid;
      String target = args[1];
      if (Bukkit.getPlayer(target) != null) {
        uuid = Bukkit.getPlayer(target).getUniqueId();
      } else {
        try {
          uuid = UUID.fromString(target);
        } catch (IllegalArgumentException ex) {
          user.sendMessage(ChatColor.RED + "Invalid UUID.");
          return;
        }
        Entity entity = Bukkit.getEntity(uuid);
        if (entity instanceof LivingEntity) {
          uuid = UUID.fromString(target);
        } else {
          user.sendMessage(ChatColor.RED + "Not a living entity.");
          return;
        }
      }
      switch (action) {
        case GET -> getBuffs(user, uuid);
        case REMOVE_ALL -> removeBuffs(user, uuid);
        case ADD -> readAddBuff(user, uuid, args);
      }
    } else {
      String[] radiusParameters = args[1].split(",");
      if (radiusParameters.length != 4) {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
        return;
      }
      double x;
      try {
        x = Double.parseDouble(radiusParameters[1]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_X.getMessage());
        return;
      }
      double y;
      try {
        y = Double.parseDouble(radiusParameters[2]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_Y.getMessage());
        return;
      }
      double z;
      try {
        z = Double.parseDouble(radiusParameters[3]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_Z.getMessage());
        return;
      }
      Set<UUID> targets = new HashSet<>();
      if (!radiusParameters[0].equals("D:!s")) {
        targets.add(user.getUniqueId());
      }
      for (Entity entity : user.getNearbyEntities(x, y, z)) {
        if (entity instanceof LivingEntity livingEntity) {
          targets.add(livingEntity.getUniqueId());
        }
      }
      switch (action) {
        case GET -> {
          for (UUID uuid : targets) {
            getBuffs(user, uuid);
          }
        }
        case REMOVE_ALL -> {
          for (UUID uuid : targets) {
            removeBuffs(user, uuid);
          }
        }
        case ADD -> {
          for (UUID uuid : targets) {
            readAddBuff(user, uuid, args);
          }
        }
      }
    }
  }

  /**
   * Responds with the entity's {@link Buffs}.
   *
   * @param user user
   * @param uuid entity uuid
   */
  private void getBuffs(Player user, UUID uuid) {
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);
    if (buffs == null) {
      user.sendMessage(ChatColor.RED + "[No Buffs] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
      return;
    }

    StringBuilder buffsBuilder = new StringBuilder();
    buffsBuilder.append(ChatColor.GREEN).append("[Get Buffs] ").append(ChatColor.DARK_PURPLE).append(Bukkit.getEntity(uuid).getName()).append(" ");
    for (Attribute attribute : buffs.getBuffedAttributes()) {
      buffsBuilder.append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(attribute.name())).append(" ");
      buffsBuilder.append(ChatColor.WHITE).append(buffs.getAttributeBuff(attribute)).append(" ");
    }
    for (AethelAttribute aethelAttribute : buffs.getBuffedAethelAttributes()) {
      buffsBuilder.append(ChatColor.AQUA).append(aethelAttribute.getProperName()).append(" ");
      buffsBuilder.append(ChatColor.WHITE).append(buffs.getAethelAttributeBuff(aethelAttribute)).append(" ");
    }
    user.sendMessage(buffsBuilder.toString());
  }

  /**
   * Removes all {@link Buffs} from the entity.
   *
   * @param user user
   * @param uuid entity uuid
   */
  private void removeBuffs(Player user, UUID uuid) {
    Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);
    if (buffs != null) {
      buffs.removeAllBuffs();
    }
    user.sendMessage(ChatColor.RED + "[All Buffs Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
  }

  /**
   * Checks if the AddBuff request was formatted correctly
   * before adding a {@link Buffs buff} on the entity.
   *
   * @param user user
   * @param uuid entity uuid
   * @param args user provided parameters
   */
  private void readAddBuff(Player user, UUID uuid, String[] args) {
    Attribute attribute = null;
    AethelAttribute aethelAttribute = null;
    try {
      attribute = Attribute.valueOf(TextFormatter.formatEnum(args[2]));
    } catch (IllegalArgumentException ex) {
      try {
        aethelAttribute = AethelAttribute.valueOf(TextFormatter.formatEnum(args[2]));
      } catch (IllegalArgumentException ex2) {
        user.sendMessage(Message.UNRECOGNIZED_ATTRIBUTE.getMessage());
        return;
      }
    }
    double value;
    try {
      value = Double.parseDouble(args[3]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_VALUE.getMessage());
      return;
    }
    int duration;
    try {
      duration = Integer.parseInt(args[4]);
    } catch (NumberFormatException ex) {
      user.sendMessage(Message.INVALID_DURATION.getMessage());
      return;
    }
    if (attribute != null) {
      addAttributeBuff(user, uuid, attribute, value, duration);
    } else {
      addAethelAttributeBuff(user, uuid, aethelAttribute, value, duration);
    }
  }

  /**
   * Adds an attribute {@link Buffs buff} on the entity.
   *
   * @param user      user
   * @param uuid      entity uuid
   * @param attribute attribute
   * @param value     attribute value
   * @param ticks     duration
   */
  private void addAttributeBuff(Player user, UUID uuid, Attribute attribute, double value, int ticks) {
    Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
    if (!entityBuffs.containsKey(uuid)) {
      entityBuffs.put(uuid, new Buffs(uuid));
    }
    entityBuffs.get(uuid).addAttributeBuff(attribute, value, ticks);
    user.sendMessage(ChatColor.GREEN + "[Buff Added] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + TextFormatter.capitalizePhrase(attribute.name()) + " " + ChatColor.WHITE + value + " " + ticks);
  }

  /**
   * Adds a {@link AethelAttribute} {@link Buffs buff} on the entity.
   *
   * @param user            user
   * @param uuid            entity uuid
   * @param aethelAttribute {@link AethelAttribute}
   * @param value           attribute value
   * @param ticks           duration
   */
  private void addAethelAttributeBuff(Player user, UUID uuid, AethelAttribute aethelAttribute, double value, int ticks) {
    Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
    if (!entityBuffs.containsKey(uuid)) {
      entityBuffs.put(uuid, new Buffs(uuid));
    }
    entityBuffs.get(uuid).addAethelAttributeBuff(aethelAttribute, value, ticks);
    user.sendMessage(ChatColor.GREEN + "[Buff Added] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + TextFormatter.capitalizePhrase(aethelAttribute.name()) + " " + ChatColor.WHITE + value + " " + ticks);
  }

  /**
   * Types of Buff command actions.
   */
  private enum Action {
    /**
     * Reads the entity's {@link me.dannynguyen.aethel.rpg.Buffs}.
     */
    GET,

    /**
     * Adds a {@link me.dannynguyen.aethel.rpg.Buffs buff} on the entity.
     */
    ADD,

    /**
     * Removes all {@link me.dannynguyen.aethel.rpg.Buffs} from the entity.
     */
    REMOVE_ALL
  }
}
