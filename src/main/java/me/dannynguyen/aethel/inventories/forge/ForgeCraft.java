package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.SlotAmount;
import me.dannynguyen.aethel.readers.ItemMetaReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ForgeCraft is an inventory under the Forge command that crafts forge recipes.
 *
 * @author Danny Nguyen
 * @version 1.2.3
 * @since 1.1.0
 */
public class ForgeCraft {
  ArrayList<SlotAmount> slotAmounts = new ArrayList<>();

  /**
   * Creates and names a ForgeCraft inventory.
   *
   * @param player interacting player
   * @return ForgeCraft inventory
   */
  public Inventory createInventory(Player player) {
    String title = ChatColor.DARK_GRAY + "Forge" + ChatColor.BLUE + " Craft";
    Inventory inv = Bukkit.createInventory(player, 27, title);
    ItemCreator itemCreator = new ItemCreator();
    inv.setItem(25, itemCreator.
        createPlayerHead("Crafting Table", ChatColor.AQUA + "Craft"));
    inv.setItem(26, itemCreator.
        createPlayerHead("Gray Backward", ChatColor.AQUA + "Back"));
    return inv;
  }

  /**
   * Expands the recipe's details to the player before crafting.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void expandRecipeDetails(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemMetaReader().getItemName(e.getCurrentItem()));

    Inventory inv = createInventory(player);
    addExistingRecipeContents(recipe, inv);

    player.openInventory(inv);
    player.setMetadata("inventory",
        new FixedMetadataValue(AethelPlugin.getInstance(), "forge-craft-confirm"));
  }

  /**
   * Adds the recipe's existing results and components to the ForgeCreate inventory.
   *
   * @param recipe forge recipe
   * @param inv    interacting inventory
   */
  private void addExistingRecipeContents(ForgeRecipe recipe, Inventory inv) {
    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    for (int i = 0; i < results.size(); i++) {
      inv.setItem(i, results.get(i));
    }
    for (int i = 0; i < components.size(); i++) {
      inv.setItem(i + 9, components.get(i));
    }
  }

  /**
   * Crafts a recipe.
   *
   * @param e      inventory click event
   * @param player interacting player
   */
  public void craftRecipe(InventoryClickEvent e, Player player) {
    AethelResources resources = AethelPlugin.getInstance().getResources();
    ForgeRecipe recipe = resources.getForgeRecipeData().getRecipesMap().
        get(new ItemMetaReader().getItemName(e.getClickedInventory().getItem(0)));

    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    if (checkExactMatch(player, components)) {
      processExactMatch(player, components, results);
    } else if (checkMatchingType(player, components)) {
      processMatchingType(player, components, results);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient components.");
    }
  }

  /**
   * Determines if the player has sufficient exact matching components to craft the recipe.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @return has sufficient components
   */
  private boolean checkExactMatch(Player player, ArrayList<ItemStack> components) {
    for (ItemStack item : components) {
      if (!player.getInventory().containsAtLeast(item, item.getAmount())) return false;
    }
    return true;
  }

  /**
   * Determines if the player has sufficient matching type components to craft the recipe.
   *
   * @param player     interacting player
   * @param components components in recipe
   * @return has sufficient components
   */
  private boolean checkMatchingType(Player player, ArrayList<ItemStack> components) {
    Inventory inv = player.getInventory();
    HashMap<Material, ArrayList<Integer>> invMap = mapMaterialIndices(inv);

    for (ItemStack item : components) {
      Material reqMaterial = item.getType();
      if (invMap.containsKey(reqMaterial)) {
        if (!checkSufficientMaterials(inv, invMap, reqMaterial, item.getAmount())) return false;
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Maps the player's inventory.
   *
   * @param inv player inventory
   * @return material:indices inventory map
   */
  private HashMap<Material, ArrayList<Integer>> mapMaterialIndices(Inventory inv) {
    HashMap<Material, ArrayList<Integer>> invMap = new HashMap<>();

    for (int i = 0; i < 36; i++) {
      if (inv.getItem(i) != null) {
        Material material = inv.getItem(i).getType();

        if (invMap.containsKey(material)) {
          invMap.get(material).add(i);
        } else {
          ArrayList<Integer> indices = new ArrayList<>();
          indices.add(i);
          invMap.put(material, indices);
        }
      }
    }
    return invMap;
  }

  /**
   * Determines if the player has sufficient amounts of the required material.
   *
   * @param inv         player inventory
   * @param invMap      material:indices inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @return has sufficient amounts of material
   */
  private boolean checkSufficientMaterials(Inventory inv, HashMap<Material, ArrayList<Integer>> invMap,
                                           Material reqMaterial, int reqAmount) {
    for (int index : invMap.get(reqMaterial)) {
      ItemStack invItem = inv.getItem(index);
      if (invItem.getItemMeta().getPersistentDataContainer().isEmpty()) {
        reqAmount -= invItem.getAmount();
        if (checkReqAmountSatisfied(index, reqAmount)) return true;
      }
    }
    return false;
  }

  /**
   * Determines if the required amount of material was satisfied.
   *
   * @param index     player inventory index
   * @param reqAmount required amount
   * @return has sufficient amounts of material
   */
  private boolean checkReqAmountSatisfied(int index, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      getSlotAmounts().add(new SlotAmount(index, 0));
      if (reqAmount == 0) {
        return true;
      }
    } else {
      getSlotAmounts().add(new SlotAmount(index, Math.abs(reqAmount)));
      return true;
    }
    return false;
  }

  /**
   * Removes the recipe's components.
   *
   * @param player     interacting player
   * @param components recipe components
   * @param results    recipe results
   */
  private void processExactMatch(Player player, ArrayList<ItemStack> components,
                                 ArrayList<ItemStack> results) {
    for (ItemStack item : components) {
      player.getInventory().removeItem(item);
    }
    giveItemsToPlayer(player, results);
  }

  /**
   * Removes the recipe's components.
   *
   * @param player     interacting player
   * @param components recipe components
   * @param results    recipe results
   */
  private void processMatchingType(Player player, ArrayList<ItemStack> components,
                                   ArrayList<ItemStack> results) {
    Inventory inv = player.getInventory();
    for (SlotAmount slotAmount : getSlotAmounts()) {
      inv.setItem(slotAmount.getSlot(),
          new ItemStack(inv.getItem(slotAmount.getSlot()).getType(), slotAmount.getAmount()));
    }
    giveItemsToPlayer(player, results);
  }

  /**
   * Adds the results directly to the player's inventory if there's space.
   * Otherwise, the results are dropped at the player's feet.
   *
   * @param player  interacting player
   * @param results recipe results
   */
  private void giveItemsToPlayer(Player player, ArrayList<ItemStack> results) {
    for (ItemStack item : results) {
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().dropItem(player.getLocation(), item);
      }
    }
  }

  private ArrayList<SlotAmount> getSlotAmounts() {
    return this.slotAmounts;
  }
}
