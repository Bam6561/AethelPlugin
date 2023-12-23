package me.dannynguyen.aethel.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

/**
 * @author Danny Nguyen
 * @version 1.0.3
 * @since 1.0.3
 */
public class WriteCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }
    Player player = (Player) sender;
    ItemStack item = player.getInventory().getItemInMainHand();
    String itemName;

    if (item.getItemMeta().hasDisplayName()) {
      itemName = item.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_");
    } else {
      itemName = item.getType().name().toLowerCase();
    }
    writeToFile(itemName, encodeItem(item));
    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    return true;
  }

  private String encodeItem(ItemStack item) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
      boos.writeObject(item);
      boos.flush();

      byte[] serializedItem = baos.toByteArray();

      return Base64.getEncoder().encodeToString(serializedItem);
    } catch (IOException e) {
      return null;
    }
  }

  private void writeToFile(String itemName, String encodedItem) {
    try {
      String fileName = itemName;
      File file = new File("./plugins/Aethel/" + fileName + ".txt");
      FileWriter fw = new FileWriter(file);
      fw.write(encodedItem);
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
