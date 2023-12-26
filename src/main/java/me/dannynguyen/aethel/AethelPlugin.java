package me.dannynguyen.aethel;

import me.dannynguyen.aethel.commands.Forge;
import me.dannynguyen.aethel.commands.Ping;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.ForgeRecipeReader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

/**
 * AethelPlugin represents the plugin an as object. Through event listeners and command executors,
 * the plugin can process various requests given to it by its users and the server.
 *
 * @author Danny Nguyen
 * @version 1.0.9
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

    getServer().getPluginManager().registerEvents(new InventoryListener(), this);

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
      this.forgeRecipes.add(new ForgeRecipeReader().readForgeRecipe(file));
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
