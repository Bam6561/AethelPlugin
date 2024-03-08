package me.dannynguyen.aethel.commands.status;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.systems.plugin.PluginMessage;
import me.dannynguyen.aethel.systems.rpg.RpgStatus;
import me.dannynguyen.aethel.systems.rpg.RpgStatusType;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command invocation that allows the user to retrieve,
 * give, or clear statuses from entities.
 * <p>
 * Additional Parameters:
 * - "get", "g": reads the entity's statuses
 * - "set", "s": sets a status on the entity
 * - "remove", "r": removes a status or all statuses from the entity
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.14.9
 * @since 1.14.8
 */
public class StatusCommand implements CommandExecutor {
  /**
   * Executes the Status command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.status")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.getMessage());
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
      case 0 -> user.sendMessage(PluginMessage.NO_PARAMETERS.getMessage());
      case 2 -> {
        switch (action) {
          case "g", "get" -> readEntityTarget(user, StatusCommandAction.GET, args);
          case "r", "remove" -> readEntityTarget(user, StatusCommandAction.REMOVE_ALL, args);
          default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case 3 -> {
        switch (action) {
          case "r", "remove" -> readEntityTarget(user, StatusCommandAction.REMOVE, args);
          default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      case 5 -> {
        switch (action) {
          case "s", "set" -> readEntityTarget(user, StatusCommandAction.SET, args);
          default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
        }
      }
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Determines which target the user is referring to.
   *
   * @param user   user
   * @param action type of interaction
   * @param args   user provided arguments
   */
  private void readEntityTarget(Player user, StatusCommandAction action, String[] args) {
    UUID uuid = null;
    String target = args[1];
    if (Bukkit.getPlayer(target) != null) {
      uuid = Bukkit.getPlayer(target).getUniqueId();
    } else {
      try {
        Entity entity = Bukkit.getEntity(UUID.fromString(target));
        if (entity instanceof LivingEntity) {
          uuid = UUID.fromString(target);
        }
      } catch (IllegalArgumentException ex) {
        user.sendMessage(ChatColor.RED + "Invalid target.");
        return;
      }
    }
    switch (action) {
      case GET -> getStatuses(user, uuid);
      case REMOVE_ALL -> removeStatuses(user, uuid);
      case REMOVE -> removeStatus(user, uuid, args);
      case SET -> readSetStatusRequest(user, uuid, args);
    }
  }

  /**
   * Responds with the entity's statuses.
   *
   * @param user user
   * @param uuid entity uuid
   */
  private void getStatuses(Player user, UUID uuid) {
    Map<UUID, Map<RpgStatusType, RpgStatus>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    if (entityStatuses.get(uuid) != null) {
      Map<RpgStatusType, RpgStatus> statusTypes = entityStatuses.get(uuid);
      StringBuilder statusesBuilder = new StringBuilder();
      statusesBuilder.append(ChatColor.GREEN).append("[Get Statuses] ").append(ChatColor.DARK_PURPLE).append(Bukkit.getEntity(uuid).getName()).append(" ");
      for (RpgStatusType statusType : statusTypes.keySet()) {
        RpgStatus status = statusTypes.get(statusType);
        statusesBuilder.append(ChatColor.AQUA).append(TextFormatter.capitalizePhrase(statusType.name())).append(" ");
        statusesBuilder.append(ChatColor.WHITE).append(status.getStackAmount()).append(" ");
        Map<Integer, Integer> stackApplications = status.getStackApplications();
        if (!stackApplications.isEmpty()) {
          statusesBuilder.append("[");
          for (Integer stackAmount : stackApplications.values()) {
            statusesBuilder.append(stackAmount).append(", ");
          }
          statusesBuilder.delete(statusesBuilder.length() - 2, statusesBuilder.length());
          statusesBuilder.append("] ");
        }
      }
      user.sendMessage(statusesBuilder.toString());
    } else {
      user.sendMessage(ChatColor.RED + "No statuses found.");
    }
  }

  /**
   * Removes all statuses from the entity.
   *
   * @param user user
   * @param uuid entity uuid
   */
  private void removeStatuses(Player user, UUID uuid) {
    Plugin.getData().getRpgSystem().getStatuses().remove(uuid);
    user.sendMessage(ChatColor.RED + "[All Statuses Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName());
  }

  /**
   * Removes a status from the entity.
   *
   * @param user user
   * @param uuid entity uuid
   * @param args user provided arguments
   */
  private void removeStatus(Player user, UUID uuid, String[] args) {
    try {
      RpgStatusType statusType = RpgStatusType.valueOf(args[2].toUpperCase());
      Map<UUID, Map<RpgStatusType, RpgStatus>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
      if (entityStatuses.get(uuid) != null) {
        Map<RpgStatusType, RpgStatus> statuses = entityStatuses.get(uuid);
        statuses.remove(statusType);
        if (statuses.isEmpty()) {
          entityStatuses.remove(uuid);
        }
      }
      user.sendMessage(ChatColor.RED + "[Status Removed] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + TextFormatter.capitalizePhrase(statusType.name()));
    } catch (IllegalArgumentException ex) {
      user.sendMessage(ChatColor.RED + "Status type does not exist.");
    }
  }

  /**
   * Checks if the SetStatus request was formatted correctly before setting a status on the entity.
   *
   * @param user user
   * @param uuid entity uuid
   * @param args user provided arguments
   */
  private void readSetStatusRequest(Player user, UUID uuid, String[] args) {
    try {
      RpgStatusType statusType = RpgStatusType.valueOf(args[2].toUpperCase());
      try {
        int stacks = Integer.parseInt(args[3]);
        try {
          int ticks = Integer.parseInt(args[4]);
          setStatus(user, uuid, statusType, stacks, ticks);
        } catch (NumberFormatException ex) {
          user.sendMessage(ChatColor.RED + "Invalid number of ticks.");
        }
      } catch (NumberFormatException ex) {
        user.sendMessage(ChatColor.RED + "Invalid number of stacks.");
      }
    } catch (IllegalArgumentException ex) {
      user.sendMessage(ChatColor.RED + "Status type does not exist.");
    }
  }

  /**
   * Sets a status on the entity.
   *
   * @param user       user
   * @param uuid       entity uuid
   * @param statusType status type
   * @param stacks     number of status to apply
   * @param ticks      status duration
   */
  private void setStatus(Player user, UUID uuid, RpgStatusType statusType, int stacks, int ticks) {
    Map<UUID, Map<RpgStatusType, RpgStatus>> entityStatuses = Plugin.getData().getRpgSystem().getStatuses();
    if (!entityStatuses.containsKey(uuid)) {
      entityStatuses.put(uuid, new HashMap<>());
    }
    Map<RpgStatusType, RpgStatus> statuses = entityStatuses.get(uuid);
    if (statuses.containsKey(statusType)) {
      statuses.get(statusType).addStacks(stacks, ticks);
    } else {
      statuses.put(statusType, new RpgStatus(uuid, statusType, stacks, ticks));
    }
    user.sendMessage(ChatColor.GREEN + "[Status Added] " + ChatColor.DARK_PURPLE + Bukkit.getEntity(uuid).getName() + " " + ChatColor.AQUA + TextFormatter.capitalizePhrase(statusType.name()) + " " + ChatColor.WHITE + stacks + " " + ticks);
  }
}
