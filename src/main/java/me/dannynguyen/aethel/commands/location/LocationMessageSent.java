package me.dannynguyen.aethel.commands.location;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for {@link LocationCommand} text inputs.
 * <p>
 * Called with {@link me.dannynguyen.aethel.listeners.MessageListener}.
 *
 * @author Danny Nguyen
 * @version 1.24.7
 * @since 1.24.7
 */
public class LocationMessageSent {
  /**
   * Message sent event.
   */
  private final AsyncPlayerChatEvent e;

  /**
   * Player who sent the message.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Associates a message sent event with its user in the context of saving
   * a location in a {@link LocationMenu} through {@link LocationMenuClick}.
   *
   * @param e message sent event
   */
  public LocationMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
  }

  /**
   * Saves the {@link LocationRegistry.SavedLocation} under a user input folder name.
   */
  public void saveLocation() {
    String[] args = e.getMessage().split(" ");
    switch (args.length) {
      case 2 -> {
        DecimalFormat df2 = new DecimalFormat();
        df2.setMaximumFractionDigits(2);

        Location here = user.getLocation();
        Location location = new Location(here.getWorld(), Double.parseDouble(df2.format(here.getX())), Double.parseDouble(df2.format(here.getY())), Double.parseDouble(df2.format(here.getZ())));
        LocationRegistry locationRegistry = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getLocationRegistry();
        Map<String, LocationRegistry.SavedLocation> locations = locationRegistry.getLocations();

        locations.put(args[1], new LocationRegistry.SavedLocation(locationRegistry, args[0], args[1], location));
        user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.WHITE + args[0] + "/" + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
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

        Location location = new Location(user.getLocation().getWorld(), Double.parseDouble(df2.format(x).replace(",", "")), Double.parseDouble(df2.format(y).replace(",", "")), Double.parseDouble(df2.format(z).replace(",", "")));
        LocationRegistry locationRegistry = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getLocationRegistry();
        Map<String, LocationRegistry.SavedLocation> locations = locationRegistry.getLocations();

        locations.put(args[1], new LocationRegistry.SavedLocation(locationRegistry, args[0], args[1], location));
        user.sendMessage(ChatColor.GREEN + "[Saved Location] " + ChatColor.WHITE + args[0] + "/" + ChatColor.AQUA + args[1] + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
      }
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }

    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      menuInput.setCategory("");
      user.openInventory(new LocationMenu(user, LocationMenu.Action.VIEW).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.LOCATION_CATEGORY);
      menuInput.setPage(0);
    });
  }
}
