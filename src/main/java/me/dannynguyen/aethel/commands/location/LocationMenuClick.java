package me.dannynguyen.aethel.commands.location;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.interfaces.MenuClick;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.listeners.MessageListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.EntityReader;
import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Inventory click event listener for {@link LocationCommand} menus.
 * <p>
 * Called with {@link MenuListener}.
 *
 * @author Danny Nguyen
 * @version 1.24.7
 * @since 1.24.7
 */
public class LocationMenuClick implements MenuClick {
  /**
   * Inventory click event.
   */
  private final InventoryClickEvent e;

  /**
   * Player who clicked.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * User's {@link LocationRegistry}.
   */
  private final LocationRegistry locationRegistry;

  /**
   * Slot clicked.
   */
  private final int slot;

  /**
   * Associates an inventory click event with its user in the context of an open {@link LocationCommand} menu.
   *
   * @param e inventory click event
   */
  public LocationMenuClick(@NotNull InventoryClickEvent e) {
    this.e = Objects.requireNonNull(e, "Null inventory click event");
    this.user = (Player) e.getWhoClicked();
    this.uuid = user.getUniqueId();
    this.locationRegistry = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getLocationRegistry();
    this.slot = e.getSlot();
  }

  /**
   * Either saves a {@link LocationRegistry.SavedLocation} or
   * gets a {@link LocationRegistry.SavedLocation} category page.
   */
  public void interpretMenuClick() {
    switch (slot) {
      case 2 -> new LocationSave().saveLocation();
      default -> {
        if (slot > 8) {
          new MenuChange().getCategoryPage();
        }
      }
    }
  }

  /**
   * Either:
   * <ul>
   *  <li>increments or decrements a {@link LocationRegistry.SavedLocation} category page
   *  <li>saves a {@link LocationRegistry.SavedLocation}
   *  <li>changes the {@link LocationMenu.Action interaction}
   *  <li>contextualizes the click to remove, track, or compare {@link LocationRegistry.SavedLocation saved locations}
   * </ul>
   *
   * @param action type of {@link LocationMenu.Action} interaction
   */
  public void interpretCategoryClick(@NotNull LocationMenu.Action action) {
    Objects.requireNonNull(action, "Null action");
    switch (slot) {
      case 0 -> new MenuChange().previousPage(action);
      case 1 -> { // Context
      }
      case 2 -> new LocationSave().saveLocation();
      case 3 -> new MenuChange().openLocationRemove();
      case 4 -> new MenuChange().openLocationTrack();
      case 5 -> new MenuChange().openLocationCompare();
      case 6 -> new MenuChange().returnToMainMenu();
      case 8 -> new MenuChange().nextPage(action);
      default -> {
        if (slot > 8) {
          interpretContextualClick(action);
        }
      }
    }
  }

