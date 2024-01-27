package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginMetadata;
import me.dannynguyen.aethel.enums.PluginPermission;
import me.dannynguyen.aethel.inventories.forge.ForgeI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * ForgeC is a command invocation that allows the user to fabricate items through clicking.
 * <p>
 * Additional Parameters:
 * - "edit", "e": allows the user to create, edit, or remove forge recipes
 * - "reload", "r": reloads forge recipes into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.7.6
 * @since 1.0.2
 */
public class ForgeC implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player user)) {
      sender.sendMessage(PluginMessage.PLAYER_ONLY_COMMAND.message);
      return true;
    }

    if (user.hasPermission(PluginPermission.FORGE.permission)) {
      readRequest(user, args);
    } else {
      user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
    }
    return true;
  }

  /**
   * Checks if the command request was formatted correctly before
   * opening a Forge crafting menu or interpreting its usage.
   *
   * @param user user
   * @param args user provided parameters
   */
  private void readRequest(Player user, String[] args) {
    switch (args.length) {
      case 0 -> openCraftingMenu(user);
      case 1 -> interpretParameter(user, args[0].toLowerCase());
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETERS.message);
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
        if (user.hasPermission(PluginPermission.FORGE_EDITOR.permission)) {
          openEditorMenu(user);
        } else {
          user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
        }
      }
      case "reload", "r" -> {
        if (user.hasPermission(PluginPermission.FORGE_EDITOR.permission)) {
          AethelResources.forgeRecipeData.loadRecipes();
          user.sendMessage(PluginMessage.FORGE_RELOAD.message);
        } else {
          user.sendMessage(PluginMessage.INSUFFICIENT_PERMISSION.message);
        }
      }
      default -> user.sendMessage(PluginMessage.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Opens a Forge main menu with the intent to craft recipes.
   *
   * @param user interacting player
   */
  private void openCraftingMenu(Player user) {
    user.setMetadata(PluginMetadata.FUTURE.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), "craft"));
    user.setMetadata(PluginMetadata.CATEGORY.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), ""));

    user.openInventory(ForgeI.openMainMenu(user, "craft"));

    user.setMetadata(PluginMetadata.INVENTORY.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.FORGE_CATEGORY));
    user.setMetadata(PluginMetadata.PAGE.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }

  /**
   * Opens a Forge main menu with the intent to edit recipes.
   *
   * @param user interacting player
   */
  private void openEditorMenu(Player user) {
    user.setMetadata(PluginMetadata.FUTURE.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), "edit"));
    user.setMetadata(PluginMetadata.CATEGORY.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), ""));

    user.openInventory(ForgeI.openMainMenu(user, "edit"));

    user.setMetadata(PluginMetadata.INVENTORY.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), PluginMetadata.FORGE_CATEGORY));
    user.setMetadata(PluginMetadata.PAGE.data,
        new FixedMetadataValue(AethelPlugin.getInstance(), "0"));
  }
}
