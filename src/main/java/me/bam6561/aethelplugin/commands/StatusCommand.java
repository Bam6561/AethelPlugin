package me.bam6561.aethelplugin.commands;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.enums.rpg.StatusType;
import me.bam6561.aethelplugin.rpg.Status;
import me.bam6561.aethelplugin.utils.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Command invocation that allows the user to retrieve,
 * give, or clear {@link Status statuses} from entities.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": reads the entity's {@link Status statuses}
 *  <li>"add", "a": adds a {@link Status status} to the entity
 *  <li>"remove", "r": removes a status or all {@link Status statuses} from the entity
 * </ul>
 * <p>
 * For multiple targets, use the "r:," target radius selector.
 * The self user is included by default, unless "r:!s," is specified.
 *
 * @author Danny Nguyen
 * @version 1.23.12
 * @since 1.14.8
 */
public class StatusCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public StatusCommand() {
  }

  /**
   * Executes the Status command.
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
      if (user.hasPermission("aethel.status")) {
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
   * Represents a Status command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.24.4
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before doing interpreting its usage.
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
        case 3 -> {
          switch (action) {
            case "r", "remove" -> readEntityTarget(Action.REMOVE);
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
          case GET -> getStatuses(uuid);
          case REMOVE_ALL -> removeStatuses(uuid);
          case REMOVE -> removeStatus(uuid);
          case ADD -> readAddStatus(uuid);
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
              getStatuses(uuid);
            }
          }
          case REMOVE_ALL -> {
            for (UUID uuid : targets) {
              removeStatuses(uuid);
            }
          }
          case REMOVE -> {
            for (UUID uuid : targets) {
              removeStatus(uuid);
            }
          }
          case ADD -> {
            for (UUID uuid : targets) {
              readAddStatus(uuid);
            }
          }
        }
      }
    }

    /**
     * Responds with the entity's {@link Status statuses}.
     *
     * @param uuid entity uuid
     */
    private void getStatuses(UUID uuid) {
      Map<StatusType, Status> statusTypes = Plugin.getData().getRpgSystem().getStatuses().get(uuid);
      if (statusTypes == null) {
        user.sendMessage(ChatColor.RED + "[No Statuses] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
        return;
      }

      StringBuilder statusesBuilder = new StringBuilder();
      statusesBuilder.append(ChatColor.GREEN).append("[Get Statuses] ").append(ChatColor.DARK_PURPLE).append(Bukkit.getEntity(uuid).getName()).append(" ");
      for (StatusType statusType : statusTypes.keySet()) {
        Status status = statusTypes.get(statusType);
        statusesBuilder.append(ChatColor.AQUA).append(statusType.getProperName()).append(" ");
        statusesBuilder.append(ChatColor.WHITE).append(status.getStackAmount()).append(" ");
        Map<Integer, Integer> stackInstances = status.getStackInstances();
        if (!stackInstances.isEmpty()) {
          statusesBuilder.append("[");
          for (Integer stackAmount : stackInstances.values()) {
            statusesBuilder.append(stackAmount).append(", ");
          }
          statusesBuilder.delete(statusesBuilder.length() - 2, statusesBuilder.length());
          statusesBuilder.append("] ");
        }
      }
      user.sendMessage(statusesBuilder.toString());
    }

    /**
     * Removes all {@link Status statuses} from the entity.
     *
     * @param uuid entity uuid
     */
    private void removeStatuses(UUID uuid) {
      Plugin.getData().getRpgSystem().getStatuses().remove(uuid);
      user.sendMessage(ChatColor.RED + "[All Statuses Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
    }

    /**
     * Removes a {@link Status status} from the entity.
     *
     * @param uuid entity uuid
     */
    private void removeStatus(UUID uuid) {
      StatusType statusType;
      try {
        statusType = StatusType.valueOf(TextFormatter.formatEnum(args[2]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_STATUS.getMessage());
        return;
      }

      Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses != null) {
        statuses.remove(statusType);
        if (statuses.isEmpty()) {
          entityStatuses.remove(uuid);
        }
      }
      user.sendMessage(ChatColor.RED + "[Status Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + statusType.getProperName());
    }

    /**
     * Checks if the AddStatus request was formatted correctly
     * before adding a {@link Status status} on the entity.
     *
     * @param uuid entity uuid
     */
    private void readAddStatus(UUID uuid) {
      StatusType statusType;
      try {
        statusType = StatusType.valueOf(TextFormatter.formatEnum(args[2]));
      } catch (IllegalArgumentException ex) {
        user.sendMessage(Message.UNRECOGNIZED_STATUS.getMessage());
        return;
      }
      int stacks;
      try {
        stacks = Integer.parseInt(args[3]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_STACKS.getMessage());
        return;
      }
      int duration;
      try {
        duration = Integer.parseInt(args[4]);
      } catch (NumberFormatException ex) {
        user.sendMessage(Message.INVALID_DURATION.getMessage());
        return;
      }
      addStatus(uuid, statusType, stacks, duration);
    }

    /**
     * Adds a {@link Status status} on the entity.
     *
     * @param uuid   entity uuid
     * @param status {@link StatusType}
     * @param stacks number of stacks to apply
     * @param ticks  duration
     */
    private void addStatus(UUID uuid, StatusType status, int stacks, int ticks) {
      Map<UUID, Map<StatusType, Status>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      if (!entityStatuses.containsKey(uuid)) {
        entityStatuses.put(uuid, new HashMap<>());
      }
      Map<StatusType, Status> statuses = entityStatuses.get(uuid);
      if (statuses.containsKey(status)) {
        statuses.get(status).addStacks(stacks, ticks);
      } else {
        statuses.put(status, new Status(uuid, status, stacks, ticks));
      }
      user.sendMessage(ChatColor.GREEN + "[Status Added] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + status.getProperName() + " " + ChatColor.WHITE + stacks + " " + ticks);
    }

    /**
     * Types of Status command actions.
     */
    private enum Action {
      /**
       * Reads the entity's {@link Status statuses}.
       */
      GET,

      /**
       * Adds a {@link Status status} on the entity.
       */
      ADD,

      /**
       * Removes a {@link Status status} from the entity.
       */
      REMOVE,

      /**
       * Removes all {@link Status statuses} from the entity.
       */
      REMOVE_ALL
    }
  }
}