  /**
   * Either removes, tracks, or compares a {@link LocationRegistry.SavedLocation}.
   *
   * @param action type of interaction
   */
  private void interpretContextualClick(LocationMenu.Action action) {
    String locationName = ItemReader.readName(e.getCurrentItem());
    switch (action) {
      case REMOVE -> {
        LocationRegistry.SavedLocation savedLocation = locationRegistry.getLocations().get(locationName);
        user.sendMessage(ChatColor.RED + "[Removed Location] " + ChatColor.WHITE + savedLocation.getName());
        savedLocation.delete();
      }
      case TRACK -> {
        if (!canTrackOrCompare()) {
          return;
        }

        Location location = locationRegistry.getLocations().get(locationName).getLocation();
        Plugin.getData().getPluginSystem().getTrackedLocations().put(user.getUniqueId(), location);
        user.sendMessage(ChatColor.GREEN + "[Tracking Location] " + ChatColor.AQUA + locationName + " " + ChatColor.WHITE + location.getX() + ", " + location.getY() + ", " + location.getZ());
      }
      case COMPARE -> {
        if (!canTrackOrCompare()) {
          return;
        }
        
        DecimalFormat df2 = new DecimalFormat();
        df2.setMaximumFractionDigits(2);

        Location userLocation = user.getLocation();
        Location here = new Location(userLocation.getWorld(), Double.parseDouble(df2.format(userLocation.getX())), Double.parseDouble(df2.format(userLocation.getY())), Double.parseDouble(df2.format(userLocation.getZ())));
        Location there = locationRegistry.getLocations().get(locationName).getLocation();

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
    }
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

  /**
   * Represents a menu change operation.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.24.7
   */
  private class MenuChange {
    /**
     * No parameter constructor.
     */
    MenuChange() {
    }

    /**
     * Gets a {@link LocationRegistry.SavedLocation} category page.
     */
    private void getCategoryPage() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = ChatColor.stripColor(ItemReader.readName(e.getCurrentItem()));
      int pageRequest = menuInput.getPage();

      menuInput.setCategory(category);
      user.openInventory(new LocationMenu(user, LocationMenu.Action.TRACK).getCategoryPage(category, pageRequest));
      menuInput.setMenu(MenuListener.Menu.LOCATION_TRACK);
    }

    /**
     * Gets the previous {@link LocationRegistry.SavedLocation} category page.
     *
     * @param action type of interaction
     */
    private void previousPage(LocationMenu.Action action) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new LocationMenu(user, action).getCategoryPage(category, pageRequest - 1));
      menuInput.setMenu(MenuListener.Menu.valueOf("LOCATION_" + action.name()));
    }

    /**
     * Opens the {@link LocationMenu} with the intent to remove
     * {@link LocationRegistry.SavedLocation saved locations}.
     */
    private void openLocationRemove() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new LocationMenu(user, LocationMenu.Action.REMOVE).getCategoryPage(category, pageRequest));
      menuInput.setMenu(MenuListener.Menu.LOCATION_REMOVE);
    }

    /**
     * Opens the {@link LocationMenu} with the intent to track
     * {@link LocationRegistry.SavedLocation saved locations}.
     */
    private void openLocationTrack() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new LocationMenu(user, LocationMenu.Action.TRACK).getCategoryPage(category, pageRequest));
      menuInput.setMenu(MenuListener.Menu.LOCATION_TRACK);
    }

    /**
     * Opens the {@link LocationMenu} with the intent to compare
     * {@link LocationRegistry.SavedLocation saved locations}.
     */
    private void openLocationCompare() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new LocationMenu(user, LocationMenu.Action.COMPARE).getCategoryPage(category, pageRequest));
      menuInput.setMenu(MenuListener.Menu.LOCATION_COMPARE);
    }

    /**
     * Returns to the {@link LocationMenu}.
     */
    private void returnToMainMenu() {
      user.openInventory(new LocationMenu(user, LocationMenu.Action.VIEW).getMainMenu());
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      menuInput.setMenu(MenuListener.Menu.LOCATION_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Gets the next {@link LocationRegistry.SavedLocation} category page.
     *
     * @param action type of interaction
     */
    private void nextPage(LocationMenu.Action action) {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
      String category = menuInput.getCategory();
      int pageRequest = menuInput.getPage();

      user.openInventory(new LocationMenu(user, action).getCategoryPage(category, pageRequest + 1));
      menuInput.setMenu(MenuListener.Menu.valueOf("LOCATION_" + action.name()));
    }
  }

  /**
   * Represents a location save operation.
   *
   * @author Danny Nguyen
   * @version 1.24.7
   * @since 1.24.7
   */
  private class LocationSave {
    /**
     * No parameter constructor.
     */
    LocationSave() {
    }

    /**
     * Asks the user for a location folder to save the {@link LocationRegistry.SavedLocation} under.
     */
    private void saveLocation() {
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Input location folder name and location name.");
      user.sendMessage(Message.NOTIFICATION_INPUT.getMessage() + ChatColor.WHITE + "Optionally, provide coordinates.");
      user.closeInventory();
      Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput().setMessageInput(MessageListener.Type.LOCATION_FOLDER);
    }
  }
}
