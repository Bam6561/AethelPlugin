package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.systems.plugin.PluginData;
import me.dannynguyen.aethel.systems.plugin.enums.MenuMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PlayerMeta;
import me.dannynguyen.aethel.systems.plugin.enums.PluginMessage;
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
 * Additional Parameters:
 * - "edit", "e": create, edit, or remove Forge recipes
 * - "reload", "r": reloads Forge recipes into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.10.1
 * @since 1.0.2
 */
public class ForgeCommand implements CommandExecutor {
  /**
   * Executes the Forge command.
   *
   * @param sender  command source
   * @param command executed command
   * @param label   command alias used
   * @param args    command arguments
   * @return true if a valid command
   */
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
    if (sender instanceof Player user) {
      if (user.hasPermission("aethel.forge")) {
        readRequest(user, args);
      } else {
        user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.getMessage());
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before
   * opening a Recipe crafting menu or interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openCraftingMenu(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.getMessage());
    }
  }

  /**
   * Either edits recipes or reloads them into memory.
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
          user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      case "reload", "r" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          PluginData.recipeRegistry.loadData();
          user.sendMessage(ChatColor.GREEN + "[Reloaded Forge Recipes]");
        } else {
          user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETER.getMessage());
    }
  }

  /**
   * Opens the Recipe menu with the intent to craft recipes.
   *
   * @param user user
   */
  private void openCraftingMenu(Player user) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.put(PlayerMeta.FUTURE, "craft");
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.CRAFT).openMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }

  /**
   * Opens the  Recipe menu with the intent to edit recipes.
   *
   * @param user user
   */
  private void openEditorMenu(Player user) {
    Map<PlayerMeta, String> playerMeta = PluginData.pluginSystem.getPlayerMetadata().get(user);
    playerMeta.put(PlayerMeta.FUTURE, "edit");
    playerMeta.put(PlayerMeta.CATEGORY, "");
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.EDIT).openMainMenu());
    playerMeta.put(PlayerMeta.INVENTORY, MenuMeta.FORGE_CATEGORY.getMeta());
    playerMeta.put(PlayerMeta.PAGE, "0");
  }
}
