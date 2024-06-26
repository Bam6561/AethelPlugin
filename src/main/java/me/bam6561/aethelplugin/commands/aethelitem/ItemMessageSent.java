package me.bam6561.aethelplugin.commands.aethelitem;

import me.bam6561.aethelplugin.Plugin;
import me.bam6561.aethelplugin.enums.plugin.Directory;
import me.bam6561.aethelplugin.listeners.MenuListener;
import me.bam6561.aethelplugin.plugin.MenuInput;
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
 * Message sent listener for {@link ItemCommand} text inputs.
 * <p>
 * Called with {@link me.bam6561.aethelplugin.listeners.MessageListener}.
 *
 * @author Danny Nguyen
 * @version 1.23.18
 * @since 1.23.13
 */
public class ItemMessageSent {
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
   * Associates a message sent event with its user and encoded data in the
   * context of saving an item in a {@link ItemMenu} through {@link ItemMenuClick}.
   *
   * @param e message sent event
   */
  public ItemMessageSent(@NotNull AsyncPlayerChatEvent e) {
    this.e = Objects.requireNonNull(e, "Null message sent event");
    this.user = e.getPlayer();
    this.uuid = user.getUniqueId();
  }

  /**
   * Saves the {@link ItemRegistry.Item item} under a user input folder name.
   */
  public void saveItem() {
    MenuInput menuInput = Plugin.getData().getPluginSystem().getPluginPlayers().get(uuid).getMenuInput();
    String folder = e.getMessage();
    String itemName = menuInput.getObjectType();
    String fileName = menuInput.getFileName();
    String encodedItem = menuInput.getEncodedData();

    ItemRegistry.Item item = Plugin.getData().getItemRegistry().getItems().get(itemName);
    if (item != null) {
      item.delete();
    }

    try {
      File filePath = new File(Directory.AETHELITEM.getFile().getPath() + "/" + folder);
      if (!filePath.exists()) {
        filePath.mkdirs();
      }

      FileWriter fw = new FileWriter(filePath.getPath() + "/" + fileName + "_itm.txt");
      fw.write(encodedItem);
      fw.close();

      menuInput.setFileName("");
      menuInput.setEncodedData("");

      user.sendMessage(ChatColor.GREEN + "[Saved Item] " + ChatColor.WHITE + folder + "/" + fileName);
    } catch (IOException ex) {
      user.sendMessage(ChatColor.RED + "Failed to write item to file.");
    }

    Bukkit.getScheduler().runTask(Plugin.getInstance(), () -> {
      menuInput.setCategory("");
      user.openInventory(new ItemMenu(user, ItemMenu.Action.VIEW).getMainMenu());
      menuInput.setMenu(MenuListener.Menu.AETHELITEM_CATEGORY);
      menuInput.setPage(0);
    });
  }
}
