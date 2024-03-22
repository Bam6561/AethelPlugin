package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.plugin.enums.Message;
import me.dannynguyen.aethel.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.plugin.listeners.MenuClick;
import me.dannynguyen.aethel.plugin.system.PluginPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command invocation that allows the user to craft items through clicking.
 * <p>
 * Registered through {@link Plugin}.
 * <p>
 * Parameters:
 * <ul>
 *  <li>"" : craft {@link PersistentRecipe recipes}
 *  <li>"edit", "e": create, edit, or remove {@link PersistentRecipe recipes}
 *  <li>"reload", "r": reloads {@link PersistentRecipe recipes} into {@link RecipeRegistry}
 * </ul>
 *
 * @author Danny Nguyen
 * @version 1.17.17
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
      case 0 -> openCraftingMenu(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(Message.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Either edits {@link PersistentRecipe recipes} or reloads them into {@link RecipeRegistry}.
   *
   * @param user   user
   * @param action type of interaction
   */
  private void interpretParameter(Player user, String action) {
    switch (action) {
      case "edit", "e" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          openEditorMenu(user);
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
   * Opens the {@link RecipeMenu}  with the intent to craft {@link PersistentRecipe recipes}.
   *
   * @param user user
   */
  private void openCraftingMenu(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    pluginPlayer.setMode(MenuClick.Mode.RECIPE_MENU_CRAFT);
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.CRAFT).getMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the {@link RecipeMenu} with the intent to edit {@link PersistentRecipe recipes}.
   *
   * @param user user
   */
  private void openEditorMenu(Player user) {
    PluginPlayer pluginPlayer = Plugin.getData().getPluginSystem().getPluginPlayers().get(user.getUniqueId());
    Map<PlayerMeta, String> playerMeta = Plugin.getData().getPluginSystem().getPlayerMetadata().get(user.getUniqueId());
    pluginPlayer.setMode(MenuClick.Mode.RECIPE_MENU_EDIT);
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }
}
