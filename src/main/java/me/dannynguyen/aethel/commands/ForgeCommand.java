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
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Forge is a command invocation that opens an inventory that allows the fabrication of items through clicking.
 *
 * @author Danny Nguyen
 * @version 1.0.3
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
    Inventory menu = Bukkit.createInventory(player, 54, "Forge");
    populateMenu(menu);
    player.openInventory(menu);
    player.setMetadata("Menu", new FixedMetadataValue(AethelPlugin.getInstance(), "Forge-Craft"));
    return true;
  }

  private void populateMenu(Inventory menu) {
    menu.setItem(0, new ItemStack(Material.RED_WOOL));
    menu.setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(2, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(4, new ItemStack(Material.COMPASS));
    menu.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(6, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    menu.setItem(8, new ItemStack(Material.GREEN_WOOL));
    menu.setItem(9, new ItemStack(Material.DIAMOND_SWORD));
    menu.setItem(10, new ItemStack(Material.SHIELD));
  }
}
