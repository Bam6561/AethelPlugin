package me.dannynguyen.aethel.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

/**
 * @author Danny Nguyen
 * @version 1.0.3
 * @since 1.0.3
 */
public class ReadCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }
    Bukkit.getLogger().warning(args[0]);
    Player player = (Player) sender;
    decodeItem(player, readFile(args));
    return true;
  }

  private String readFile(String[] args) {
    String data = "";
    try {
      File file = new File("./plugins/Aethel/" + args[0] + ".txt");
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        data = scanner.nextLine();
      }
    } catch (FileNotFoundException e) {
      return "";
    }
    return data;
  }

  private void decodeItem(Player player, String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      ItemStack item = (ItemStack) bois.readObject();
      player.getInventory().setItemInMainHand(item);
    } catch (IOException | ClassNotFoundException e) {

    }
  }
}
