package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.utils.item.ItemReader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Represents an ItemStack stored in the file system.
 * <p>
 * Loaded into memory when {@link ItemRegistry#loadData()} is called.
 *
 * @author Danny Nguyen
 * @version 1.17.12
 * @since 1.3.2
 */
public class PersistentItem {
  /**
   * Item file.
   * <ul>
   *  <li>May be deleted from file system.
   *  <li>Path persists until data is reloaded.
   * </ul>
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
   * @throws IllegalArgumentException if provided file is not a file
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
  @NotNull
  protected ItemStack getItem() {
    return this.item;
  }

  /**
   * Gets the ItemStack's effective name.
   *
   * @return item name
   */
  @NotNull
  protected String getName() {
    return this.name;
  }
}
