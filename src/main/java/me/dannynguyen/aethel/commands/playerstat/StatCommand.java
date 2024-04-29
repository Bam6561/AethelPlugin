package me.dannynguyen.aethel.commands.playerstat;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import me.dannynguyen.aethel.utils.EntityReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Command invocation that retrieves a player's statistics.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *   <li>"": opens a {@link StatMenu}
 *   <li>playerName: opens a {@link StatMenu} belonging to the player
 *   <li>"past", "p": opens a {@link PastStatMenu} with the last 27 shown stats
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.23.12
 * @since 1.4.7
 */
public class StatCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public StatCommand() {
  }

  /**
   * Executes the PlayerStat command.
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
      if (user.hasPermission("aethel.playerstat")) {
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
   * Represents a Stat command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.24.10
   * @since 1.23.12
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly
     * before opening the player's {@link StatMenu}.
     */
    private void readRequest() {
      switch (args.length) {
        case 0 -> openPlayerStatSelf();
        case 1 -> interpretParameter();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Either opens a {@link StatMenu} belonging
     * to another player or opens a {@link PastStatMenu}.
     */
    private void interpretParameter() {
      String parameter = args[0];
      if (parameter.equals("p") || parameter.equals("past")) {
        user.openInventory(new PastStatMenu(user).getMainMenu());
        Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput().setMenu(MenuListener.Menu.PLAYERSTAT_PAST);
      } else {
        if (!canViewOtherStats()) {
          return;
        }

        openPlayerStatOther(parameter);
      }
    }

    /**
     * Opens a {@link StatMenu} belonging to the user.
     */
    private void openPlayerStatSelf() {
      UUID target = user.getUniqueId();
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(target).getMenuInput();

      menuInput.setTarget(target);
      user.openInventory(new StatMenu(user, user.getName()).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_CATEGORY);
    }

    /**
     * Opens a {@link StatMenu} belonging to another player.
     *
     * @param owner requested user
     */
    private void openPlayerStatOther(String owner) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
      if (player.hasPlayedBefore()) {
        UUID target = player.getUniqueId();
        MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();

        menuInput.setTarget(target);
        user.openInventory(new StatMenu(user, player.getName()).getMainMenu());
        menuInput.setMenu(MenuListener.Menu.PLAYERSTAT_CATEGORY);
      } else {
        user.sendMessage(ChatColor.RED + owner + " has never played on this server.");
      }
    }

    /**
     * The user must have a spyglass in their hand, off-hand,
     * or trinket slot to view other players' statistics.
     *
     * @return if the user can view other players' statistics
     */
    private boolean canViewOtherStats() {
      if (EntityReader.hasTrinket(user, Material.SPYGLASS)) {
        return true;
      } else {
        user.sendMessage(ChatColor.RED + "[PlayerStats] No spyglass in hand, off-hand, or trinket slot.");
        return false;
      }
    }
  }
}