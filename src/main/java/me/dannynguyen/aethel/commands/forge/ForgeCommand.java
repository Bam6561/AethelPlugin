package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuEvent;
import me.dannynguyen.aethel.plugin.PluginPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to craft items through clicking.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"" : craft {@link RecipeRegistry.Recipe recipes}
 *  <li>"edit", "e": create, edit, or remove {@link RecipeRegistry.Recipe recipes}
 *  <li>"reload", "r": reloads {@link RecipeRegistry.Recipe recipes} into {@link RecipeRegistry}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.19.9
 * @since 1.0.2
 */
public class ForgeCommand implements CommandExecutor {
  /**
   * No parameter constructor.
   */
  public ForgeCommand() {
  }

  /**
   * Executes the Forge command.
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
      if (user.hasPermission("aethel.forge")) {
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
   * Checks if the command request was formatted correctly before
   * opening a {@link RecipeMenu} or interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openCrafting(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Either edits {@link RecipeRegistry.Recipe recipes} or reloads them into {@link RecipeRegistry}.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    switch (action) {
      case "edit", "e" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          openEditor(user);
        } else {
          user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      case "reload", "r" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          Plugin.getData().getRecipeRegistry().loadData();
          user.sendMessage(ChatColor.GREEN + "[Reloaded Forge Recipes]");
        } else {
          user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETER.getMessage());
    }
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to craft {@link RecipeRegistry.Recipe recipes}.
   *
   * @param user user
   */
  private void openCrafting(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    pluginPlayer.setMode(MenuEvent.Mode.RECIPE_DETAILS_MENU_CRAFT);
    pluginPlayer.setCategory("");
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.FORGE_CATEGORY);
    pluginPlayer.setPage(0);
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to edit {@link RecipeRegistry.Recipe recipes}.
   *
   * @param user user
   */
  private void openEditor(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    pluginPlayer.setMode(MenuEvent.Mode.RECIPE_DETAILS_MENU_EDIT);
    pluginPlayer.setCategory("");
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
    pluginPlayer.setMenu(MenuEvent.Menu.FORGE_CATEGORY);
    pluginPlayer.setPage(0);
  }
}
