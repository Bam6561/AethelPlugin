package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.enums.plugin.Directory;
import me.dannynguyen.aethel.listeners.MenuListener;
import me.dannynguyen.aethel.plugin.MenuInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Message sent listener for {@link ForgeCommand} text inputs.
 * <p>
 * Called with {@link me.dannynguyen.aethel.listeners.MessageListener}.
 *
 * @author Danny Nguyen
 * @version 1.23.11
 * @since 1.23.8
 */
public class ForgeMessageSent {
  /**
   * Message sent event.
   */
  private final AsyncPlayerChatEvent e;

  /**
   * Player who sent the message.
   */
  private final Player user;

  /**
   * User's UUID.
   */
  private final UUID uuid;

  /**
   * Associates a message sent event with its user and encoded data in the context of
   * saving a recipe in an {@link RecipeDetailsMenu} through {@link ForgeMenuClick}.
   *
   * @param e message sent event
   */
  public ForgeMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
  }

  /**
   * Saves the {@link RecipeRegistry.Recipe recipe} under a user input folder name.
   */
  public void saveRecipe() {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String folder = e.getMessage();
    String fileName = menuInput.getFileName();
    String encodedRecipe = menuInput.getEncodedData();

    File filePath = new File(Directory.FORGE.getFile().getPath() + "/" + folder);
    if (!filePath.exists()) {
      filePath.mkdirs();
    }

    try {
      FileWriter fw = new FileWriter(filePath.getPath() + "/" + fileName + "_rcp.txt");
      fw.write(encodedRecipe);
      fw.close();

      menuInput.setFileName("");
      menuInput.setEncodedData("");

      user.sendMessage(ChatColor.GREEN + "[Saved Recipe] " + ChatColor.WHITE + folder + "/" + fileName);
    } catch (IOException ex) {
      user.sendMessage(ChatColor.RED + "Failed to write recipe to file.");
    }

    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      menuInput.setMode(MenuListener.Mode.RECIPE_DETAILS_MENU_EDIT);
      menuInput.setCategory("");
      user.openInventory(new RecipeMenu(user, RecipeMenu.Action.EDIT).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.FORGE_CATEGORY);
      menuInput.setPage(0);
    });
  }
}