package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.enums.rpg.StatusType;
import me.dannynguyen.aethel.rpg.Status;
import me.dannynguyen.aethel.utils.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
 * For multiple targets, use the "D:," target radius selector.
 * The self user is included by default, unless "D:!s," is specified.
 *
 * @author Danny Nguyen
 * @version 1.21.5
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
   * Checks if the command request was formatted correctly before doing interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    int numberOfParameters = args.length;
    String action = "";
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
      case 3 -> {
        switch (action) {
          case "r", "remove" -> readEntityTarget(user, Action.REMOVE, args);
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
        case GET -> getStatuses(user, uuid);
        case REMOVE_ALL -> removeStatuses(user, uuid);
        case REMOVE -> removeStatus(user, uuid, args);
        case ADD -> readAddStatus(user, uuid, args);
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
            getStatuses(user, uuid);
          }
        }
        case REMOVE_ALL -> {
          for (UUID uuid : targets) {
            removeStatuses(user, uuid);
          }
        }
        case REMOVE -> {
          for (UUID uuid : targets) {
            removeStatus(user, uuid, args);
          }
        }
        case ADD -> {
          for (UUID uuid : targets) {
            readAddStatus(user, uuid, args);
          }
        }
      }
    }
  }

  /**
   * Responds with the entity's {@link Status statuses}.
   *
   * @param user user
   * @param uuid entity uuid
   */
  private void getStatuses(Player user, UUID uuid) {
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
   * @param user user
   * @param uuid entity uuid
   */
  private void removeStatuses(Player user, UUID uuid) {
    Plugin.getData().getRpgSystem().getStatuses().remove(uuid);
    user.sendMessage(ChatColor.RED + "[All Statuses Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
  }

  /**
   * Removes a {@link Status status} from the entity.
   *
   * @param user user
   * @param uuid entity uuid
   * @param args user provided parameters
   */
  private void removeStatus(Player user, UUID uuid, String[] args) {
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
   * @param user user
   * @param uuid entity uuid
   * @param args user provided parameters
   */
  private void readAddStatus(Player user, UUID uuid, String[] args) {
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
    addStatus(user, uuid, statusType, stacks, duration);
  }

  /**
   * Adds a {@link Status status} on the entity.
   *
   * @param user   user
   * @param uuid   entity uuid
   * @param status {@link StatusType}
   * @param stacks number of stacks to apply
   * @param ticks  duration
   */
  private void addStatus(Player user, UUID uuid, StatusType status, int stacks, int ticks) {
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
