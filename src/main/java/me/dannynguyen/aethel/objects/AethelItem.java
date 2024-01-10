package me.dannynguyen.aethel.objects;

import org.bukkit.inventory.ItemStack;

import java.io.File;

/**
 * AethelItem is an object relating a custom item with its file in storage.
 *
 * @author Danny Nguyen
 * @version 1.5.1
 * @since 1.3.2
 */
public record AethelItem(File file, String name, ItemStack item) {

  public File getFile() {
    return this.file;
  }

  public String getName() {
    return this.name;
  }

  public ItemStack getItem() {
    return this.item;
  }
}
