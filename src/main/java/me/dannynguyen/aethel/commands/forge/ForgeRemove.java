package me.dannynguyen.aethel.commands.forge;


import me.dannynguyen.aethel.PluginData;
import me.dannynguyen.aethel.commands.forge.objects.ForgeRecipe;
import me.dannynguyen.aethel.enums.PluginMessage;
import me.dannynguyen.aethel.utility.ItemReader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * ForgeRemove is a utility class that removes forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.4.13
 * @since 1.0.9
 */
public class ForgeRemove {
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
    user.sendMessage(PluginMessage.Success.FORGE_REMOVE.message + ChatColor.WHITE + recipe.getName());
  }
}
