package me.dannynguyen.aethel.commands.aethelitem;

import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * Represents a ItemStack stored in the file system.
 *
 * @author Danny Nguyen
 * @version 1.11.6
 * @since 1.3.2
 */
class PersistentItem {
  /**
   * Item file.
   * <p>
   * - May be deleted from file system.
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
  public PersistentItem(@NotNull File file, @NotNull ItemStack item) throws IllegalArgumentException {
    if (file.isFile()) {
      this.file = Objects.requireNonNull(file, "Null file");
      this.item = Objects.requireNonNull(item, "Null item");
      this.name = ItemReader.readName(item);
    } else {
      throw new IllegalArgumentException("Non-file");
    }
  }

  /**
   * Deletes the item file from the file system.
   *
   * @return true if the file was deleted
   */
  protected boolean delete() {
    return getFile().delete();
  }

  /**
   * Gets the file the ItemStack is stored in.
   *
   * @return item file
   */
  @NotNull
  protected File getFile() {
    return this.file;
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
