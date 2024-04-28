package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Message;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
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
 * @version 1.23.11
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
   * Represents a Forge command request.
   *
   * @param user command user
   * @param args user provided parameters
   * @author Danny Nguyen
   * @version 1.23.11
   * @since 1.23.11
   */
  private record Request(Player user, String[] args) {
    /**
     * Checks if the command request was formatted correctly before
     * opening a {@link RecipeMenu} or interpreting its usage.
     */
    private void readRequest() {
      switch (args.length) {
        case 0 -> openCrafting();
        case 1 -> interpretParameter();
        default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
      }
    }

    /**
     * Either edits {@link RecipeRegistry.Recipe recipes} or reloads them into {@link RecipeRegistry}.
     */
    private void interpretParameter() {
      switch (args[0].toLowerCase()) {
        case "e", "edit" -> {
          if (user.hasPermission("aethel.forge.editor")) {
            openEditor();
          } else {
            user.sendMessage(Message.INSUFFICIENT_PERMISSION.getMessage());
          }
        }
        case "r", "reload" -> {
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
     */
    private void openCrafting() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      menuInput.setMode(MenuListener.Mode.RECIPE_DETAILS_MENU_CRAFT);
      menuInput.setCategory("");
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.FORGE_CATEGORY);
      menuInput.setPage(0);
    }

    /**
     * Opens the {@link RecipeMenu} with the intent to edit {@link RecipeRegistry.Recipe recipes}.
     */
    private void openEditor() {
      MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId()).getMenuInput();
      menuInput.setMode(MenuListener.Mode.RECIPE_DETAILS_MENU_EDIT);
      menuInput.setCategory("");
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.FORGE_CATEGORY);
      menuInput.setPage(0);
    }
  }
}
