package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.AethelAttribute;
import me.dannynguyen.aethel.rpg.Buffs;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
 * For multiple targets, use the "r:," target radius selector.
 * The self user is included by default, unless "r:!s," is specified.
 *
 * @author Danny Nguyen
 * @version 1.23.12
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
   * Represents a Buff command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.24.4
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before interpreting its usage.
     */
    private void readRequest() {
      int numberOfParameters = args.length;
      if (numberOfParameters == 0) {
        user.sendMessage(Message.NO_PARAMETERS.getMessage());
        return;
      }

      String action = args[0].toLowerCase();
      switch (numberOfParameters) {
        case 2 -> {
          switch (action) {
            case "g", "get" -> readEntityTarget(Action.GET);
            case "r", "remove" -> readEntityTarget(Action.REMOVE_ALL);
            default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
          }
        }
        case 5 -> {
          switch (action) {
            case "a", "add" -> readEntityTarget(Action.ADD);
            default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
          }
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Determines which target(s) the user is referring to.
     *
     * @param action type of interaction
     */
    private void readEntityTarget(Action action) {
      if (!args[1].startsWith("r:")) {
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
          case GET -> getBuffs(uuid);
          case REMOVE_ALL -> removeBuffs(uuid);
          case ADD -> readAddBuff(uuid);
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

        Location location = user.getLocation();
        Set<LivingEntity> livingEntities = new HashSet<>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, x, y, z)) {
          if (entity instanceof LivingEntity livingEntity) {
            livingEntities.add(livingEntity);
          }
        }

        String selector = radiusParameters[0];
        if (selector.contains("!s")) {
          livingEntities.remove(user);
        }
        if (selector.contains("p")) {
          Set<LivingEntity> players = new HashSet<>();
          for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player player) {
              players.add(player);
            }
          }

          if (selector.contains("!p")) {
            for (LivingEntity player : players) {
              livingEntities.remove(player);
            }
          } else {
            livingEntities = players;
          }
        }

        Set<UUID> targets = new HashSet<>();
        for (LivingEntity livingEntity : livingEntities) {
          targets.add(livingEntity.getUniqueId());
        }

        switch (action) {
          case GET -> {
            for (UUID uuid : targets) {
              getBuffs(uuid);
            }
          }
          case REMOVE_ALL -> {
            for (UUID uuid : targets) {
              removeBuffs(uuid);
            }
          }
          case ADD -> {
            for (UUID uuid : targets) {
              readAddBuff(uuid);
            }
          }
        }
      }
    }

    /**
     * Responds with the entity's {@link Buffs}.
     *
     * @param uuid entity uuid
     */
    private void getBuffs(UUID uuid) {
      Buffs buffs = Plugin.getData().getRpgSystem().getBuffs().get(uuid);
      if (buffs == null) {
        user.sendMessage(ChatColor.RED + "[No Buffs] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
        return;
      }

      StringBuilder buffsBuilder = new StringBuilder();
      buffsBuilder.append(ChatColor.GREEN).append("[Get Buffs] ").append(ChatColor.DARK_PURPLE).append(Bukkit.getEntity(uuid).getName()).append(" ");
      for (Attribute attribute : buffs.getAttributeBuffs()) {
        buffsBuilder.append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(attribute.name())).append(" ");
        buffsBuilder.append(ChatColor.WHITE).append(buffs.getAttribute(attribute)).append(" ");
      }
      for (AethelAttribute aethelAttribute : buffs.getAethelAttributeBuffs()) {
        buffsBuilder.append(ChatColor.AQUA).append(aethelAttribute.getProperName()).append(" ");
        buffsBuilder.append(ChatColor.WHITE).append(buffs.getAethelAttribute(aethelAttribute)).append(" ");
      }
      user.sendMessage(buffsBuilder.toString());
    }

    /**
     * Removes all {@link Buffs} from the entity.
     *
     * @param uuid entity uuid
     */
    private void removeBuffs(UUID uuid) {
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
     * @param uuid entity uuid
     */
    private void readAddBuff(UUID uuid) {
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
        addAttributeBuff(uuid, attribute, value, duration);
      } else {
        addAethelAttributeBuff(uuid, aethelAttribute, value, duration);
      }
    }

    /**
     * Adds an attribute {@link Buffs buff} on the entity.
     *
     * @param uuid      entity uuid
     * @param attribute attribute
     * @param value     attribute value
     * @param ticks     duration
     */
    private void addAttributeBuff(UUID uuid, Attribute attribute, double value, int ticks) {
      Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
      if (!entityBuffs.containsKey(uuid)) {
        entityBuffs.put(uuid, new Buffs(uuid));
      }
      entityBuffs.get(uuid).addAttribute(attribute, value, ticks);
      user.sendMessage(ChatColor.GREEN + "[Buff Added] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + TextFormatter.capitalizePhrase(attribute.name()) + " " + ChatColor.WHITE + value + " " + ticks);
    }

    /**
     * Adds a {@link AethelAttribute} {@link Buffs buff} on the entity.
     *
     * @param uuid            entity uuid
     * @param aethelAttribute {@link AethelAttribute}
     * @param value           attribute value
     * @param ticks           duration
     */
    private void addAethelAttributeBuff(UUID uuid, AethelAttribute aethelAttribute, double value, int ticks) {
      Map<UUID, Buffs> entityBuffs = Plugin.getData().getRpgSystem().getBuffs();
      if (!entityBuffs.containsKey(uuid)) {
        entityBuffs.put(uuid, new Buffs(uuid));
      }
      entityBuffs.get(uuid).addAethelAttribute(aethelAttribute, value, ticks);
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
}
