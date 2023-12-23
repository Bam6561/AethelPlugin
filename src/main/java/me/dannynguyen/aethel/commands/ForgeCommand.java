package me.dannynguyen.aethel.commands;

import me.dannynguyen.aethel.AethelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Forge is a command invocation that opens an inventory that allows the fabrication of items through clicking.
 *
 * @author Danny Nguyen
 * @version 1.0.4
 * @since 1.0.2
 */
public class ForgeCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }

    Player player = (Player) sender;
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("editor") && player.isOp()) {
        player.openInventory(createEditorMenu(player));
      } else {
        player.sendMessage("Insufficient permissions.");
      }
    } else {
      player.openInventory(createCraftMenu(player));
    }
    return true;
  }

  private Inventory createEditorMenu(Player player) {
    Inventory editorMenu = Bukkit.createInventory(player, 9, "Forge Editor");
    editorMenu.setItem(2, createMenuItem(Material.GREEN_CONCRETE, "Create Recipe"));
    editorMenu.setItem(4, createMenuItem(Material.YELLOW_CONCRETE, "Modify Recipe"));
    editorMenu.setItem(6, createMenuItem(Material.RED_CONCRETE, "Delete Recipe"));
    player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-editor"));
    return editorMenu;
  }

  private Inventory createCraftMenu(Player player) {
    Inventory craftMenu = Bukkit.createInventory(player, 54, "Forge");
    craftMenu.setItem(0, new ItemStack(Material.RED_WOOL));
    craftMenu.setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(4, new ItemStack(Material.COMPASS));
    craftMenu.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    craftMenu.setItem(8, new ItemStack(Material.GREEN_WOOL));
    player.setMetadata("menu", new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft"));
    return craftMenu;
  }

  private ItemStack createMenuItem(Material material, String displayName) {
    ItemStack item = new ItemStack(material, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }
}
