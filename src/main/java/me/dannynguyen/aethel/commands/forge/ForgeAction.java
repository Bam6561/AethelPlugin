package me.dannynguyen.aethel.commands.forge;

import me.dannynguyen.aethel.Plugin;
import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.object.ForgeRecipe;
import me.dannynguyen.aethel.enums.PluginDirectory;
import me.dannynguyen.aethel.enums.PluginPlayerHead;
import me.dannynguyen.aethel.enums.PluginPlayerMeta;
import me.dannynguyen.aethel.listeners.InventoryListener;
import me.dannynguyen.aethel.utility.ItemCreator;
import me.dannynguyen.aethel.utility.ItemReader;
import me.dannynguyen.aethel.utility.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * ForgeAction is a utility class that:
 * - expands recipes' details
 * - saves new recipes
 * - edits recipes
 * - removes recipes
 *
 * @author Danny Nguyen
 * @version 1.8.4
 * @since 1.7.13
 */
public class ForgeAction {
  private enum Success {
    SAVE_RECIPE(ChatColor.GREEN + "[Saved Recipe] "),
    REMOVE_RECIPE(ChatColor.RED + "[Removed Recipe] ");

    public final String message;

    Success(String message) {
      this.message = message;
    }
  }

  private enum Failure {
    NO_RECIPE_COMPONENTS(ChatColor.RED + "No recipe components."),
    NO_RECIPE_RESULTS(ChatColor.RED + "No recipe results."),
    UNABLE_TO_SAVE(ChatColor.RED + "Unable to save recipe.");

    public final String message;

    Failure(String message) {
      this.message = message;
    }
  }

  private enum Context {
    EXPANDED_CRAFT(List.of(
        ChatColor.AQUA + "Rows",
        ChatColor.AQUA + "1 " + ChatColor.WHITE + "Results",
        ChatColor.AQUA + "2 " + ChatColor.WHITE + "Components",
        ChatColor.AQUA + "3 " + ChatColor.WHITE + "Components"));

    public final List<String> context;

    Context(List<String> context) {
      this.context = context;
    }
  }

  /**
   * Expands the recipe's details to the user before crafting.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void craftRecipeDetails(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.
        getRecipesMap().get(ItemReader.readName(e.getCurrentItem()));

    String invType = "craft";
    Inventory inv = createInventory(user, invType);
    addRecipeContents(recipe, inv);
    addContext(inv);
    addActions(inv, invType);

    user.openInventory(inv);
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_CRAFT_CONFIRM.inventory));
  }

  /**
   * Expands the recipe's details to the user before crafting.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void editRecipeDetails(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.
        getRecipesMap().get(ItemReader.readName(e.getCurrentItem()));

    String invType = "save";
    Inventory inv = createInventory(user, invType);
    addRecipeContents(recipe, inv);
    addContext(inv);
    addActions(inv, invType);

    user.openInventory(inv);
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_SAVE.inventory));
  }

  /**
   * Opens a ForgeSave inventory.
   *
   * @param user user
   */
  public static void openForgeSaveInventory(Player user) {
    String invType = "save";
    Inventory inv = createInventory(user, invType);
    addContext(inv);
    addActions(inv, invType);

    user.openInventory(inv);
    user.setMetadata(PluginPlayerMeta.Namespace.INVENTORY.namespace,
        new FixedMetadataValue(Plugin.getInstance(), InventoryListener.Inventory.FORGE_SAVE.inventory));
  }

  /**
   * Removes an existing recipe.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void removeRecipe(InventoryClickEvent e, Player user) {
    ForgeRecipe recipe = PluginData.forgeData.
        getRecipesMap().get(ItemReader.readName(e.getCurrentItem()));

    recipe.getFile().delete();
    user.sendMessage(Success.REMOVE_RECIPE.message + ChatColor.WHITE + recipe.getName());
  }

  /**
   * Checks if the Forge recipe's details were formatted correctly before saving the recipe.
   *
   * @param e    inventory click event
   * @param user user
   */
  public static void readSaveClick(InventoryClickEvent e, Player user) {
    ItemStack[] inv = e.getInventory().getContents();
    String fileName = nameRecipeFile(inv);
    if (!fileName.equals("")) {
      String encodedRecipe = encodeRecipe(inv);
      if (!encodedRecipe.equals("")) {
        saveRecipeToFile(user, fileName, encodedRecipe);
      } else {
        user.sendMessage(Failure.NO_RECIPE_COMPONENTS.message);
      }
    } else {
      user.sendMessage(Failure.NO_RECIPE_RESULTS.message);
    }
    e.setCancelled(true);
  }

