package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Forge;
import me.dannynguyen.aethel.commands.Ping;
import me.dannynguyen.aethel.gui.ForgeGUI;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

/**
 * AethelPlugin represents the plugin an as object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.6
 * @since 1.0.0
 */
public class AethelPlugin extends JavaPlugin {
  private final String resourceDirectory = "./plugins/Aethel";
  private ArrayList<ForgeRecipe> forgeRecipes = new ArrayList<>();

  /**
   * On startup:
   * - Reads existing plugin-related data.
   * - Registers event listeners.
   * - Registers commands.
   */
  @Override
  public void onEnable() {
    readResourceFiles(getResourceDirectory());

    getServer().getPluginManager().registerEvents(new ForgeGUI(), this);

    this.getCommand("forge").setExecutor(new Forge());
    this.getCommand("ping").setExecutor(new Ping());
  }

  /**
   * Reads existing plugin-related data. Creates data directories if they do not already exist.
   *
   * @param filePath resource directory file path
   */
  private void readResourceFiles(String filePath) {
    File resourceDirectory = new File(filePath);
    if (!resourceDirectory.exists()) resourceDirectory.mkdir();

    File forgeRecipeDirectory = new File(filePath + "/forge");
    if (forgeRecipeDirectory.exists()) {
      readForgeRecipes(new File(filePath + "/forge/"));
    } else {
      forgeRecipeDirectory.mkdir();
    }
  }

  /**
   * Reads the forge recipe directory.
   *
   * @param directory forge recipe directory file path
   */
  private void readForgeRecipes(File directory) {
    File[] forgeRecipes = directory.listFiles();
    for (File file : forgeRecipes) {
      readForgeRecipe(file);
    }
  }

  /**
   * Reads a forge recipe and loads it into memory.
   *
   * @param file forge recipe file
   * @throws FileNotFoundException file not found
   */
  private void readForgeRecipe(File file) {
    ArrayList<ItemStack> results = new ArrayList<>();
    ArrayList<ItemStack> components = new ArrayList<>();
    int recipeDataType = 1;

    try {
      Scanner scanner = new Scanner(file);
      scanner.nextLine(); // Skip Results line
      while (scanner.hasNextLine()) {
        String data = scanner.nextLine();

        if (data.equals("Components")) {
          recipeDataType++;
          data = scanner.nextLine();
        }

        ItemStack item = decodeItem(data);
        if (item != null) {
          switch (recipeDataType) {
            case 1 -> results.add(decodeItem(data));
            case 2 -> components.add(decodeItem(data));
          }
        }
      }
      this.forgeRecipes.add(new ForgeRecipe(results, components));
    } catch (FileNotFoundException ex) {
    }
  }

  /**
   * Deserializes an item.
   *
   * @param data serialized item string
   * @return ItemStack representing item
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

  /**
   * Returns the file path of the plugin's resource directory.
   *
   * @return resource directory
   */
  public String getResourceDirectory() {
    return this.resourceDirectory;
  }

  /**
   * Returns the ForgeRecipes loaded into memory.
   *
   * @return arraylist containing forge recipes
   */
  public ArrayList<ForgeRecipe> getForgeRecipes() {
    return this.forgeRecipes;
  }

  /**
   * Returns an AethelPlugin object that identifies this plugin.
   *
   * @return plugin
   */
  public static AethelPlugin getInstance() {
    return getPlugin(AethelPlugin.class);
  }
}
