package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Command invocation that saves, tracks, and compares locations.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"get", "g": gets saved locations
 *  <li>"add", "a": saves a new location
 *  <li>"remove", "r": removes a saved location
 *  <li>"track", "t": tracks a location
 *  <li>"compare", "c": compares two locations
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.22.6
 * @since 1.22.5
 */
public class LocationCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public LocationCommand() {
  }

  /**
   * Executes the Location command.
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
      if (user.hasPermission("aethel.location")) {
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
    if (numberOfParameters == 0) {
      user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      return;
    }

    String action = args[0].toLowerCase();
    switch (action) {
      case "g", "get" -> new LocationRequest(user, args).getLocations();
      case "a", "add" -> new LocationRequest(user, args).saveLocation();
      case "r", "remove" -> new LocationRequest(user, args).removeLocation();
      case "t", "track" -> new LocationRequest(user, args).trackLocation();
      case "c", "comp", "compare" -> new LocationRequest(user, args).compareLocations();
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Represents a location query.
   *
   * @author Danny Nguyen
   * @version 1.22.5
   * @since 1.22.5
   */
  static class LocationRequest {
    /**
     * Interacting user.
     */
    private final Player user;

    /**
     * User provided parameters.
     */
    private final String[] args;

    /**
     * User's saved locations.
     */
    private final Map<String, Location> locations;

    /**
     * Associates a location request with its user and parameters.
     *
     * @param user user
     * @param args user provided parameters
     */
    LocationRequest(Player user, String[] args) {
      this.user = user;
      this.args = args;
      this.locations = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getLocationRegistry().getLocations();
    }

    /**
     * Gets the user's saved locations.
     */
    private void getLocations() {
      if (locations.isEmpty()) {
        user.sendMessage(ChatColor.RED + "No locations saved.");
        return;
      }

      switch (args.length) {
        case 1 -> {
          StringBuilder locationsBuilder = new StringBuilder();
          locationsBuilder.append(ChatColor.GREEN).append("[Get Locations] ").append(ChatColor.AQUA);
          for (String location : locations.keySet()) {
            locationsBuilder.append(location).append(", ");
          }
          user.sendMessage(locationsBuilder.substring(0, locationsBuilder.length() - 2));
        }
        case 2 -> {
          Location location = locations.get(args[1]);
          if (location == null) {
            user.sendMessage(ChatColor.RED + "Location does not exist.");
            return;
          }
          user.sendMessage(ChatColor.GREEN + "[Get Location] " + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getWorld().getName() + " " + location.getX() + ", " + location.getY() + ", " + location.getBlockZ());
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Saves a location based on the current world of the user.
     */
    private void saveLocation() {
      switch (args.length) {
        case 2 -> {
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location here = user.getLocation();
          Location location = new Location(here.getWorld(), Double.parseDouble(df2.format(here.getX())), Double.parseDouble(df2.format(here.getY())), Double.parseDouble(df2.format(here.getZ())));
          locations.put(args[1], location);
          user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        case 5 -> {
          double x;
          try {
            x = Double.parseDouble(args[2]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_X.getMessage());
            return;
          }
          double y;
          try {
            y = Double.parseDouble(args[3]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Y.getMessage());
            return;
          }
          double z;
          try {
            z = Double.parseDouble(args[4]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Z.getMessage());
            return;
          }
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location location = new Location(user.getLocation().getWorld(), Double.parseDouble(df2.format(x)), Double.parseDouble(df2.format(y)), Double.parseDouble(df2.format(z)));
          locations.put(args[1], location);
          user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Removes a location.
     */
    private void removeLocation() {
      if (args.length == 2) {
        locations.remove(args[1]);
        user.sendMessage(ChatColor.RED + "[Removed Location] " + ChatColor.WHITE + args[1]);
      } else {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Tracks a location.
     */
    private void trackLocation() {
      switch (args.length) {
        case 1 -> {
          Plugin.getData().getPluginSystem().getTrackedLocations().remove(user.getUniqueId());
          user.sendMessage(ChatColor.RED + "[Stopped Location Tracking]");
        }
        case 2 -> {
          Location location = locations.get(args[1]);
          if (location == null) {
            user.sendMessage(ChatColor.RED + "Location does not exist.");
            return;
          }
          Plugin.getData().getPluginSystem().getTrackedLocations().put(user.getUniqueId(), location);
          user.sendMessage(ChatColor.GREEN + "[Tracking Location] " + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        case 4 -> {
          double x;
          try {
            x = Double.parseDouble(args[1]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_X.getMessage());
            return;
          }
          double y;
          try {
            y = Double.parseDouble(args[2]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Y.getMessage());
            return;
          }
          double z;
          try {
            z = Double.parseDouble(args[3]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Z.getMessage());
            return;
          }
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location location = new Location(user.getLocation().getWorld(), Double.parseDouble(df2.format(x)), Double.parseDouble(df2.format(y)), Double.parseDouble(df2.format(z)));
          Plugin.getData().getPluginSystem().getTrackedLocations().put(user.getUniqueId(), location);
          user.sendMessage(ChatColor.GREEN + "[Tracking Location] " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Compares two locations.
     */
    private void compareLocations() {
      switch (args.length) {
        case 2, 3 -> {
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location here;
          Location there;
          if (args.length == 2) {
            Location userLocation = user.getLocation();
            here = new Location(userLocation.getWorld(), Double.parseDouble(df2.format(userLocation.getX())), Double.parseDouble(df2.format(userLocation.getY())), Double.parseDouble(df2.format(userLocation.getZ())));
            there = locations.get(args[1]);
          } else {
            here = locations.get(args[1]);
            there = locations.get(args[2]);
          }

          if (here == null) {
            user.sendMessage(ChatColor.RED + "Starting location does not exist.");
            return;
          }
          if (there == null) {
            user.sendMessage(ChatColor.RED + "Destination location does not exist.");
            return;
          }
          if (!here.getWorld().getName().equals(there.getWorld().getName())) {
            user.sendMessage(ChatColor.RED + "Locations not in the same world.");
            return;
          }

          double xLength = Math.abs(here.getX() - there.getX());
          double yLength = Math.abs(here.getY() - there.getY());
          double zLength = Math.abs(here.getZ() - there.getZ());

          user.sendMessage(ChatColor.GREEN + "[Compare Locations] " + ChatColor.AQUA + here.getX() + ", " + here.getY() + ", " + here.getZ() + ChatColor.WHITE + " -> " + ChatColor.AQUA + there.getX() + ", " + there.getY() + ", " + there.getZ());
          user.sendMessage(ChatColor.GOLD + "Lengths: " + ChatColor.WHITE + df2.format(xLength) + ", " + df2.format(yLength) + ", " + df2.format(zLength));
          user.sendMessage(ChatColor.GOLD + "Midpoint: " + ChatColor.WHITE + df2.format((here.getX() + there.getX() / 2)) + ", " + df2.format((here.getY() + there.getY() / 2)) + ", " + df2.format((here.getZ() + there.getZ() / 2)));
          user.sendMessage(ChatColor.GOLD + "Distance: " + ChatColor.WHITE + df2.format(here.distance(there)));
          user.sendMessage(ChatColor.GOLD + "Area: " + ChatColor.WHITE + df2.format((xLength * zLength)));
          user.sendMessage(ChatColor.GOLD + "Volume: " + ChatColor.WHITE + df2.format((xLength * yLength * zLength)));
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }
  }
}
