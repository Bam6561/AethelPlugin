package me.dannynguyen.aethel.inventories.forge;

import me.dannynguyen.aethel.AethelPlugin;
import me.dannynguyen.aethel.AethelResources;
import me.dannynguyen.aethel.creators.ItemCreator;
import me.dannynguyen.aethel.objects.ForgeRecipe;
import me.dannynguyen.aethel.objects.InventorySlot;
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
 * @version 1.2.5
 * @since 1.1.0
 */
public class ForgeCraft {
  HashMap<Material, ArrayList<InventorySlot>> invMap = new HashMap<>();
  ArrayList<InventorySlot> setInventory = new ArrayList<>();

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
        get(new ItemMetaReader().readItemName(e.getCurrentItem()));

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
        get(new ItemMetaReader().readItemName(e.getClickedInventory().getItem(0)));

    ArrayList<ItemStack> results = recipe.getResults();
    ArrayList<ItemStack> components = recipe.getComponents();

    if (checkMatchingType(player, components)) {
      processMatchingType(player, results);
    } else {
      player.sendMessage(ChatColor.RED + "Insufficient components.");
    }
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
    setInvMap(mapMaterialIndices(inv));

    for (ItemStack item : components) {
      Material reqMaterial = item.getType();
      int reqAmount = item.getAmount();

      if (getInvMap().containsKey(reqMaterial)) {
        if (!checkSufficientMaterials(getInvMap(), reqMaterial, reqAmount)) return false;
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
   * @return material:inventory slots inventory map
   */
  private HashMap<Material, ArrayList<InventorySlot>> mapMaterialIndices(Inventory inv) {
    HashMap<Material, ArrayList<InventorySlot>> invMap = new HashMap<>();

    for (int i = 0; i < 36; i++) {
      ItemStack item = inv.getItem(i);
      if (inv.getItem(i) != null) {
        Material material = item.getType();
        int amount = item.getAmount();

        if (invMap.containsKey(material)) {
          invMap.get(material).add(new InventorySlot(i, item, amount));
        } else {
          ArrayList<InventorySlot> invSlots = new ArrayList<>();
          invSlots.add(new InventorySlot(i, item, amount));
          invMap.put(material, invSlots);
        }
      }
    }
    return invMap;
  }

  /**
   * Determines if the player has sufficient amounts of the required material.
   *
   * @param invMap      material:inventory slots inventory map
   * @param reqMaterial required material
   * @param reqAmount   required amount
   * @return has sufficient amounts of material
   */
  private boolean checkSufficientMaterials(HashMap<Material, ArrayList<InventorySlot>> invMap,
                                           Material reqMaterial, int reqAmount) {
    for (InventorySlot invSlot : invMap.get(reqMaterial)) {
      if (invSlot.getItem().getItemMeta().getPersistentDataContainer().isEmpty()) {
        if (invSlot.getAmount() > 0) {
          reqAmount -= invSlot.getAmount();
          if (checkReqAmountSatisfied(invSlot, reqAmount)) return true;
        }
      }
    }
    return false;
  }

  /**
   * Determines if the required amount of material was satisfied.
   *
   * @param reqAmount required amount
   * @return has sufficient amounts of material
   */
  private boolean checkReqAmountSatisfied(InventorySlot invSlot, int reqAmount) {
    if (reqAmount > 0 || reqAmount == 0) {
      invSlot.setAmount(0);
      getSetInventory().add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), 0));
      if (reqAmount == 0) {
        return true;
      }
    } else {
      int difference = Math.abs(reqAmount);
      invSlot.setAmount(difference);
      getSetInventory().add(new InventorySlot(invSlot.getSlot(), invSlot.getItem(), difference));
      return true;
    }
    return false;
  }

  /**
   * Removes the recipe's components.
   *
   * @param player  interacting player
   * @param results recipe results
   */
  private void processMatchingType(Player player, ArrayList<ItemStack> results) {
    Inventory inv = player.getInventory();
    for (InventorySlot invSlot : getSetInventory()) {
      inv.setItem(invSlot.getSlot(),
          new ItemStack(inv.getItem(invSlot.getSlot()).getType(), invSlot.getAmount()));
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

  private HashMap<Material, ArrayList<InventorySlot>> getInvMap() {
    return this.invMap;
  }

  private void setInvMap(HashMap<Material, ArrayList<InventorySlot>> invMap) {
    this.invMap = invMap;
  }

  private ArrayList<InventorySlot> getSetInventory() {
    return this.setInventory;
  }
}
