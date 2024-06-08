package me.bam6561.aethelplugin.commands.character;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import me.bam6561.aethelplugin.utils.EntityReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Command invocation that allows the user to view a player's RPG character data.
 * <p>
 * From the {@link SheetMenu}, the user can also view the player's quests and collectibles.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": opens a {@link SheetMenu} belonging to the user
 *  <li>playerName: opens a {@link SheetMenu} belonging to the player
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.23.10
 * @since 1.6.3
 */
public class CharacterCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public CharacterCommand() {
  }

  /**
   * Executes the Character command.
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
      if (user.hasPermission("aethel.character")) {
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
   * Represents a Character command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.24.5
   * @since 1.23.10
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before opening a {@link SheetMenu}.
     */
    private void readRequest() {
      switch (args.length) {
        case 0 -> openSheetSelf();
        case 1 -> openSheetOther();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Opens a {@link SheetMenu} belonging to the user.
     */
    private void openSheetSelf() {
      UUID target = user.getUniqueId();
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(target).getMenuInput();
      menuInput.setTarget(target);
      user.openInventory(new SheetMenu(user, user).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.CHARACTER_SHEET);
    }

    /**
     * Opens a {@link SheetMenu} belonging to another player.
     */
    private void openSheetOther() {
      if (!canViewOtherSheets()) {
        return;
      }

      String requestedPlayer = args[0];
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player.getName().equals(requestedPlayer)) {
          UUID target = player.getUniqueId();
          MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
          menuInput.setTarget(target);
          user.openInventory(new SheetMenu(user, player).getMainMenu());
          menuInput.setMenu(MenuListener.Menu.CHARACTER_SHEET);
          return;
        }
      }
      user.sendMessage(ChatColor.RED + requestedPlayer + " not online.");
    }

    /**
     * The user must have a spyglass in their hand, off-hand,
     * or trinket slot to view other players' character sheets.
     *
     * @return if the user can view other players' character sheets
     */
    private boolean canViewOtherSheets() {
      if (user.getName().equals(args[0]) || EntityReader.hasTrinket(user, Material.SPYGLASS)) {
        return true;
      } else {
        user.sendMessage(ChatColor.RED + "[Character] No spyglass in hand, off-hand, or trinket slot.");
        return false;
      }
    }
  }
}
