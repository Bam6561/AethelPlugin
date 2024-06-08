package me.bam6561.aethelplugin.commands.aethelitem;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Message;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to obtain
 * {@link ItemRegistry.Item items} through clicking.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"": opens {@link ItemMenu}
 *  <li>"reload", "r": reloads {@link ItemRegistry.Item items} into {@link ItemRegistry}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.23.13
 * @since 1.3.2
 */
public class ItemCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ItemCommand() {
  }

  /**
   * Executes the AethelItem command.
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
      if (user.hasPermission("aethel.aethelitem")) {
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
   * Represents an AethelItem command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.13
   * @since 1.23.13
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before interpreting its usage.
     */
    private void readRequest() {
      switch (args.length) {
        case 0 -> openMenu();
        case 1 -> readParameter();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Opens the {@link ItemMenu}.
     */
    private void openMenu() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      menuInput.setCategory("");
      user.openInventory(new ItemMenu(user, ItemMenu.Action.VIEW).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.AETHELITEM_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Checks if the action is "reload" before reloading
     * {@link ItemRegistry.Item items} into {@link ItemRegistry}.
     */
    private void readParameter() {
      switch (args[0].toLowerCase()) {
        case "r", "reload" -> {
          Plugin.getData().getItemRegistry().loadData();
          user.sendMessage(ChatColor.GREEN + "[Reloaded Aethel Items]");
        }
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
      }
    }
  }
}
