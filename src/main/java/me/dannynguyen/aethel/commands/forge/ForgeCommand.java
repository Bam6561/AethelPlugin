package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryMenuListener;
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
 * @version 1.9.15
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
        user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
      }
    } else {
      sender.sendMessage(PluginMessage.Failure.PLAYER_ONLY_COMMAND.message);
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
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETERS.message);
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
          user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
        }
      }
      case "reload", "r" -> {
        if (user.hasPermission("aethel.forge.editor")) {
          PluginData.forgeData.loadData();
          user.sendMessage(ChatColor.GREEN + "[Reloaded Forge Recipes]");
        } else {
          user.sendMessage(PluginMessage.Failure.INSUFFICIENT_PERMISSION.message);
        }
      }
      default -> user.sendMessage(PluginMessage.Failure.UNRECOGNIZED_PARAMETER.message);
    }
  }

  /**
   * Opens a Forge main menu with the intent to craft recipes.
   *
   * @param user user
   */
  private void openCraftingMenu(Player user) {
    user.setMetadata(PluginPlayerMeta.FUTURE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "craft"));
    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), ""));
    user.openInventory(new ForgeMenu(user, ForgeMenuAction.CRAFT).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }

  /**
   * Opens a Forge main menu with the intent to edit recipes.
   *
   * @param user user
   */
  private void openEditorMenu(Player user) {
    user.setMetadata(PluginPlayerMeta.FUTURE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "edit"));
    user.setMetadata(PluginPlayerMeta.CATEGORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), ""));
    user.openInventory(new ForgeMenu(user, ForgeMenuAction.EDIT).openMainMenu());
    user.setMetadata(PluginPlayerMeta.INVENTORY.getMeta(), new FixedMetadataValue(Plugin.getInstance(), InventoryMenuListener.Menu.FORGE_CATEGORY.menu));
    user.setMetadata(PluginPlayerMeta.PAGE.getMeta(), new FixedMetadataValue(Plugin.getInstance(), "0"));
  }
}
