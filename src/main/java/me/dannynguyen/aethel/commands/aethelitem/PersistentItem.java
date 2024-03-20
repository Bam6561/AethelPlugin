package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * Represents an ItemStack stored in the file system.
 *
 * @author Danny Nguyen
 * @version 1.17.7
 * @since 1.3.2
 */
class PersistentItem {
  /**
   * Item file.
   * <p>
   * - May be deleted from file system.
   * </p>
   * <p>
   * - Path persists until data is reloaded.
   * </p>
   */
  private final File file;

  /**
   * ItemStack.
   */
  private final ItemStack item;

  /**
   * Effective item name.
   */
  private final String name;

  /**
   * Associates an ItemStack with its file.
   *
   * @param file item file
   * @param item ItemStack
   * @throws IllegalArgumentException provided file is not a file
   */
  PersistentItem(File file, ItemStack item) throws IllegalArgumentException {
    this.file = file;
    this.item = item;
      this.name = ItemReader.readName(item);
  }

  /**
   * Deletes the item file from the file system.
   */
  protected void delete() {
    file.delete();
  }

  /**
   * Gets the ItemStack.
   *
   * @return ItemStack
   */
  protected ItemStack getItem() {
    return this.item;
  }

  /**
   * Gets the ItemStack's effective name.
   *
   * @return item name
   */
  protected String getName() {
    return this.name;
  }
}
