package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

/**
 * AethelItem is a command invocation that opens an inventory
 * to allow the retrieval of items through clicking.
 * <p>
 * Additional Parameters:
 * - "reload": reloads items into memory
 * </p>
 *
 * @author Danny Nguyen
 * @version 1.3.2
 * @since 1.3.2
 */
public class AethelItem implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Player-only command.");
      return true;
    }

    Player player = (Player) sender;
    if (player.isOp()) {
      readRequest(player, args);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient permissions.");
    }
    return true;
  }

  /**
   * Checks if the AethelItem request was formatted correctly before interpreting its usage.
   *
   * @param player interacting player
   * @param args   user provided parameters
   */
  private void readRequest(Player player, String[] args) {
    switch (args.length) {
      case 0 -> readItemToSave(player);
      case 1 -> readParameter(player, args[0].toLowerCase());
      default -> player.sendMessage(ChatColor.RED + "Unrecognized parameters.");
    }
  }

  /**
   * Checks if the player has an item in their main hand to save.
   *
   * @param player interacting player
   */
  private void readItemToSave(Player player) {
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item.getType() != Material.AIR) {
      saveItemToFile(player, nameItemFile(item), encodeItem(item));
    } else {
      player.sendMessage(ChatColor.RED + "No main hand item.");
    }
  }


  /**
   * Checks if the action request is "reload" before reloading items into memory.
   *
   * @param player interacting player
   * @param action type of action
   */
  private void readParameter(Player player, String action) {
    if (action.equals("reload")) {
      AethelPlugin.getInstance().getResources().getAethelItemData().loadItems();
      player.sendMessage(ChatColor.GREEN + "[Reloaded] " + ChatColor.WHITE + "Aethel Items");
    } else {
      player.sendMessage(ChatColor.RED + "Unrecognized parameter.");
    }
  }

  /**
   * Names an item file by either its display name or material.
   *
   * @param item interacting item
   * @return item file name
   */
  private String nameItemFile(ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      return meta.getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      return item.getType().name().toLowerCase();
    }
  }

  /**
   * Encodes an item into bytes.
   *
   * @param item item to encode
   * @return encoded item string
   * @throws IOException item could not be encoded
   */
  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * Saves an item file to the file system.
   *
   * @param player      interacting player
   * @param itemName    item name
   * @param encodedItem encoded item string
   * @throws IOException file could not be created
   */
  private void saveItemToFile(Player player, String itemName, String encodedItem) {
    try {
      FileWriter fw = new FileWriter(AethelPlugin.getInstance().getResources().getAethelItemDirectory()
          + "/" + itemName + ".txt");
      fw.write(encodedItem);
      fw.close();
      player.sendMessage(ChatColor.GREEN + "[Saved] " + ChatColor.WHITE + itemName + ".txt");
    } catch (IOException e) {
      player.sendMessage(ChatColor.RED + "Unable to save item.");
    }
  }

  /**
   * Opens an AethelItem inventory.
   *
   * @param player interacting player
   */
  private void openAethelItemInventory(Player player) {
  }
}