  /**
   * Creates and names a ForgeCraft or ForgeSave inventory.
   *
   * @param user    user
   * @param invType inventory type
   * @return ForgeCraft or ForgeSave inventory
   */
  private static Inventory createInventory(Player user, String invType) {
    if (invType.equals("craft")) {
      return Bukkit.createInventory(user, 27,
          ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft");
    } else {
      return Bukkit.createInventory(user, 27,
          ChatColor.DARK_GRAY + "Forge" + ChatColor.DARK_GREEN + " Save");
    }
  }

  /**
   * Adds the recipe's results and components to the inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private static void addRecipeContents(ForgeRecipe recipe, Inventory inv) {
    List<ItemStack> results = recipe.getResults();
    List<ItemStack> components = recipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }
  }

  /**
   * Adds a help context to the expanded craft or save action.
   *
   * @param inv interacting inventory
   */
  private static void addContext(Inventory inv) {
    inv.setItem(8, ItemCreator.createPluginPlayerHead(PluginPlayerHead.QUESTION_MARK_WHITE.head,
        ChatColor.GREEN + "Help", Context.EXPANDED_CRAFT.context));
  }

  /**
   * Adds craft or save and back buttons.
   *
   * @param inv     interacting inventory
   * @param invType inventory type
   */
  private static void addActions(Inventory inv, String invType) {
    if (invType.equals("craft")) {
      inv.setItem(25, ItemCreator.
          createPluginPlayerHead(PluginPlayerHead.CRAFTING_TABLE.head, ChatColor.AQUA + "Craft"));
    } else {
      inv.setItem(25, ItemCreator.
          createPluginPlayerHead(PluginPlayerHead.STACK_OF_PAPER.head, ChatColor.AQUA + "Save"));
    }
    inv.setItem(26, ItemCreator.
        createPluginPlayerHead(PluginPlayerHead.BACKWARD_GRAY.head, ChatColor.AQUA + "Back"));
  }

  /**
   * Names a recipe by the first item in the results row.
   *
   * @param inv items in the inventory
   * @return recipe file name
   */
  private static String nameRecipeFile(ItemStack[] inv) {
    for (int i = 0; i < 8; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
          return meta.getDisplayName().toLowerCase().replace(" ", "_");
        } else {
          return item.getType().name().toLowerCase();
        }
      }
    }
    return "";
  }

  /**
   * Encodes the inventory by its results and components.
   * <p>
   * At this stage in the process, it is known the results are non-null,
   * so the method checks if the components are non-null first.
   * <p>
   *
   * @param inv items in the inventory
   * @return encoded recipe string
   */
  private static String encodeRecipe(ItemStack[] inv) {
    StringBuilder components = new StringBuilder();
    for (int i = 9; i < 24; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        components.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }

    if (components.toString().equals("")) {
      return "";
    }

    StringBuilder results = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      ItemStack item = inv[i];
      if (item != null) {
        results.append(ItemCreator.encodeItem(item)).append(" ");
      }
    }

    return results.append("\n").append(components).toString();
  }

  /**
   * Saves a recipe file to the file system.
   *
   * @param user          user
   * @param fileName      file name
   * @param encodedRecipe encoded recipe string
   * @throws IOException file could not be created
   */
  private static void saveRecipeToFile(Player user, String fileName, String encodedRecipe) {
    try {
      FileWriter fw = new FileWriter(PluginDirectory.FORGE.file.getPath()
          + "/" + fileName + "_rcp.txt");
      fw.write(encodedRecipe);
      fw.close();
      user.sendMessage(Success.SAVE_RECIPE.message + ChatColor.WHITE + TextFormatter.capitalizePhrase(fileName));
    } catch (IOException ex) {
      user.sendMessage(Failure.UNABLE_TO_SAVE.message);
    }
  }
}
