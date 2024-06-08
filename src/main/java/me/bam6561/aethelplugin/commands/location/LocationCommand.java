package me.bam6561.aethelplugin.commands.location;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import me.bam6561.aethelplugin.utils.EntityReader;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Command invocation that allows the user to get, save, remove, track, and compare
 * {@link LocationRegistry.SavedLocation saved locations} through text or clicking.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": opens {@link LocationMenu}
 *  <li>"reload", "r": reloads {@link LocationRegistry.SavedLocation saved locations} into {@link LocationRegistry}
 *  <li>"get", "g": gets {@link LocationRegistry.SavedLocation saved locations}
 *  <li>"save", "s": saves a new {@link LocationRegistry.SavedLocation}
 *  <li>"remove", "rm": removes a {@link LocationRegistry.SavedLocation}
 *  <li>"track", "t": tracks a {@link LocationRegistry.SavedLocation} or coordinate
 *  <li>"compare", "c": compares two {@link LocationRegistry.SavedLocation saved locations} or coordinates
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.24.7
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
   * Represents a Location command request.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.23.12
   */
  private static class Request {
    /**
     * Interacting user.
     */
    private final Player user;

    /**
     * User provided parameters.
     */
    private final String[] args;

    /**
     * User's {@link LocationRegistry}.
     */
    private final LocationRegistry locationRegistry;

    /**
     * User's {@link LocationRegistry.SavedLocation saved locations}.
     */
    private final Map<String, LocationRegistry.SavedLocation> locations;

    /**
     * Associates a location request with its user and parameters.
     *
     * @param user user
     * @param args user provided parameters
     */
    Request(Player user, String[] args) {
      this.user = user;
      this.args = args;
      this.locationRegistry = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getLocationRegistry();
      this.locations = locationRegistry.getLocations();
    }

    /**
     * Checks if the command request was formatted correctly before interpreting its usage.
     */
    private void readRequest() {
      int numberOfParameters = args.length;
      if (numberOfParameters == 0) {
        openMenu();
        return;
      }

      switch (args[0].toLowerCase()) {
        case "r", "reload" -> reloadLocations();
        case "g", "get" -> getLocations();
        case "s", "save" -> saveLocation();
        case "rm", "remove" -> removeLocation();
        case "t", "track" -> trackLocation();
        case "c", "comp", "compare" -> compareLocations();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Opens the {@link LocationMenu}.
     */
    private void openMenu() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      menuInput.setCategory("");
      user.openInventory(new LocationMenu(user, LocationMenu.Action.VIEW).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.LOCATION_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Reloads {@link LocationRegistry.SavedLocation saved locations} into {@link LocationRegistry}.
     */
    private void reloadLocations() {
      locationRegistry.loadData();
      user.sendMessage(ChatColor.GREEN + "[Reloaded Locations]");
    }

    /**
     * Gets the user's {@link LocationRegistry.SavedLocation saved locations}.
     */
    private void getLocations() {
      if (locations.isEmpty()) {
        user.sendMessage(ChatColor.RED + "No saved locations.");
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
          LocationRegistry.SavedLocation savedLocation = locations.get(args[1]);
          if (savedLocation == null) {
            user.sendMessage(ChatColor.RED + "Location does not exist.");
            return;
          }
          Location location = savedLocation.getLocation();
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
        case 3 -> {
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location here = user.getLocation();
          Location location = new Location(here.getWorld(), Double.parseDouble(df2.format(here.getX())), Double.parseDouble(df2.format(here.getY())), Double.parseDouble(df2.format(here.getZ())));
          locations.put(args[2], new LocationRegistry.SavedLocation(locationRegistry, args[1], args[2], location));
          user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.WHITE + args[1] + "/" + ChatColor.AQUA + args[2] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        case 6 -> {
          double x;
          try {
            x = Double.parseDouble(args[3]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_X.getMessage());
            return;
          }
          double y;
          try {
            y = Double.parseDouble(args[4]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Y.getMessage());
            return;
          }
          double z;
          try {
            z = Double.parseDouble(args[5]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Z.getMessage());
            return;
          }
          DecimalFormat df2 = new DecimalFormat();
          df2.setMaximumFractionDigits(2);

          Location location = new Location(user.getLocation().getWorld(), Double.parseDouble(df2.format(x).replace(",", "")), Double.parseDouble(df2.format(y).replace(",", "")), Double.parseDouble(df2.format(z).replace(",", "")));
          locations.put(args[2], new LocationRegistry.SavedLocation(locationRegistry, args[1], args[2], location));
          user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.WHITE + args[1] + "/" + ChatColor.AQUA + args[2] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Removes a {@link LocationRegistry.SavedLocation}
     */
    private void removeLocation() {
      if (args.length == 2) {
        LocationRegistry.SavedLocation savedLocation = locations.get(args[1]);
        if (savedLocation != null) {
          savedLocation.delete();
        }
        user.sendMessage(ChatColor.RED + "[Removed Location] " + ChatColor.WHITE + args[1]);
      } else {
        user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Tracks a {@link LocationRegistry.SavedLocation} or coordinates.
     */
    private void trackLocation() {
      if (!canTrackOrCompare()) {
        return;
      }

      switch (args.length) {
        case 1 -> {
          Plugin.getData().getPluginSystem().getTrackedLocations().remove(user.getUniqueId());
          user.sendMessage(ChatColor.RED + "[Tracking Location] Stopped.");
        }
        case 2 -> {
          LocationRegistry.SavedLocation savedLocation = locations.get(args[1]);
          if (savedLocation == null) {
            user.sendMessage(ChatColor.RED + "Location does not exist.");
            return;
          }
          Location location = savedLocation.getLocation();
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

          Location location = new Location(user.getLocation().getWorld(), Double.parseDouble(df2.format(x).replace(",", "")), Double.parseDouble(df2.format(y).replace(",", "")), Double.parseDouble(df2.format(z).replace(",", "")));
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
      if (!canTrackOrCompare()) {
        return;
      }

      DecimalFormat df2 = new DecimalFormat();
      df2.setMaximumFractionDigits(2);

      Location here;
      Location there;

      switch (args.length) {
        case 2 -> {
          LocationRegistry.SavedLocation savedLocation = locations.get(args[1]);
          if (savedLocation == null) {
            user.sendMessage(ChatColor.RED + "Destination location does not exist.");
            return;
          }
          Location userLocation = user.getLocation();
          here = new Location(userLocation.getWorld(), Double.parseDouble(df2.format(userLocation.getX())), Double.parseDouble(df2.format(userLocation.getY())), Double.parseDouble(df2.format(userLocation.getZ())));
          there = savedLocation.getLocation();
        }
        case 3 -> {
          LocationRegistry.SavedLocation savedHere = locations.get(args[1]);
          if (savedHere == null) {
            user.sendMessage(ChatColor.RED + "Starting location does not exist.");
            return;
          }
          LocationRegistry.SavedLocation savedThere = locations.get(args[2]);
          if (savedThere == null) {
            user.sendMessage(ChatColor.RED + "Destination location does not exist.");
            return;
          }
          here = savedHere.getLocation();
          there = savedThere.getLocation();
        }
        case 7 -> {
          double x1;
          try {
            x1 = Double.parseDouble(args[1]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_X.getMessage());
            return;
          }
          double y1;
          try {
            y1 = Double.parseDouble(args[2]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Y.getMessage());
            return;
          }
          double z1;
          try {
            z1 = Double.parseDouble(args[3]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Z.getMessage());
            return;
          }
          double x2;
          try {
            x2 = Double.parseDouble(args[4]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_X.getMessage());
            return;
          }
          double y2;
          try {
            y2 = Double.parseDouble(args[5]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Y.getMessage());
            return;
          }
          double z2;
          try {
            z2 = Double.parseDouble(args[6]);
          } catch (NumberFormatException ex) {
            user.sendMessage(Message.INVALID_Z.getMessage());
            return;
          }
          here = new Location(user.getWorld(), Double.parseDouble(df2.format(x1)), Double.parseDouble(df2.format(y1)), Double.parseDouble(df2.format(z1)));
          there = new Location(user.getWorld(), Double.parseDouble(df2.format(x2)), Double.parseDouble(df2.format(y2)), Double.parseDouble(df2.format(z2)));
        }
        default -> {
          user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
          return;
        }
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

    /**
     * The user must have a compass in their hand,
     * off-hand, or trinket slot to track or compare locations.
     *
     * @return if the user can track or compare locations
     */
    private boolean canTrackOrCompare() {
      if (EntityReader.hasTrinket(user, Material.COMPASS)) {
        return true;
      } else {
        user.sendMessage(ChatColor.RED + "[Location] No compass in hand, off-hand, or trinket slot.");
        return false;
      }
    }
  }
}
