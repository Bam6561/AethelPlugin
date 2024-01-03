package me.dannynguyen.aethel.readers;

import me.dannynguyen.aethel.objects.AethelItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

/**
 * AethelItemReader decodes items from the file system.
 *
 * @author Danny Nguyen
 * @version 1.4.0
 * @since 1.4.0
 */
public class AethelItemReader {
  /**
   * Reads an item file.
   *
   * @param file
   * @return decoded item
   * @throws IOException file not found
   */
  public AethelItem readItem(File file) {
    try {
      Scanner scanner = new Scanner(file);
      ItemStack item = decodeItem(scanner.nextLine());
      return new AethelItem(file, new ItemMetaReader().readItemName(item), item);
    } catch (IOException ex) {
      return null;
    }
  }

  /**
   * Deserializes an item.
   *
   * @param data serialized item string
   * @return item
   * @throws IOException            file not found
   * @throws ClassNotFoundException item could not be decoded
   */
  private ItemStack decodeItem(String data) {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(data));
      BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
      ItemStack item = (ItemStack) bois.readObject();
      return item;
    } catch (IOException | ClassNotFoundException ex) {
      return null;
    }
  }
}
