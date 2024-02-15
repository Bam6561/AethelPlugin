package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.PluginEnum;
import me.dannynguyen.aethel.listeners.MenuClick;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * Command invocation that allows the user to craft items through clicking.
 * <p>
 * Additional Parameters:
 * - "edit", "e": create, edit, or remove Forge recipes
 * - "reload", "r": reloads Forge recipes into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.9.21
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
        user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
      }
    } else {
      sender.sendMessage(PluginEnum.Message.PLAYER_ONLY_COMMAND.getMessage());
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
      default -> user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETERS.getMessage());
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
          user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      case "reload", "r" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          PluginData.recipeRegistry.loadData();
          user.sendMessage(ChatColor.GREEN + "[Reloaded Forge Recipes]");
        } else {
          user.sendMessage(PluginEnum.Message.INSUFFICIENT_PERMISSION.getMessage());
        }
      }
      default -> user.sendMessage(PluginEnum.Message.UNRECOGNIZED_PARAMETER.getMessage());
    }
  }

  /**
   * Opens the Recipe menu with the intent to craft recipes.
   *
   * @param user user
   */
  private void openCraftingMenu(Player user) {
    user.setMetadata(PluginEnum.PlayerMeta.FUTURE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "craft"));
    user.setMetadata(PluginEnum.PlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), ""));
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.CRAFT).openMainMenu());
    user.setMetadata(PluginEnum.PlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), MenuClick.Menu.FORGE_CATEGORY.menu));
    user.setMetadata(PluginEnum.PlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens the  Recipe menu with the intent to edit recipes.
   *
   * @param user user
   */
  private void openEditorMenu(Player user) {
    user.setMetadata(PluginEnum.PlayerMeta.FUTURE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "edit"));
    user.setMetadata(PluginEnum.PlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), ""));
    user.openInventory(new RecipeMenu(user, ForgeMenuAction.EDIT).openMainMenu());
    user.setMetadata(PluginEnum.PlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), MenuClick.Menu.FORGE_CATEGORY.menu));
    user.setMetadata(PluginEnum.PlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }
}
