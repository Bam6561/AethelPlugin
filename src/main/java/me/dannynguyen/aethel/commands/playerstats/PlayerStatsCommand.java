package me.dannynguyen.aethel.commands.playerstats;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * PlayerStatsCommand is a command invocation that retrieves a player's statistics.
 * <p>
 * Additional Parameters:
 * - "past", "p": opens an inventory with the last 9 shown stats
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.4.7
 */
public class PlayerStatsCommand implements CommandExecutor {
  public enum Permission {
    PLAYERSTATS("aethel.playerstats");

    public final String permission;

    Permission(String permission) {
      this.permission = permission;
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(Permission.PLAYERSTATS.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly
   * before opening a player's PlayerStats main menu.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openPlayerStatsSelf(user);
      case 1 -> interpretParameter(user, args[0]);
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
    }
  }

  /**
   * Either opens a PlayerStats main menu belonging to another player or opens a PlayerStatsPast inventory.
   *
   * @param user      user
   * @param parameter player given parameter
   */
  private void interpretParameter(Player user, String parameter) {
    if (parameter.equals("past") || parameter.equals("p")) {
      user.openInventory(PlayerStatsPast.createInventory(user));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.PLAYERSTATS_PAST.inventory));
    } else {
      openPlayerStatsOther(user, parameter);
    }
  }

  /**
   * Opens a PlayerStats main menu.
   *
   * @param user user
   */
  private void openPlayerStatsSelf(Player user) {
    user.setMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace,
        new FixedMetadataValue(Plugin.getInstance(), user.getName()));

    user.openInventory(PlayerStatsInventory.openMainMenu(user, user.getName()));
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.PLAYERSTATS_CATEGORY.inventory));
  }

  /**
   * Opens a PlayerStats main menu belonging to another player.
   *
   * @param user            user
   * @param requestedPlayer requested player's name
   */
  private void openPlayerStatsOther(Player user, String requestedPlayer) {
    OfflinePlayer player = Bukkit.getOfflinePlayer(requestedPlayer);
    if (player.hasPlayedBefore()) {
      user.setMetadata(PluginPlayerMeta.Namespace.PLAYER.namespace,
          new FixedMetadataValue(Plugin.getInstance(), player.getName()));

      user.openInventory(PlayerStatsInventory.openMainMenu(user, player.getName()));
      user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
          new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.PLAYERSTATS_CATEGORY.inventory));
    } else {
      user.sendMessage(ChatColor.RED + requestedPlayer + " has never played on this server.");
    }
  }
}